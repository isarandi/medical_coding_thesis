/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers.neuralnetwork;

import classifiers.neuralnetwork.ErrorCalculator;
import vector.DenseVector;
import vector.Vector;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class LogLikelihoodErrorCalculator implements ErrorCalculator
{

    public double calculateError(Vector actual, Vector desired)
    {
        int dim = Math.max(actual.dimensionalityAtLeast(), desired.dimensionalityAtLeast());
                
        double sum = 0.0;
        for (int pos = 0; pos<dim; ++pos)
        {
            double des = desired.get(pos);
            double act = actual.get(pos);
            sum+=   des * Math.log(act) +
                (1-des)* Math.log(1-act);

        }
        
        return -sum;
    }

    public Vector minusHalfderivWRTOutput(Vector actual, Vector desired)
    {
        
        int dim = Math.max(actual.dimensionalityAtLeast(), desired.dimensionalityAtLeast());
        Vector error = new DenseVector(dim);
        
        for (int pos = 0; pos<dim; ++pos)
        {
            double errorHere = 0.5*(actual.get(pos)-desired.get(pos));
            
            error.set(pos, errorHere);
        }
        
        return error;
        
//        int dim = Math.max(actual.dimensionalityAtLeast(), desired.dimensionalityAtLeast());
//        Vector error = new FullVector(dim);
//        
//        for (int pos = 0; pos<dim; ++pos)
//        {
//            double errorHere = 0.5*(
//                    desired.get(pos) * 1.0 / actual.get(pos) +
//                    (1-desired.get(pos))* 1.0/(1-actual.get(pos)));
//            if (Double.isInfinite(errorHere))
//                System.out.println("wtf");
//            
//            error.set(pos, errorHere);
//        }
//        
//        return error;
    }

    @Override
    public String toString()
    {
        return "LogErrorCalculator";
    }

    
}
