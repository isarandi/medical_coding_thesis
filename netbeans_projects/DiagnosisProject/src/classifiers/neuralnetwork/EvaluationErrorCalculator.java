/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers.neuralnetwork;

import framework.ResultSet;
import java.util.Iterator;
import framework.evaluation.EvaluationMetric;
import vector.Vector;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class EvaluationErrorCalculator implements ErrorCalculator {

    private EvaluationMetric metric;

    public EvaluationErrorCalculator(EvaluationMetric metric) {
        this.metric = metric;
    }

    public double calculateError(Vector actual, Vector desired) throws RuntimeException {
        ResultSet<Integer> rs = MLPClassifier.outputToResultSet(actual, Integer.MAX_VALUE);

        Iterator<Integer> iter = desired.nonZeroPositions().iterator();
        if (!iter.hasNext())
        {
            throw new RuntimeException("ValidationErrorCalculator expects the desired vector to be an 1 of N indicator vector.");
        }



        return 1.0 - metric.calculate(rs, iter.next());

        
    }

    public Vector minusHalfderivWRTOutput(Vector actual, Vector desired)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
