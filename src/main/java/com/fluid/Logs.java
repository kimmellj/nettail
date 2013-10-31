package main.java.com.fluid;

import com.sun.org.apache.xerces.internal.dom.DeferredDocumentImpl;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * This class facilitates the management of Logs
 *
 * @author James Kimmell <jkimmell@fluid.com>
 */
public class Logs {

    /**
     * Favorite for this set of logs
     */
    private Favorite favorite;
    /**
     * Actual logs contained in this directory
     */
    private ArrayList<Log> logs;
    
    /**
     * A list model to keep in sync with the list of logs
     */
    private DefaultListModel listModel;

    /**
     * Constructor
     *
     * Initialize the list of logs
     */
    public Logs() {
        this.logs = new ArrayList<>();
    }

    /**
     * Get the List Model being used
     *
     * @return
     */
    public DefaultListModel getListModel() {
        return listModel;
    }

    /**
     * Set the list model to be used
     *
     * @param listModel
     */
    public void setListModel(DefaultListModel listModel) {
        this.listModel = listModel;
    }

    /**
     * Get the current list of logs
     *
     * @return
     */
    public ArrayList<Log> getLogs() {
        return logs;
    }

    /**
     * Set the favorite to be used
     *
     * @param favorite
     */
    public void setFavorite(Favorite favorite) {
        this.favorite = favorite;
    }

    /**
     * Get a specific log file by index
     *
     * @param index
     * @return
     */
    public Log getLog(int index) {
        return this.logs.get(index);
    }

    /**
     * Build the list of logs, using either HTTP or WebDav
     */
    public void buildLogs() {
        this.listModel.removeAllElements();

        this.logs = new ArrayList();
        
        if (this.favorite.isWebdav()) {
            this.buildLogsWebDav();
        } else {
            this.buildLogsHttp();
        }
    }

    /**
     * Build the list of log files based on the Favorite using the http protocol
     *
     * This will look at the list of files in a directory and parse out the
     * links that end in a .txt or .log
     */
    public void buildLogsHttp() {
        RemoteRequest request = new RemoteRequest(
                this.favorite.getProtocol(),
                this.favorite.getDomain(),
                this.favorite.getPort(),
                this.favorite.getUsername(),
                this.favorite.getPassword()
        );

        /**
         * Perform an regular expression to look for all links that contain
         * .txt or .log
         */
        String content = request.getContent(this.favorite.getPath(), 0);

        Pattern r = Pattern.compile("\\<a href=\"(.*?)\"\\>");
        Matcher m = r.matcher(content);

        while (m.find()) {
            String link = m.group(0).replace("<a href=\"", "").replace("\">", "");

            if (link.contains(".log") || link.contains(".txt")) {
                Log log = new Log();
                log.setPath("/" + link);
                log.setFavorite(this.favorite);
                this.logs.add(log);
            }
        }

        this.addLogsToList();
    }

    /**
     * Build the list of log files based on the Favorite using the WebDav protocol
     *
     * This will look at the list of files in a directory and parse out the
     * links that end in a .txt or .log
     */    
    public void buildLogsWebDav() {
        RemoteRequest request = new RemoteRequest(
                this.favorite.getProtocol(),
                this.favorite.getDomain(),
                this.favorite.getPort(),
                this.favorite.getUsername(),
                this.favorite.getPassword()
        );

        String content = request.getWebDavContent(this.favorite.getPath());

        /**
         * Use XML to parse the HTML response
         * 
         * We're looking for links that end in .log or .txt
         */
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            DeferredDocumentImpl deferredDoc = (DeferredDocumentImpl) docBuilder.parse(new InputSource(new ByteArrayInputStream(content.getBytes("utf-8"))));

            Element doc = deferredDoc.getDocumentElement();
            NodeList multistatusElements = deferredDoc.getElementsByTagName("multistatus");
            NodeList responsesNodeList = multistatusElements.item(0).getChildNodes();

            for (int i = 0; i < responsesNodeList.getLength(); i++) {
                Node responseNode = responsesNodeList.item(i);
                if (responseNode.getNodeName().equals("response")) {
                    NodeList responseChildrenNodeList = responseNode.getChildNodes();

                    Node href = responseChildrenNodeList.item(0);
                    Node propStat = responseChildrenNodeList.item(2);

                    String link = href.getTextContent();

                    if (link.contains(".log") || link.contains(".txt")) {
                        Log log = new Log();
                        log.setPath(link);
                        log.setFavorite(this.favorite);
                        log.setLastModified(propStat.getFirstChild().getFirstChild().getTextContent());
                        this.logs.add(log);
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {
            System.out.println("There was an issue parsing the XML document");
            System.out.println(e.getMessage());
        }

        Collections.sort(this.logs, new CustomComparator());

        this.addLogsToList();
    }

    /**
     * Update the list model with the current list of log files
     */
    public void addLogsToList() {
        this.listModel.removeAllElements();
        
        /**
         * Add all of the multistatusElements to the list We do this separately
         * so that we can sort the list first
         */
        Iterator<Log> it = this.logs.iterator();

        while (it.hasNext()) {
            Log log = it.next();

            this.listModel.addElement(log);
        }
    }

    /**
     * Represent this list of logs as a string
     *
     * @return
     */
    @Override
    public String toString() {
        String returnString = "";

        Iterator<Log> it = this.logs.iterator();

        while (it.hasNext()) {
            Log log = it.next();
            returnString += log.toString() + "\n";
        }

        return returnString;
    }
}

/**
 * This comparator is used to sort a list of log files by their time stamp descending
 * 
 * @author James Kimmell <jkimmell@fluid.com>
 */
class CustomComparator implements Comparator<Log> {

    @Override
    public int compare(Log o1, Log o2) {
        if (o1.getLastModifiedTimeStamp() == o2.getLastModifiedTimeStamp()) {
            return 0;
        }

        return o1.getLastModifiedTimeStamp() > o2.getLastModifiedTimeStamp() ? -1 : 1;
    }
}