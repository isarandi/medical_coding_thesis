/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classifiers.neuralnetwork;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class LinearActivationFunction implements ActivationFunction {

    public double getValue(double x) {
        return x;
    }

    public double getDerivative(double x) {
        return 1;
    }

    public double getMaxDerivative() {
        return 1;
    }

    @Override
    public String toString()
    {
        return "Linear";
    }

}
