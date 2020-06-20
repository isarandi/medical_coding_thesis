/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import java.util.ArrayList;
import java.util.List;
import parallel.ParallelFor;

/**
 *
 * @author Istvan Sarandi
 */
public abstract class AbstractSingleClassifier<In,Out> implements Classifier<In,Out>
{

    public abstract ResultSet<Out> classify(In input, int limit);

    public List<ResultSet<Out>> classifyMany(final List<In> inputs, final int limit)
    {
        final List<ResultSet<Out>> resultSets = new ArrayList<ResultSet<Out>>(inputs.size());
        while (resultSets.size()<inputs.size())
            resultSets.add(null);
        
        new ParallelFor<Integer>()
        {
            @Override
            protected void ForBody(Integer inputIndex)
            {
                ResultSet<Out> rs = classify(inputs.get(inputIndex), limit);
                resultSets.set(inputIndex, rs);
            }
        }.execute(0, inputs.size());

        return resultSets;
    }

    public abstract void train(SampleSet<In, Out> trainingSet);

    public boolean prefersMany()
    {
        return false;
    }
    
}
