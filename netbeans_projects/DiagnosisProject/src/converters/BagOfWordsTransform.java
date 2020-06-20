/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package converters;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import vector.Vector;
import vector.IndicatorVector;

/**
 *
 * @author Jeno
 */
public class BagOfWordsTransform extends VectorizerTransform<String,String> {


    protected  Vector vectorize(String sentence, boolean remember)
    {
        Vector vec = new IndicatorVector();

        StringTokenizer tokenizer = new StringTokenizer(sentence.replace(".", ". "), " (),:/[]");
        while (tokenizer.hasMoreTokens())
        {
            String word = tokenizer.nextToken();

            if (!word.toUpperCase().equals(word))
                word = word.toLowerCase();

            if (word.startsWith("-"))
                word = word.substring(1);

            if (word.endsWith("-"))
                word = word.substring(0, word.length() - 1);

            handleToken(word, vec, remember);
        }

        return vec;
    }

    @Override
    public String toString()
    {
        return "BagOfWords";
    }
    
    public String revert(Vector v)
    {
        Map<Integer,String> revMap = new HashMap<Integer, String>();
        for (String word : tokenMap.keySet())
        {
            revMap.put(tokenMap.get(word), word);
        }
        
        StringBuilder sb = new StringBuilder();
        for (int pos: v.nonZeroPositions())
        {
            sb.append(revMap.get(pos)).append(" ");
        }
        return sb.toString();
    }

    
}
