/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers.neuralnetwork;

import vector.Vector;

public abstract class LayerTrainer<LT extends LayerTrainer<LT>>
{

    protected Layer layer;
    protected LT prevTrainer;

    public void setLayer(Layer l)
    {
        this.layer = l;
    }

    public void setPreviousTrainer(LT lt)
    {
        prevTrainer = lt;
    }

    public abstract void dealWithAlpha(Vector errors);

    public abstract void dealWithAlpha(final Vector alpha, final boolean wrt_excitation);

    public abstract Vector getOutput();

    public abstract void newSample();
    public abstract void newEpoch();
}
