package main.java.com.fluid.webdav;

import com.sun.org.apache.xerces.internal.dom.DeferredDocumentImpl;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import main.java.com.fluid.Favorite;
import main.java.com.fluid.LogListDateComparator;
import main.java.com.fluid.Log;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author James Kimmell <jkimmell@fluid.com>
 */
public class Logs extends main.java.com.fluid.Logs {
    
    /**
     * Set the favorite to be used
     * 
     * Make sure the url has a trailing slash because webdav get's weird about it
     *
     * @param favorite
     */
    @Override
    public void setFavorite(Favorite favorite) {
        this.favorite = favorite;
        
        if (!this.favorite.getBaseUrl().endsWith("/")) {
            try {
                this.favorite.setBaseUrl(this.favorite.getBaseUrl() + "/");
            } catch (Exception e) {
                System.out.println("Error Setting Base URL: "+e.getMessage());
                System.out.println(this.favorite.getBaseUrl() + "/");
            }
        }
    }    
    
    /**
     * Build up the WebDav connection with the settings from the favorite
     */
    @Override
    public void setConnection() {
        this.connection = new Connection();
        this.connection.addCredentials(this.getFavorite().getUsername(), this.getFavorite().getPassword());
        this.connection.connect(this.getFavorite().getProtocol(), this.getFavorite().getDomain(), this.getFavorite().getPort());
    }
    
    /**
     * Build up the Log Files for the base url of the favorite for this instance
     * 
     * This will request a list of files that contain a last modified date
     * from the getFileList method, build a Log instance for each file,
     * append the log to the list attribute, sort the list and refresh the the
     * list model.
     * 
     * @return 
     */
    @Override
    public boolean buildLogs() {
        Connection webDavConnection = (Connection)this.getConnection();
        ArrayList<HashMap> listOfFiles = this.getFileList(webDavConnection.getProperties(this.getFavorite().getPath()));
        
        for (HashMap<String, String> logFile : listOfFiles) {
            Log log = new Log(this.getConnection(), this.getFavorite());
            log.setPath("/" + logFile.get("link"));
            log.setLastModified(logFile.get("date"));
            log.setFavorite(this.getFavorite());
            this.logs.add(log);
        }
        
        Collections.sort(this.logs, new LogListDateComparator());
        this.refreshList();
        
        return true;
    }
    
    /**
     * This method will parse the XML returned by getProperties by using
     * XPath expressions to get a list of log files and their last modified 
     * dates.
     *
     * @param content
     * @return 
     */
    public ArrayList<HashMap> getFileList(String content) {
        ArrayList<HashMap> logFiles = new ArrayList<>();
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DeferredDocumentImpl doc = (DeferredDocumentImpl) builder.parse(new InputSource(new ByteArrayInputStream(content.getBytes("utf-8"))));

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile("/multistatus/response/href");
            NodeList n1 = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);

            XPathExpression expr2 = xpath.compile("/multistatus/response/propstat/prop/getlastmodified");
            NodeList n2 = (NodeList)expr2.evaluate(doc, XPathConstants.NODESET);      

            for (int i = 0; i < n1.getLength(); i++) {
                Node node = n1.item(i);
                String link = node.getTextContent();

                if (link.contains(".log") || link.contains(".txt")) {
                    HashMap hash = new HashMap();
                    hash.put("link", link);
                    hash.put("date", n2.item(i).getTextContent());

                    logFiles.add(hash);
                }            
            }
        } catch (ParserConfigurationException | SAXException | IOException | DOMException | XPathExpressionException e) {
            System.out.println("There was an issue parsing the XML document");
            System.out.println(e.getMessage());
            System.out.println(content);
        }
        
        return logFiles;
    }  
}
