/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework.evaluation;

import framework.Result;
import framework.ResultSet;

/**
 *
 * @author Istvan Sarandi
 */
public class CertaintyMetric implements EvaluationMetric
{

    public <Out> double calculate(ResultSet<Out> rs, Out desired)
    {
        int i = 0;
        double[] confs = new double[2];
        for (Result<Out> r : rs)
        {
            confs[i] = r.getConfidence();
            ++i;
            if (!(i < 2))
            {
                break;
            }
        }
        if (confs[0]==0)
            return 0;
        
        return (confs[0] - confs[1]) / confs[0];
    }
}
