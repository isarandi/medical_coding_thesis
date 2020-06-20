/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vector;

import java.util.*;

/**
 *
 * @author Jeno
 */
public class SparseVector extends Vector {
    private SortedMap<Integer, Double> posval = new TreeMap<Integer,Double>();

    //constructors

    /*public CompactVector(List<PositionValuePair> newcoords) {
        for (int pos : nonZeroPositions())
        {
            posval.put(posvalpair.getPosition(), posvalpair.getValue());
        }
    }*/

    public SparseVector(Collection<Integer> positions, double value) {
        for (Integer pos: positions)
        {
            posval.put(pos, value);
        }
    }
    
    public SparseVector(Vector other) {
        for (Integer pos: other.nonZeroPositions())
        {
            posval.put(pos, other.get(pos));
        }
    }

    public SparseVector() {
    }

    public SparseVector(int size) {
        setDimensionality(size);
    }

    @Override
    public double get(int index) {
        return (posval.containsKey(index)) ? posval.get(index) : 0.0;
    }

    public void set(int index, double newvalue) {
        if (newvalue!=0.0)
            posval.put(index, newvalue);
        else
            posval.remove(index);
    }

    @Override
    public void scale(Vector vec) {
        for (int pos : nonZeroPositions())
        {
            set(pos, get(pos)*vec.get(pos));
        }
    }

    @Override
    public double absoluteValue()
    {
        double result = 0;
        for (int pos : nonZeroPositions())
        {
            double val = get(pos);
            result+=val*val;
        }

        return Math.sqrt(result);
    }


    @Override
    public void clear() {
        posval.clear();
    }

    @Override
    protected int getComplexity()
    {
        return posval.size();
    }

    public static VectorFactory getFactory()
    {
        return Factory.getFactory();
    }

    @Override
    public Iterable<Integer> nonZeroPositions() {
        return posval.keySet();
    }

    @Override
    public int dimensionalityAtLeast() {
        if (posval.isEmpty())
            return 0;
        
        return Collections.max(posval.keySet())+1;
    }

    @Override
    public int nonZeroCount()
    {
        return posval.size();
    }

    private static class Factory implements VectorFactory
    {
        private static VectorFactory fac = new Factory();
        private Factory(){}

        private static VectorFactory getFactory()
        {
            return fac;
        }

        public Vector newVector(int size) {
            return new SparseVector(size);
        }
    }

}
