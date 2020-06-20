/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.xml;

import io.FileProcessor;

import classifiers.team.MixtureOfExpertsClassifier;
import classifiers.bayes.LaplacianNaiveBayesClassifier;
import classifiers.neuralnetwork.BackpropLayerTrainer;
import classifiers.neuralnetwork.DecreaseRatioLearningMonitor;
import classifiers.neuralnetwork.ErrorCalculator;
import classifiers.neuralnetwork.Layer;
import classifiers.neuralnetwork.LayerTrainer;
import classifiers.neuralnetwork.LearningMonitor;
import classifiers.neuralnetwork.LogLikelihoodErrorCalculator;
import classifiers.neuralnetwork.MLP;
import classifiers.neuralnetwork.MLPClassifier;
import classifiers.neuralnetwork.MLPTraining;
import classifiers.neuralnetwork.SquareErrorCalculator;
import classifiers.neuralnetwork.EvaluationErrorCalculator;

import classifiers.svm.LibLinear;
import classifiers.svm.MulticlassSVM;
import classifiers.team.IdealTeam;
import classifiers.team.ConstantWeightTeam;
import classifiers.vectorspace.VectorSpaceClassifier;

import converters.BagOfWordsTransform;
import converters.CounterOutputTransform;
import converters.IDFTransform;
import converters.VectorCompressor;

import framework.Classifier;
import framework.adaption.ClassifierAdapter;
import framework.adaption.ConcatInputTransform;
import framework.adaption.Converter;
import framework.adaption.InputTransform;
import framework.Instantiator;
import framework.adaption.OutputTransform;

