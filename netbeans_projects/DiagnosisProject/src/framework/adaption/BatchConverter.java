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
public interface BatchConverter<From,To> extends Converter<List<From>, List<To>>, Serializable {

}
