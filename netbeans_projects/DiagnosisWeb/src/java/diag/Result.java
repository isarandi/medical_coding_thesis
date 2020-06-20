/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diag;

/**
 * A result of classification with the corresponding confidence
 * @author qrl
 */
public class Result {
    /**
     * the code
     */
    public String code;
    /**
     * the confidence
     */
    public String confidence;

    /**
     *
     * @param code
     * @param confidence
     */
    public Result(String code, String confidence) {
        this.code = code;
        this.confidence = confidence;
    }
}
