/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io;

import diagnosisproject.Main;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author qr
 */
public class ObjectPersistance {
    public static Object import_(String path)
    {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        ObjectInputStream ois=null;
        Object result = null;
        try {
            ois = new ObjectInputStream(fis);
            result = ois.readObject();

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally
        {
            try {
                ois.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    public static void export(Serializable ser, String path)
    {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        ObjectOutputStream oos=null;
        try {
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);

        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally
        {
            try {
                oos.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static Object roundTrip(Serializable serializable)
    {
        String path = "C:\\"+Long.toString(Calendar.getInstance().getTimeInMillis());
        export(serializable, path);
        return import_(path);
    }
}
