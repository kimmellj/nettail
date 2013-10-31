package main.java.com.fluid;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class facilitates the management of FavoritesList
 *
 * This is the interface to the file store of FavoritesList for permanent use
 *
 * @author James Kimmell <jkimmell@fluid.com>
 */
public class FavoritesList extends ArrayList<Favorite> {
    /**
     * Where did we load favorites from?
     */
    private String loadedFile;
    
    /** 
     * ComboBox Model to keep in sync
     */
    private DefaultComboBoxModel comboBoxModel;
    
    /**
     * List Model to Keep in Sync
     */
    private DefaultListModel listModel;

    /**
     * Should we pause re-saving the file config file?
     */
    private boolean pauseSaving = false;
    
    /**
     * Add an item to the list
     * 
     * @param favorite
     * @return 
     */
    @Override
    public boolean add(Favorite favorite) {
        super.add(favorite);
        
        if (!this.pauseSaving) {
            this.saveFavoritesToFile();
        }
        
        if (this.comboBoxModel != null) {
            this.comboBoxModel.addElement(favorite);
        }
        
        if (this.listModel != null) {
          this.listModel.addElement(favorite);
            System.out.println("Adding "+favorite.toString()+" to the pane");
        }        
        
        return true;
    }
    
    /**
     * Remove an item from the list
     * 
     * @param index
     * @return 
     */
    @Override
    public Favorite remove(int index) {
        Favorite favorite = super.remove(index);
        
        if (!this.pauseSaving) {
            this.saveFavoritesToFile();
        }
        
        if (this.comboBoxModel != null) {
            this.comboBoxModel.removeElementAt(index);
        }        
        
        if (this.listModel != null) {
            this.listModel.remove(index);
        }
        
        return favorite;
    }
    
    /** 
     * Remove an item from the list
     * 
     * @param favorite
     * @return 
     */
    public boolean remove(Favorite favorite) {
        int index = this.indexOf(favorite);
        
        super.remove(favorite);
        
        if (!this.pauseSaving) {
            this.saveFavoritesToFile();
        }
        
        if (this.comboBoxModel != null) {
            this.comboBoxModel.removeElement(favorite);
        }        
        
        if (this.listModel != null) {
            this.listModel.removeElement(favorite);
        }       
        
        return true;
    }
    
    /**
     * Set the ComboBoxModel to be kept in sync with this data
     * 
     * @param comboBoxModel 
     */
    public void setComboBoxModel(DefaultComboBoxModel comboBoxModel) {
        this.comboBoxModel = comboBoxModel;
    }    

    /**
     * Set the ListModel to be kept in sync with this data
     * 
     * @param listModel 
     */
    public void setListModel(DefaultListModel listModel) {
        this.listModel = listModel;
    }
    
    /**
     * Load favorites from a JSON encoded file
     *
     * @param filename file to load favorites from
     * @throws Exception
     */
    public void loadFavoritesFromFile(String filename) throws Exception {
        /**
         * Clear out the list
         */
        Iterator<Favorite> iterator = this.iterator();

        while (iterator.hasNext()) {
            this.remove(iterator.next());
        }

        this.loadedFile = filename;

        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String content = reader.readLine();

        JSONObject json = new JSONObject(content);
        JSONArray array = (JSONArray) json.get("configs");

        for (int i = 0; i < array.length(); i++) {
            Favorite favorite;
            favorite = new Favorite();
            JSONObject jsonFavorite = (JSONObject) array.get(i);

            favorite.setUsername(jsonFavorite.getString("username"));
            favorite.setPassword(jsonFavorite.getString("password"));
            favorite.setBaseUrl(jsonFavorite.getString("baseUrl"));
            
            if (jsonFavorite.isNull("webdav")) {
                favorite.setWebdav(false);
            } else {
                favorite.setWebdav(jsonFavorite.getBoolean("webdav"));
            }

            this.add(favorite);
        }
    }
    
    
    /**
     * Save all favorites out to the loaded favorite file
     */
    public void saveFavoritesToFile() {
        if (this.loadedFile == null) {
            return;
        }

        try {
            this.saveFavoritesToFile(this.loadedFile);
        } catch (Exception e) {
            System.out.println("Error saving configurations: \n" + e.getMessage());
        }
    }

    /**
     * Save favorites out to a specified file
     *
     * @param filename
     * @throws Exception
     */
    public void saveFavoritesToFile(String filename) throws Exception {
        try (PrintWriter favoritesFile = new PrintWriter(filename, "UTF-8")) {
            JSONObject json = new JSONObject();
            json.put("configs", this.toArray());

            favoritesFile.write(json.toString());
        }
    }
    
    
    /**
     * Print out all of the favorites
     *
     * @return
     */
    @Override
    public String toString() {
        String returnString = "";

        Iterator<Favorite> iterator = this.iterator();

        while (iterator.hasNext()) {
            System.out.println("Next");
            returnString += iterator.next().toString() + "\n";
        }

        return returnString;
    }
}