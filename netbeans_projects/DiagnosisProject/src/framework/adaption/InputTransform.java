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
public interface InputTransform<From,To> extends Serializable{
    BatchConverter<From,To> getTrainingConverter();
    Converter<From,To> getClassifyConverter();
}
