/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package framework.adaption;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author qr
 */
public class ConcatBatchConverter<From,To> implements BatchConverter<From,To> {

    ConcatConverter<List<From>,List<To>> concatConv;

    public ConcatBatchConverter(BatchConverter<?,?>... bconverters)
    {
        concatConv = new ConcatConverter<List<From>, List<To>>(bconverters);
    }

    public List<To> convert(List<From> input)
    {
        return concatConv.convert(input);
    }

}
