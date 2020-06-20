/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.xml;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xmlprocnstream.XMLParseException;
import xmlprocnstream.XMLUtil;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class XMLRequestParser {

    List<String> inputs = null;
    int limit = -1;

    public List<String> getInputs() {
        return inputs;
    }

    public int getLimit() {
        return limit;
    }

    public void parse(Document document) throws XMLParseException {
        inputs = new ArrayList<String>();
        
        Element documentElement = document.getDocumentElement();
        
        List<Element> inputSets = XMLUtil.getChildElementsByTagName(documentElement, "inputs");
        if (inputSets.size() != 1) {
            throw new XMLParseException("The number of 'inputs' elements needs to be 1.");
        }
        
        List<Element> inputElements = XMLUtil.getChildElementsByTagName(inputSets.get(0), "input");
        for (Element inputElement: inputElements)
        {
            String inputString = XMLUtil.getSimpleContent(inputElement).getTextContent();
            inputs.add(inputString);
        }
        
        String resultCountString = XMLUtil.getOnlyElementContent(documentElement, "resultcount").getTextContent();
        
        limit = Integer.parseInt(resultCountString);
        
    }

    public XMLRequestParser() {  }
}
