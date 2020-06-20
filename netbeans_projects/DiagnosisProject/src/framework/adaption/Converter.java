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
public interface Converter<From,To> extends Serializable{
    To convert (From input);

}
