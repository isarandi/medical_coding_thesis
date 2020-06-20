/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers.team;

import framework.AbstractSingleClassifier;
import framework.Classifier;
import framework.ResultSet;
import framework.SampleSet;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import vector.Vector;

/**
 *
 * @author Istvan Sarandi
 */
public class RandomTeam<In,Out> extends AbstractSingleClassifier<In, Out>
{
    
    List<Classifier<In, Out>> members;
    Random r = new Random(0);

    public RandomTeam(Classifier<In, Out>... members)
    {
        this.members = Arrays.asList(members);
    }

    @Override
    public ResultSet<Out> classify(In input, int limit)
    {
        ResultSet<Out> endResults = new ResultSet<Out>(limit);

        for (int i = 0; i < members.size(); ++i) {
            ResultSet<Out> memberResults = members.get(i).classify(input, limit*2);
            memberResults.normalize();
            endResults.blend(memberResults, r.nextDouble(), ResultSet.ADD_RESULT_BLENDER); //expertiseGuesses.get(i)
        }
        
        return endResults;
    }

    @Override
    public void train(SampleSet<In, Out> trainingSet)
    {
        for (Classifier<In,Out> member: members)
        {
            member.train(trainingSet);
        }
    }
    
}
