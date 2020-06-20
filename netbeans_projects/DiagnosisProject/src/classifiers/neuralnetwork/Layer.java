/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers.neuralnetwork;

import framework.Fac;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import parallel.ParallelFor;
import vector.DenseVector;
import vector.Vector;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */

public class Layer implements Serializable {

    Layer prevLayer;
    transient ThreadLocal<Vector> inputVector = new ThreadLocal<Vector>();
    int prevsize;
    
    int size;
    
    protected ActivationFunction activationFunc;
    protected Vector biases;
    protected List<Vector> weightVectors;
   

    public Layer(ActivationFunction activationFunc, int size) {
        this.activationFunc = activationFunc;
        this.size = size;

    }
    
    public Layer(ActivationFunction activationFunc) {
        this(activationFunc, -1);
    }

    Vector getInput() {
        if (prevLayer == null) {
            return inputVector.get();
        } else {
            return prevLayer.getOutput();
        }
    }
    
    public void setInput(Vector input)
    {
        if (inputVector == null)
            inputVector = new ThreadLocal<Vector>();
        this.inputVector.set(input);
    }
    public void setInput(Layer layer)
    {
        this.prevLayer = layer;
    }
    
    public void setPrevSize(int insize) {
        prevsize = insize;
    }

    Vector getOutput()
    {
        final Vector outputs = new DenseVector(size);
        final Vector input = getInput();

        new ParallelFor<Integer>() {

            @Override
            protected void ForBody(Integer i) {

                double dotProd = biases.get(i) + weightVectors.get(i).dotProduct(input);
                double output = activationFunc.getValue(dotProd);

                outputs.set(i, output);

            }
        }.execute(0, size, 0, 500);
        
        return outputs;
    }

    void setSize(int outsize) {
        this.size = outsize;
    }
       
    //random
    private static Random globalRandom = new Random(0);
    private static ThreadLocal<Random> r = new ThreadLocal<Random>()    {

        @Override
        public synchronized Random initialValue()
        {
            return new Random(globalRandom.nextLong());
        }
    };
        
    private double randomWeight()
    {
        return (r.get().nextDouble() - 0.5) * 0.1;
    }
    
    private int prevSize()
    {
        if (prevLayer != null)
            return prevLayer.size;
        else
            return prevsize;
    }

    public void initRandomWeights()
    {
        biases = new DenseVector(size);
        weightVectors = new ArrayList<Vector>(size);

        for (int i = 0; i < size; ++i) {
            weightVectors.add(null);
        }

        new ParallelFor<Integer>()
        {

            @Override
            protected void ForBody(Integer i)
            {

                biases.set(i, randomWeight());

                Vector v = new DenseVector(prevSize());
                for (int ii = 0; ii < prevSize(); ++ii) {
                    v.set(ii, randomWeight());
                }
                weightVectors.set(i, v);

            }
        }.execute(0, size, 1, 0);

    }

    @Override
    public String toString()
    {
        return "Layer{" + "size=" + size + ", actFunc=" + activationFunc + '}';
    }
    
    public Fac<Layer> getCloningFactory() {
        return new Factory();
    }

    class Factory implements Fac<Layer> {

        public Layer createNew() {
            return new Layer(activationFunc, size);
        }
    }
    
    
}
