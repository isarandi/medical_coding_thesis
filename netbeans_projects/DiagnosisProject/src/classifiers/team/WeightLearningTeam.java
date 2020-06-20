/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers.team;

import classifiers.neuralnetwork.MLP;
import framework.AbstractSingleClassifier;
import framework.Classifier;
import framework.ResultSet;
import framework.Sample;
import framework.SampleSet;
import vector.DenseVector;
import vector.Vector;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class WeightLearningTeam<Out> extends AbstractSingleClassifier<Vector, Out>
{
    private MLP leaderMLP;
    private Classifier<Vector, Out> c1;
    private Classifier<Vector, Out> c2;
    private double trainRatio;

    public ResultSet<Out> classify(Vector input, int limit)
    {
        ResultSet<Out> c1r = c1.classify(input, limit * 10);
        ResultSet<Out> c2r = c2.classify(input, limit * 10);

        c1r.normalize();
        c2r.normalize();

        double w = leaderMLP.getOutput(input).get(0);

        ResultSet<Out> mixedResults = new ResultSet<Out>();
        mixedResults.blend(c1r, w, ResultSet.ADD_RESULT_BLENDER);
        mixedResults.blend(c2r, 1 - w, ResultSet.ADD_RESULT_BLENDER);

        mixedResults.setLimit(limit);

        return mixedResults;
    }

    public void train(SampleSet<Vector, Out> trainingSet)
    {
        SampleSet<Vector, Out> memberTrainingSet = trainingSet.lowerPart(trainRatio);
        SampleSet<Vector, Out> memberTestSet = trainingSet.higherPart(trainRatio);

        c1.train(memberTrainingSet);
        c2.train(memberTrainingSet);

        SampleSet<Vector, Vector> leaderSet = new SampleSet<Vector, Vector>();

        for (Sample<Vector, Out> sample : memberTestSet)
        {
            ResultSet<Out> c1r = c1.classify(sample.getInput(), 15);
            ResultSet<Out> c2r = c2.classify(sample.getInput(), 15);

            c1r.normalize();
            c2r.normalize();

            double bestw = 0.0;
            double bestconfidence = 0.0;

            for (double w = 0.0; w < 1.0; w += 1e-2)
            {
                ResultSet<Out> mixedResults = new ResultSet<Out>();
                mixedResults.blend(c1r, w, ResultSet.ADD_RESULT_BLENDER);
                mixedResults.blend(c2r, 1 - w, ResultSet.ADD_RESULT_BLENDER);
                mixedResults.normalize();

                double nowconfidence = mixedResults.getConfidence(sample.getOutput());
                if (nowconfidence > bestconfidence)
                {
                    bestconfidence = nowconfidence;
                    bestw = w;
                }


            }

            double[] arr =
            {
                bestw
            };
            Vector v = new DenseVector(arr);
            leaderSet.add(sample.getInput(), v);
        }

        leaderMLP.train(leaderSet);

        c1.train(trainingSet);
        c2.train(trainingSet);


    }

    public WeightLearningTeam(MLP leaderMLP, Classifier<Vector, Out> c1, Classifier<Vector, Out> c2, double trainRatio)
    {
        this.c1 = c1;
        this.c2 = c2;
        this.trainRatio = trainRatio;
        this.leaderMLP = leaderMLP;
        leaderMLP.setVerbose(true);
    }
}
