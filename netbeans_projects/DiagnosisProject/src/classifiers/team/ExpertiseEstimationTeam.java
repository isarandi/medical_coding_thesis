/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers.team;

import classifiers.neuralnetwork.MLP;

import framework.AbstractSingleClassifier;
import framework.Classifier;
import java.util.Arrays;
import java.util.List;
import framework.Fac;
import framework.ResultSet;
import framework.Sample;
import framework.SampleSet;
import java.util.concurrent.atomic.AtomicInteger;
import parallel.ParallelFor;
import framework.evaluation.EvaluationMetric;
import java.util.logging.Level;
import java.util.logging.Logger;
import vector.Vector;
import vector.DenseVector;

/**
 *
 * @author qr
 */
public class ExpertiseEstimationTeam<Out> extends AbstractSingleClassifier<Vector,Out> {

    MLP leader;
    List<Classifier<Vector, Out>> members;
    
    EvaluationMetric memberMetric;

    double trainRatio;

    public ExpertiseEstimationTeam(EvaluationMetric memberMetric, double trainRatio, MLP leader, Classifier<Vector, Out>... members) {
        this.leader = leader;
        this.members = Arrays.asList(members);
        this.memberMetric = memberMetric;
        this.trainRatio = trainRatio;

    }

    public void train(SampleSet<Vector, Out> trainingSet) {

        //splitting the training set to a reduced training set and a test set
        //the test set will be used to validate each member on them and teach how they perform to the leader
        final SampleSet<Vector, Out> memberTrainingSet = trainingSet.lowerPart(trainRatio);
        SampleSet<Vector, Out> test = trainingSet.higherPart(trainRatio);

        //training each member with the reduced training set
        trainMembers(memberTrainingSet);
        System.out.println("members done");

        //validating with the test samples
        //returns sample set for the leader. Will contain test inputs and the success of each member at guessing the code as output vectors
        final SampleSet<Vector, Vector> leaderSet = validateMembers(test);
        
        // normalizáljuk a kimeneteket
        //System.out.println("normlizing");
        //Vector.removeMean(leaderSet.outList());
        //Collections.shuffle(leaderSet.inList());

        //ObjectPersistance.export(leaderSet, Util.baseDir+"\\onlab\\magyar_leaderset.dat");
        
        //training the leader
        leader.setVerbose(true);
        Thread t = new Thread() {

            @Override
            public void run() {
                System.out.println("training leader");
                leader.train(leaderSet);
                System.out.println("leader done.");
            }
        };
        t.start();

        //retraining the members with the whole training set
        trainMembers(trainingSet);
        
        try {
            t.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(ExpertiseEstimationTeam.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public ResultSet<Out> classify(Vector input, int limit) {
        Vector expertiseGuesses = leader.getOutput(input);
        ResultSet<Out> endResults = new ResultSet<Out>(limit);

        for (int i = 0; i < members.size(); ++i) {
            ResultSet<Out> memberResults = members.get(i).classify(input, limit*2);
            memberResults.normalize();
            endResults.blend(memberResults, expertiseGuesses.get(i), ResultSet.ADD_RESULT_BLENDER); //expertiseGuesses.get(i)
        }
        
        return endResults;

    }

    private void trainMembers(final SampleSet<Vector, Out> samples) {

        for (Classifier<Vector,Out> member: members)
            member.train(samples);
    }

    private SampleSet<Vector, Vector> validateMembers(SampleSet<Vector, Out> samples) {
        final SampleSet<Vector, Vector> leaderSet = new SampleSet<Vector, Vector>();

        final AtomicInteger counter = new AtomicInteger(0);
        new ParallelFor<Sample<Vector, Out>>() {

            @Override
            protected void ForLoop(Iterable<Sample<Vector, Out>> taskSamples) {
                SampleSet<Vector, Vector> taskLeaderSet = new SampleSet<Vector, Vector>();

                for (Sample<Vector, Out> s : taskSamples) {
                    Vector successVector = new DenseVector(members.size());

                    int memberID = 0;

                    //validating each member
                    for (Classifier<Vector, Out> member : members) {
                        ResultSet<Out> rs = member.classify(s.getInput(), 150);
                        double success = memberMetric.calculate(rs, s.getOutput());
                                                
                        successVector.set(memberID, success);

                        ++memberID;
                    }
                    int c = counter.getAndIncrement();
                    /*if (c % 50 == 0) {
                        System.out.println(String.format("sample %d", c));
                    }*/
                    taskLeaderSet.add(s.getInput(), successVector);
                }

                synchronized (leaderSet) {
                    leaderSet.addAll(taskLeaderSet);
                }

            }
        }.execute(samples);
        
        return leaderSet;
    }

    public static class Factory<Out> implements Fac<Classifier<Vector, Out>> {

        public Classifier<Vector, Out> createNew() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
