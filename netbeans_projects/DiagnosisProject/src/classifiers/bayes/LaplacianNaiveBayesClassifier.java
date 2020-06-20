/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers.bayes;

import framework.AbstractSingleClassifier;
import framework.Classifier;
import framework.Fac;
import framework.ResultSet;
import framework.Sample;
import framework.SampleSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import vector.Vector;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class LaplacianNaiveBayesClassifier<Out> extends AbstractSingleClassifier<Vector, Out>
{

    Map<Out, Double> outputFreq;
    Map<Out, Map<Integer, Double>> conditionalCoordProb;
    Map<Out, Double> pseudoConditionalProb; //smoothed conditional probability for positions that are set "now", but were never set in training
    protected Map<Integer, Set<Out>> index;
    int maxTrainingVectorDimension;
    double laplaceParam=1e-3;

    public LaplacianNaiveBayesClassifier(double laplaceParam)
    {
        this.laplaceParam = laplaceParam;
    }

    public LaplacianNaiveBayesClassifier()
    {
    }
    
    
    public void setLaplaceParam(double smooth)
    {
        laplaceParam = smooth;
    }
    

    @Override
    public void train(SampleSet<Vector, Out> trainingSet)
    {
        maxTrainingVectorDimension = Vector.largestSize(trainingSet.inList());

        outputFreq = new HashMap<Out, Double>(); //frequency of each output value
        index = new HashMap<Integer, Set<Out>>();

        //when Out is the output and Integer the vector position, the Double is the conditional probability
        conditionalCoordProb = new HashMap<Out, Map<Integer, Double>>();
        pseudoConditionalProb = new HashMap<Out, Double>();

        //summing up the co-occurrance counts and counting outputs
        for (Sample<Vector, Out> sample : trainingSet) {
            Vector vec = sample.getInput();
            Out out = sample.getOutput();

            if (outputFreq.containsKey(out)) {
                outputFreq.put(out, outputFreq.get(out) + 1.0);
            } else {
                outputFreq.put(out, 1.0);
            }

            if (!conditionalCoordProb.containsKey(out)) {
                conditionalCoordProb.put(out, new HashMap<Integer, Double>());
            }

            Map<Integer, Double> condProbsHere = conditionalCoordProb.get(out);

            for (int pos : vec.nonZeroPositions()) {
                if (condProbsHere.containsKey(pos)) {
                    condProbsHere.put(pos, condProbsHere.get(pos) + 1.0);
                } else {
                    condProbsHere.put(pos, 1.0);
                }

                if (!index.containsKey(pos)) {
                    index.put(pos, new HashSet<Out>());
                }
                index.get(pos).add(out);
            }

        }

        //smooth normalizing
        for (Map.Entry<Out, Map<Integer, Double>> conditionalEntry : conditionalCoordProb.entrySet()) {
            Out output = conditionalEntry.getKey();
            pseudoConditionalProb.put(output, laplaceParam / (outputFreq.get(output) + laplaceParam * 2));

            double denominator = outputFreq.get(output) + laplaceParam * 2;
            for (Map.Entry<Integer, Double> innerEntry : conditionalEntry.getValue().entrySet()) {
                innerEntry.setValue((innerEntry.getValue() + laplaceParam) / denominator);
            }
        }

        //prior normalizing, with log
        double size = trainingSet.size();
        for (Map.Entry<Out, Double> entry : outputFreq.entrySet()) {
            entry.setValue(Math.log(entry.getValue() / size));
        }
    }

    @Override
    public ResultSet<Out> classify(Vector input, int limit)
    {
        ResultSet<Out> results = new ResultSet<Out>(limit);

        //inverted index optimization
        Set<Out> candidateOutputs = new HashSet<Out>();
        for (int pos : input.nonZeroPositions()) {
            if (index.containsKey(pos)) {
                candidateOutputs.addAll(index.get(pos));
            }
        }

        
        for (Map.Entry<Out, Map<Integer, Double>> entry : conditionalCoordProb.entrySet()) {
            double logProb = 0.0;

            Out output = entry.getKey();

            if (!candidateOutputs.contains(output)) {
                continue;
            }

            Map<Integer, Double> condProbsHere = entry.getValue();

            // prior probability of the output
            logProb += outputFreq.get(output);

            int positionsAccountedFor = 0; //how many positions have been taken into the calculation. 
            double pseudoLogCondProb = Math.log(pseudoConditionalProb.get(output));
            double antiPseudoLogCondProb = Math.log(1 - pseudoConditionalProb.get(output)); //smoothed cond.prob. for pos. that were never seen

            for (int pos : input.nonZeroPositions()) {
                //training && now
                if (condProbsHere.containsKey(pos)) {
                    logProb += Math.log(condProbsHere.get(pos));
                    ++positionsAccountedFor;
                } // !training && now
                else {
                    logProb += pseudoLogCondProb;//Math.log(1e-6);//pseudoLogCondProb;
                }
            }

            for (int pos : condProbsHere.keySet()) {
                // training && !now
                if (input.get(pos) == 0) {
                    logProb += Math.log(1 - condProbsHere.get(pos));
                    ++positionsAccountedFor;
                }
            }

            //!training && !now
            logProb += (maxTrainingVectorDimension-positionsAccountedFor) * antiPseudoLogCondProb;

            results.push(output, Math.exp(logProb));
        }

        return results;

    }

    @Override
    public String toString()
    {
        return "Bayes{" + "laplaceP=" + laplaceParam + '}';
    }

    public static class Factory<Out> implements Fac<Classifier<Vector, Out>>
    {
        //default;
        double smooth = 1e-3;

        public Factory(double smooth)
        {
            this.smooth = smooth;
        }
        
        public Factory()
        {
            
        }

        public Classifier<Vector, Out> createNew()
        {
            LaplacianNaiveBayesClassifier lap = new LaplacianNaiveBayesClassifier();
            lap.setLaplaceParam(smooth);
            return lap;
        }
    }
}