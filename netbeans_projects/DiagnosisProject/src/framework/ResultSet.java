/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import framework.adaption.Converter;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author Jeno
 */
public class ResultSet<Out> implements Iterable<Result<Out>>
{

    public static final ResultBlender ADD_RESULT_BLENDER = new AddResultBlender();
    public static final ResultBlender MAX_RESULT_BLENDER = new MaxResultBlender();
    public static final ResultBlender MULT_RESULT_BLENDER = new MaxResultBlender();

    private TreeMap<Out, Double> map;
    private int limit;

    public ResultSet(int limit)
    {
        this.limit = limit;
        map = new TreeMap<Out, Double>();
    }

    public int getLimit()
    {
        return limit;
    }
    
    public ResultSet()
    {
        this(Integer.MAX_VALUE);
    }

    public void setLimit(int limit)
    {
        this.limit = limit;
        
        while (map.size() > limit)
        {
            pop();
        }
    }

    public double getConfidence(Out output)
    {
        if (!contains(output))
        {
            return 0.0;
        } else
        {
            return map.get(output);
        }
    }

    public ResultSet<Out> tops(int limit)
    {
        ResultSet<Out> newResultSet = new ResultSet<Out>(limit);
        for (Result<Out> r : this)
        {
            newResultSet.push(r);
        }
        return newResultSet;
    }

    private void pop()
    {
        if (map.isEmpty())
        {
            return;
        }

        Map.Entry<Out, Double> minentry = null;
        double minconf = Double.POSITIVE_INFINITY;
        for (Map.Entry<Out, Double> entry : map.entrySet())
        {
            if (entry.getValue() < minconf)
            {
                minconf = entry.getValue();
                minentry = entry;

            }
        }
        map.remove(minentry.getKey());
    }

    public boolean contains(Out output)
    {
        return map.containsKey(output);
    }

    public void blend(ResultSet<Out> rs, double overallConfidence, ResultBlender rb)
    {
        for (Result<Out> r : rs)
        {
            push(r.getOutput(), overallConfidence * r.getConfidence(), rb);
        }
    }

    public void blend(ResultSet<Out> rs, double overallConfidence)
    {
        blend(rs, overallConfidence, MAX_RESULT_BLENDER);
    }

    public void push(Result<Out> r)
    {
        push(r.getOutput(), r.getConfidence());
    }

    public void push(Out output, double confidence, ResultBlender rb)
    {
        if (confidence <= 0.0)
            return;
        
        // already there, blend in
        if (map.containsKey(output))
        {
            double old = map.get(output);
            map.put(output, rb.blend(old, confidence));
            return;
        }

        // worse than the worse of a filled resultset
        if (!map.isEmpty() && confidence <= map.firstEntry().getValue() && map.size() >= limit)
            return;

        // need to make place
        if (map.size() >= limit)
        {
            pop();
        }
        map.put(output, confidence);
    }

    public void push(Out output, double confidence)
    {
        push(output, confidence, MAX_RESULT_BLENDER);
    }

    public void normalize()
    {
        double sum = 0.0;
        for (double d : map.values())
        {
            sum += d;
        }

        for (Map.Entry<Out, Double> entry : map.entrySet())
        {
            entry.setValue(entry.getValue() / sum);
        }
    }

    public int getPosition(Out desired)
    {
        int pos = 1;
        for (Result<Out> r : this)
        {
            if (r.getOutput().equals(desired))
            {
                return pos;
            }
            ++pos;
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for (Result<Out> result : this)
        {
            sb.append(result.getOutput()).append(" ");
            sb.append(result.getConfidence()).append("\n");
        }
        return sb.toString();
    }

    public void clear()
    {
        map.clear();
    }

    public Iterator<Result<Out>> iterator()
    {
        return sorted().iterator();
        //return new ResultIterator<Out>(map.entrySet().iterator());
    }

    public Iterable<Result<Out>> sorted()
    {
        return new SortedResults();
    }

    public void softmaxTransform()
    {
        double sum = 0;
        for (double d : map.values())
        {
            sum += Math.exp(d);
        }

        for (Entry<Out, Double> entry : map.entrySet())
        {
            entry.setValue(Math.exp(entry.getValue()) / sum);
        }

    }

    private class SortedResults implements Iterable<Result<Out>>
    {

        public Iterator<Result<Out>> iterator()
        {
            List<Entry<Out, Double>> entryList = new ArrayList<Entry<Out, Double>>(map.entrySet());
            Collections.sort(entryList, new EntryComparator());
            return new ResultIterator<Out>(entryList.iterator());
        }

        private class EntryComparator implements Comparator<Map.Entry<Out, Double>>
        {

            public int compare(Entry<Out, Double> o1, Entry<Out, Double> o2)
            {
                double diff = o1.getValue() - o2.getValue();

                if (diff < 0)
                {
                    return 1;
                } else if (diff > 0)
                {
                    return -1;
                } else
                {
                    return 0;
                }
            }
        }
    }

    private static class ResultIterator<Out> implements Iterator<Result<Out>>
    {

        Iterator<Map.Entry<Out, Double>> entryIterator;

        public ResultIterator(Iterator<Map.Entry<Out, Double>> entryIterator)
        {
            this.entryIterator = entryIterator;
        }

        public boolean hasNext()
        {
            return entryIterator.hasNext();
        }

        public Result<Out> next()
        {
            Map.Entry<Out, Double> entry = entryIterator.next();
            return new Result<Out>(entry.getKey(), entry.getValue());
        }

        public void remove()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public interface ResultBlender
    {

        double blend(double c1, double c2);
    }

    static class MaxResultBlender implements ResultBlender
    {

        public double blend(double c1, double c2)
        {
            return ((c1 > c2) ? c1 : c2);
        }
    }

    static class AddResultBlender implements ResultBlender
    {

        public double blend(double c1, double c2)
        {
            return c1 + c2;
        }
    }

    static class MultAddResultBlender implements ResultBlender
    {
        double factor;

        public MultAddResultBlender(double factor)
        {
            this.factor = factor;
        }
        
        public double blend(double c1, double c2)
        {
            return (c1 + c2)*factor;
        }
    }
}
