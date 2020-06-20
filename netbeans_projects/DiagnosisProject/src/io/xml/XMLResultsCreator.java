/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.xml;

import framework.Result;
import framework.ResultSet;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xmlprocnstream.XMLUtil;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class XMLResultsCreator {


    public static Document createDocumentFromResultSets(List<ResultSet<String>> resultSets)
    {
        Document doc = XMLUtil.createDocument();
        Element rootElement = doc.createElement("resultsets");
        doc.appendChild(rootElement);
        
        for (ResultSet<String> resultSet : resultSets)
        {
            resultSet.normalize();
            
            Element resultSetElement = doc.createElement("resultset");
            
            for (Result<String> r : resultSet) {
                String output = r.getOutput();
                String confidence = Double.toString(r.getConfidence());

                Element resultElement = doc.createElement("result");
                Element confidenceElement = doc.createElement("confidence");
                Element classElement = doc.createElement("class");

                classElement.appendChild(doc.createTextNode(output));
                confidenceElement.appendChild(doc.createTextNode(confidence));

                resultElement.appendChild(classElement);
                resultElement.appendChild(confidenceElement);

                resultSetElement.appendChild(resultElement);
            }
            
            rootElement.appendChild(resultSetElement);
        }
        
        return doc;
        
    }

}
