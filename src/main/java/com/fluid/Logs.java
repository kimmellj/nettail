package main.java.com.fluid;

import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.DefaultListModel;


/**
 * This class facilitates the management of Logs
 *
 * @author James Kimmell <jkimmell@fluid.com>
 */
public abstract class Logs {

    /**
     * Favorite for this set of logs
     */
    protected Favorite favorite;
    
    /**
     * Actual logs contained in this directory
     */
    protected ArrayList<Log> logs;
    
    /**
     * A list model to keep in sync with the list of logs
     */
    protected DefaultListModel listModel;
    
    /**
     * Connection
     */
    protected Connection connection;
    
    /**
     * Constructor
     *
     * Initialize the list of logs
     */
    public Logs() {
        this.logs = new ArrayList<>();
    }
    
    /**
     * Refresh the log list model
     * 
     * This should be executed any times there are changes to the log files
     */
    public void refreshList() {
        this.listModel.clear();
        
        for (Log logFile : this.logs) {
            this.listModel.addElement(logFile);
        }
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
     * Get the favorite being used
     * @return 
     */
    public Favorite getFavorite() {
        return this.favorite;
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
    public abstract boolean buildLogs();
    
    /**
     * Setup the connection
     */
    public abstract void setConnection();
    
    /**
     * Get the current connection
     */
    public Connection getConnection() {
        
        if (this.connection == null) {
            this.setConnection();
        }
        
        return this.connection;
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