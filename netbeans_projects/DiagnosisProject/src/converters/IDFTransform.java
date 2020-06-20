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
import framework.Fac;
import vector.Vector;
import vector.DenseVector;
import vector.SparseVector;

/**
 *
 * @author qr
 */
public class IDFTransform implements InputTransform<Vector, Vector> {

    Vector idfVector;

    private void calculateIDF(List<Vector> inputs)
    {
        List<Double> df = new ArrayList<Double>(); //frequency of vector positions

        //Calculating DFs
        for (Vector v : inputs)
        {
            for (int pos : v.nonZeroPositions())
            {
                while (df.size() <= pos)
                {
                    df.add(0.0);
                }

                Double nowfreq = df.get(pos);
                df.set(pos, nowfreq + 1.0);
            }
        }

        //Calculating IDFs
        List<Double> idf = new ArrayList<Double>();

        double size = inputs.size();
        for (Double freq : df)
        {
            if (freq==0)
            {
                idf.add(0.0);
            }
            else
            {
                idf.add(Math.log(size)-Math.log(freq));
            }
        }

        idfVector = new DenseVector(idf);
    }

    public BatchConverter<Vector, Vector> getTrainingConverter()
    {
        return new BatchConverter<Vector, Vector>() {

            public List<Vector> convert(List<Vector> inputs)
            {
                calculateIDF(inputs);

                //Scaling training points with IDFs
                List<Vector> result = new ArrayList<Vector>(inputs.size());
                for (Vector unscaled : inputs)
                {
                    Vector scaled = unscaled.scaled(idfVector, SparseVector.getFactory());
                    result.add(scaled);
                }

                return result;
            }
        };
    }

    public Converter<Vector, Vector> getClassifyConverter()
    {
        return new Converter<Vector, Vector>() {

            public Vector convert(Vector input)
            {
                return input.scaled(idfVector, SparseVector.getFactory());
            }
        };
    }

    @Override
    public String toString()
    {
        return "IDFTransform";
    }

    
}
