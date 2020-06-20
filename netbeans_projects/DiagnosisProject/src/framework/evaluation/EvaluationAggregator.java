/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework.evaluation;

import java.util.List;




public interface EvaluationAggregator<Aggr>
{
    void push(List<Double> validationvalues);
    List<Aggr> getAggregate();
}
