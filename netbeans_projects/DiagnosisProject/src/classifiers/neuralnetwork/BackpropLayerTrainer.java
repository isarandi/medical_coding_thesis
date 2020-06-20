/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers.neuralnetwork;

import framework.Fac;
import java.util.List;
import java.util.Random;
import parallel.ParallelFor;
import vector.DenseVector;
import vector.Vector;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class BackpropLayerTrainer extends LayerTrainer<BackpropLayerTrainer>
{

    Vector outputCache;
    Vector derivsCache;
    double learnRate;
    double regulParam;
    int epochnum = 1;
    
    //random
    private static Random globalRandom = new Random(0);
    private static ThreadLocal<Random> r = new ThreadLocal<Random>()
    {

        @Override
        public synchronized Random initialValue()
        {
            return new Random(globalRandom.nextLong());
        }
    };

    public BackpropLayerTrainer(double learnRate, double regulParam)
    {
        this.regulParam = regulParam;
        this.learnRate = learnRate;
    }

    void clearCacheRecursive()
    {
        derivsCache = null;
        outputCache = null;

        if (prevTrainer != null) {
            prevTrainer.clearCacheRecursive();
        }
    }

    void calculateOutputAndDerivative()
    {
        outputCache = new DenseVector(layer.size);
        derivsCache = new DenseVector(layer.size);
        final Vector input = getInput();

        new ParallelFor<Integer>()
        {

            @Override
            protected void ForBody(Integer i)
            {
                double dotProd = layer.biases.get(i) + layer.weightVectors.get(i).dotProduct(input);
                double output = layer.activationFunc.getValue(dotProd);
                double derivative = layer.activationFunc.getDerivative(dotProd);

                derivsCache.set(i, derivative);
                outputCache.set(i, output);

            }
        }.execute(0, layer.size);
    }

    protected void backPropagate(final Vector alpha)
    {
        if (prevTrainer != null) {
            final Vector backAlpha = new DenseVector(layer.prevLayer.size);

            new ParallelFor<Integer>()
            {

                @Override
                protected void ForBody(Integer prev)
                {
                    double err = 0.0;
                    for (int here = 0; here < layer.size; ++here) {
                        err += layer.weightVectors.get(here).get(prev) * alpha.get(here) * derivsCache.get(here);
                    }

                    backAlpha.set(prev, err);
                }
            }.execute(0, layer.prevLayer.size);


            prevTrainer.dealWithAlpha(backAlpha);
        }
    }

    //minus half the partial derivative of the error function w.r.t. the outputs of this layer
    public void dealWithAlpha(final Vector alpha, final boolean wrt_excitation)
    {
        final Vector input = getInput();
        //backing
        backPropagate(alpha);

        //weight adjusting
        modifWeights(alpha, wrt_excitation, layer.weightVectors, layer.biases);
    }

    protected void modifWeights(final Vector alpha, final boolean wrt_excitation, final List<Vector> weights, final Vector biases)
    {
        final Vector input = getInput();
        new ParallelFor<Integer>()
        {

            @Override
            protected void ForBody(Integer i)
            {
                double outputInfluenceOnError = 2 * alpha.get(i); //partial derivative of error (cost) w.r.t output of this neuron
                double factor = -learnRate * outputInfluenceOnError * (wrt_excitation ? 1.0 : derivsCache.get(i));// - r.get().nextGaussian()/Math.sqrt(epochnum);

                biases.set(i, biases.get(i) + factor);

                for (int pos : input.nonZeroPositions()) {
                    weights.get(i).set(pos, weights.get(i).get(pos) + factor * input.get(pos));
                }
            }
        }.execute(0, weights.size());
    }

    public void dealWithAlpha(final Vector alpha)
    {
        dealWithAlpha(alpha, false);
    }

    public Vector getOutput()
    {
        if (outputCache == null) {
            calculateOutputAndDerivative();
        }
        return outputCache;
    }

    protected Vector getInput()
    {
        if (prevTrainer != null) {
            return prevTrainer.getOutput();
        } else {
            return layer.inputVector.get();
        }
    }

    @Override
    public void newSample()
    {
        clearCacheRecursive();
    }

    @Override
    public void newEpoch()
    {
        if (prevTrainer != null) {
            prevTrainer.newEpoch();
        }
        ++epochnum;
    }

    public static class Factory implements Fac<LayerTrainer>
    {

        double learnRate;
        double regulParam;

        public Factory(double learnRate, double regulParam)
        {
            this.learnRate = learnRate;
            this.regulParam = regulParam;
        }

        public BackpropLayerTrainer createNew()
        {
            return new BackpropLayerTrainer(learnRate, regulParam);
        }

        @Override
        public String toString()
        {
            return "BackpropF{" + "learnRate=" + learnRate + '}';
        }
    }

    @Override
    public String toString()
    {
        return "Backprop{" + "learnRate=" + learnRate + '}';
    }
}
