/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parallel;

import java.util.*;

/**
 *
 * @author qr
 */
public class CollectionSplittableIterable<T> implements SplittableIterable<T>{

    List<T> list;

    public CollectionSplittableIterable(Collection<T> coll) {
        list = new ArrayList<T>(coll);
    }

    public Iterator<T> iterator() {
        return list.iterator();
    }

    public Iterable<T> splitPart(int from, int to) {
        return list.subList(from, to);
    }

    public int size() {
        return list.size();
    }

}
