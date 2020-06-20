/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package other;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class MultiSet<T> implements Iterable<T>
{

    Map<T, Integer> map = new HashMap<T, Integer>();

    public int size()
    {
        int sum = 0;
        for (int num : map.values()) {
            sum += num;
        }
        return sum;
    }
    
    public T mostFrequent()
    {
        int bestF = 0;
        T bestobj=null;
        
        for (Map.Entry<T,Integer> entry: map.entrySet())
        {
            if (entry.getValue() > bestF)
            {
                bestF = entry.getValue();
                bestobj = entry.getKey();
            }
        }
        
        return bestobj;
    }
    
    public int distinctSize()
    {
        return map.size();
    }
    
    public Set<T> distinctSet()
    {
        return map.keySet();
    }

    public MultiSet(MultiSet other)
    {
        map.putAll(other.map);
    }
    
    public MultiSet(Collection<T> other)
    {
        for (T obj: other)
        {
            add(obj);
        }
    }

    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    public boolean contains(T o)
    {
        return map.containsKey(o);
    }
    
    public int getMultiplicity(T o)
    {
        return map.get(o);
    }

    public final boolean add(T e)
    {
        if (map.containsKey(e)) {
            map.put(e, map.get(e) + 1);
        } else {
            map.put(e, 1);
        }
        return true;
    }

    public boolean remove(T o)
    {
        if (map.containsKey(o)) {
            int num = map.get(o);
            if (num > 1) {
                map.put(o, num - 1);
            } else {
                map.remove(o);
            }
        }
        return true;
    }

    public void clear()
    {
        map.clear();
    }

    public MultiSet()
    {
    }

    public Iterator<T> iterator()
    {
        return new Iter();
    }

    class Iter implements Iterator<T>
    {

        Iterator<T> keyiter = map.keySet().iterator();
        T nowElem;
        int nowMultiplicity;
        int nowPos;

        public boolean hasNext()
        {
            return nowPos < nowMultiplicity || keyiter.hasNext();
        }

        public T next()
        {
            if (nowPos < nowMultiplicity) {
                ++nowPos;
                return nowElem;

            } else {
                nowElem = keyiter.next();
                nowMultiplicity = map.get(nowElem);
                nowPos = 1;
                return nowElem;
            }
        }

        public void remove()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
