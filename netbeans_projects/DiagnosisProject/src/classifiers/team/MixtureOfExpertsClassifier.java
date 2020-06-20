/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers.team;

import framework.AbstractSingleClassifier;
import framework.adaption.Converter;
import framework.Classifier;
import java.util.HashMap;
import java.util.Map;
import framework.ResultSet;
import framework.Result;
import framework.SampleSet;
import framework.Sample;
import framework.Fac;
import parallel.ParallelFor;

/**
 *
 * @author qr
 */
public class MixtureOfExpertsClassifier<In, Out, ChildID> extends AbstractSingleClassifier<In, Out>
{

    private Converter<Out, ChildID> childSelector;
    private transient Fac<Classifier<In, Out>> childFactory;
    private Classifier<In, ChildID> rootClassifier;
    private Map<ChildID, Classifier<In, Out>> childClassifiers;

    public Converter<Out, ChildID> getChildSelector()
    {
        return childSelector;
    }

    public Classifier<In, ChildID> getRootClassifier()
    {
        return rootClassifier;
    }

    public MixtureOfExpertsClassifier(Converter<Out, ChildID> childSelector,
            Classifier<In, ChildID> root,
            Fac<Classifier<In, Out>> childFactory)
    {
        this.childSelector = childSelector;
        this.childFactory = childFactory;

        this.rootClassifier = root;
        this.childClassifiers = new HashMap<ChildID, Classifier<In, Out>>();
    }

    @Override
    public void train(SampleSet<In, Out> trainingSet)
    {
        final Map<ChildID, SampleSet<In, Out>> childSampleSets = new HashMap<ChildID, SampleSet<In, Out>>();
        final SampleSet<In, ChildID> rootSampleSet = new SampleSet<In, ChildID>();

        childClassifiers.clear();

        for (Sample<In, Out> sample : trainingSet) {
            ChildID label = childSelector.convert(sample.getOutput());
            rootSampleSet.add(sample.getInput(), label);

            if (!childSampleSets.containsKey(label)) {
                childClassifiers.put(label, childFactory.createNew());
                childSampleSets.put(label, new SampleSet<In, Out>());
            }

            childSampleSets.get(label).add(sample.getInput(), sample.getOutput());
        }

        //FileProcessor.writeToFile(rootSampleSet.export(), "D:\\onlab\\mintak\\rootHDB.rep");
        
        rootClassifier.train(rootSampleSet);

        new ParallelFor<ChildID>()
        {

            @Override
            protected void ForBody(ChildID label)
            {
                childClassifiers.get(label).train(childSampleSets.get(label));
                //System.out.println(String.format("Child %s done.", label.toString()));
            }
        }.execute(childClassifiers.keySet());

        //System.out.println("hier done.");

    }

    @Override
    public ResultSet<Out> classify(In input, int limit)
    {

        ResultSet<Out> endResults = new ResultSet<Out>();
        ResultSet<ChildID> childIDResults = rootClassifier.classify(input, Integer.MAX_VALUE);

        for (Result<ChildID> childIDResult : childIDResults) {
            ChildID childID = childIDResult.getOutput();

            if (!childClassifiers.containsKey(childID)) //if the high classifier was never trained for the returned label, it is unaccaptable
            {
                continue;
            }
            ResultSet<Out> localResults = childClassifiers.get(childID).classify(input, limit*10);
            endResults.blend(localResults, childIDResult.getConfidence(), ResultSet.ADD_RESULT_BLENDER);
        }

        endResults.setLimit(limit);
        return endResults;
    }

    @Override
    public String toString()
    {
        return "MOE{ " + childSelector + ", child= " + childFactory + ", root= " + rootClassifier + '}';
    }

    public static class Factory<In, Out, ChildID> implements Fac<Classifier<In, Out>>
    {

        private Fac<Classifier<In, ChildID>> rootfac;
        private Converter<Out, ChildID> childSelector;
        private Fac<Classifier<In, Out>> childFactory;

        public Factory(Converter<Out, ChildID> childSelector, Fac<Classifier<In, ChildID>> rootfac, Fac<Classifier<In, Out>> childFactory)
        {
            this.rootfac = rootfac;
            this.childSelector = childSelector;
            this.childFactory = childFactory;
        }

        public Classifier<In, Out> createNew()
        {
            return new MixtureOfExpertsClassifier<In, Out, ChildID>(childSelector, rootfac.createNew(), childFactory);
        }

        @Override
        public String toString()
        {
            return "MOEF{ " + childSelector + ", child= " + childFactory + ", root= " + rootfac + '}';
        }
    }
}
