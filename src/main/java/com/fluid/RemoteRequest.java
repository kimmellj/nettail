package main.java.com.fluid;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * This class is used to make Remote HTTP requests and retrieve the content of
 * the request and the headers
 *
 * This class allows the use of Basic HTTP Authentication
 *
 * @todo Enable SSL validation by default
 *
 * @author James Kimmell <jkimmell@fluid.com>
 */
public class RemoteRequest {

    private String domain, protocol;
    private int port;
    private String username = "";
    private String password = "";
    public long contentLength;
    DefaultHttpClient httpclient;
    
    /**
     * Create a remote request specifying the URI information
     * 
     * @param protocol
     * @param domain
     * @param port
     * @param username
     * @param password 
     */
    public RemoteRequest(String protocol, String domain, int port, String username, String password) {
        this.protocol = protocol;
        this.domain = domain;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * Port Setter
     *
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Protocol Setter
     *
     * @param protocol
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Protocol Getter
     *
     * @return
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Port Getter
     *
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * Domain Setter
     *
     * @param domain
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * Username Setter
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Password Setter
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Domain Getter
     *
     * @return
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Username Getter
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Password Getter
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * Start the HTTP request
     *
     * Enable / Set Authentication if the configuration specifies it is needed
     * Disable the SSL Certificate Check Return the response
     *
     * @param path
     * @return
     * @throws IOException
     */
    public HttpResponse start(String path) throws IOException {
        HttpHost targetHost = new HttpHost(this.domain, this.port, this.protocol);
        this.httpclient = new DefaultHttpClient();
        this.httpclient = (DefaultHttpClient) WebClientDevWrapper.wrapClient(this.httpclient);

        // Add AuthCache to the execution context
        BasicHttpContext localcontext = new BasicHttpContext();

        if (this.username.length() > 0) {
            this.httpclient.getCredentialsProvider().setCredentials(
                    new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                    new UsernamePasswordCredentials(this.username, this.password));

            AuthCache authCache = new BasicAuthCache();
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(targetHost, basicAuth);
            localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
        }

        HttpGet httpget = new HttpGet(path);

        HttpResponse response = this.httpclient.execute(targetHost, httpget, localcontext);
        return response;
    }

    /**
     * Get the content of a path, skipping the specified number of bytes
     *
     * @param path
     * @param skip
     * @return
     */
    public String getContent(String path, long skip) {
        String content = "";

        try {
            HttpEntity entity = this.start(path).getEntity();
            InputStream is = entity.getContent();
            this.contentLength = entity.getContentLength();

            is.skip(skip);

            int i;
            char c;

            while ((i = is.read()) != -1) {
                c = (char) i;
                content += c;
            }

            EntityUtils.consume(entity);

            this.end();
        } catch (IOException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        return content;
    }

    /**
     * Read Content from a WebDav server at the specified path
     * 
     * @param path
     * @return 
     */
    public String getWebDavContent(String path) {
        try {
            HttpHost targetHost = new HttpHost(this.getDomain(), this.getPort(), this.getProtocol());
            DefaultHttpClient localHttpclient = new DefaultHttpClient();
            localHttpclient = (DefaultHttpClient) WebClientDevWrapper.wrapClient(localHttpclient);

            // Add AuthCache to the execution context
            BasicHttpContext localcontext = new BasicHttpContext();

            /**
             * If there is a username, attempt to login 
             */
            if (this.getUsername().length() > 0) {
                localHttpclient.getCredentialsProvider().setCredentials(
                        new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                        new UsernamePasswordCredentials(this.getUsername(), this.getPassword()));

                AuthCache authCache = new BasicAuthCache();
                BasicScheme basicAuth = new BasicScheme();
                authCache.put(targetHost, basicAuth);
                localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
            }

            /**
             * Needed for the WebDav request
             */
            String param = "<?xml version=\"1.0\"?>"
                    + "<a:propfind xmlns:a=\"DAV:\">"
                    + "<a:prop><a:resourcetype/></a:prop>"
                    + "<a:prop><a:getlastmodified/></a:prop>"
                    + "</a:propfind>";

            URI uri = new URI(path);

            HttpPropFind httppost = new HttpPropFind(uri);

            StringEntity strEntity = new StringEntity(param, HTTP.UTF_8);
            strEntity.setContentType("text/xmlmc; charset=utf-8");
            httppost.setEntity(strEntity);

            HttpResponse response = localHttpclient.execute(targetHost, httppost, localcontext);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();

            int i;
            char c;
            String content = "";

            while ((i = is.read()) != -1) {
                c = (char) i;
                content += c;
            }

            return content;
        } catch (URISyntaxException | IOException | IllegalStateException e) {
            System.out.println("Error reading WebDav data");
            System.out.println(e.getMessage());
        }

        return "";
    }

    /**
     * End a request
     */
    public void end() {
        this.httpclient.getConnectionManager().shutdown();
    }
}
