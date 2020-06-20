/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classifiers.neuralnetwork;

import vector.Vector;

/**
 *
 * @author qr
 */
public interface ErrorCalculator {
    public double calculateError(Vector actual, Vector desired);
    public Vector minusHalfderivWRTOutput(Vector actual, Vector desired);
}

