/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework.evaluation;

import framework.ResultSet;

/**
 *
 * @author qr
 */
public class PositionAtMostMetric implements EvaluationMetric  {

    int limit;

    public PositionAtMostMetric(int limit)
    {
        this.limit = limit;
    }
    
    
    public <Out> double calculate(ResultSet<Out> rs, Out desired)
    {
        return (rs.getPosition(desired) <= limit) ? 1.0 : 0.0;
    }
    
    @Override
    public String toString() {
        return "Position at most " + Integer.toString(limit);
    }
    
}
