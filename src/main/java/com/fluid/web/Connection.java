package main.java.com.fluid.web;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author James Kimmell <jkimmell@fluid.com>
 */
public class Connection extends main.java.com.fluid.Connection {
    protected DefaultHttpClient httpClient;
    protected HttpHost targetHost;
    protected BasicHttpContext localContext;
    protected UsernamePasswordCredentials credentials;
    private org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(Connection.class);
    
    /**
     * Setup the HTTP Client and wrap it with the needed settings to handle 
     * HTTPS connections
     */
    public Connection() {
        this.httpClient = new DefaultHttpClient();
        this.httpClient = (DefaultHttpClient) WebClientDevWrapper.wrapClient(this.httpClient);
    }
    
    /**
     * Setter for the current status code
     * 
     * @param statusCode 
     */
    public void setStatusCode(int statusCode) {
        this.log.info("Setting status code to: "+statusCode);
        this.statusCode = statusCode;
    }
    
    /**
     * Implement the add credentials method by initializing the credentials 
     * attribute with a UsernamePassswordCredentials instance
     * 
     * @param username
     * @param password 
     */
    @Override
    public void addCredentials(String username, String password) {
        this.log.info("Adding Credentials: "+username);
        this.credentials = new UsernamePasswordCredentials(username, password);
    }
    
    /**
     * Implement the connect method by setting up an HTTP Host, HTTP Context
     * and prepping the HTTP Client
     * 
     * @param protocol
     * @param domain
     * @param port 
     */
    @Override
    public void connect(String protocol, String domain, int port) {
        this.log.info("Connecting to: "+protocol+"://"+domain+":"+port);
        
        this.targetHost = new HttpHost(domain, port, protocol);

        // Add AuthCache to the execution context
        this.localContext = new BasicHttpContext();

        if (this.credentials != null) {
            this.httpClient.getCredentialsProvider().setCredentials(
                    new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                    this.credentials);

            AuthCache authCache = new BasicAuthCache();
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(targetHost, basicAuth);
            this.localContext.setAttribute(ClientContext.AUTH_CACHE, authCache);
        }
    }
    
    /**
     * Disconnect the current connection manager for the HTTP Client
     */
    @Override
    public void disconnect () {
        this.log.info("Disconnecting");
        
        this.httpClient.getConnectionManager().shutdown();
    }
    
    /**
     * Get the content length of a request to the given path.
     * This is useful if you want to know what the content length for a request
     * will be but you don't need the actual content yet.
     * 
     * @param path
     * @return 
     */
    @Override
    public long getContentLength(String path) {
        HttpGet httpget = new HttpGet(path);

        try {
            HttpResponse response = this.httpClient.execute(targetHost, httpget, this.localContext);   
            this.setStatusCode(response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            this.contentLength = entity.getContentLength();   
            
            EntityUtils.consume(entity);
        } catch (IOException | IllegalStateException e) {
            System.out.println("Exception getting content length: "+e.getMessage());
        }
        
        return this.contentLength;
    }
    
    /**
     * Request all content from a supplied path
     * This will call the other requestContent method with a starting point of zero
     * 
     * @param path
     * @return 
     */
    @Override
    public boolean requestContent (String path) {
        return this.requestContent(path, 0);
    }
    
    /**
     * Request Content from a specified path skipping the specified number of bytes
     * 
     * This method will build and execute the HTTP request, setting the class 
     * attributes with the relevant data found (content, content length, status code)
     * 
     * @param path path to retrieve content from
     * @param start bytes to skip
     * @return 
     */
    @Override
    public boolean requestContent (String path, long start) {
        HttpGet httpget = new HttpGet(path);
        this.content = "";
        
        System.out.println("Start: "+Long.toString(start));

        try {
            HttpResponse response = this.httpClient.execute(targetHost, httpget, this.localContext);   
            this.setStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("Status Code:"+response.getStatusLine().getStatusCode());
            
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            this.contentLength = entity.getContentLength();
            
            /**
             * Skip the stream forward to starting point
             */
            is.skip(start);
            
            int i;
            char c;

            while ((i = is.read()) != -1) {
                c = (char) i;
                this.content += c;
            }

            EntityUtils.consume(entity);

        } catch (IOException | IllegalStateException e) {
            System.out.println(e.getMessage());
            return false;
        }
        
        return false;
    }
}
