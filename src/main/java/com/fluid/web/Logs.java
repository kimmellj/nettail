package main.java.com.fluid.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import main.java.com.fluid.Log;

/**
 *
 * @author James Kimmell <jkimmell@fluid.com>
 */
public class Logs extends main.java.com.fluid.Logs {
    /**
     * Build up and set the connection for this instance
     */
    @Override
    public void setConnection() {
        this.connection = new Connection();
        this.connection.addCredentials(this.getFavorite().getUsername(), this.getFavorite().getPassword());
        this.connection.connect(this.getFavorite().getProtocol(), this.getFavorite().getDomain(), this.getFavorite().getPort());
    }
    
    /**
     * Build the logs by requesting the content from the favorites path and 
     * passing that content the getFileList method. This method will return a 
     * list of files for us to build logs for.
     * 
     * After we update the log list will refresh the log list models.
     * 
     * @return 
     */
    @Override
    public boolean buildLogs() {
        this.connection.requestContent(this.getFavorite().getPath()); 
        ArrayList<HashMap> listOfFiles = this.getFileList(this.getConnection().getContent());
        
        for (HashMap logFile : listOfFiles) {
            Log log = new Log(this.getConnection(), this.getFavorite());
            log.setPath("/" + logFile.get("link"));
            log.setFavorite(this.getFavorite());
            this.logs.add(log);
        }
        
        this.refreshList();
        
        return true;
    }
    
    /**
     * Parse the content requested for this favorite and retrieve any links
     * that end in .txt or .log.
     * 
     * @param content
     * @return 
     */
    public ArrayList<HashMap> getFileList(String content) {
        ArrayList<HashMap> logFiles = new ArrayList<>();
        
        Pattern r = Pattern.compile("\\<a href=\"(.*?)\"\\>");
        Matcher m = r.matcher(content);
        
        while (m.find()) {
            String link = m.group(0).replace("<a href=\"", "").replace("\">", "");
            
            if (link.contains(".log") || link.contains(".txt")) {
                /**
                 * Use a hash map here instead of returning just the link
                 * for consistency with the other protocols
                 */
                HashMap hash = new HashMap();
                hash.put("link", link);
                
                logFiles.add(hash);
            }
        }        
        
        return logFiles;
    }    
}
