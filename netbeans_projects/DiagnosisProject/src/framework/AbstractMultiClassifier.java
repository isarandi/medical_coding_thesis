/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Istvan Sarandi
 */
public abstract class AbstractMultiClassifier<In,Out> implements Classifier<In, Out>
{
    public ResultSet<Out> classify(In input, int limit)
    {
        return classifyMany(Collections.singletonList(input), limit).get(0);
    }

    public abstract List<ResultSet<Out>> classifyMany(List<In> inputs, int limit);

    public abstract void train(SampleSet<In, Out> trainingSet);

    public boolean prefersMany()
    {
        return true;
    }

}
