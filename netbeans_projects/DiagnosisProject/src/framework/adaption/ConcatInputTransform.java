/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework.adaption;

import framework.Fac;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author qr
 */
public class ConcatInputTransform<From, To> implements InputTransform<From, To> {

    List<InputTransform<?, ?>> transforms;
    BatchConverter<From, To> trainingC;
    Converter<From, To> classifyC;

    public ConcatInputTransform(InputTransform<?, ?>... transformarray)
    {
        BatchConverter<?, ?>[] trainingCs = new BatchConverter<?, ?>[transformarray.length];
        Converter<?, ?>[] classifyCs = new Converter<?, ?>[transformarray.length];
        for (int i = 0; i < transformarray.length; ++i)
        {
            InputTransform<?, ?> transform = transformarray[i];
            trainingCs[i] = transform.getTrainingConverter();
            classifyCs[i] = transform.getClassifyConverter();
        }

        trainingC = new ConcatBatchConverter<From, To>(trainingCs);
        classifyC = new ConcatConverter<From, To>(classifyCs);
        
        transforms = Arrays.asList(transformarray);
    }

    public BatchConverter<From, To> getTrainingConverter()
    {
        return trainingC;
    }

    public Converter<From, To> getClassifyConverter()
    {
        return classifyC;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("ConcatInTr{");
        int i=0;
        for (InputTransform<?,?> inputTransform : transforms)
        {
            sb.append(inputTransform.toString());
            if (i< transforms.size()-1)
                sb.append(", ");
            i++;
        }
        sb.append("}");
        return sb.toString();
    }
       

    public static class Factory<From, To> implements Fac<InputTransform<From, To>> {

        Fac<InputTransform>[] factories;
        
        public Factory(Fac<InputTransform>... transformarray)
        {
            factories = transformarray;
        }

        public InputTransform<From, To> createNew()
        {
            InputTransform<?,?>[] transforms = new InputTransform<?, ?>[factories.length];
            for (int i=0; i<factories.length; ++i)
            {
                transforms[i]=factories[i].createNew();
            }
            return new ConcatInputTransform<From, To>(transforms);
        }
    }
}
