/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diag;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import xmlprocnstream.BadDataFormatException;
import xmlprocnstream.XMLParseException;
import xmlprocnstream.XMLStreaming;

/**
 * An object that makes the communication with the classification server possible
 * @author qrl
 */
public class Communicator {

    private String host;
    private int port;
    private String errorMessage = null;
    private List<Result> results;

    /**
     * Constructor
     * @param host the remote host of the classification server
     * @param port the remote port of the classification server
     */
    public Communicator(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Gets the error message, after an unsuccessful communication.
     * @return
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Gets the results after a successful communication
     * @return
     */
    public List<Result> getResults() {
        return results;
    }


    private void closeSocket(Socket s) {
        try {
            s.close();
        } catch (IOException ex) {
            Logger.getLogger(Communicator.class.getName()).log(Level.SEVERE, "Could not close socket with classification server", ex);
        }
    }

    /**
     * Classifies a diagnosis by requesting at the classification server
     * @param diagnosis the input of the classification
     * @param limit the maximum number of results wanted
     * @return
     */
    public boolean remoteClassify(String diagnosis, int limit) {
        Document requestDocument = XMLRequestCreator.createRequest(diagnosis, limit);

        Socket socket = null;
        try {
            socket = new Socket(host, port);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Communicator.class.getName()).log(Level.SEVERE, "Could not find classification server.", ex);
            errorMessage = "Nem sikerült elérni a kódolást végző kiszolgálót.";
            return false;
        } catch (IOException ex) {
            Logger.getLogger(Communicator.class.getName()).log(Level.SEVERE,  "Could not find classification server.", ex);
            errorMessage = "Nem sikerült elérni a kódolást végző kiszolgálót.";
            return false;
        }
        
        try {
            XMLStreaming.send(socket.getOutputStream(), requestDocument);
        } catch (IOException ex) {
            Logger.getLogger(Communicator.class.getName()).log(Level.SEVERE,  "Could not find classification server.", ex);
            errorMessage = "Nem sikerült elküldeni a kérést a kódolást végző kiszolgálónak.";
            closeSocket(socket);
            return false;
        }


        Document responseDocument;
        try {
            responseDocument = XMLStreaming.receive(socket.getInputStream());
        } catch (BadDataFormatException ex) {
            Logger.getLogger(Communicator.class.getName()).log(Level.SEVERE, "Bad formatted response from classification server.", ex);
            errorMessage = "Nem sikerült fogadni a kódolást végző kiszolgáló válaszát.";
            return false;
        } catch (IOException ex) {
            Logger.getLogger(Communicator.class.getName()).log(Level.SEVERE, "Could not receive response from classification server.", ex);
            errorMessage = "Nem sikerült fogadni a kódolást végző kiszolgáló válaszát.";
            return false;
        } finally {
            closeSocket(socket);
        }

        String rootTagName = responseDocument.getDocumentElement().getTagName();

        if (rootTagName.equals("resultsets")) {
            handleResultResponse(responseDocument);
            return true;
        } else if (rootTagName.equals("error")) {
            handleErrorResponse(requestDocument);
            errorMessage="Sikertelen párbeszéd a kódolást végző kiszolgálóval.";
            return false;
        } else {
            errorMessage="Sikertelen párbeszéd a kódolást végző kiszolgálóval.";
            return false;
        }
       

    }

    private void handleResultResponse(Document responseDocument) {
        try {
            results = XMLResposeParser.parse(responseDocument);
        } catch (XMLParseException ex) {
            Logger.getLogger(Communicator.class.getName()).log(Level.SEVERE, "Bad formatted response from classification server.", ex);
            errorMessage = "Nem sikerült fogadni a kódolást végző kiszolgáló válaszát.";
        }
    }

    private void handleErrorResponse(Document requestDocument) {
        String message = requestDocument.getDocumentElement().getTextContent();
        Logger.getLogger(Communicator.class.getName()).log(Level.SEVERE, "Error message received from classification server: {0}", message);

    }
}
