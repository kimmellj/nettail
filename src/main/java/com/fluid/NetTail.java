package main.java.com.fluid;

import java.io.File;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;

/**
 * This is the main application class
 *
 * This will build all of the needed data structures and inject them into the
 * needed windows.
 *
 * @author James Kimmell <jkimmell@fluid.com>
 */
public class NetTail {

    /**
     * Loaded and managed configurations
     */
    private FavoritesList favoritesList;
    /**
     * Loaded log files for the specified configuration
     */
    private Logs logs;
    /**
     * MainWindow Application Window
     */
    private MainWindow mainWindow;
    /**
     * Configuration List Window
     */
    private FavoritesWindow favoritesWindow;

    /**
     * Constructor - This is the main one for the application. It acts as a director
     * by setting up all of the windows and make sure they have references to 
     * the things that they need
     */
    public NetTail() {
        
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        
        /**
         * Ensure that the configuration directory exists and that there is a
         * favoritesList file within it. The FavoritesWindow class will expect this.
         */
        String configsDirName = System.getProperty("user.home") + "/.nettail/";
        String configsFileName = configsDirName + "configs";

        try {
            File configDir = new File(configsDirName);
            configDir.mkdirs();

            File configFile = new File(configsFileName);
            configFile.createNewFile();
        } catch (Exception e) {
            System.out.println("Issues building the config directory or file");
            System.out.println(e.getMessage());
            System.exit(1);
        }
        
        /**
         * Favorites Models
         */
        DefaultComboBoxModel favoritesComboBoxModel = new DefaultComboBoxModel();
        DefaultListModel favoritesListModel = new DefaultListModel();        
        
        /**
         * Create instances for the application windows
         */
        this.mainWindow = new MainWindow();
        this.favoritesWindow = new FavoritesWindow();
        
        /**
         * Configure Windows
         */
        this.mainWindow.setFavoritesWindow(this.favoritesWindow);
        
        this.favoritesWindow.setVisible(false);
        
        this.favoritesWindow.getFavoritesList().setModel(favoritesListModel);
        
        /**
         * A reference to all of the Favorites
         */
        this.favoritesList = new FavoritesList();
        
        /**
         * Add models to the necessary places so that everything updates nicely
         */
        this.favoritesList.setComboBoxModel(favoritesComboBoxModel);
        this.favoritesList.setListModel(favoritesListModel);
        this.mainWindow.setFavoriteListModel(favoritesComboBoxModel);
        
        /**
         * We're going to try and load the favorite configurations
         * Hopefully this works but if it doesn't it maybe because this is the 
         * first run
         */
        try {
            favoritesList.loadFavoritesFromFile(configsFileName);
        } catch (Exception e) {
            System.out.println("There was an issue loading the configuration files. This maybe caused by the first run");
            System.out.println(e.getMessage());
        }
        
        
        this.favoritesWindow.favorites(this.favoritesList);
        this.mainWindow.setVisible(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                NetTail webTail = new NetTail();
            }
        });
    }
}
