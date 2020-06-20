/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers;

import converters.BagOfWordsTransform;
import framework.AbstractSingleClassifier;
import framework.ResultSet;
import framework.Sample;
import framework.SampleSet;
import framework.adaption.InputTransform;
import java.util.ArrayList;
import java.util.List;
import other.MultiSet;
import vector.Vector;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
class CoreIdealClassifier<Out>
{
    SampleSet<Vector, Out> trainingSamples;
    SampleSet<Vector, Out> testSamples;

    
    ResultSet<Out> aprioriResultSet;
    
    int labelNotSeen = 0;
    int inconsistent = 0;
    int wordsNotSeen = 0;

    public CoreIdealClassifier()
    {

    }

    MultiSet<Out> getCorrect(SampleSet<Vector, Out> samples, Vector input)
    {
        MultiSet<Out> correctOutputs = new MultiSet<Out>();
        for (Sample<Vector, Out> s : samples)
        {

            if (s.getInput().nonZeroCount() == input.nonZeroCount())
            {
                boolean equal = true;

                for (int pos : s.getInput().nonZeroPositions())
                {
                    if (Math.abs(s.getInput().get(pos) - input.get(pos)) > 1e-7)
                    {
                        equal = false;
                        break;
                    }
                }
                if (equal)
                {
                    correctOutputs.add(s.getOutput());
                    break;

                }
            }
        }
        return correctOutputs;
    }

    public ResultSet<Out> createApriori(SampleSet<Vector, Out> sampleSet, int limit)
    {
        aprioriResultSet = new ResultSet<Out>(limit);

        MultiSet<Out> ms = new MultiSet<Out>(trainingSamples.outList());
        for (Out out : ms.distinctSet())
        {
            aprioriResultSet.push(out, ((double) ms.getMultiplicity(out)) / ms.size());
        }
        return aprioriResultSet;
    }

    public ResultSet<Out> classify(final Vector input, int limit, int pro)
    {
        aprioriResultSet.setLimit(limit);
        
        if (input.nonZeroCount() == 0)
        {
            wordsNotSeen++;
            return aprioriResultSet;
        }

        Out correctOutput = getCorrect(testSamples, input).mostFrequent();
        

        MultiSet<Out> trainingOutputs = getCorrect(trainingSamples, input);
        Out trainingOutput = trainingOutputs.mostFrequent();

        // if correct output not present in the training
        if (!trainingSamples.outList().contains(correctOutput))
        {
            labelNotSeen++;
            return aprioriResultSet;
        }

//        // if correct output is not the real answer
//        if (trainingOutput != null && !trainingOutput.equals(correctOutput))
//        {
//            inconsistent++;
//            ResultSet<Out> rs = new ResultSet<Out>(limit);
//
//            for (Out out : trainingOutputs.distinctSet())
//            {
//                rs.push(out, ((double) trainingOutputs.getMultiplicity(out)) / trainingOutputs.size());
//            }
//            return rs;
//        }

        // if never present with this label
        if (pro<1)
        {
            boolean foundOverlap = false;
            for (Sample<Vector, Out> s : trainingSamples)
            {
                if (s.getOutput().equals(correctOutput))
                {
                    if (s.getInput().dotProduct(input) != 0)
                    {
                        foundOverlap = true;
                        break;
                    }
                }
            }

            if (!foundOverlap)
            {
                return aprioriResultSet;
            }
        }

        ResultSet<Out> rs = new ResultSet<Out>(limit);
        rs.push(correctOutput, 1.0);
        return rs;

    }

    public void setTestSamples(SampleSet<Vector, Out> allSamples)
    {
        this.testSamples = allSamples;
    }

    public void train(SampleSet<Vector, Out> trainingSet)
    {
        inconsistent = labelNotSeen = wordsNotSeen = 0;
        trainingSamples = trainingSet;
        createApriori(trainingSet, 150);

    }
}

public class IdealClassifier<In, Out> extends AbstractSingleClassifier<In, Out> 
{
    InputTransform<In, Vector> inputTransform;
    CoreIdealClassifier<Out> coreClassifier;
    MultiSet<Sample<In, Out>> allSamples;
    private final int pro;

    public final void setAllSamples(SampleSet<In, Out> allSamples)
    {
        this.allSamples = new MultiSet<Sample<In, Out>>();
        for (Sample<In, Out> s : allSamples)
        {
            this.allSamples.add(s);
        }
    }

    public IdealClassifier(InputTransform<In, Vector> inputTransform, int pro)
    {
        this.inputTransform = inputTransform;

        this.pro = pro;
        coreClassifier = new CoreIdealClassifier<Out>();
    }

    public ResultSet<Out> classify(In input, int limit)
    {
        return coreClassifier.classify(inputTransform.getClassifyConverter().convert(input), limit, pro);
    }

    public IdealClassifier(InputTransform<In, Vector> inputTransform, SampleSet<In, Out> allSamples, int pro)
    {
        this(inputTransform, pro);
        setAllSamples(allSamples);
    }

    public void train(SampleSet<In, Out> trainingSet)
    {
        coreClassifier.train(trainingSet.<Vector, Out>convert(inputTransform.getTrainingConverter(), null));

        MultiSet<Sample<In, Out>> testSamples = new MultiSet<Sample<In, Out>>(allSamples);

        for (Sample<In, Out> trs : trainingSet)
        {
            testSamples.remove(trs);
        }

        SampleSet<Vector, Out> testSet = new SampleSet<Vector, Out>();
        for (Sample<In, Out> s : testSamples)
        {
            testSet.add(inputTransform.getClassifyConverter().convert(s.getInput()), s.getOutput());
        }


        coreClassifier.setTestSamples(testSet);
    }
}
