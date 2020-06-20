/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diag;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xmlprocnstream.XMLUtil;

/**
 * Creates a request XML document to be sent to the classification server
 * @author qrl
 */
public class XMLRequestCreator {
    
    /**
     *
     * @param input the input text (diagnosis) to be classified
     * @param limit how many results are expected at most
     * @return
     */
    public static Document createRequest(String input, int limit)
    {
        Document doc = XMLUtil.createDocument();
        Element rootElement = doc.createElement("request");
        doc.appendChild(rootElement);
        
        Element inputsElement = doc.createElement("inputs");
        rootElement.appendChild(inputsElement);
        
        Element inputElement = doc.createElement("input");
        inputsElement.appendChild(inputElement);
        
        Node inputTextNode = doc.createTextNode(input);
        inputElement.appendChild(inputTextNode);
        
        Element resultcountElement = doc.createElement("resultcount");
        rootElement.appendChild(resultcountElement);
        
        Node resultcountTextNode = doc.createTextNode(Integer.toString(limit));
        resultcountElement.appendChild(resultcountTextNode);
        
        return doc;
        
    }
}
