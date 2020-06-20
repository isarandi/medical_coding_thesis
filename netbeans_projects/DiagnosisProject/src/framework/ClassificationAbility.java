/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package framework;

import java.util.List;

/**
 *
 * @author qr
 */
public interface ClassificationAbility<In,Out>  {
        ResultSet<Out> classify(In input, int limit);
        List<ResultSet<Out>> classifyMany(List<In> input, int limit);
        
        boolean prefersMany();
}
