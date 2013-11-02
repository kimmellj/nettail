/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.com.fluid.webdav;

import main.java.com.fluid.webdav.Connection;
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
public class WebDavConnectionTest {
    
    public WebDavConnectionTest() {
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
     * Test of getProperties method, of class Connection.
     */
    @Test
    public void testGetProperties() {
        System.out.println("getProperties");
        String path = "/uploads/";
        Connection instance = new Connection();
        instance.addCredentials("admin", "admin");
        instance.connect("http", "localhost", 80);
        String expResult = "";
        String result = instance.getProperties(path);
        
        assertNotNull(result);
    }
}