/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package converters;

import framework.adaption.BatchConverter;
import framework.adaption.Converter;
import framework.adaption.InputTransform;
import vector.Vector;
import vector.SparseVector;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class VectorCompressor extends VectorizerTransform<Vector,Integer> {

    @Override
    protected Vector vectorize(Vector tokens, boolean remember) {
        
        Vector vec = new SparseVector();
        for (int pos: tokens.nonZeroPositions())
        {
            handleToken(pos, vec, remember);
        }
        return vec;
    }
    
}
