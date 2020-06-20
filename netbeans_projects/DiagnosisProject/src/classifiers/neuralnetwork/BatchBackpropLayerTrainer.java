/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers.neuralnetwork;

import framework.Fac;
import java.util.ArrayList;
import java.util.List;
import vector.DenseVector;
import vector.Vector;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class BatchBackpropLayerTrainer extends BackpropLayerTrainer
{

//    ListThreadAggregation<Vector> weightModifs;
//
//    @Override
//    public void setLayer(Layer l)
//    {
//        super.setLayer(l);
//
//        weightModifs = new ListThreadAggregation<Vector>(layer.size)
//        {
//
//            @Override
//            protected Vector elementInitialValue()
//            {
//                return new FullVector(layer.prevsize);
//            }
//
//            @Override
//            protected void elementAggregate(Vector into, Vector from)
//            {
//                into.plus(from);
//            }
//        };
//
//    }
//    ThreadAggregation<Vector> biasModifs
    final List<InnerTrainer> innerTrainers = new ArrayList<InnerTrainer>();
    ThreadLocal<InnerTrainer> localTrainer = new ThreadLocal<InnerTrainer>()
    {

        @Override
        protected InnerTrainer initialValue()
        {
            InnerTrainer it = new InnerTrainer();
            synchronized (innerTrainers) {
                innerTrainers.add(it);
            }
            return it;
        }
    };

    @Override
    public void newEpoch()
    {
        super.newEpoch();

        for (int i = 0; i < layer.size; ++i) {
            for (InnerTrainer it : innerTrainers) {
                layer.weightVectors.get(i).plus(it.weightModifs.get(i));
            }
        }

        for (InnerTrainer it : innerTrainers) {
            layer.biases.plus(it.biasModifs);
            it.clearModifs();
        }
        
        
        
    }

    @Override
    void calculateOutputAndDerivative()
    {
        localTrainer.get().calculateOutputAndDerivative();
    }

    @Override
    void clearCacheRecursive()
    {
        localTrainer.get().clearCacheRecursive();
    }

    @Override
    public Vector getOutput()
    {
        return localTrainer.get().getOutput();
    }

    public BatchBackpropLayerTrainer(double learnRate, double regulParam)
    {
        super(learnRate, regulParam);
    }

    @Override
    public void dealWithAlpha(final Vector alpha, final boolean wrt_excitation)
    {
        localTrainer.get().backPropagate(alpha);
        localTrainer.get().modifWeights(alpha, wrt_excitation);
    }

    class InnerTrainer extends BackpropLayerTrainer
    {

        List<Vector> weightModifs;
        Vector biasModifs;

        public InnerTrainer()
        {
            super(BatchBackpropLayerTrainer.this.learnRate, BatchBackpropLayerTrainer.this.regulParam);
            setLayer(BatchBackpropLayerTrainer.this.layer);
            setPreviousTrainer(BatchBackpropLayerTrainer.this.prevTrainer);
            
            weightModifs = new ArrayList<Vector>(layer.size);
            for (int i=0; i<layer.size;++i)
            {
                weightModifs.add(new DenseVector(layer.prevsize));
            }
            
            biasModifs = new DenseVector(layer.size);
        }

        private void modifWeights(Vector alpha, boolean wrt_excitation)
        {
            super.modifWeights(alpha, wrt_excitation, weightModifs, biasModifs);
        }

        private void clearModifs()
        {
            for (int i=0; i<layer.size;++i)
            {
                weightModifs.set(i,new DenseVector(layer.prevsize));
            }
            biasModifs = new DenseVector(layer.size);
        }
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

        public BatchBackpropLayerTrainer createNew()
        {
            return new BatchBackpropLayerTrainer(learnRate, regulParam);
        }

        @Override
        public String toString()
        {
            return "batchBackpropF{" + "learnRate=" + learnRate + '}';
        }
    }
}
