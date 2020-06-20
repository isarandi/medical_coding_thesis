/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.networking;

import framework.Classifier;
import framework.ResultSet;
import io.xml.XMLErrorMessageCreator;

import io.xml.XMLResultsCreator;
import io.xml.XMLRequestParser;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import xmlprocnstream.BadDataFormatException;
import xmlprocnstream.XMLParseException;
import xmlprocnstream.XMLStreaming;


/**
 *
 * @author Istvan Sarandi (istvan.sarandi@gmail.com)
 */
public class ClassificationWorker implements Runnable {

    Classifier<String, String> classifier;
    Socket socket;

    public ClassificationWorker(Classifier<String, String> classifier, Socket socket) {
        this.classifier = classifier;
        this.socket = socket;
    }
    
    private void answerWithErrorMessage(String message) throws IOException
    {
        try {
            XMLStreaming.send(socket.getOutputStream(), XMLErrorMessageCreator.createErrorDocument(message));
        } catch (IOException ex) {
            throw new IOException("XML error message could not be sent to client.", ex);
        }
    }

    private void handleRequest(Document requestDocument) throws IOException {
        XMLRequestParser req = new XMLRequestParser();
        try {
            req.parse(requestDocument);
        } catch (XMLParseException ex) {
            answerWithErrorMessage("The request XML could not be parsed: "+ex.getMessage());
            return;
        }
        
        int limit = req.getLimit();
        List<String> inputs = req.getInputs();
        
        List<ResultSet<String>> classifyMany = classifier.classifyMany(inputs, limit);
        Document answerDocument = XMLResultsCreator.createDocumentFromResultSets(classifyMany);

        try {
            XMLStreaming.send(socket.getOutputStream(), answerDocument);
        } catch (IOException ex) {
            throw new IOException("XML classification results could not be sent to client.", ex);
        }
    }

    public void run() {
        try {
            Document document;
            
            try {
                document = XMLStreaming.receive(socket.getInputStream());
            } catch (IOException ex) {
                throw new IOException("Error receiving input.", ex);
            } catch (BadDataFormatException ex) {
                answerWithErrorMessage(ex.getMessage());
                return;
            }
            
            String rootName = document.getDocumentElement().getTagName();

            if (rootName.equals("request")) {
                handleRequest(document);
            }
            
            socket.close();
            
        } catch (IOException ex) {
            Logger.getLogger(ClassificationWorker.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return;
        }


    }
}
