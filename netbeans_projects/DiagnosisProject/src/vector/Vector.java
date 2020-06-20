/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vector;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Jeno
 */
public abstract class Vector implements Serializable {

    public static void removeMean(List<Vector> vectors) {
        Vector sum = new DenseVector(largestSize(vectors));
        for (Vector v: vectors)
        {
            sum.plus(v);
        }

        for (Vector v: vectors)
        {
            for (int pos=0; pos<sum.dimensionality(); ++pos)
            {
                v.set(pos, v.get(pos)-sum.get(pos)/vectors.size());
            }
        }
    }
    
    public static int maxNonZeroCount(List<Vector> vectors)
    {
        int largestTillNow = 0;
        for (Vector v : vectors)
        {
            int now = v.nonZeroCount();
            if (now > largestTillNow)
            {
                largestTillNow = now;
            }
        }
        return largestTillNow;
    }
    protected int dimensionality = -1;

    public Vector()
    {
    }

    public abstract double get(int index);

    public abstract void set(int index, double newvalue);

    public abstract void clear();

    public void set(int index)
    {
        set(index, 1.0);
    }

    public void unset(int index)
    {
        set(index, 0.0);
    }

    public int dimensionality()
    {
        return dimensionality;
    }
    
    public abstract int nonZeroCount();
    
    public boolean isDimensionalityKnown()
    {
        return dimensionality != -1;
    }

    public abstract int dimensionalityAtLeast();

    public void setDimensionality(int size)
    {
        this.dimensionality = size;
    }

    public double[] toArray()
    {
        double[] arr = new double[this.dimensionality()];
        for (int i=0; i<this.dimensionality(); ++i)
        {
            arr[i] = this.get(i);
        }
        return arr;
    }

    public static int largestSize(List<Vector> vectors)
    {
        int largestTillNow = 0;
        for (Vector v : vectors)
        {
            int now = v.dimensionalityAtLeast();
            if (now > largestTillNow)
            {
                largestTillNow = now;
            }
        }
        return largestTillNow;
    }

    protected int getComplexity()
    {
        return dimensionality;
    }

    public double dotProduct(Vector other)
    {
        Vector complexer = (other.getComplexity() > this.getComplexity()) ? other : this;
        Vector lighter = (complexer == this) ? other : this;

        double result = 0;
        for (int pos : lighter.nonZeroPositions())
        {
            result += lighter.get(pos) * complexer.get(pos);
        }
        return result;
    }

    public abstract void scale(Vector vec);

    public Vector scaled(double factor, VectorFactory fac)
    {
        Vector output = fac.newVector(dimensionality());
        for (int pos : nonZeroPositions())
        {
            output.set(pos, get(pos) * factor);
        }
        return output;
    }
    
    public Vector subtraced(Vector other, VectorFactory fac)
    {
        Vector output = fac.newVector(dimensionality());
        for (int pos : other.nonZeroPositions())
        {
            output.set(pos, get(pos) - other.get(pos));
        }
        return output;
    }

    public Vector scaled(Vector scaler, VectorFactory fac)
    {

        Vector output = fac.newVector(dimensionality);
        for (int pos : nonZeroPositions())
        {
            output.set(pos, get(pos) * scaler.get(pos));
        }
        return output;
    }

    public double absoluteValue()
    {
        double squareSum = 0;
        for (int pos : this.nonZeroPositions())
        {
            double value = this.get(pos);
            squareSum += value * value;
        }
        return Math.sqrt(squareSum);
    }

    public void minus(Vector vec)
    {
        for (Integer pos : vec.nonZeroPositions())
        {
            set(pos, this.get(pos) - vec.get(pos));
        }
    }
    
    
    public void plus(Vector vec) {
        for (Integer pos : vec.nonZeroPositions())
        {
            set(pos, this.get(pos) + vec.get(pos));
        }
    }
    
    public void plus(int pos, double val) {
        set(pos, this.get(pos) + val);
    }

    public abstract Iterable<Integer> nonZeroPositions();

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        for (Integer pos: this.nonZeroPositions())
        {
            sb.append(pos+1).append(":");
            sb.append(this.get(pos)).append(" ");
        }
        return sb.toString();
    }


}
