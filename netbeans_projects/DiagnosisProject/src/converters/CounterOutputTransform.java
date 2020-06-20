/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package converters;

import framework.adaption.BatchConverter;
import framework.adaption.Converter;
import framework.adaption.OutputTransform;
import java.util.*;
import framework.Fac;

/**
 *
 * @author qr
 */
public class CounterOutputTransform<Out> implements OutputTransform<Integer, Out>
{

    private List<Out> outs = new ArrayList<Out>();
    Converter<Integer, Out> toOuterConverter = new Converter<Integer, Out>()
    {

        public Out convert(Integer input)
        {
            return outs.get(input);
        }
    };
    BatchConverter<Out, Integer> toInnerConverter = new BatchConverter<Out, Integer>()
    {

        public List<Integer> convert(List<Out> outers)
        {
            outs.clear();
            outs.addAll(new HashSet<Out>(outers));

            List<Integer> inners = new ArrayList<Integer>(outers.size());
            for (Out outer : outers) {
                inners.add(outs.indexOf(outer));
            }
            return inners;
        }
    };

    public BatchConverter<Out, Integer> getToInnerConverter()
    {
        return toInnerConverter;
    }

    public Converter<Integer, Out> getToOuterConverter()
    {
        return toOuterConverter;
    }

    @Override
    public String toString()
    {
        return "Counter";
    }
    
    
}
