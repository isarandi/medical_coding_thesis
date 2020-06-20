/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework.adaption;

import framework.Classifier;
import framework.Instantiator;
import framework.Fac;
import framework.SeparateClassifier;

/**
 *
 * @author qr
 */
public class ClassifierAdapter<In, Out> extends SeparateClassifier<In, Out>
{

    private Classifier<?, ?> innerClassifier;
    private InputTransform<In, ?> inTransf;
    private OutputTransform<?, Out> outTransf;

    public <ThruIn, ThruOut> ClassifierAdapter(Classifier<ThruIn, ThruOut> trcl,
                                               InputTransform<In, ThruIn> inTransf,
                                               OutputTransform<ThruOut, Out> outTransf)
    {
        super(new ClassificationAdapter<In, ThruIn, ThruOut, Out>(trcl, inTransf.getClassifyConverter(), outTransf.getToOuterConverter()),
              new TrainingAdapter<In, ThruIn, ThruOut, Out>(trcl, inTransf.getTrainingConverter(), outTransf.getToInnerConverter()));

        this.inTransf = inTransf;
        this.outTransf = outTransf;
        innerClassifier = trcl;

    }

    public <ThruIn, ThruOut extends Out> ClassifierAdapter(Classifier<ThruIn, ThruOut> trcl,
                                                           InputTransform<In, ThruIn> inTransf)
    {
        super(new ClassificationAdapter<In, ThruIn, ThruOut, Out>(trcl, inTransf.getClassifyConverter(), null),
              new TrainingAdapter<In, ThruIn, ThruOut, Out>(trcl, inTransf.getTrainingConverter(), null));

        this.inTransf = inTransf;
        this.outTransf = null;
        innerClassifier = trcl;
    }

    public <ThruIn extends In, ThruOut> ClassifierAdapter(Classifier<ThruIn, ThruOut> trcl,
                                                          OutputTransform<ThruOut, Out> outTransf)
    {
        super(new ClassificationAdapter<In, ThruIn, ThruOut, Out>(trcl, null, outTransf.getToOuterConverter()),
              new TrainingAdapter<In, ThruIn, ThruOut, Out>(trcl, null, outTransf.getToInnerConverter()));
        this.inTransf = null;
        this.outTransf = outTransf;
        innerClassifier = trcl;
    }

    @Override
    public String toString()
    {
        if (outTransf == null) {
            return "Adap{" + "inner= " + innerClassifier + ", in=" + inTransf + '}';
        } else if (inTransf == null) {
            return "Adap{" + "inner= " + innerClassifier + ", out=" + outTransf + '}';
        } else {
            return "Adap{" + "inner= " + innerClassifier + ", in=" + inTransf + ", out=" + outTransf + '}';
        }
    }

    public static class Factory<In, ThruIn, ThruOut, Out> implements Fac<Classifier<In, Out>>
    {

        private Fac<Classifier<ThruIn, ThruOut>> trclFac;
        private Fac<InputTransform<In, ThruIn>> inTransfFac;
        private Fac<OutputTransform<ThruOut, Out>> outTransfFac;

        public Factory(Fac<Classifier<ThruIn, ThruOut>> trclFac,
                       Fac<InputTransform<In, ThruIn>> inTransfFac,
                       Fac<OutputTransform<ThruOut, Out>> outTransfFac)
        {
            this.trclFac = trclFac;
            this.inTransfFac = inTransfFac;
            this.outTransfFac = outTransfFac;
        }

        public Factory(Fac<Classifier<ThruIn, ThruOut>> trclFac,
                       Class<? extends InputTransform> inTransfClass,
                       Class<? extends OutputTransform> outTransfClass)
        {
            this.trclFac = trclFac;
            this.inTransfFac = new Instantiator<InputTransform<In, ThruIn>>(inTransfClass);
            this.outTransfFac = new Instantiator<OutputTransform<ThruOut, Out>>(outTransfClass);
        }

        public Classifier<In, Out> createNew()
        {
            if (inTransfFac == null) {
                return new ClassifierAdapter(trclFac.createNew(), outTransfFac.createNew());
            } else if (outTransfFac == null) {
                return new ClassifierAdapter(trclFac.createNew(), inTransfFac.createNew());
            } else {
                return new ClassifierAdapter(trclFac.createNew(), inTransfFac.createNew(), outTransfFac.createNew());
            }
        }

        @Override
        public String toString()
        {
            if (outTransfFac == null) {
                return "Adap{" + "inner= " + trclFac + ", in=" + inTransfFac + '}';
            } else if (inTransfFac == null) {
                return "Adap{" + "inner= " + trclFac + ", out=" + outTransfFac + '}';
            } else {
                return "Adap{" + "inner= " + trclFac + ", in=" + inTransfFac + ", out=" + outTransfFac + '}';
            }
        }
    }
}
