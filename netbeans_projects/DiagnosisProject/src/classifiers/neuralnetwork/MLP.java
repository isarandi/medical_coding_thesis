/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers.neuralnetwork;


import framework.Fac;
import framework.SampleSet;
import framework.TrainableConverter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import vector.Vector;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class MLP implements Serializable, TrainableConverter<Vector, Vector> {

    public static final ActivationFunction LOGSIG = new LogsigActivationFunction();
    public static final ActivationFunction TANHSIG = new TanhSigActivationFunction();
    public static final ActivationFunction PURELIN = new LinearActivationFunction();
    
    protected List<Layer> layers;
    protected transient MLPTraining trainer;


    public MLP(MLPTraining trainer, Layer... layers)
    {
        this.layers = Arrays.asList(layers);
        for (int i=1; i<this.layers.size(); ++i)
        {
            this.layers.get(i).setInput(this.layers.get(i-1));
        }
        this.trainer = trainer;
    }


    public Vector getOutput(Vector v)
    {
        inputLayer().setInput(v);
        return outputLayer().getOutput();
    }
    
    public Vector getLayerState(Vector v, int layerNum)
    {
        inputLayer().setInput(v);
        return layers.get(layerNum).getOutput();

    }
    
    Layer inputLayer()
    {
        return layers.get(0);
    }
    Layer outputLayer()
    {
        return layers.get(layers.size()-1);
    }

    private void initialize(SampleSet<Vector, Vector> trainingSet)
    {
        int outsize = Vector.largestSize(trainingSet.outList());
        int insize = Vector.largestSize(trainingSet.inList());
        
        //System.out.println(String.format("insize %d\noutsize %d", insize, outsize));

        layers.get(0).setPrevSize(insize);
        
        for (Layer layer : layers.subList(0, layers.size()-1))
        {
            layer.initRandomWeights();
        }

        Layer outputLayer = layers.get(layers.size()-1);
        outputLayer.setSize(outsize);
        outputLayer.initRandomWeights();

    }
    
    public void train(SampleSet<Vector, Vector> trainingSet)
    {
        initialize(trainingSet);
        
        trainer.train(this, trainingSet);
    }
    
    public void setTrainer(MLPTraining trainer) {
        this.trainer = trainer;
    }
    
    public Vector convert(Vector input) {
        return getOutput(input);
    }
    
    public Fac<MLP> getCloningFactory()
    {
        return new CloningFactory();
    }

    @Override
    public String toString()
    {
        StringBuilder layerinfo = new StringBuilder("layers:");
        for (Layer l: layers)
            layerinfo.append(" "+l.toString());
        
        return "MLP{" + "trainer=" + trainer + ", "+ layerinfo.toString()+'}';
    }
    
    public void setVerbose(boolean verbose)
    {
        this.trainer.setVerbose(verbose);
    }

      
    class CloningFactory implements Fac<MLP>
    {
        public MLP createNew() {
            Layer[] newLayers = new Layer[layers.size()];
            for (int i=0; i<layers.size(); ++i)
            {
                newLayers[i] = layers.get(i).getCloningFactory().createNew();
            }
            return new MLP(trainer.getCloningFactory().createNew(), newLayers);
        }

    }
}
