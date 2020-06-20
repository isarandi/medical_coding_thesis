/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package framework.adaption;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author qr
 */
public class ConcatConverter<From,To> implements Converter<From,To> {

    List<Converter<?,?>> converters;

    public ConcatConverter(Converter<?, ?>... converters)
    {
        this.converters = Arrays.asList(converters);
    }

    public To convert(From input)
    {
        Object state = input;
        for (Converter c: converters)
        {
            state = c.convert(state);
        }
        return (To) state;
    }

}
