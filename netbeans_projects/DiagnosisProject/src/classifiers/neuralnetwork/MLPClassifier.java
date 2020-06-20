/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifiers.neuralnetwork;

import converters.CoordinateSetVectorizerConverter;
import framework.AbstractSingleClassifier;
import framework.Classifier;
import framework.Fac;
import framework.ResultSet;
import framework.SampleSet;
import vector.Vector;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class MLPClassifier extends AbstractSingleClassifier<Vector, Integer>{
    
    private MLP inner;

    public MLPClassifier(MLP inner)
    {
        this.inner=inner;
    }

    public void train(SampleSet<Vector, Integer> trainingSet)
    {
        SampleSet<Vector, Vector> convertedSampleSet =
                trainingSet.convert(null, new CoordinateSetVectorizerConverter());
        
        inner.train(convertedSampleSet);
    }

    @Override
    public ResultSet<Integer> classify(Vector input, int limit)
    {
        Vector output = inner.convert(input);
        return outputToResultSet(output, limit);
    }

    static ResultSet<Integer> outputToResultSet(Vector output, int limit)
    {
        ResultSet<Integer> results = new ResultSet<Integer>(limit);

        for (int pos : output.nonZeroPositions())
        {
            results.push(pos, output.get(pos));
        }
        return results;
    }

    public Fac<Classifier<Vector, Integer>> getCloningFactory()
    {
        return new CloningFactory();
    }

    @Override
    public String toString()
    {
        return "MLPCl{" + inner + '}';
    }
      
    class CloningFactory implements Fac<Classifier<Vector, Integer>>
    {
        public Classifier<Vector, Integer> createNew() {
            return new MLPClassifier(inner.getCloningFactory().createNew());
        }

    }
    
}