/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers.neuralnetwork;

import framework.Fac;
import java.util.ArrayList;
import java.util.List;
import parallel.ParallelFor;
import vector.DenseVector;
import vector.Vector;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class BatchLayerTrainer extends BackpropLayerTrainer
{

    List<Vector> weightModifs;
    Vector biasModifs;

    public BatchLayerTrainer(double learnRate, double regulParam)
    {
        super(learnRate, regulParam);
    }

    @Override
    public void dealWithAlpha(final Vector alpha, final boolean wrt_excitation)
    {
        backPropagate(alpha);
        modifWeights(alpha, wrt_excitation, weightModifs, biasModifs);
    }

    @Override
    public void setLayer(Layer l)
    {
        super.setLayer(l);

        weightModifs = new ArrayList<Vector>(layer.size);
        for (int i = 0; i < layer.size; ++i) {
            weightModifs.add(new DenseVector(layer.prevsize));
        }

        biasModifs = new DenseVector(layer.size);

    }

    @Override
    public void newEpoch()
    {
        super.newEpoch();

        new ParallelFor<Integer>()
        {

            @Override
            protected void ForBody(Integer i)
            {
                layer.weightVectors.get(i).plus(weightModifs.get(i));
                for (int j = 0; j < layer.prevsize; ++j) {
                    weightModifs.get(i).set(j, 0.0);
                }
            }
        }.execute(0, layer.size);

        layer.biases.plus(biasModifs);

        for (int i = 0; i < layer.size; ++i) {
            biasModifs.set(i, 0.0);
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

        public BatchLayerTrainer createNew()
        {
            return new BatchLayerTrainer(learnRate, regulParam);
        }

        @Override
        public String toString()
        {
            return "batchBackpropF{" + "learnRate=" + learnRate + '}';
        }
    }
}
