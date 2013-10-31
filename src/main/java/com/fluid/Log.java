package main.java.com.fluid;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.JTextArea;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

/**
 * This class represents a log file
 *
 * It is meant to be ran as a sepeartely thread that will continue to update a
 * textarea with the contents of the log file
 *
 * @author James Kimmell <jkimmell@fluid.com>
 */
public class Log implements Runnable {

    /**
     * The relative path to this file from the Favorite path
     */
    private String path;
    
    /**
     * Last modified time
     */
    private String lastModified = "";
    
    /**
     * Last modified time stamp
     */
    private long lastModifiedTimeStamp = 0;

    /**
     * Favorite file being used
     */
    Favorite favorite;
    /**
     * An instance of Remote Request that is used to get the contents of the
     * file
     */
    RemoteRequest remoteRequest;
    /**
     * The largest number of bytes to retrieve at one time
     */
    int limit = 5000;
    /**
     * Should the thread keep looping?
     */
    boolean continueLogging = true;
    /**
     * Text field to deposit the goods
     */
    JTextArea textField;
    
    /**
     * Refresh Interval
     */
    int refreshInterval = 2000;
    
    /**
     * Last Modified Getter
     * @return 
     */
    public String getLastModified() {
        return lastModified;
    }

    /**
     * Last Modified Setter
     * @param lastModified 
     */
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
        
        try {
            Date date = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss zzz", Locale.ENGLISH).parse(this.lastModified);
            this.lastModifiedTimeStamp = date.getTime();
        } catch (Exception e) {
            System.out.println("Error Parsing Date");
            System.out.println(e.getMessage());
        }        
    }    

    public long getLastModifiedTimeStamp() {
        return lastModifiedTimeStamp;
    }
    
    /**
     * Text Field Setter
     *
     * @param textField
     */
    public void setTextField(JTextArea textField) {
        this.textField = textField;
    }

    /**
     * Continue Logging Getter
     *
     * @return
     */
    public boolean isContinueLogging() {
        return continueLogging;
    }

    /**
     * Continue Logging Setter
     *
     * @param continueLogging
     */
    public void setContinueLogging(boolean continueLogging) {
        this.continueLogging = continueLogging;
    }

    /**
     * Limit Getter
     *
     * @return
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Limit Setter
     *
     * @param limit
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    /**
     * Favorite Setter
     *
     * @param favorite
     */
    public void setFavorite(Favorite favorite) {
        this.favorite = favorite;
        this.setRemoteRequest();
    }

    /**
     * Path Getter
     *
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * Path Setter
     *
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Build the Remote request from the configuration
     */
    public void setRemoteRequest() {
        this.remoteRequest = new RemoteRequest(
                this.favorite.getProtocol(),
                this.favorite.getDomain(),
                this.favorite.getPort(),
                this.favorite.getUsername(),
                this.favorite.getPassword()
        );
    }

    /**
     * Retrieve the remote Request
     *
     * @return
     */
    public RemoteRequest getRemoteRequest() {
        return this.remoteRequest;
    }

    /**
     * The heart and soul of this class
     *
     * This method will retrieve all of the contents (up to the limit) of this
     * log file and then periodically request any changes. All content will be
     * added to the text field of this class
     */
    @Override
    public void run() {
        /**
         * Clear out the text filed by specifying loading
         */
        if (this.textField != null) {
            this.textField.setText("Loading ...");
            this.textField.setCaretPosition(this.textField.getDocument().getLength());
        } else {
            System.out.println("Loading ...");
        }

        HttpEntity httpEntity;

        try {
            httpEntity = this.remoteRequest.start(this.getPath()).getEntity();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        /**
         * Consume the initial bulk of the file If the file is larger than the
         * limit, retrieve the last portion of the file up to the limit
         */
        int skip = 0;
        long contentLength = httpEntity.getContentLength();
        
        System.out.println("Content Limit: "+this.getLimit());

        if (contentLength > this.getLimit()) {
            System.out.println("Content too large: " + httpEntity.getContentLength());
            skip = (int) httpEntity.getContentLength() - this.getLimit();
        }

        String content = this.consume(httpEntity, skip);

        if (this.textField != null) {
            this.textField.setText(content);
            this.textField.setCaretPosition(this.textField.getDocument().getLength());
        } else {
            System.out.println(content);
        }

        /**
         * Continue looping until someone tells us not to by an interupt or the
         * continue flag becoming false
         */
        do {
            try {
                httpEntity = this.remoteRequest.start(this.getPath()).getEntity();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return;
            }

            /**
             * We're going to retrieve all content skipping the last content
             * length this conveniently gives us anything that has changed since
             * the last time
             */
            content = this.consume(httpEntity, (int) contentLength);

            if (this.textField != null) {
                this.textField.append(content);
                this.textField.setCaretPosition(this.textField.getDocument().getLength());
            } else {
                System.out.println(content);
            }

            contentLength = httpEntity.getContentLength();

            try {
                System.out.println("Sleeping for: "+this.getRefreshInterval());
                Thread.sleep(this.getRefreshInterval());
            } catch (InterruptedException e) {
                this.setContinueLogging(false);
            }

        } while (this.isContinueLogging());
    }

    /**
     * Consume the current HttpEntity after the skip number and build a string
     * of the contents
     *
     * @param httpEntity
     * @param skip
     * @return
     */
    public String consume(HttpEntity httpEntity, int skip) {
        String content = "";
        try {
            InputStream is = httpEntity.getContent();
            is.skip(skip);

            int i;
            char c;

            while ((i = is.read()) != -1) {
                c = (char) i;
                content += c;
            }

            EntityUtils.consume(httpEntity);

            this.remoteRequest.end();

        } catch (IOException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        return content;
    }

    /**
     * Represent this log file as a
     *
     * @return
     */
    @Override
    public String toString() {
        String logPath = this.getPath();
        Date date = new Date(this.getLastModifiedTimeStamp());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        
        String returnString = "";
        
        if (this.getLastModifiedTimeStamp() > 0) {
            returnString += sdf.format(date) + " - ";
        }
        
        returnString += logPath.substring(logPath.lastIndexOf('/') + 1);
        
        return returnString;
    }
}
