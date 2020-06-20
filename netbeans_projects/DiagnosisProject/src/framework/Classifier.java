/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import java.io.Serializable;

/**
 *
 * @author qr
 */
public interface Classifier<In, Out> extends ClassificationAbility<In, Out>, Trainable<In, Out>, Serializable {
}
