/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.com.fluid.web;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author James Kimmell <jkimmell@fluid.com>
 */
public class WebConnectionTest {
    
    public WebConnectionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }


    /**
     * Test of getContent method, of class Connection.
     */
    @Test
    public void testRequestContent_String() {
        System.out.println("getContent");
        String path = "/";
        
        Connection instance = new Connection();
        instance.connect("https", "www.google.com", 443);
        instance.requestContent(path);
        
        if (instance.getStatusCode() != 200) {
            fail("Failed request with status code: "+instance.getStatusCode());
        }
        
        if (instance.getContent().length() <= 0) {
            fail("No content retrieved");
        }        
    }

    /**
     * Test of getContent method, of class Connection.
     */
    @Test
    public void testRequestContent_3args() {
        System.out.println("getContent");
        String path = "/";
        
        Connection instance = new Connection();
        instance.connect("https", "www.google.com", 443);
        instance.requestContent(path, 1024);
        
        if (instance.getStatusCode() != 200) {
            fail("Failed request with status code: "+instance.getStatusCode());
        }
        
        if (instance.getContent().length() <= 0) {
            fail("No content retrieved");
        }
    } 
}