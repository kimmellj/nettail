package main.java.com.fluid;

/**
 *
 * @author James Kimmell <jkimmell@fluid.com>
 */
public abstract class Connection {
    protected int statusCode;
    protected long contentLength;
    protected String content;

    /**
     * Getter for the content
     * @return 
     */
    public String getContent() {
        return content;
    }

    /**
     * Getter for the content length
     * @return 
     */
    public long getContentLength() {
        return contentLength;
    }
    
    /**
     * Getter for the status code
     * @return 
     */
    public int getStatusCode() {
        return statusCode;
    }
    
    /**
     * Add Credentials to the current connection
     * @param username
     * @param password 
     */
    public abstract void addCredentials(String username, String password);
    
    /**
     * Perform the connect procedures for the current connection
     * @param protocol
     * @param domain
     * @param port 
     */
    public abstract void connect(String protocol, String domain, int port);
    
    /**
     * Disconnect from the current connection 
     */
    public abstract void disconnect();
    
    /**
     * Retrieve the content length for a given path
     * @param path
     * @return 
     */
    public abstract long getContentLength(String path);
    
    /**
     * Make a request for content at the given path
     * @param path
     * @return 
     */
    public abstract boolean requestContent(String path);
    
    /**
     * Make a request for content at the given path starting at the given number of bytes
     * @param path
     * @param start
     * @return 
     */
    public abstract boolean requestContent(String path, long start);    
}
