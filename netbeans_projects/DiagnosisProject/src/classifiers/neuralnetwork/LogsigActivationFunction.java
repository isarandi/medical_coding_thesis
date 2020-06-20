/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers.neuralnetwork;

/**
 *
 * @author qr
 */
public class LogsigActivationFunction implements ActivationFunction {

    public double getValue(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    public double getDerivative(double x) {
        double value = getValue(x);
        return (1 - value) * value;
    }

    public double getMaxDerivative() {
        return 0.25;
    }

    @Override
    public String toString()
    {
        return "Logsig";
    }
}
