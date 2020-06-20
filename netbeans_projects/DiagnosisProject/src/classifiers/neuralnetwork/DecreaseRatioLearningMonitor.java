/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers.neuralnetwork;

import java.util.LinkedList;
import java.util.Queue;
import framework.Fac;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class DecreaseRatioLearningMonitor implements LearningMonitor
{

    Queue<Boolean> decreaseQueue;
    double lastError;
    double nowError;
    private int windowSize;
    private double ratio;

    public boolean shouldContinue()
    {
        int decreaseNum = 0;
        if (nowError < 1e-5) {
            return false;
        }

        for (boolean b : decreaseQueue) {
            if (b) {
                ++decreaseNum;
            }
        }
        return (decreaseNum > ratio * windowSize);
    }

    public DecreaseRatioLearningMonitor(int windowSize, double ratio)
    {
        this.windowSize = windowSize;
        this.ratio = ratio;
        reset();

    }

    public void pushTestError(double errorMetric)
    {
        lastError = nowError;
        nowError = errorMetric;

        decreaseQueue.add(lastError > nowError);
        decreaseQueue.remove();
    }

    public final void reset()
    {
        decreaseQueue = new LinkedList<Boolean>();
        for (int i = 0; i < windowSize; ++i) {
            decreaseQueue.add(true);
        }

        lastError = Double.POSITIVE_INFINITY;
        nowError = Double.POSITIVE_INFINITY;
    }

    public Fac<LearningMonitor> getCloningFactory()
    {
        return new CloningFactory();
    }

    public class CloningFactory implements Fac<LearningMonitor>
    {

        public LearningMonitor createNew()
        {
            return new DecreaseRatioLearningMonitor(windowSize, ratio);
        }

        @Override
        public String toString()
        {
            return "DecreaseRatioLMF{" + "windowsize=" + windowSize + ", ratio=" + ratio + '}';
        }
    }

    @Override
    public String toString()
    {
        return "DecreaseRatioLM{" + "window=" + windowSize + ", ratio=" + ratio + '}';
    }
}