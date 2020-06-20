/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diagnosisproject;

import converters.BagOfWordsTransform;
import converters.CounterOutputTransform;
import framework.Classifier;
import framework.adaption.ClassifierAdapter;
import framework.adaption.InputTransform;
import framework.Instantiator;
import framework.adaption.OutputTransform;
import framework.Fac;
import vector.Vector;

public class DiagnosisAdapter extends ClassifierAdapter<String,String> {

    public DiagnosisAdapter(Classifier<Vector,Integer> trcl) {
        super(trcl, new BagOfWordsTransform(), new CounterOutputTransform<String>());
    }
    
    public class Factory extends ClassifierAdapter.Factory<String,Vector,Integer,String>
    {
        public Factory(Fac<Classifier<Vector,Integer>> trclFac)
        {
            super(trclFac,
                    new Instantiator<InputTransform<String, Vector>>(BagOfWordsTransform.class),
                    new Instantiator<OutputTransform<Integer, String>>(CounterOutputTransform.class));
            
        }

    }
    
}
