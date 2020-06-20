/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package framework;

import framework.adaption.Converter;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public interface TrainableConverter<In,Out> extends Trainable<In,Out>, Converter<In,Out> {

}
