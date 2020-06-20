/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework.adaption;

import framework.Classifier;
import framework.SampleSet;
import framework.Trainable;
import java.util.List;

/**
 *
 * @author qr
 */

public class TrainingAdapter<In,ThruIn,ThruOut,Out> implements Trainable<In, Out>{

    private Trainable<ThruIn,ThruOut> trainable;
    private BatchConverter<In,ThruIn> inputsConverter;
    private BatchConverter<Out,ThruOut> outputsToInnerConverter;

    public TrainingAdapter(Classifier<ThruIn, ThruOut> trainable,
            BatchConverter<In, ThruIn> inputsConverter,
            BatchConverter<Out, ThruOut> outputsToInnerConverter) {
        this.trainable = trainable;
        this.inputsConverter = inputsConverter;
        this.outputsToInnerConverter = outputsToInnerConverter;
    }
    
    public TrainingAdapter(Classifier<ThruIn, ThruOut> trainable,
            Converter<In, ThruIn> inputsConverter,
            Converter<Out, ThruOut> outputsToInnerConverter) {
        this.trainable = trainable;
        this.inputsConverter =
                (inputsConverter == null) ? null :
                new IndependentBatchConverter<In, ThruIn>(inputsConverter);
        
        this.outputsToInnerConverter =
                (outputsToInnerConverter==null)? null :
                new IndependentBatchConverter<Out, ThruOut>(outputsToInnerConverter);
    }
    
    public void train(SampleSet<In, Out> trainingSet) {
        List<ThruIn> convertedInputs = (inputsConverter == null) ?
            (List<ThruIn>) trainingSet.inList() : inputsConverter.convert(trainingSet.inList());

        List<ThruOut> convertedOutputs = (outputsToInnerConverter == null) ?
            (List<ThruOut>) trainingSet.outList() : outputsToInnerConverter.convert(trainingSet.outList());

        SampleSet<ThruIn,ThruOut> convertedSampleSet = new SampleSet<ThruIn, ThruOut>(convertedInputs, convertedOutputs);
        
        trainable.train(convertedSampleSet);
    }
    
}
