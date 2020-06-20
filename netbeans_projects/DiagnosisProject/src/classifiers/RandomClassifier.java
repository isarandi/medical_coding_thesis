/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers;

import framework.AbstractSingleClassifier;
import framework.ResultSet;
import framework.SampleSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class RandomClassifier<In, Out> extends AbstractSingleClassifier<In, Out>
{

    List<Out> outputs;
    Random r = new Random(Calendar.getInstance().getTimeInMillis());

    public ResultSet<Out> classify(In input, int limit)
    {
        ResultSet<Out> rs = new ResultSet<Out>(limit);
        for (int i=0; i<limit; ++i)
            rs.push(outputs.get(r.nextInt(outputs.size())), 1.0/limit);
        return rs;
    }

    public void train(SampleSet<In, Out> trainingSet)
    {
        outputs = new ArrayList<Out>(trainingSet.outList());

    }
}
