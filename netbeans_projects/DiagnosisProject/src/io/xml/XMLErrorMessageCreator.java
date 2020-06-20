/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xmlprocnstream.XMLUtil;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class XMLErrorMessageCreator {
    public static Document createErrorDocument(String errorMessage)
    {
        Document document = XMLUtil.createDocument();
        Element rootElement = document.createElement("error");
        document.appendChild(rootElement);
        
        Node messageNode = document.createTextNode(errorMessage);
        rootElement.appendChild(messageNode);
        return document;
    }
}
