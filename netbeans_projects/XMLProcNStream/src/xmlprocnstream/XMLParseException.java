package xmlprocnstream;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * Exception for problems while parsing an XML document
 * @author qrl
 */
public class XMLParseException extends Exception {

    public XMLParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public XMLParseException(String message) {
        super(message);
    }
    
}
