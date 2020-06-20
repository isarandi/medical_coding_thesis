/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parallel;

import java.util.Iterator;


/**
 *
 * @author qr
 */
public class CounterSplittableIterable implements SplittableIterable<Integer>, Iterator<Integer> {
    private final int from;
    private final int to;
    private int counter;

    public CounterSplittableIterable(int from, int to) {
        this.from = from;
        this.to = to;
        this.counter = from;
    }

    public Iterable<Integer> splitPart(int from_, int to_) {
        return new CounterSplittableIterable(from+from_,from+to_);
    }

    public int size() {
        return to-from;
    }
    
    

    public Iterator<Integer> iterator() {
        return this;
    }

    public boolean hasNext() {
        return counter<to;
    }

    public Integer next() {
        return counter++;
    }

    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }



}
