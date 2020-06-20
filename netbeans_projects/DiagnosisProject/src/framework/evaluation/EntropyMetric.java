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
public class EntropyMetric implements EvaluationMetric
{
    private int limit;

    public EntropyMetric(int limit)
    {
        this.limit = limit;
    }
       

    public <Out> double calculate(ResultSet<Out> rs, Out desired)
    {
        ResultSet<Out> normResultSet = rs.tops(limit);
        normResultSet.normalize();
        
        double h = 0;
        for (Result<Out> r : normResultSet)
        {
            double conf = r.getConfidence();
            h+=conf*Math.log(conf);
            
        }
        return -h;
    }
}
