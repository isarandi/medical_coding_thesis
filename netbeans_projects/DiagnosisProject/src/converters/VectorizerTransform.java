/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package converters;

import framework.adaption.BatchConverter;
import framework.adaption.Converter;
import framework.adaption.InputTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import vector.Vector;

/**
 *
 * @author qr
 */
abstract class VectorizerTransform<In, Token> implements InputTransform<In, Vector>
{

    Map<Token, Integer> tokenMap = new TreeMap<Token, Integer>();

    protected abstract Vector vectorize(In tokens, boolean remember);

    protected void handleToken(Token t, Vector vec, boolean remember)
    {
        Integer num = tokenMap.get(t);

        if (num == null) {
            if (!remember) {
                return;
            }

            num = tokenMap.size();
            tokenMap.put(t, num);
        }

        vec.set(num);
    }
    BatchConverter<In, Vector> trainingConverter = new BatchConverter<In, Vector>()
    {
        public List<Vector> convert(List<In> inputs)
        {
            tokenMap.clear();

            List<Vector> vecs = new ArrayList<Vector>(inputs.size());
            for (In input : inputs) {
                vecs.add(vectorize(input, true));
            }

            for (Vector vec : vecs) {
                vec.setDimensionality(tokenMap.size());
            }
            return vecs;
        }
    };
    
    private Converter<In, Vector> classifyConverter = new Converter<In, Vector>()
    {
        public Vector convert(In input)
        {
            Vector v = vectorize(input, false);
            v.setDimensionality(tokenMap.size());
            return v;
        }
    };

    
    
    public Converter<In, Vector> getClassifyConverter()
    {
        return classifyConverter;
    }

    public BatchConverter<In, Vector> getTrainingConverter()
    {
        return trainingConverter;
    }

}