/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework.evaluation;

import framework.ResultSet;

/**
 *
 * @author Istvan Sarandi
 */
public class NegativePositionMetric implements EvaluationMetric
{

    public <Out> double calculate(ResultSet<Out> rs, Out desired)
    {
        return -rs.getPosition(desired);
    }
    
}
