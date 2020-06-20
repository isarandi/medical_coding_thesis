package vector;

import java.util.*;

public class IndicatorVector extends Vector {

    private SortedSet<Integer> setIndices = new TreeSet<Integer>();

    public IndicatorVector(Collection<Integer> ones)
    {
        this.setIndices.addAll(ones);
        setDimensionality(Collections.max(ones));
    }

    public IndicatorVector()
    {
    }

    public IndicatorVector(Vector vec)
    {
        for (int pos : vec.nonZeroPositions())
        {
            setIndices.add(pos);
        }
        setDimensionality(vec.dimensionality());
    }

    @Override
    protected int getComplexity()
    {
        return setIndices.size();
    }

    public IndicatorVector(int size)
    {
        setDimensionality(size);
    }

    @Override
    public double dotProduct(Vector other)
    {
        if (other instanceof IndicatorVector)
        {
            return dotProduct((IndicatorVector) other);
        } else
        {
            double result = 0;
            for (Integer onepos : setIndices)
            {
                result += other.get(onepos);
            }
            return result;
        }

    }

    public double dotProduct(IndicatorVector other)
    {
        IndicatorVector complexer = (other.getComplexity() > this.getComplexity()) ? other : this;
        IndicatorVector lighter = (complexer == this) ? other : this;

        int result = 0;
        for (Integer pos : lighter.setIndices)
        {
            if (complexer.setIndices.contains(pos))
            {
                ++result;
            }
        }

        return result;
    }

    @Override
    public double absoluteValue()
    {
        return Math.sqrt(setIndices.size());
    }

    @Override
    public Vector scaled(Vector vec, VectorFactory fac)
    {
        Vector res = fac.newVector(dimensionality);

        for (Integer onepos : setIndices)
        {
            res.set(onepos, vec.get(onepos));
        }

        return res;
    }

    @Override
    public double get(int index)
    {
        return (setIndices.contains(index)) ? 1.0 : 0.0;
    }

    @Override
    public void set(int index, double newvalue)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void scale(Vector vec)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public void clear()
    {
        setIndices.clear();
    }

    @Override
    public void set(int index)
    {
        setIndices.add(index);
    }

    @Override
    public void unset(int index)
    {
        setIndices.remove(index);
    }

    public static VectorFactory getFactory()
    {
        return IndicatorFactory.getFactory();
    }

    @Override
    public Iterable<Integer> nonZeroPositions()
    {
        return setIndices;
    }

    @Override
    public int dimensionalityAtLeast()
    {
        if (setIndices.isEmpty())
            return 0;
        
        return Collections.max(setIndices)+1;
    }

    @Override
    public int nonZeroCount()
    {
        return setIndices.size();
    }

    private static class IndicatorFactory implements VectorFactory {

        private static VectorFactory fac = new IndicatorFactory();

        private IndicatorFactory()
        {
        }

        public static VectorFactory getFactory()
        {
            return fac;
        }

        public Vector newVector(int size)
        {
            return new IndicatorVector(size);
        }
    }
}
