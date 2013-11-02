package main.java.com.fluid;

import java.net.URL;

/**
 * This class represents and individual configuration
 *
 * @author James Kimmell <jkimmell@fluid.com>
 */
public class Favorite {

    private String username, password, baseUrl, protocol, domain, path;
    private int port;
    private boolean webdav;

    /**
     * Load all of the data elements into this instance at once
     *
     * @param username
     * @param password
     * @param baseUrl
     * @throws Exception
     */
    public void load(String username, String password, String baseUrl, boolean webdav) throws Exception {
        this.setUsername(username);
        this.setPassword(password);
        this.setBaseUrl(baseUrl);
        this.setWebdav(webdav);
    }

    public boolean isWebdav() {
        return webdav;
    }
    
    public String getConnectionType() {
        if (this.isWebdav()) {
            return "WebDav";
        } else {
            return "Web";
        }
    }

    /**
     * Set whether this is a webdav connection or not
     * 
     * @param webdav 
     */
    public void setWebdav(boolean webdav) {
        this.webdav = webdav;
    }

    /**
     * Get the Favorite Protocol
     *
     * @return
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Get the Favorite Port
     *
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * Get the Favorite Domain
     *
     * @return
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Get the Favorite Path
     *
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * Get the Favorite Username
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get the Favorite Password
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * Get the Favorite Base URL
     *
     * @return
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Set the Favorite Username
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Set the Favorite Password
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Set the Favorite Base URL
     *
     * This will parse the URL into each of its parts and save them separately
     *
     * @param baseUrl
     * @return
     * @throws Exception
     */
    public boolean setBaseUrl(String baseUrl) throws Exception {
        this.baseUrl = baseUrl;
        boolean successful = false;

        URL url = new URL(this.baseUrl);
        this.protocol = url.getProtocol();
        this.domain = url.getHost();
        this.path = url.getPath();
        this.port = url.getPort();

        if (this.port == -1) {
            if (this.protocol.equals("https")) {
                this.port = 443;
            } else {
                this.port = 80;
            }
        }

        return successful;
    }

    /**
     * Represent this object as a string
     *
     * @return
     */
    @Override
    public String toString() {
        String returnString = "";

        if (this.isWebdav()) {
            returnString += "webdav:";
        }
        
        if (this.getUsername().length() > 0) {
            returnString += this.getUsername() + "@" + this.getBaseUrl();
        } else {
            returnString += this.getBaseUrl();
        }

        return returnString;
    }
}
