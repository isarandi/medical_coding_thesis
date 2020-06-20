/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io;

import framework.SampleSet;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;
import converters.IntervalLabeler;
import diagnosisproject.Main;
import framework.adaption.Converter;
import java.nio.channels.FileChannel;

/**
 *
 * @author Jeno
 */
public class FileProcessor {

    private static File sampleDir;
    
    public static SampleSet<String, String> readSampleSetFromFile(String path) {
        Pattern pattern = Pattern.compile("\\$(\\w+?)\\+?\\$ (.+)");
        SampleSet<String, String> results = new SampleSet<String, String>();
        
        sampleDir = new File(path).getParentFile();

        String line = null;
        BufferedReader bufread = null;
        try {
            bufread = new BufferedReader(new InputStreamReader(new FileInputStream(path), "latin2"));
            while ((line = bufread.readLine()) != null) {
                Matcher m = pattern.matcher(line);
                if (m.matches()) {
                    String code = m.group(1);
                    String diagnosis = m.group(2);
                    results.add(diagnosis, code);
                }
            }
            bufread.close();

        } catch (IOException ex) {
            Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }

        return results;
    }

    public static Converter<String, Integer> readIntervalLabelerFromFile(String path) {
        final Pattern pattern = Pattern.compile("(.+?)\\s*-\\s*(.+?)");
        IntervalLabeler<String> labeler = new IntervalLabeler<String>();
        
        String line = null;
        BufferedReader bufread = null;
                
        File inputFile = new File(path);
        if (! inputFile.isAbsolute())
            inputFile = new File(sampleDir, path);
        
        try {
            bufread = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
            while ((line = bufread.readLine()) != null) {
                Matcher m = pattern.matcher(line);
                if (m.matches()) {
                    String from = m.group(1);
                    String to = m.group(2);
                    labeler.addInterval(from, to);
                }
            }
            bufread.close();

        } catch (IOException ex) {
            Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }

        return labeler;

    }

    public static void writeToFile(String text, String path) {
        FileWriter fw = null;
        
        for (int i=0; i<100 && fw == null; ++i)
        {
            try
            {
                fw = new FileWriter(path);
            } catch (IOException ex)
            {
                path+="_";
                fw = null;
                System.out.println("File at "+path+" cannot be overwritten. Adding a _ to the end of the filename and trying again.");
            }
        }
        if (fw != null)
        {
            try {
                fw = new FileWriter(path);
                fw.write(text);
            } catch (IOException ex) {
                Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fw.close();
                } catch (IOException ex) {
                    Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            System.out.println(text);
        }
    }

    public static String readFromFile(String path) {
        BufferedReader reader = null;
        try {
            StringBuilder fileData = new StringBuilder(1000);
            reader = new BufferedReader(new FileReader(path));
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                fileData.append(buf, 0, numRead);
            }
            reader.close();
            return fileData.toString();
        } catch (IOException ex) {
            Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void copyFile(File in, File out) throws IOException {
        FileChannel inChannel = new FileInputStream(in).getChannel();
        FileChannel outChannel = new FileOutputStream(out).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            throw e;
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }
}
