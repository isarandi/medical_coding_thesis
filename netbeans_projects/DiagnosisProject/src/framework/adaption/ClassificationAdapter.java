/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework.adaption;

import framework.ClassificationAbility;
import framework.ResultSet;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author qr
 */
public class ClassificationAdapter<In, ThruIn, ThruOut, Out>
        implements ClassificationAbility<In, Out>, Serializable
{

    private ClassificationAbility<ThruIn, ThruOut> classifier;
    private Converter<In, ThruIn> inputConverter;
    private Converter<ThruOut, Out> outputConverter;

    public ClassificationAdapter(ClassificationAbility<ThruIn, ThruOut> classifier,
                                 Converter<In, ThruIn> inputConverter,
                                 Converter<ThruOut, Out> outputConverter)
    {
        this.classifier = classifier;
        this.inputConverter = inputConverter;
        this.outputConverter = outputConverter;
    }

    public ResultSet<Out> classify(In input, int limit)
    {
        ThruIn convertedInput = (inputConverter != null) ? inputConverter.convert(input) : (ThruIn) input;
        ResultSet<ThruOut> rs = classifier.classify(convertedInput, limit);

        if (outputConverter == null)
        {
            return (ResultSet<Out>) rs;
        } else
        {
            return new ResultSetConverter<ThruOut, Out>(outputConverter, ResultSet.ADD_RESULT_BLENDER).convert(rs);
        }
    }

    public List<ResultSet<Out>> classifyMany(List<In> input, int limit)
    {
        List<ThruIn> convInputs =
                     (inputConverter != null)
                ? new IndependentBatchConverter<In, ThruIn>(inputConverter).convert(input)
                : (List<ThruIn>) input;
        
        List<ResultSet<ThruOut>> rss = classifier.classifyMany(convInputs, limit);
        
        if (outputConverter == null)
        {
            return (List<ResultSet<Out>>)(Object) rss;
        } else
        {
            ResultSetConverter<ThruOut, Out> rsconv = new ResultSetConverter<ThruOut, Out>(outputConverter, ResultSet.ADD_RESULT_BLENDER);
            return new IndependentBatchConverter<ResultSet<ThruOut>, ResultSet<Out>>(rsconv).convert(rss);
        }
    }

    public boolean prefersMany()
    {
        return classifier.prefersMany();
    }
}
