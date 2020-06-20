/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package framework.evaluation;

import framework.ResultSet;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class NormalizedConfidenceMetric implements EvaluationMetric {
    private int limit;

    public NormalizedConfidenceMetric(int limit) {
        this.limit = limit;
    }

    public <Out> double calculate(ResultSet<Out> rs, Out desired) {
        ResultSet<Out> firstPart = rs.tops(limit);
        firstPart.normalize();
        return firstPart.getConfidence(desired);
    }

    public String getName() {
        return "Normalized Confidence";
    }

}
