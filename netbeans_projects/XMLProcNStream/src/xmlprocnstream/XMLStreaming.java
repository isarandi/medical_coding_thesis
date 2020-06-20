package xmlprocnstream;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Provides methods for sending and receiving XML Documents in streams.
 * @author qrl
 */
public class XMLStreaming {

    private static int bigEndianBytesToInteger(byte[] arr) {
        int integer = 0;
        int shift = 0;
        for (int i = 3; i >= 0; --i) {
            integer |= unsignedByteToInt(arr[i]) << shift;
            shift += 8;
        }
        return integer;
    }
    
    private static byte[] integerToBigEndianArray(int integer) {
        byte[] arr = new byte[4];

        for (int i = arr.length - 1; i >= 0; --i) {
            byte b = (byte) (integer % 256);
            integer >>= 8;
            arr[i] = b;
        }
        return arr;

    }

    private static int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }

    private static byte[] readGivenNumberOfBytes(InputStream is, int numOfBytes) throws MoreBytesExpectedException, IOException {
        byte[] arr = new byte[numOfBytes];

        int readByte;
        for (int i = 0; i < numOfBytes; ++i) {
            readByte = is.read();

            if (readByte == -1) {
                throw new MoreBytesExpectedException(i, numOfBytes);
            }
            arr[i] = (byte) readByte;
        }
        return arr;
    }

    private static InputStream getSizedInputStream(InputStream is) throws IOException, SizedFormatException {
        byte[] bigEndianLength;
        
        try {
            bigEndianLength = readGivenNumberOfBytes(is, 4);
        } catch (MoreBytesExpectedException ex) {
            throw new SizedFormatException("Length could not be read.", ex);
        }
        int length = bigEndianBytesToInteger(bigEndianLength);

        byte[] data;
        try {
            data = readGivenNumberOfBytes(is, length);
        } catch (MoreBytesExpectedException ex) {
            throw new SizedFormatException("Data too short.", ex);
        }
        
        //System.out.println(new String(data));
        
        return new ByteArrayInputStream(data);
    }

    /**
     * Receives a Document from an input stream. The first four bytes are the size (number of bytes)
     * of the document (without these four bytes).
     * @param is
     * @return
     * @throws IOException
     * @throws BadDataFormatException
     */
    public static Document receive(InputStream is) throws IOException, BadDataFormatException {

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        

        Document document = null;
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            document = builder.parse(getSizedInputStream(is));
        } catch (SAXException ex) {
            throw new BadDataFormatException("XML could not be parsed. Cause: "+ex.getMessage(), ex);
        } catch (SizedFormatException ex) {
            throw new BadDataFormatException("Data size not properly set.", ex);
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }

        return document;
    }

    /**
     * Sends a Document to an output stream. The first four bytes sent are the size (number of bytes)
     * of the document (without these four bytes).
     * @param os
     * @param document
     * @throws IOException
     */
    public static void send(OutputStream os, Document document) throws IOException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", 2);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(baos);
            transformer.transform(source, result);
            
        } catch (TransformerException ex) {
            throw new RuntimeException(ex);
        }
        
        byte[] xmlBytes = baos.toByteArray();
        os.write(integerToBigEndianArray(xmlBytes.length));
        //System.out.write(xmlBytes);
        os.write(xmlBytes);

    }
}
