/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classifiers.neuralnetwork;

import java.io.Serializable;

/**
 *
 * @author qr
 */
public interface ActivationFunction extends Serializable {

    double getValue(double x);
    double getDerivative(double x);
    double getMaxDerivative();
}
