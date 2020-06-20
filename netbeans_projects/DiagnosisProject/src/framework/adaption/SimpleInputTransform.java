/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework.adaption;

/**
 *
 * @author qr
 */
public class SimpleInputTransform<From,To> implements InputTransform<From, To> {
    BatchConverter<From, To> trainingConverter;
    Converter<From, To> classifyConverter;

    public SimpleInputTransform(BatchConverter<From, To> trainingConverter, Converter<From, To> classifyConverter)
    {
        this.trainingConverter = trainingConverter;
        this.classifyConverter = classifyConverter;
    }

    public SimpleInputTransform(Converter<From,To> converter)
    {
        this(new IndependentBatchConverter<From, To>(converter), converter);
    }
    
    public BatchConverter<From, To> getTrainingConverter()
    {
        return trainingConverter;
    }

    public Converter<From, To> getClassifyConverter()
    {
        return classifyConverter;
    }

    @Override
    public String toString()
    {
        return "SimpleInputTransform{" + "clconv=" + classifyConverter + '}';
    }



    
    
}
