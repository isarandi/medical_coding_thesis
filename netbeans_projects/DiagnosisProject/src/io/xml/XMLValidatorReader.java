//
///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package io.xml;
//
//import framework.Classifier;
//import framework.Fac;
//import framework.validation.PositionAtLeastMetric;
//import framework.validation.ValidationMetric;
//import java.io.IOException;
//import java.util.List;
//import xmlprocnstream.XMLParseException;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import xmlprocnstream.XMLUtil;
//
///**
// *
// * @author Istvan Sarandi (istvan.sarandi@gmail.com)
// */
//public class XMLValidatorReader {
//    private String path;
//
//    public XMLValidatorReader(String path) {
//        this.path = path;
//    }
//    
//    private ValidationMetric readValidationMetric(Node node) {
//        String text = node.getTextContent().toLowerCase();
//        
//        if (text.equals("first"))
//        {
//            return new PositionAtLeastMetric(1);
//        }
//        return null;
//    }
//    
//    public void parse() throws XMLParseException {
//        Element rootElement;
//        try {
//            rootElement = XMLUtil.readDocumentFromFile(path).getDocumentElement();
//        } catch (IOException ex) {
//            throw new XMLParseException("File not found: "+path, ex);
//        }
//        List<Element> metricElements = XMLUtil.getChildElementsByTagName(rootElement, "validationmetric");
//
//        for (Element classifierElement : classifierElements) {
//            String id = classifierElement.getAttribute("id");
//            Fac<Classifier> fac = readClassifierFac(classifierElement);
//            classifierFacs.put(id, fac);
//        }
//    }
//}
