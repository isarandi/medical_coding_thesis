/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers.neuralnetwork;

import framework.Fac;

/**
 *
 * @author qr
 */
public interface LearningMonitor {
    boolean shouldContinue();
    void reset();
    void pushTestError(double errorMetric);
    Fac<LearningMonitor> getCloningFactory();
}

class IdleErrorMonitor implements LearningMonitor {

    public boolean shouldContinue() {
        return true;
    }

    public void pushTestError(double errorMetric) {
    }

    public void reset() {
    }

    public Fac<LearningMonitor> getCloningFactory()
    {
        return new Fac<LearningMonitor>() {

            public LearningMonitor createNew()
            {
                return new IdleErrorMonitor();
            }
        };
    }
}



