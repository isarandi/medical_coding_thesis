/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diag;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import xmlprocnstream.XMLParseException;
import xmlprocnstream.XMLUtil;

/**
 * Parses a response from the classification server
 * @author qrl
 */
public class XMLResposeParser {

    /**
     * Parses the response
     * @param document the response document
     * @return the list of results sent by the classification server
     * @throws XMLParseException
     */
    public static List<Result> parse(Document document) throws XMLParseException {
        NodeList resultElements = document.getElementsByTagName("result");
        List<Result> results = new ArrayList<Result>();

        for (int i = 0; i < resultElements.getLength(); ++i) {
            Element resultElement = (Element) resultElements.item(i);
            String code = XMLUtil.getOnlyElementContent(resultElement, "class").getTextContent();
            String confidence = XMLUtil.getOnlyElementContent(resultElement, "confidence").getTextContent();
            
            double confidenceDouble;
            
            try {
                confidenceDouble = Double.parseDouble(confidence);
            } catch (NumberFormatException ex) {
                throw new XMLParseException("Could not parse confidence value as floating point number.", ex);
            }
            DecimalFormat twoPlaces = new DecimalFormat("0.000");
            confidence = twoPlaces.format(confidenceDouble);
            
            results.add(new Result(code, confidence));
        }
        
        return results;
        
    }
}
