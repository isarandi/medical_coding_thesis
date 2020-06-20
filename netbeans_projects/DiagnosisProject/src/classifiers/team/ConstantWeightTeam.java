/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers.team;

import framework.AbstractSingleClassifier;
import framework.Classifier;
import framework.ResultSet;
import framework.SampleSet;
import framework.Fac;
import framework.Sample;
import framework.evaluation.PositionWeightedMetric;
import framework.evaluation.EvaluationMetric;
import java.util.HashMap;
import java.util.Map;
import parallel.ParallelFor;

/**
 *
 * @author qr
 */
public class ConstantWeightTeam<In,Out> extends AbstractSingleClassifier<In,Out> {

    Classifier<In, Out> c1;
    Classifier<In, Out> c2;
    transient double ratio;
    double weight = 0.0;
    
    transient EvaluationMetric vm = new PositionWeightedMetric(5);

    public ConstantWeightTeam(Classifier<In, Out> c1, Classifier<In, Out> c2, double ratio)
    {
        this.c1 = c1;
        this.c2 = c2;
        this.ratio = ratio;
    }

    public Classifier<In, Out> getC1()
    {
        return c1;
    }

    public Classifier<In, Out> getC2()
    {
        return c2;
    }

    public void train(SampleSet<In, Out> trainingSet)
    {
        SampleSet<In, Out> memberTrainingSet = trainingSet.lowerPart(ratio);
        SampleSet<In, Out> test = trainingSet.higherPart(ratio);
        
        if (memberTrainingSet.size()==0 || test.size() ==0)
        {
            weight=0.5;
            c1.train(trainingSet);
            c2.train(trainingSet);
            return;
        }
        
        c1.train(memberTrainingSet);
        c2.train(memberTrainingSet);
        
        final Map<In,ResultSet<Out>> c1results = new HashMap<In, ResultSet<Out>>();
        final Map<In,ResultSet<Out>> c2results = new HashMap<In, ResultSet<Out>>();
        
        new ParallelFor<Sample<In,Out>>()
        {
            @Override
            protected void ForBody(Sample<In, Out> sample)
            {
                In input = sample.getInput();
                ResultSet<Out> r1 = c1.classify(input, 20);
                ResultSet<Out> r2 = c2.classify(input, 20);

                r1.normalize();
                r2.normalize();

                synchronized(this)
                {
                    c1results.put(input,r1);
                    c2results.put(input,r2);
                }
            }
        }.execute(test);
        
       
        double bestw = 0.0;
        double bestpercent = 0.0;
        for (double w = 0.0; w<=1; w+=0.03)
        {
            double nowpercent = validateWeighting(c1results, c2results, test, w, vm);
            //System.out.println(String.format("weight: %f - %f", w, nowpercent));
            if (nowpercent>bestpercent)
            {
                bestpercent=nowpercent;
                bestw = w;
            }
        }
        this.weight = bestw;
        //System.out.println("Best weight: "+bestw+" ("+bestpercent+")");
        
        c1.train(trainingSet);
        c2.train(trainingSet);
    }
    
    private ResultSet<Out> blendResults(ResultSet<Out> r1, ResultSet<Out> r2, double w1, int limit)
    {
        ResultSet<Out> endResults = new ResultSet<Out>(limit);
       
        endResults.blend(r1, w1, ResultSet.ADD_RESULT_BLENDER);
        endResults.blend(r2, 1-w1, ResultSet.ADD_RESULT_BLENDER);
        return endResults;
    }

    public ResultSet<Out> classify(In input, int limit)
    {
        ResultSet<Out> c1r = c1.classify(input, 40);
        ResultSet<Out> c2r = c2.classify(input, 40);
        
        c1r.normalize();
        c2r.normalize();

        return blendResults(c1r, c2r, weight, limit);
    }

    private double validateWeighting(final Map<In,ResultSet<Out>> c1results, final Map<In,ResultSet<Out>> c2results, final SampleSet<In,Out> sampleSet, final double w, final EvaluationMetric vm)
    {
        final double overallSum[] = new double[1];
        overallSum[0] = 0.0;
        
        new ParallelFor<Sample<In,Out>>()
        {

            @Override
            protected void ForLoop(Iterable<Sample<In,Out>> samples) 
            {
                double taskSum = 0.0;
                for (Sample<In,Out> s: samples)
                {
                    ResultSet<Out> r1 = c1results.get(s.getInput());
                    ResultSet<Out> r2 = c2results.get(s.getInput());

                    ResultSet<Out> blended = blendResults(r1, r2, w, Integer.MAX_VALUE);
                    taskSum+=vm.calculate(blended, s.getOutput());
                }
                synchronized(overallSum)
                {
                    overallSum[0]+=taskSum;
                }
            }
            
        }.execute(sampleSet);
        
        return overallSum[0]/c1results.size();
    }
    
    public static class Factory<In,Out> implements Fac<Classifier<In,Out>> 
    {
        Fac<Classifier<In,Out>> c1Fac;
        Fac<Classifier<In,Out>> c2Fac;
        double ratio;

        public Factory(Fac<Classifier<In, Out>> c1Fac, Fac<Classifier<In, Out>> c2Fac, double ratio) {
            this.c1Fac = c1Fac;
            this.c2Fac = c2Fac;
        }
        
        public Classifier<In, Out> createNew() {
            return new ConstantWeightTeam<In, Out>(c1Fac.createNew(), c2Fac.createNew(), ratio);
        }
        
    }
}
