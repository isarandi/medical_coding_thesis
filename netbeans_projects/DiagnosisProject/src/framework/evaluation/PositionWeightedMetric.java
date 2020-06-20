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
public class PositionWeightedMetric  implements EvaluationMetric {

    double stddev;

    public PositionWeightedMetric(int limit)
    {
        this.stddev = 0.4*limit;
    }
    
    public double phi(double x) {
        x = x - 1;
        return Math.exp(-x*x/(2*stddev*stddev));
    }

    public double weight(int pos)
    {
        if (pos==1)
            return 1.0;

        return phi(pos);
    }
    
    public <Out> double calculate(ResultSet<Out> rs, Out desired)
    {
        int pos = rs.getPosition(desired);
        return weight(pos);
    }
    
    @Override
    public String toString() {
        return "Position weighted (stddev " + Double.toString(stddev) + ")";
    }
    
}