/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vector;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Jeno
 */
public class DenseVector extends Vector {

    private double[] values;

    public DenseVector(List<Double> list)
    {
        values = new double[list.size()];
        int i = 0;
        for (double d : list)
        {
            values[i++] = d;
        }
        setDimensionality(values.length);
    }

    public DenseVector(int size)
    {
        values = new double[size];
        setDimensionality(size);
    }

    public DenseVector(double[] arr)
    {
        values = arr;
        setDimensionality(arr.length);
    }

    @Override
    public double dotProduct(Vector other)
    {
        if (other instanceof DenseVector)
        {
            double result = 0.0;
            for (int i = 0; i < values.length; ++i)
            {
                result += values[i] * other.get(i);
            }

            return result;
        } else
        {
            return super.dotProduct(other);
        }
    }

    @Override
    public double get(int index)
    {
        if (values.length <= index)
        {
            return 0.0;
        }
        return values[index];

    }

    @Override
    public Iterable<Integer> nonZeroPositions()
    {
        return new FullNonZeroIterator();
    }

    @Override
    public void scale(Vector vec)
    {
        for (int i = 0; i < values.length; ++i)
        {
            values[i] *= vec.get(i);
        }
    }

    @Override
    public void set(int index, double newvalue)
    {
        values[index] = newvalue;//.set(index,newvalue);
    }

    @Override
    public void clear()
    {
        for (int i = 0; i < values.length; ++i)
        {
            values[i] = 0;
        }
    }

    /*@Override
    public Vector minus(Vector vec, VectorFactory fac)
    {
        Vector result = fac.newVector(size);

        for (int i = 0; i < size; ++i)
        {
            result.set(i, this.get(i) - vec.get(i));
        }

        return result;
    }*/

    @Override
    public void minus(Vector vec)
    {
        
        for (Integer pos : vec.nonZeroPositions())
        {
            values[pos] -= vec.get(pos);
        }
    }

    @Override
    public int dimensionalityAtLeast()
    {
        return values.length;
    }

    @Override
    public double[] toArray()
    {
        return values;
    }

    @Override
    public int nonZeroCount()
    {
        int c = 0;
        for (int i = 0; i < values.length; ++i)
        {
            if (values[i]!=0.0)
                ++c;
        }
        return c;
    }


    public class FullNonZeroIterator implements Iterable<Integer>, Iterator<Integer> {

        int counter = 0;

        @Override
        public boolean hasNext()
        {
            double val = 0.0;
            for (; counter < values.length; ++counter)
            {
                val = values[counter];

                if (val != 0.0)
                {
                    break;
                }
            }

            return val != 0.0;
        }

        @Override
        public Integer next()
        {
            return counter++;
        }

        public Iterator<Integer> iterator()
        {
            return this;
        }

        public void remove()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public static VectorFactory getFactory()
    {
        return FullFactory.getFactory();
    }

    private static class FullFactory implements VectorFactory {

        private static VectorFactory fac = new FullFactory();

        private FullFactory()
        {
        }

        private static VectorFactory getFactory()
        {
            return fac;
        }

        public Vector newVector(int size)
        {
            return new DenseVector(size);
        }
    }
}
