/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers.svm;

import framework.Classifier;
import vector.Vector;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public interface SVM extends Classifier<Vector,Integer>
{
    public void setC(double c);
}
