/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework.evaluation;

import framework.ClassificationAbility;
import framework.Classifier;
import framework.Result;
import framework.ResultSet;
import framework.Sample;
import framework.SampleSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import parallel.ParallelFor;

/**
 *
 * @author qr
 */
public class Evaluator
{
    public static <In, Out> List<List<Double>> crossValidate(Classifier<In, Out> classif, SampleSet<In, Out> sampleSet, int fold, EvaluationMetric... vms)
    {
        return crossValidate(classif, sampleSet, fold, fold, vms);
    }

    public static <In, Out> List<List<Double>> crossValidate(Classifier<In, Out> classif, SampleSet<In, Out> sampleSet, int fold, int howmanytorun, EvaluationMetric... vms)
    {
        List<List<Double>> successes = new ArrayList<List<Double>>();//new double[vms.length][fold+1];
        successes.add(new ArrayList<Double>()); //for avg

        for (int i = 0; i < howmanytorun; ++i)
        {
            System.out.print("training time: ");
            long before = Calendar.getInstance().getTimeInMillis();
            
            classif.train(sampleSet.crossValidTrainingPart(fold, i));

            long after = Calendar.getInstance().getTimeInMillis();
            System.out.println(String.format("%d ms", after-before));
            
            System.out.print("evaluation time: ");
            before = Calendar.getInstance().getTimeInMillis();
            
            List<Double> successesNow = validate(classif, sampleSet.crossValidTestPart(fold, i), new AvgAggregator(), vms);
            
            after = Calendar.getInstance().getTimeInMillis();
            System.out.println(String.format("%d ms", after-before));
            
            successes.add(successesNow);
        }

        for (int i = 0; i < vms.length; ++i)
        {
            double sum = 0.0;
            for (int j = 0; j < howmanytorun; ++j)
            {
                sum += successes.get(j + 1).get(i);
            }

            successes.get(0).add(sum / howmanytorun);
        }

        return successes;
    }

    public static <In, Out> List<List<Double>> learningCurve(final Classifier<In, Out> classif, SampleSet<In, Out> sampleSet, int numSteps, int numFolds, int doNumFolds, EvaluationMetric... vms)
    {
        List<List<Double>> avgResults = new ArrayList<List<Double>>(numSteps);
        for (int i = 0; i < numSteps; ++i)
        {
            List<Double> stepavgs = new ArrayList<Double>();
            avgResults.add(stepavgs);
            for (int j = 0; j < vms.length*2; ++j)
            {
                stepavgs.add(0.0);
            }
        }

        for (int fold = 0; fold < doNumFolds; ++fold)
        {
            SampleSet<In, Out> te = sampleSet.crossValidTestPart(numFolds, fold);
            SampleSet<In, Out> tr = sampleSet.crossValidTrainingPart(numFolds, fold);


            for (int step = 1; step <= numSteps; ++step)
            {
                double ratio = ((double) step) / numSteps;
                classif.train(tr.lowerPart(ratio));
                List<Double> valresTest = validate(classif, te, vms);
                List<Double> valresTrain = validate(classif, tr.lowerPart(ratio), vms);

                for (int j = 0; j < vms.length; ++j)
                {
                    double sumWas = avgResults.get(step - 1).get(j);
                    avgResults.get(step - 1).set(j, sumWas + valresTest.get(j)/doNumFolds);
                }
                
                for (int j = vms.length; j < 2*vms.length; ++j)
                {
                    double sumWas = avgResults.get(step - 1).get(j);
                    avgResults.get(step - 1).set(j, sumWas + valresTrain.get(j-vms.length)/doNumFolds);
                }
            }

        }

        return avgResults;
    }

