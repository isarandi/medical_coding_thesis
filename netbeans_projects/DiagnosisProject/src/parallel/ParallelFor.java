/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parallel;

//import com.amd.aparapi.Kernel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author qr
 */
class DaemonThreadFactory implements ThreadFactory
{

    public Thread newThread(Runnable r)
    {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    }
}

public class ParallelFor<T>
{

    private static final int numProcessors = Runtime.getRuntime().availableProcessors();
    private static ExecutorService executorService =
            new ThreadPoolExecutor(numProcessors, numProcessors * 10, 10, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(),
            new DaemonThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy());
    
   // private List<Future> tasks = new ArrayList<Future>();

    private List<? extends Iterable<T>> getParts(SplittableIterable<T> spliter, int numThreads)
    {

        int smallerPart = spliter.size() / numThreads;
        int largerPart = smallerPart + 1;

        int largerPartNum = (spliter.size() % numThreads);
        int smallerPartNum = numThreads - largerPartNum;

        List<Iterable<T>> result = new ArrayList<Iterable<T>>();

        int from = 0;
        for (int i = 0; i < largerPartNum; ++i) {
            result.add(spliter.splitPart(from, from + largerPart));
            from += largerPart;
        }

        if (smallerPart != 0) {
            for (int i = 0; i < smallerPartNum; ++i) {
                result.add(spliter.splitPart(from, from + smallerPart));
                from += smallerPart;
            }
        }

        return result;
    }
    //To be overridden

    protected void ForLoop(Iterable<T> items)
    {
        for (T item : items) {
            ForBody(item);
        }
    }
    //To be overridden

    protected void ForBody(T item)
    {
    }

    private void executeParallel(List<? extends Iterable<T>> parts, ExecutorService exec)
    {
        final CountDownLatch countdown = new CountDownLatch(parts.size());

        for (final Iterable<T> iter : parts) {
            //Future<?> task = 
            exec.execute(new Runnable()
            {
                public void run()
                {
                    ForLoop(iter);
                    countdown.countDown();
                }
            });
            
            //tasks.add(task);
            //task.

        }

        try {
            countdown.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(ParallelFor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
//    protected void _break()
//    {
//        for (Future f : tasks)
//        {
//            f.cancel(true);
//        }
//    }

    public void execute(SplittableIterable<T> spliter, int numThreads, int minTaskSize, ExecutorService exec)
    {
        if (numThreads == 0) {
            numThreads = numProcessors;
        }

        if (spliter.size() / numThreads < minTaskSize || numThreads == 1) {
            ForLoop(spliter);
        } else {
            executeParallel(getParts(spliter, numThreads), exec);
        }
    }

    public void execute(SplittableIterable<T> spliter, int numThreads, int minTaskSize)
    {
        execute(spliter, numThreads, minTaskSize, executorService);
    }

    public void execute(SplittableIterable<T> spliter)
    {
        execute(spliter, 0, 0);
    }

    public void execute(Collection<T> coll)
    {
        execute(coll, 0, 0);
    }

    public void execute(Collection<T> coll, int numThreads, int minTaskSize)
    {
        SplittableIterable<T> spliter = new CollectionSplittableIterable<T>(coll);
        execute(spliter, numThreads, minTaskSize);
    }

    public void execute(int from, int to)
    {
        execute(from, to, 0, 0);
    }
    
//    public void executeGPU(final int from, int to)
//    {
//        Kernel kernel = new Kernel() {
//
//            @Override
//            public void run()
//            {
//                Integer gid = getGlobalId()+from;
//                ForBody((T)gid);
//            }
//        };
//        kernel.execute(to-from);
//    }


    public void execute(int from, int to, int numThreads, int minTaskSize)
    {
        SplittableIterable<T> spliter = (SplittableIterable<T>) new CounterSplittableIterable(from, to);
        execute(spliter, numThreads, minTaskSize);
    }
}
