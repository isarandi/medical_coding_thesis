package xmlprocnstream;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * Exception for problems with the "sized format"
 * (the size of the content on four bytes big endian, then the content)
 * @author qrl
 */
public class SizedFormatException extends Exception {

    public SizedFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public SizedFormatException(String message) {
        super(message);
    }
     
}
