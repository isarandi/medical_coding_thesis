/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers.neuralnetwork;

import framework.Fac;
import framework.Sample;
import framework.SampleSet;
import framework.evaluation.NormalizedConfidenceMetric;
import framework.evaluation.PositionAtMostMetric;
import framework.evaluation.PositionWeightedMetric;
import parallel.ParallelFor;
import vector.Vector;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class MLPTraining
{
    int maxepochs;
    double trainRatio;
    LearningMonitor learningMonitor;
    ErrorCalculator errorCalculator;
    ErrorCalculator monitoringErrorCalculator;
    Fac<LayerTrainer> layerTrainerFac;
    boolean batch;
    MLP mlp;
    SampleSet<Vector, Vector> realTrainingSet;
    SampleSet<Vector, Vector> testSet;
    private LayerTrainer outputLayerTrainer;
    boolean verbose = false;

    public MLPTraining(int maxepochs, double trainRatio, Fac<LayerTrainer> layerTrainerFac, LearningMonitor learningMonitor, ErrorCalculator errorCalculator, ErrorCalculator monitoringErrorCalculator, boolean batch)
    {
        this.maxepochs = maxepochs;
        this.trainRatio = trainRatio;
        this.learningMonitor = learningMonitor;
        this.errorCalculator = errorCalculator;
        this.monitoringErrorCalculator = monitoringErrorCalculator;
        this.layerTrainerFac = layerTrainerFac;
        this.batch = batch;
    }

    public MLPTraining(int maxepochs, double trainRatio, Fac<LayerTrainer> layerTrainerFac, LearningMonitor learningMonitor, ErrorCalculator errorCalculator)
    {
        this(maxepochs, trainRatio, layerTrainerFac, learningMonitor, errorCalculator, errorCalculator, false);
    }

    public MLPTraining(int maxepochs, Fac<LayerTrainer> layerTrainerFac, ErrorCalculator errorCalculator)
    {
        this(maxepochs, 1.0, layerTrainerFac, null, errorCalculator, errorCalculator, false);
    }

    public MLPTraining(int maxepochs, double trainRatio, Fac<LayerTrainer> layerTrainerFac, LearningMonitor learningMonitor, ErrorCalculator errorCalculator, boolean batch)
    {
        this(maxepochs, trainRatio, layerTrainerFac, learningMonitor, errorCalculator, errorCalculator, batch);
    }

    public void train(MLP mlp, SampleSet<Vector, Vector> trainingSet)
    {
        this.mlp = mlp;

        ErrorCalculator firstEC = new EvaluationErrorCalculator(new PositionAtMostMetric(1));
        ErrorCalculator tenEC = new EvaluationErrorCalculator(new PositionAtMostMetric(10));
        ErrorCalculator weighted = new EvaluationErrorCalculator(new PositionWeightedMetric(10));
        ErrorCalculator confidence = new EvaluationErrorCalculator(new NormalizedConfidenceMetric(10));

        // initializing Layer Trainers.
        initLayerTrainers();

        realTrainingSet = new SampleSet<Vector, Vector>(trainingSet.lowerPart(trainRatio));
        testSet = trainingSet.higherPart(trainRatio);

        initLearningMonitor();

        int epochNum;
        for (epochNum = 0; epochNum < maxepochs && learningMonitor.shouldContinue(); ++epochNum)
        {

            epoch();

            if (trainRatio != 1.0)
            {
                double avgErr = averageError();
                learningMonitor.pushTestError(avgErr);
                if (verbose)
                {
                    System.out.println(
                            String.format("error: %d;%f",
                                          epochNum,
                                          avgErr));
                }
//                                      averageError(realTrainingSet, errorCalculator), //averageError(mlp, test, firstEC), 
//                                      averageError(testSet, firstEC),
//                                      averageError(testSet, tenEC)));
//                averageError(mlp, test, weighted),
//                averageError(mlp, test, confidence)
//                        ));

                //Logger.getLogger("MLPTraining").log(Level.FINEST, String.format("Epoch %d done. Error: %f", epochNum, error));
            }
        }

        int optimalEpochs = epochNum;
        //System.out.println(String.format("Optimal epochNum: %d",optimalEpochs));
        
        realTrainingSet = trainingSet;
        //retraining with the optimal number of epochs
        if (trainRatio != 1.0)
        {
            for (Layer layer : mlp.layers)
            {
                layer.initRandomWeights();
            }

            for (epochNum = 0; epochNum < optimalEpochs; ++epochNum)
            {
                epoch();
            }
        }

    }

    private void initLayerTrainers()
    {
        LayerTrainer prevLayerTrainer = layerTrainerFac.createNew();
        prevLayerTrainer.setLayer(mlp.inputLayer());

        for (Layer l : mlp.layers.subList(1, mlp.layers.size()))
        {
            LayerTrainer newLayerTrainer = layerTrainerFac.createNew();
            newLayerTrainer.setPreviousTrainer(prevLayerTrainer);
            newLayerTrainer.setLayer(l);

            prevLayerTrainer = newLayerTrainer;
        }
        this.outputLayerTrainer = prevLayerTrainer;
    }

    protected void epoch()
    {

        new ParallelFor<Sample<Vector, Vector>>()
        {
            @Override
            protected void ForBody(Sample<Vector, Vector> sample)
            {
                outputLayerTrainer.newSample();
                mlp.inputLayer().setInput(sample.getInput());
                Vector actual = outputLayerTrainer.getOutput();
                Vector alpha = errorCalculator.minusHalfderivWRTOutput(actual, sample.getOutput());
                outputLayerTrainer.dealWithAlpha(alpha, errorCalculator instanceof LogLikelihoodErrorCalculator);
            }
        }.execute(realTrainingSet, (batch ? 0 : 1), 0);

        outputLayerTrainer.newEpoch();

    }

    protected double averageError()
    {
        return averageError(testSet, monitoringErrorCalculator);
    }

    protected double averageError(SampleSet<Vector, Vector> samples, final ErrorCalculator ec)
    {
        final double[] errorSum = new double[1];
        errorSum[0] = 0.0;

        new ParallelFor<Sample<Vector, Vector>>()
        {
            @Override
            protected void ForLoop(Iterable<Sample<Vector, Vector>> items)
            {

                double myErrorSum = 0;
                for (Sample<Vector, Vector> s : items)
                {
                    Vector actual = mlp.getOutput(s.getInput());
                    Vector desired = s.getOutput();
                    myErrorSum += ec.calculateError(actual, desired);
                }
                synchronized (errorSum)
                {
                    errorSum[0] += myErrorSum;
                }
            }
        }.execute(samples);

        return errorSum[0] / samples.size();
    }

    public void initLearningMonitor()
    {
        if (learningMonitor == null)
            learningMonitor = new IdleErrorMonitor();
        else
            learningMonitor = learningMonitor.getCloningFactory().createNew();
    }

    @Override
    public String toString()
    {
        return "MLPTr{" + "maxepochs=" + maxepochs + ", trainRatio=" + trainRatio + ", learningMonitor=" + learningMonitor + ", errorCalc=" + errorCalculator + '}';
    }

    public Fac<MLPTraining> getCloningFactory()
    {
        return new CloningFactory();
    }

    void setVerbose(boolean verbose)
    {
        this.verbose = verbose;
    }

    public class CloningFactory implements Fac<MLPTraining>
    {
        public MLPTraining createNew()
        {
            return new MLPTraining(maxepochs, trainRatio, layerTrainerFac, learningMonitor, errorCalculator, monitoringErrorCalculator, batch);
        }
    }
}