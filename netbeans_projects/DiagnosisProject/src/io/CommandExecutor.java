/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class CommandExecutor {

    private static void ignoreStream(InputStream is) throws IOException {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        while ((line = br.readLine()) != null) {
            //System.out.println(line);
        }
    }

    public static void execute(String command) {
        
        try {
            Runtime rt = Runtime.getRuntime();
            //System.out.println("Executing: "+command);
            Process proc = rt.exec(command);
            ignoreStream(proc.getInputStream());
            ignoreStream(proc.getErrorStream());
            
            proc.waitFor();
            //System.out.println("- Exectution done: "+command);
        } catch (InterruptedException ex) {
          
            
        } catch (IOException ex) {
            Logger.getLogger(CommandExecutor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
