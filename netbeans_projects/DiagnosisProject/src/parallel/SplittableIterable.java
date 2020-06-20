/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parallel;

import java.util.List;

/**
 *
 * @author qr
 */
public interface SplittableIterable<T> extends Iterable<T> {
    public Iterable<T> splitPart(int from, int to);

    public int size();
    //public List<SplittableIterable<T>> split(List<Integer> sizes);

}
