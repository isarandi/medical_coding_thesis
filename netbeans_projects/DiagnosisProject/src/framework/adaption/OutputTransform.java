/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework.adaption;

import java.io.Serializable;

/**
 *
 * @author qr
 */

public interface OutputTransform<Inner, Outer> extends Serializable{

    Converter<Inner, Outer> getToOuterConverter();

    BatchConverter<Outer, Inner> getToInnerConverter();
}
