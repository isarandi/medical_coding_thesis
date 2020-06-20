/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework.evaluation;

import framework.ResultSet;

public interface EvaluationMetric  {
    <Out> double calculate(ResultSet<Out> rs, Out desired);
    //void push(ResultSet<Out> rs, Out desired);
    
}

