package main.java.com.fluid.webdav;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import main.java.com.fluid.HttpPropFind;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * This class overrides the Connection class and add some WebDav specific
 * functionality
 * 
 * @author James Kimmell <jkimmell@fluid.com>
 */
public class Connection extends main.java.com.fluid.web.Connection {
    public Connection() {
        super();
    }
    
    /**
     * Request all of the WebDav properties for a specified path
     * This response will be an XML string that will need to be parsed
     * 
     * @todo Potentially just return a hash map of the properties?
     * 
     * @param path
     * @return 
     */
    public String getProperties(String path) {
        this.content = "";
        
        /**
        * Needed for the WebDav request
        */
        String param = "<?xml version=\"1.0\"?>"
                + "<a:propfind xmlns:a=\"DAV:\">"
                + "<a:allprop/>"
                + "</a:propfind>";

        try {
            URI uri = new URI(path);
            
            HttpPropFind httppost = new HttpPropFind(uri);
            httppost.setHeader("Depth", "1");

            StringEntity strEntity = new StringEntity(param, HTTP.UTF_8);
            strEntity.setContentType("text/xmlmc; charset=utf-8");
            httppost.setEntity(strEntity);

            HttpResponse response = this.httpClient.execute(targetHost, httppost, this.localContext);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();

            int i;
            char c;

            while ((i = is.read()) != -1) {
                c = (char) i;
                content += c;
            }   
            
            EntityUtils.consume(entity);
        } catch (URISyntaxException | IOException | IllegalStateException e) {
            System.out.println("Exception occured while getting webdav properties: "+e.getMessage());
        }
        
        return content;
    }
}
