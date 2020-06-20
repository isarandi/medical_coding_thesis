/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classifiers.team;

import framework.AbstractSingleClassifier;
import framework.Classifier;
import framework.Fac;
import framework.ResultSet;
import framework.SampleSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class IdealTeam<In,Out> extends AbstractSingleClassifier<In,Out> {

    private  SampleSet<In,Out> cheatSet;
    private Classifier<In, Out> c1;
    private Classifier<In, Out> c2;
    private final double trainRatio;

    
    public void setCheats(SampleSet<In,Out> cheatSet)
    {
        this.cheatSet = cheatSet;
    }

    public ResultSet<Out> classify(In input, int limit) {
        Out solution = cheatSet.getCorrectOutput(input);

        ResultSet<Out> c1r = c1.classify(input, limit);
        ResultSet<Out> c2r = c2.classify(input, limit);

        c1r.normalize();
        c2r.normalize();
        
        int pos1 = c1r.getPosition(solution);
        int pos2 = c2r.getPosition(solution);

        ResultSet<Out> bestMix = new ResultSet<Out>(limit);
        int bestpos = Integer.MAX_VALUE;

        for (double w=0.0; w<1.0; w+=1e-2)
        {
            ResultSet<Out> mixedResults = new ResultSet<Out>(limit);
            mixedResults.blend(c1r, w, ResultSet.ADD_RESULT_BLENDER);
            mixedResults.blend(c2r, 1-w, ResultSet.ADD_RESULT_BLENDER);

            int nowpos = mixedResults.getPosition(solution);
            if (nowpos < bestpos)
            {
                bestpos = nowpos;
                bestMix = mixedResults;
                if (bestpos == 1)
                    break;
            }
            

        }
        
        if (Math.min(pos1, pos2) > bestpos)
            System.out.println(String.format("%d; %d; %d", pos1, pos2, bestpos));

        return bestMix;
    }

    public void train(SampleSet<In, Out> trainingSet) {
        final SampleSet<In,Out> memberTrainingSet = trainingSet.lowerPart(trainRatio);
        c1.train(memberTrainingSet);
        c2.train(memberTrainingSet);
  
    }

    public IdealTeam(Classifier<In, Out> c1, Classifier<In, Out> c2, double trainRatio) {
        this.c1 = c1;
        this.c2 = c2;
        this.trainRatio = trainRatio;
    }

    public static class Factory<In,Out> implements Fac<Classifier<In,Out>>
    {
        private Fac<Classifier<In, Out>> cf1;
        private Fac<Classifier<In, Out>> cf2;
        private final double trainRatio;

        public Factory(Fac<Classifier<In, Out>> cf1, Fac<Classifier<In, Out>> cf2, double trainRatio) {
            this.cf1 = cf1;
            this.cf2 = cf2;
            this.trainRatio = trainRatio;
        }
        
        public Classifier<In, Out> createNew() {
            return new IdealTeam<In, Out>(cf1.createNew(), cf2.createNew(), trainRatio);
        }
        
        
        
    }

}
