/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classifiers.vectorspace;

import framework.AbstractSingleClassifier;
import framework.Fac;
import vector.Vector;
import framework.Classifier;
import framework.SampleSet;
import framework.ResultSet;
import framework.Sample;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import parallel.ParallelFor;
import vector.SparseVector;


/**
 *
 * @author Jeno
 */
public class VectorSpaceClassifier<Out> extends AbstractSingleClassifier<Vector,Out> {

    private SampleSet<Vector,Out> trainingSet;
    //protected Map<Integer, Set<Integer>> invertedIndex;
    private InvertedSampleIndex invertedIndex;

    @Override
    public void train(SampleSet<Vector,Out> trainingSet) {
        this.trainingSet = trainingSet;
        this.invertedIndex = new InvertedSampleIndex(trainingSet);
       
    }

    protected double cosineSimilarity(Vector input1, Vector input2)
    {
        double sim = input1.dotProduct(input2) / (input1.absoluteValue() * input2.absoluteValue());
        if (Double.isNaN(sim))
            sim = 0.0;
        return sim;
    }
    

    protected double euclidSimilarity(Vector input1, Vector input2)
    {
        Vector diff = input1.subtraced(input2, SparseVector.getFactory());
        
        double sim = 1.0/diff.absoluteValue();
        if (Double.isNaN(sim))
            sim = 0.0;
        return sim;
    }

    @Override
    public ResultSet<Out> classify(final Vector input, int limit) {
        final ResultSet<Out> results = new ResultSet<Out>(limit*20);
        
        Set<Integer> candidateIndices = invertedIndex.lookup(input);

        new ParallelFor<Integer> ()
        {
            @Override
            protected void ForLoop(Iterable<Integer> taskIndices) {
                ResultSet<Out> taskResults = new ResultSet<Out>();
                for (int index : taskIndices)
                {
                    double similarity = cosineSimilarity(input, trainingSet.inList().get(index));
                    taskResults.push(trainingSet.outList().get(index), similarity);
                }
                synchronized (results)
                {
                    results.blend(taskResults, 1.0);
                }
            }
        }.execute(candidateIndices);
        results.setLimit(limit);
        return results;
    }
    
    

    public Fac<Classifier<Vector, Out>> getCloningFactory()
    {
        return new Factory();
    }
    
    class Factory implements Fac<Classifier<Vector, Out>>
    {

        public Classifier<Vector, Out> createNew()
        {
            return new VectorSpaceClassifier<Out>();
        }
        
    }

    @Override
    public String toString()
    {
        return "VecSp";
    }
    
    
}
class InvertedSampleIndex implements Serializable {
    protected Map<Integer, Set<Integer>> index;

    public <Out> InvertedSampleIndex(SampleSet<Vector,Out> samples) {
        index = new HashMap<Integer, Set<Integer>>();
        int i=0;
        
        for (Sample<Vector, Out> s: samples)
        {
            for (int pos: s.getInput().nonZeroPositions())
            {
                if (!index.containsKey(pos))
                {
                    index.put(pos, new HashSet<Integer>());
                }
                index.get(pos).add(i);
            }
            ++i;
        }
    }
    
    public Set<Integer> lookup(Vector input)
    {
        Set<Integer> candidateIndices = new HashSet<Integer>();
        for (int pos: input.nonZeroPositions())
        {
            if (index.containsKey(pos))
                candidateIndices.addAll(index.get(pos));
        }
        return candidateIndices;
    }
    
    
}
