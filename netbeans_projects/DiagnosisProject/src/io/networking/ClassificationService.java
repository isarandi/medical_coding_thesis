/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.networking;

import framework.Classifier;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassificationService
{

    Classifier<String, String> classifier;
    
    private static final int numProcessors = Runtime.getRuntime().availableProcessors();
        
    private static ExecutorService executorService =
            new ThreadPoolExecutor(numProcessors, (int) (numProcessors * 1.5), 10, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public ClassificationService(Classifier<String, String> classifier)
    {
        this.classifier = classifier;
    }

    public void listenSocket(int port)
    {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(ClassificationService.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }

        while (true) {
            ClassificationWorker w;
            try {
                w = new ClassificationWorker(classifier, serverSocket.accept());
                executorService.execute(w);
            } catch (IOException e) {
                System.out.println("Accept failed: "+port);
                System.exit(-1);
            }
        }
    }
}
