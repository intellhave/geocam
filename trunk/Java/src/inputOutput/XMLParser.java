package inputOutput;

import java.io.FileOutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSSerializer;

public class XMLParser {
  private static LSParser lsParser;
  private static LSSerializer lsSerializer;
  private static LSOutput lsOutput;
  private static LSInput lsInput;
  private static DOMImplementation impl;
  private static boolean built = false;
  
  private XMLParser() {
  }
  
  private static void buildParser() {
    try {
      // get DOM Implementation using DOM Registry
      System.setProperty(DOMImplementationRegistry.PROPERTY,"org.apache.xerces.dom.DOMXSImplementationSourceImpl");
      DOMImplementationRegistry registry =
          DOMImplementationRegistry.newInstance();

      DOMImplementationLS implLS = 
          (DOMImplementationLS)registry.getDOMImplementation("LS");
      lsParser = implLS.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
      lsSerializer = implLS.createLSSerializer();
      lsOutput = implLS.createLSOutput();
      lsInput = implLS.createLSInput();
      
      impl = registry.getDOMImplementation("XML 3.0");
      built = true;
    }catch (Exception e){
      e.printStackTrace();
    }
  }
  
  public static Document parseDocument(String documentPath) {
    if(!built) {
      buildParser();
    }
    Document document;
    try {
            
      DOMConfiguration domConfig = lsParser.getDomConfig();

      String schemaType = "http://www.w3.org/2001/XMLSchema";
      //domConfig.setParameter("schema-type", schemaType);
      //domConfig.setParameter("schema-location", "../TriangulationSchema.xsd");

            
      document = lsParser.parseURI(documentPath);
    }catch (DOMException e){
      e.printStackTrace();  
      return null;
    }catch (LSException e){
      System.err.println("Error Unable to parse document: " + documentPath);
      return null;
    }catch (Exception e){
      e.printStackTrace();
      return null;
    }
    return document;
  }
  
  public static Document createDocument(String namespaceURI, String qualifiedName) {
    if(!built) {
      buildParser();
    }
    return impl.createDocument(namespaceURI, qualifiedName, null);
  }
  
  public static void writeDocument(Document doc, String path){
    try{      
      TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2" );

        DOMSource source = new DOMSource(doc);
        FileOutputStream stream = new FileOutputStream(path);
        StreamResult result = new StreamResult(stream);  
        transformer.transform(source, result);

        stream.close();
        
      } catch (Exception e) {
        e.printStackTrace();
      }
  }
  
}
