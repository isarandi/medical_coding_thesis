/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package framework;

import framework.adaption.BatchConverter;
import framework.adaption.Converter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
import parallel.SplittableIterable;

/**
 *
 * @author Jeno
 */
public class SampleSet<In,Out> implements SplittableIterable<Sample<In,Out>>, Serializable {

    List<In> ins;
    List<Out> outs;

    public SampleSet() {
        ins = new ArrayList<In>();
        outs = new ArrayList<Out>();
    }

    public SampleSet(List<In> ins, List<Out> outs) {
        this.ins = ins;
        this.outs = outs;
    }

    public SampleSet(SampleSet<In, Out> other)
    {
        this();
        this.addAll(other);
    }
    
    public void add(In input, Out output)
    {
        ins.add(input);
        outs.add(output);
    }
    public void add(Sample<In,Out> s)
    {
        add(s.getInput(),s.getOutput());
    }
    
    public final void addAll(SampleSet<In,Out> other)
    {
        ins.addAll(other.inList());
        outs.addAll(other.outList());
    }

    public Iterator<Sample<In,Out>> iterator()
    {
        return new SampleIterator();
    }
    

    public List<In> inList()
    {
        return ins;
    }

    public List<Out> outList()
    {
        return outs;
    }

    public Out getCorrectOutput(In input)
    {
        int index = ins.indexOf(input);
        return outs.get(index);
    }

    public void shuffle()
    {
        shuffle(new Random(0));
    }

    public void shuffle(Random r)
    {
        List<Sample<In,Out>> slist= new LinkedList<Sample<In,Out>>();

        Iterator<In> inIT = ins.iterator();
        Iterator<Out> outIT = outs.iterator();
        while (inIT.hasNext())
        {
            slist.add(new Sample(inIT.next(), outIT.next()));
        }
        Collections.shuffle(slist,r);

        ins.clear();
        outs.clear();

        for (Sample<In, Out> s: slist)
        {
            ins.add(s.getInput());
            outs.add(s.getOutput());
        }
    }
    
    public void remove(Sample<In,Out> s)
    {
        int index = ins.indexOf(s.getInput());
        if (index != -1 && outs.get(index).equals(s.getOutput()))
        {
            ins.remove(index);
            outs.remove(index);
        }
    }

    public SampleSet<In,Out> splitPart(int begin, int end)
    {
        return new SampleSet<In,Out>(ins.subList(begin, end), outs.subList(begin, end));
    }
    
    private int[] crossValidationTestBounds(int numParts, int which)
    {
        int partSize = (int)Math.round(size()/(double)numParts);
        
        int testFrom = partSize*which;
        int testEnd = Math.min(testFrom+partSize, this.size());        
        int[] results = {testFrom, testEnd};
        return results;
    }
    
    public SampleSet<In,Out> crossValidTrainingPart(int numParts, int which)
    {
        int[] testBounds = crossValidationTestBounds(numParts, which);
        if (testBounds[0]>=size())
            return this;
        
        SampleSet<In,Out> trSet = new SampleSet<In, Out>(this);
        trSet.inList().subList(testBounds[0],testBounds[1]).clear();
        trSet.outList().subList(testBounds[0],testBounds[1]).clear();
        return trSet;
    }
    
    
    public SampleSet<In,Out> crossValidTestPart(int numParts, int which)
    {
        int[] testBounds = crossValidationTestBounds(numParts, which);
        if (testBounds[0]>=size())
            return new SampleSet<In, Out>();
        
        return splitPart(testBounds[0],testBounds[1]);
    }

    public int size()
    {
        return ins.size();
    }
    
    public void clear()
    {
        ins.clear();
        outs.clear();
    }

    public SampleSet<In,Out> lowerPart(double splitProportion)
    {
        int cut = (int) (size() * splitProportion);
        return splitPart(0, cut);
    }

    public SampleSet<In,Out> higherPart(double splitProportion)
    {
        int cut = (int) (size() * splitProportion);
        return splitPart(cut, size());
    }
    
    public Sample<In,Out> get(int i)
    {
        return new Sample<In, Out>(ins.get(i), outs.get(i));
    }
    
    public String export()
    {
        return this.export("%s %s");
    }
    
    public String export(String format)
    {
        StringBuilder sb = new StringBuilder();
        
        for (Sample<In,Out> s: this)
        {
            String outstr = (s.getOutput() instanceof Integer) ? Integer.toString(((Integer)s.getOutput())+1) : s.getOutput().toString();
            sb.append(String.format(format, outstr, s.getInput().toString()));
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }
   
    public <NewIn,NewOut> SampleSet<NewIn,NewOut> convert(Converter<In,NewIn> inputconv, Converter<Out,NewOut> outputconv)
    {
        SampleSet<NewIn,NewOut> convertedSampleSet = new SampleSet<NewIn, NewOut>();
        
        for (Sample<In,Out> s: this)
        {
            convertedSampleSet.add(
                    inputconv != null ? inputconv.convert(s.getInput())  : (NewIn) s.getInput(),
                    outputconv!= null ? outputconv.convert(s.getOutput()): (NewOut)s.getOutput());
        }
        
        return convertedSampleSet;
    }
    
    public <NewIn,NewOut> SampleSet<NewIn,NewOut> convert(BatchConverter<In,NewIn> inputconv, BatchConverter<Out,NewOut> outputconv)
    {
        
        SampleSet<NewIn,NewOut> convertedSampleSet =
                new SampleSet<NewIn, NewOut>(
                        inputconv != null ? inputconv.convert(ins)   : (List<NewIn>) ins,
                        outputconv!= null ? outputconv.convert(outs) : (List<NewOut>) outs);
        
        return convertedSampleSet;
    }

    public void uniqueFilter()
    {
        Set<Sample<In,Out>> set = new HashSet<Sample<In,Out>>();
        for (Sample<In,Out> s: this)
        {
            set.add(s);
        }
        
        this.clear();
        
        for (Sample<In,Out> s: set)
        {
            this.add(s);
        }
    }
       
      
    public class SampleIterator implements ListIterator<Sample<In,Out>>
    {
        ListIterator<In> inIT=ins.listIterator();
        ListIterator<Out> outIT=outs.listIterator();


        public boolean hasNext() {
            return inIT.hasNext();
        }

        public Sample<In, Out> next() {
            return new Sample(inIT.next(),outIT.next());
        }

        public void remove() {
            inIT.remove();
            outIT.remove();
        }

        public boolean hasPrevious() {
            return inIT.hasPrevious();
        }

        public Sample<In, Out> previous() {
            return new Sample(inIT.previous(),outIT.previous());
        }

        public int nextIndex() {
            return inIT.nextIndex();
        }

        public int previousIndex() {
            return inIT.previousIndex();
        }

        public void set(Sample<In, Out> e) {
            inIT.set(e.getInput());
            outIT.set(e.getOutput());
        }

        public void add(Sample<In, Out> e) {
            inIT.add(e.getInput());
            outIT.add(e.getOutput());
        }
        
      

    }

}