    public static <In, Out, Aggr> List<Aggr> validateBatch(final ClassificationAbility<In, Out> classif, SampleSet<In, Out> testSet, final EvaluationAggregator<Aggr> aggreg, final EvaluationMetric... vms)
    {

        List<ResultSet<Out>> resultSets = classif.classifyMany(testSet.inList(), 150);

        int i = 0;
        for (ResultSet<Out> resultSet : resultSets)
        {
            List<Double> validationList = new ArrayList<Double>(vms.length);
            for (EvaluationMetric valmet : vms)
            {
                validationList.add(valmet.calculate(resultSet, testSet.outList().get(i)));
            }

            aggreg.push(validationList);
            ++i;
        }

        return aggreg.getAggregate();

    }

    public static <In, Out, Aggr> List<Aggr> validate(final ClassificationAbility<In, Out> classif, SampleSet<In, Out> testSet, final EvaluationAggregator<Aggr> aggreg, final EvaluationMetric... vms)
    {
        if (classif.prefersMany())
        {
            return validateBatch(classif, testSet, aggreg, vms);
        }

        final AtomicInteger total = new AtomicInteger(0);

        new ParallelFor<Sample<In, Out>>()
        {
            @Override
            protected void ForBody(Sample<In, Out> sample)
            {
                ResultSet<Out> resultSet = classif.classify(sample.getInput(), 150);

                List<Double> validationList = new ArrayList<Double>(vms.length);
                for (EvaluationMetric valmet : vms)
                {
                    validationList.add(valmet.calculate(resultSet, sample.getOutput()));
                }

                aggreg.push(validationList);

                int totalnow = total.incrementAndGet();
                if (totalnow % 100 == 0)
                {
                    //System.out.println("Done validating "+Integer.toString(totalnow));
                }
            }
        }.execute(testSet);

        return aggreg.getAggregate();
    }

    public static <In, Out> List<Double> validate(final ClassificationAbility<In, Out> classif, SampleSet<In, Out> testSet, final EvaluationMetric... vms)
    {
        return validate(classif, testSet, new AvgAggregator(), vms);
    }
//
//    public <In, Out> double[] multiValidate(ClassificationAbility<In, Out> classif, Trainable<In, Out> trainable, int times, SampleSet<In, Out> sampleSet, double splitProportion) {
//        Random r = new Random(0);
//        double[] values = new double[vm.length];
//        for (int c = 0; c < times; ++c) {
//            sampleSet.shuffle(r);
//            SampleSet<In, Out> training = sampleSet.lowerPart(splitProportion);
//            SampleSet<In, Out> test = sampleSet.higherPart(splitProportion);
//
//            trainable.train(training);
//            double[] currentvalues = validate(classif, test);
//            for (int i = 0; i < currentvalues.length; ++i) {
//                values[i] += currentvalues[i];
//            }
//        }
//
//        for (int i = 0; i < values.length; ++i) {
//            values[i] /= times;
//        }
//        return values;
//    }

//    public <In, Out> double[] multiValidate(Classifier<In, Out> classifier, int times, SampleSet<In, Out> sampleSet, double splitProportion) {
//        return multiValidate(classifier, classifier, times, sampleSet, splitProportion);
//    }
    public static <In, Out> String fullReport(ClassificationAbility<In, Out> classif, SampleSet<In, Out> testSet)
    {
        StringBuilder bw = new StringBuilder();

        int snum = 0;
        for (Sample<In, Out> sample : testSet)
        {
            ResultSet<Out> results = classif.classify(sample.getInput(), Integer.MAX_VALUE);

            bw.append(sample.getInput().toString()).append(";");
            bw.append(sample.getOutput().toString()).append(";");
            bw.append(results.getPosition(sample.getOutput())).append(";");

            int counter = 1;
            for (Result<Out> r : results)
            {
                bw.append(counter).append(";");
                bw.append(r.getOutput().toString()).append(";");
                bw.append(r.getConfidence()).append(";");

                ++counter;
                if (counter > 10)
                {
                    break;
                }
            }

            bw.append(System.getProperty("line.separator"));
            snum++;

            if (snum % 100 == 0)
            {
                System.out.println("done " + snum);
            }
        }
        return bw.toString();
    }
}