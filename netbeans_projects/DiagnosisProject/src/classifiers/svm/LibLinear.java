/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers.svm;

import classifiers.svm.liblinear.FeatureNode;
import classifiers.svm.liblinear.Linear;
import classifiers.svm.liblinear.Model;
import classifiers.svm.liblinear.Parameter;
import classifiers.svm.liblinear.Problem;
import classifiers.svm.liblinear.SolverType;
import framework.AbstractSingleClassifier;
import framework.Classifier;
import framework.Fac;
import framework.ResultSet;
import framework.SampleSet;
import vector.Vector;

import framework.Sample;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class LibLinear extends AbstractSingleClassifier<Vector,Integer> implements SVM
{

    int biasindex;
    double bias;
    double c=100;
    Model model;

    public ResultSet<Integer> classify(Vector input, int limit)
    {
        double[] probest = new double[model.getNrClass()];
        Linear.predictProbability(model, convertVector(input), probest);
        int[] labels = model.getLabels();

        ResultSet<Integer> rs = new ResultSet<Integer>(limit);


        for (int i = 0; i < probest.length; ++i) {
            rs.push(labels[i], probest[i]);
        }

        return rs;
    }

    public LibLinear(double c)
    {
        this.c = c;
    }

    public LibLinear()
    {
    }

    public void train(SampleSet<Vector, Integer> trainingSet)
    {
        Parameter param = new Parameter(SolverType.L2R_LR_DUAL, c, Double.POSITIVE_INFINITY);

        bias = 1;

        SolverType st = param.getSolverType();

        if (st == SolverType.L2R_LR
                || st == SolverType.L2R_L2LOSS_SVC
                || st == SolverType.L1R_L2LOSS_SVC
                || st == SolverType.L1R_LR) {
            param.setEps(0.01);
        } else if (st == SolverType.L2R_L2LOSS_SVC_DUAL
                || st == SolverType.L2R_L1LOSS_SVC_DUAL
                || st == SolverType.MCSVM_CS
                || st == SolverType.L2R_LR_DUAL) {
            param.setEps(0.1);
        }

        int largestsize = Vector.largestSize(trainingSet.inList());
        biasindex = largestsize;

        Problem prob = new Problem();
        prob.bias = bias;
        prob.l = trainingSet.size();
        prob.n = largestsize + ((bias >= 0) ? 1 : 0);
        prob.y = new int[prob.l];
        prob.x = new FeatureNode[prob.l][];

        int j = 0;
        for (Sample<Vector, Integer> s : trainingSet) {
            prob.x[j] = convertVector(s.getInput());
            prob.y[j] = s.getOutput();
            ++j;
        }

        Linear.disableDebugOutput();
        model = Linear.train(prob, param);


    }

    private FeatureNode[] convertVector(Vector vector)
    {
        int m = vector.nonZeroCount();
        FeatureNode[] x;
        if (bias >= 0) {
            x = new FeatureNode[m + 1];
            x[m] = new FeatureNode(biasindex + 1, bias);
        } else {
            x = new FeatureNode[m];
        }

        int j = 0;
        for (int pos : vector.nonZeroPositions()) {
            x[j] = new FeatureNode(pos + 1, vector.get(pos));
            ++j;
        }

        return x;
    }

    public void setC(double c)
    {
        this.c = c;
    }

    @Override
    public String toString()
    {
        return "LibLinear{c=" + c + '}';
    }

    public static class Factory implements Fac<Classifier<Vector, Integer>>
    {

        double c;

        public Factory(double c)
        {
            this.c = c;
        }

        public Classifier<Vector, Integer> createNew()
        {
            LibLinear svm = new LibLinear();
            svm.setC(c);
            return svm;
        }
    }
}
