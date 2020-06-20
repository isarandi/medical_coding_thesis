package xmlprocnstream;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * Exception for a bad data format.
 * @author qrl
 */
public class BadDataFormatException extends Exception {

    public BadDataFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadDataFormatException(String message) {
        super(message);
    }
    
}
