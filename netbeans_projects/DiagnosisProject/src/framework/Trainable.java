/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package framework;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public interface Trainable<In,Out> {

    public void train(SampleSet<In,Out> trainingSet);
}
