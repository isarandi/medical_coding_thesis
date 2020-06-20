/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework.evaluation;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Istvan Sarandi
 */
public class AvgAggregator implements EvaluationAggregator<Double>
{

    List<Double> sums=new ArrayList<Double>();
    int n=0;
    public synchronized void push(List<Double> validationvalues)
    {
        
        for (int i=0; i<validationvalues.size(); ++i)
        {
            if (sums.size()<=i)
                sums.add(validationvalues.get(i));
            else
                sums.set(i, sums.get(i)+validationvalues.get(i));
            
        }
        ++n;
    }

    public List<Double> getAggregate()
    {
        List<Double> avg = new ArrayList<Double>();
        for (double sum: sums)
        {
            avg.add(sum/n);
        }
        return avg;
    }

}