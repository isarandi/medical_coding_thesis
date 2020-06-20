package xmlprocnstream;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Provides useful static methods for handling DOM structures
 * @author qrl
 */
public class XMLUtil {

    /**
     * Returns the a given element's immediate children with a given tagname.
     * @param parent the parent element
     * @param tagName the tagname of the children we are looking for
     * @return
     */
    public static List<Element> getChildElementsByTagName(Element parent, String tagName) {
        NodeList all = parent.getChildNodes();
        List<Element> elements = new ArrayList<Element>();
        for (int i = 0; i < all.getLength(); ++i) {

            Node n = all.item(i);
            if (n instanceof Element) {
                Element e = (Element) n;
                if (e.getTagName().equals(tagName)) {
                    elements.add(e);
                }
            }
        }
        return elements;
    }

    /**
     * Returns the first child (the "content") of a single child element with a given tagname in a parent
     * @param parent
     * @param tagName
     * @return
     * @throws XMLParseException if there are more than one child elements with the given tagname
     */
    public static Node getOnlyElementContent(Element parent, String tagName) throws XMLParseException {
        List<Element> nodeList = getChildElementsByTagName(parent,tagName);
        if (nodeList.size() != 1) {
            throw new XMLParseException(String.format("The number of %s elements should have been 1.", tagName));
        }

        Element foundElem = (Element) nodeList.get(0);
        Node n = foundElem.getFirstChild();
        return n;
    }

    /**
     * Read an XML document from a file path
     * @param path
     * @return
     * @throws XMLParseException
     * @throws IOException
     */
    public static Document readDocumentFromFile(String path) throws XMLParseException, IOException
    {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        builderFactory.setValidating(true);
        Document document = null;
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            document = builder.parse(new FileInputStream(path), path);
        } catch (SAXException ex) {
            throw new XMLParseException("",ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XMLUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return document;
    }
    
    /**
     * Creates a new empty Document.
     * @return
     */
    public static Document createDocument()
    {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            return builder.newDocument();
            
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Gets the "simple content of an element", that is
     * if there is an Element child of the argument, then that.
     * otherwise if there are multiple Element children, then the argument itself.
     * otherwise the first child Node of the argument.
     * @param elem
     * @return
     */
    public static Node getSimpleContent(Element elem)
    {
        NodeList nodes = elem.getChildNodes();
        
        if (nodes.getLength()==0)
            return null;
        
        Node foundOneContent = null;
        for (int i=0; i<nodes.getLength(); ++i)
        {
            Node n = nodes.item(i);
            
            if (n.getNodeType() == Node.ELEMENT_NODE)
            {
                if (foundOneContent==null)
                    foundOneContent=n;
                else
                    return elem;
            }
                
        }
        
        if (foundOneContent!=null)        
            return foundOneContent;

        return nodes.item(0);
    }


}
