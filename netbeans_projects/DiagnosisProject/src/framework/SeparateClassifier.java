/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import java.util.List;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class SeparateClassifier<In,Out> implements Classifier<In, Out>
{

    ClassificationAbility<In, Out> cl;
    transient Trainable<In, Out> tr;

    public SeparateClassifier(ClassificationAbility<In, Out> cl, Trainable<In, Out> tr)
    {
        this.cl = cl;
        this.tr = tr;
    }
            
    public ResultSet<Out> classify(In input, int limit)
    {
        return cl.classify(input, limit);
    }
    
    public List<ResultSet<Out>> classifyMany(List<In> input, int limit)
    {
        return cl.classifyMany(input, limit);
    }

    public void train(SampleSet<In, Out> trainingSet)
    {
        tr.train(trainingSet);
    }

    public boolean prefersMany()
    {
        return cl.prefersMany();
    }
   
}
