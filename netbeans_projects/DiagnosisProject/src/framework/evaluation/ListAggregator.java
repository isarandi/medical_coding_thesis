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
public class ListAggregator implements EvaluationAggregator<List<Double>>
{

    List<List<Double>> list=new ArrayList<List<Double>>();
    public synchronized void push(List<Double> validationvalues)
    {
        list.add(validationvalues);
    }

    public List<List<Double>> getAggregate()
    {
        return list;
    }

}