import java.io.IOException;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import framework.Fac;
import framework.evaluation.PositionAtMostMetric;
import framework.evaluation.PositionWeightedMetric;
import framework.evaluation.EvaluationMetric;
import xmlprocnstream.XMLParseException;
import xmlprocnstream.XMLUtil;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class XMLClassifierReader
{

    private Map<String, Fac<Classifier>> classifierFacs = new TreeMap<String, Fac<Classifier>>();
    private String path;
    
    public static Classifier readClassifier(String path) throws XMLParseException
    {
        XMLClassifierReader reader = new XMLClassifierReader(path);
        reader.parse();
        return reader.getMainClassifier();
    }

    public XMLClassifierReader(String path)
    {
        this.path = path;
    }

    private Fac<Classifier> readClassifierFac(Element element) throws XMLParseException
    {

        Fac<Classifier> fac = null;

        if (element.hasAttribute("ref")) {
            fac = classifierFacs.get(element.getAttribute("ref"));
        } else if (element.hasAttribute("file")) {
            XMLClassifierReader otherClassifierReader = new XMLClassifierReader(element.getAttribute("file"));
            otherClassifierReader.parse();
            fac = otherClassifierReader.getMainClassifierFactory();
        } else {
            String type = XMLUtil.getOnlyElementContent(element, "type").getTextContent().toLowerCase();
            Map<String, Node> params = getParams(element);

            if (type.equals("bayes")) {
                fac = readNaiveBayesClassifierFac(params);
            } else if (type.equals("vectorspace")) {
                fac = new Instantiator<Classifier>(VectorSpaceClassifier.class);
            } else if (type.equals("hierarchic")) {
                fac = readHierarchicFac(params);
            } else if (type.equals("mlp")) {
                fac = readMLPClassifierFac(params);
            } else if (type.equals("msvm")) {
                fac = readMSVMClassifierFac(params);
            } else if (type.equals("liblinear")) {
                fac = readLibLinearClassifierFac(params);
            } else if (type.equals("constantweightteam")) {
                fac = readConstantWeightTeamFac(params);
            } else if (type.equals("cheatingteam")) {
                fac = readCheatingTeam(params);
                //        } else if (type.equals("expertiseestimation")) {
                //            fac = readExpertise(params);
            }
        }

        Fac<InputTransform> inputTransformFac = readFullInputTransformFac(element);
        Fac<OutputTransform> outputTransformFac = readOutputTransformFac(element);


        if (inputTransformFac != null || outputTransformFac != null) {
            return new ClassifierAdapter.Factory(fac, inputTransformFac, outputTransformFac);
        } else {
            return fac;
        }
    }

    public void parse() throws XMLParseException
    {
        Element rootElement;
        try {
            rootElement = XMLUtil.readDocumentFromFile(path).getDocumentElement();
        } catch (IOException ex) {
            throw new XMLParseException("File not found: " + path, ex);
        }
        List<Element> classifierElements = XMLUtil.getChildElementsByTagName(rootElement, "classifier");

        for (Element classifierElement : classifierElements) {
            String id = classifierElement.getAttribute("id");
            Fac<Classifier> fac = readClassifierFac(classifierElement);
            classifierFacs.put(id, fac);
        }
    }

    public Fac<Classifier> getMainClassifierFactory()
    {
        return classifierFacs.get("main");
    }

    public Classifier getMainClassifier()
    {
        return getMainClassifierFactory().createNew();
    }

    private Fac<Classifier> readHierarchicFac(Map<String, Node> params) throws XMLParseException
    {

        Fac<Classifier> rootFac = readClassifierFac((Element) params.get("root"));
        Fac<Classifier> childFac = readClassifierFac((Element) params.get("child"));

        Converter childSelector = readConverter((Element) params.get("childselector"));
        Fac<Classifier> hierFac = new MixtureOfExpertsClassifier.Factory(childSelector, rootFac, childFac);
        return hierFac;
    }

    private Fac<InputTransform> readFullInputTransformFac(Element elem)
    {
        List<Element> inputTransformElements = XMLUtil.getChildElementsByTagName(elem, "inputtransform");
        if (inputTransformElements.isEmpty()) {
            return null;
        }
        if (inputTransformElements.size() == 1) {
            return readOneInputTransformFac(inputTransformElements.get(0));
        } else {
            Fac<InputTransform>[] transforms = new Fac[inputTransformElements.size()];
            int i = 0;
            for (Element e : inputTransformElements) {
                transforms[i] = readOneInputTransformFac(e);
                ++i;
            }
            return new ConcatInputTransform.Factory(transforms);
        }
    }

    private Fac<InputTransform> readOneInputTransformFac(Node inputtrNode)
    {

        String text = inputtrNode.getTextContent().toLowerCase();
        if (text.equals("bagofwords")) {
            return new Instantiator<InputTransform>(BagOfWordsTransform.class);
        } else if (text.equals("idf")) {
            return new Instantiator<InputTransform>(IDFTransform.class);
        } else if (text.equals("compress")) {
            return new Instantiator<InputTransform>(VectorCompressor.class);
        }
        return null;
    }

    private Fac<OutputTransform> readOutputTransformFac(Element elem)
    {
        List<Element> outputtrNodes = XMLUtil.getChildElementsByTagName(elem, "outputtransform");
        if (outputtrNodes.isEmpty()) {
            return null;
        }

        Node outputtrNode = outputtrNodes.get(0);
        String text = outputtrNode.getTextContent().toLowerCase();
        if (text.equals("counter")) {
            return new Instantiator<OutputTransform>(CounterOutputTransform.class);
        }
        return null;
    }

    private Converter readConverter(Element element) throws XMLParseException
    {
        String type = XMLUtil.getOnlyElementContent(element, "type").getTextContent().toLowerCase();

        Map<String, Node> params = getParams(element);

        if (type.equals("interval")) {
            return readIntervalLabeler(params);
        }
        return null;
    }

    private Converter readIntervalLabeler(Map<String, Node> params)
    {
        String filepath = params.get("file").getTextContent();
        return FileProcessor.readIntervalLabelerFromFile(filepath);
    }

    protected Map<String, Node> getParams(Element rootElement)
    {
        Map<String, Node> map = new HashMap<String, Node>();
        List<Element> paramElements = XMLUtil.getChildElementsByTagName(rootElement, "param");

        for (Element e : paramElements) {
            String key = e.getAttribute("name");
            Node innerNode = XMLUtil.getSimpleContent(e);
            map.put(key.toLowerCase(), innerNode);
        }
        return map;
    }

    private Fac<Classifier> readMLPClassifierFac(Map<String, Node> params) throws XMLParseException
    {
        double learnRate = Double.parseDouble(params.get("learnrate").getTextContent());
        int maxEpochs = Integer.parseInt(params.get("maxepochs").getTextContent());

        MLPTraining trainer;
        Fac<LayerTrainer> layerTrainerFac = new BackpropLayerTrainer.Factory(learnRate,1.0);


        if (params.containsKey("splitratio")) {
            double splitRatio = Double.parseDouble(params.get("splitratio").getTextContent());
            LearningMonitor lm = readLearningMonitor(params.get("learningmonitor"));
            ErrorCalculator ec = readErrorCalculator(params.get("errorcalculator"));

            ErrorCalculator mec = ec;
            if (params.containsKey("monitorerrorcalculator")) {
                mec = readErrorCalculator(params.get("monitorerrorcalculator"));
            }

            trainer = new MLPTraining(maxEpochs, splitRatio, layerTrainerFac, lm, ec, mec, false);
        } else {
            trainer = new MLPTraining(maxEpochs, layerTrainerFac, new LogLikelihoodErrorCalculator());
        }

        MLP mlp = new MLP(trainer, new Layer(MLP.LOGSIG));

        MLPClassifier mlpc = new MLPClassifier(mlp);

        return (Fac<Classifier>) (Object) mlpc.getCloningFactory();
    }

    private Fac<Classifier> readConstantWeightTeamFac(Map<String, Node> params) throws XMLParseException
    {
        Fac<Classifier> c1fac = readClassifierFac((Element) params.get("c1"));
        Fac<Classifier> c2fac = readClassifierFac((Element) params.get("c2"));
        double ratio = Double.parseDouble(params.get("ratio").getTextContent());

        return new ConstantWeightTeam.Factory(c1fac, c2fac, ratio);
    }

    private LearningMonitor readLearningMonitor(Node node) throws XMLParseException
    {

        LearningMonitor lm = null;
        if (node.getNodeType() == Node.ELEMENT_NODE) {

            Element element = (Element) node;
            String type = XMLUtil.getOnlyElementContent(element, "type").getTextContent().toLowerCase();
            Map<String, Node> params = getParams(element);

            if (type.equals("decreaseratio")) {
                lm = readDecreaseRatioLearningMonitor(params);
            }
        }

        return lm;
    }

    private LearningMonitor readDecreaseRatioLearningMonitor(Map<String, Node> params)
    {
        double ratio = Double.parseDouble(params.get("ratio").getTextContent());
        int windowSize = Integer.parseInt(params.get("windowsize").getTextContent());

        return new DecreaseRatioLearningMonitor(windowSize, ratio);
    }

    private ErrorCalculator readErrorCalculator(Node node) throws XMLParseException
    {
        ErrorCalculator ec = null;
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            String type = XMLUtil.getOnlyElementContent(element, "type").getTextContent().toLowerCase();
            Map<String, Node> params = getParams(element);

            if (type.equals("validation")) {
                ec = readValidationErrorCalculator(params);
            }
        } else {
            String errorCalculator = node.getTextContent().toLowerCase();
            if (errorCalculator.equals("squareerror")) {
                ec = new SquareErrorCalculator();
            } else if (errorCalculator.equals("loglikelihood")) {
                ec = new LogLikelihoodErrorCalculator();
            }
        }
        return ec;
    }

    private ErrorCalculator readValidationErrorCalculator(Map<String, Node> params)
    {
        EvaluationMetric vm = readValidationMetric(params.get("validationmetric"));
        return new EvaluationErrorCalculator(vm);
    }

    private EvaluationMetric readValidationMetric(Node node)
    {
        String text = node.getTextContent().toLowerCase();

        if (text.equals("first")) {
            return new PositionAtMostMetric(1);
        } else if (text.startsWith("weight")) {
            return new PositionWeightedMetric(Integer.parseInt(text.substring("weight".length())));
        } else {
            return new PositionAtMostMetric(Integer.parseInt(text));
        }

    }

    private Fac<Classifier> readCheatingTeam(Map<String, Node> params) throws XMLParseException
    {
        Fac<Classifier> c1fac = readClassifierFac((Element) params.get("c1"));
        Fac<Classifier> c2fac = readClassifierFac((Element) params.get("c2"));

        return new IdealTeam.Factory(c1fac, c2fac, 1.0);
    }

    private Fac<Classifier> readMSVMClassifierFac(Map<String, Node> params)
    {
        double c = Double.parseDouble(params.get("c").getTextContent());
        return (Fac<Classifier>) (Object) new MulticlassSVM.Factory(c);
    }

    private Fac<Classifier> readLibLinearClassifierFac(Map<String, Node> params)
    {
        double c = Double.parseDouble(params.get("c").getTextContent());
        return (Fac<Classifier>) (Object) new LibLinear.Factory(c);
    }

    private Fac<Classifier> readNaiveBayesClassifierFac(Map<String, Node> params)
    {
        if (params.containsKey("smooth")) {
            double smooth = Double.parseDouble(params.get("smooth").getTextContent());
            return new LaplacianNaiveBayesClassifier.Factory(smooth);
        }
        return new LaplacianNaiveBayesClassifier.Factory();

    }
}
