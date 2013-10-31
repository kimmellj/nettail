package main.java.com.fluid;

import java.net.URI;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.protocol.HTTP;

/**
 * This class extends the HttpEntityEnclosingRequestBase class and used to 
 * make WEBDAV requests with an HTTP Client
 * 
 * This code was taken from:
 * http://blog.andre-lam.com/how-to-send-webdav-requests-with-httpclient/
 * 
 * @author James Kimmell <jkimmell@fluid.com>
 */
public class HttpPropFind extends HttpEntityEnclosingRequestBase
{
    /**
     * The custom HTTP method name to be used 
     */
    public static final String METHOD_NAME = "PROPFIND";

    /**
     * Constructor for this class. This will set URI for the HTTP request and 
     * the necessary headers
     * 
     * @param The URI that this HTTP request is for 
     */
    public HttpPropFind(final URI uri)
    {
        this.setURI(uri);
        this.setHeader(HttpHeaders.CONTENT_TYPE, "text/xml" + HTTP.CHARSET_PARAM + HTTP.UTF_8.toLowerCase());
    }    

    /**
     * Getter Method for the HTTP Method
     * @return This HTTP entities method name
     */
    @Override
    public String getMethod()
    {
        return HttpPropFind.METHOD_NAME;
    }
}