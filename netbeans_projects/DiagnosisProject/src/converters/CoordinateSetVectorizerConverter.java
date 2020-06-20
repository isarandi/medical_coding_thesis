/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package converters;

import framework.adaption.Converter;
import vector.SparseVector;
import vector.Vector;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class CoordinateSetVectorizerConverter implements Converter<Integer, Vector> {

    public Vector convert(Integer input) {
        Vector v = new SparseVector();
        v.set(input);
        return v;
    }
}
