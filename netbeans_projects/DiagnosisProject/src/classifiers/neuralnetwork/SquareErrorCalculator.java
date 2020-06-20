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
public class SquareErrorCalculator implements ErrorCalculator {

    public double calculateError(Vector actual, Vector desired) {
        
        int dim = Math.max(actual.dimensionalityAtLeast(), desired.dimensionalityAtLeast());
                
        double sum = 0.0;
        for (int pos = 0; pos<dim; ++pos)
        {
            double error = desired.get(pos) - actual.get(pos);
            sum+= error*error;
        }
        
        return sum;
    }

    @Override
    public String toString()
    {
        return "SquareErrorCalc";
    }

    public Vector minusHalfderivWRTOutput(Vector actual, Vector desired)
    {
        actual.minus(desired);
        return actual;
    }

}
