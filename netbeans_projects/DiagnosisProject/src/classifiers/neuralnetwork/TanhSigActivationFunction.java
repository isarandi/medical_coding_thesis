/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classifiers.neuralnetwork;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class TanhSigActivationFunction implements ActivationFunction {

    public double getValue(double x) {
        return Math.tanh(x);
    }

    public double getDerivative(double x) {
        double val = getValue(x);
        return 1 - val*val;
    }

    public double getMaxDerivative() {
        return 0.25;
    }

    @Override
    public String toString()
    {
        return "TanhSig";
    }
    
    
}