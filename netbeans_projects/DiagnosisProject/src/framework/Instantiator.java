/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import java.util.logging.Level;
import java.util.logging.Logger;
import framework.Fac;

/**
 *
 * @author qr
 */
public class Instantiator<T> implements Fac<T> {

    private Class cl;

    public Instantiator(Class cl)
    {
        this.cl = cl;
    }

    public Instantiator(T obj)
    {
        this.cl = obj.getClass();
    }

    public T createNew()
    {
        T res = null;

        try
        {
            res = (T) cl.newInstance();
        } catch (InstantiationException ex)
        {
            Logger.getLogger(Instantiator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex)
        {
            Logger.getLogger(Instantiator.class.getName()).log(Level.SEVERE, null, ex);
        }

        return res;
    }

    public static <T> T clone(T obj)
    {
        return new Instantiator<T>(obj).createNew();
    }

    @Override
    public String toString()
    {
        return "Instantiator{" + "cl=" + cl + '}';
    }
}
