/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.com.fluid.webdav;

import main.java.com.fluid.Favorite;
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
public class WebDavLogsTest {
    
    public WebDavLogsTest() {
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
     * Test of buildLogs method, of class Logs.
     */
    @Test
    public void testBuildLogs() {
        System.out.println("buildLogs");
        Logs instance = new Logs();
        Favorite favorite = new Favorite();
        
        try {
            favorite.load("", "", "", "http://localhost/uploads/", false);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        instance.setFavorite(favorite);
        
        instance.buildLogs();
    }

}