/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package framework.adaption;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author qr
 */
public class IndependentBatchConverter<From,To> implements BatchConverter<From, To> {

    private Converter<From,To> innerConverter;

    public IndependentBatchConverter(Converter<From, To> innerConverter)
    {
        this.innerConverter = innerConverter;
    }

    public List<To> convert(List<From> inputs)
    {
        List<To> convertedInputs = new ArrayList<To>(inputs.size());
        for (From input: inputs)
        {
            convertedInputs.add(innerConverter.convert(input));
        }
        return convertedInputs;
    }

}
