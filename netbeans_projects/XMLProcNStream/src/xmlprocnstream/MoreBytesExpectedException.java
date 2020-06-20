package xmlprocnstream;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * Exception for cases when a reader would expect more bytes, but the stream
 * has no more.
 * @author qrl
 */
public class MoreBytesExpectedException extends Exception {

    private int actual;
    private int expected;

    /**
     * The number of acual bytes found.
     * @return
     */
    public int getActual() {
        return actual;
    }

    /**
     * The number of expected bytes.
     * @return
     */
    public int getExpected() {
        return expected;
    }
    
    /**
     * 
     * @param actual
     * @param expected should be larger than actual
     */
    public MoreBytesExpectedException(int actual, int expected) {
        this.actual = actual;
        this.expected = expected;
    }

    
}
