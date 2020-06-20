/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package converters;

import framework.adaption.Converter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author qr
 */
public class IntervalLabeler<T extends Comparable<T> & Serializable> implements Converter<T, Integer> {
    
    private static class Interval<T extends Comparable<T> & Serializable> implements Serializable
    {
        private T begin;
        private T end;

        public Interval(T begin, T end) {
            this.begin = begin;
            this.end = end;
        }

        public boolean contains(T between)
        {
            return (begin.compareTo(between) <= 0) && (between.compareTo(end) < 0);
        }
    }
    
    List<Interval<T>> intervals = new ArrayList<Interval<T>>();

    public void addInterval(T from, T to)
    {
        intervals.add(new Interval<T>(from,to));
    }
    public Integer convert(T o)
    {
        for (int i=0; i<intervals.size(); ++i)
        {
            if (intervals.get(i).contains(o))
                return i;
        }
        return -1;
    }

    @Override
    public String toString()
    {
        return "IntervalLabeler";
    }
    
    
}
