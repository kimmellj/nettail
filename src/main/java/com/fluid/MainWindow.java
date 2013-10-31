/*
 * @todo maybe nest logs inside of a favorite?
 */
package main.java.com.fluid;

import java.awt.Font;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JTextArea;

/**
 * The MainWindow Application Window
 *
 * This window has: - A Drop-down of loaded FavoritesWindow - A List of Log files
 * for the loaded Favorite - A text area that will be updated with the
 * content of the loaded log file
 *
 * @author James Kimmell <jkimmell@fluid.com>
 */
public class MainWindow extends javax.swing.JFrame {

    /**
     * List Model for the list of Logs
     */
    DefaultListModel logListModel;
    /**
     * CombboBox Model for the list of favorites
     */
    ComboBoxModel favoriteListModel;
    /**
     * The thread that is being run to get the contents of a log file
     */
    Thread loggerThread;
    /**
     * The list of logs
     */
    Logs logs;
    
    /**
     * Currently Running Log
     */
    Log log;
    
    /**
     * The favorites list window
     */
    FavoritesWindow favoritesWindow;
    
    /**
     * Configuration settings window
     */
    ConfigWindow configWindow;
    
    /**
     * Creates new form MainWindow
     *
     * Initializes the models and set the log list to have a reference
     */
    public MainWindow() {
        initComponents();

        this.logListModel = new DefaultListModel();

        this.logList.setModel(logListModel);
        
        this.configWindow = new ConfigWindow();
        this.configWindow.setVisible(false);
    }    

    /**
     * Favorite Window Setter
     *
     * @param favoritesWindow
     */
    public void setFavoritesWindow(FavoritesWindow favoritesWindow) {
        this.favoritesWindow = favoritesWindow;
    }

    /**
     * Logs Setter
     *
     * @param logs
     */
    public void setLogs(Logs logs) {
        this.logs = logs;
    }

    /**
     * Log List Model Getter
     *
     * @return
     */
    public DefaultListModel getLogListModel() {
        return logListModel;
    }

    /**
     * Log Viewer Getter
     *
     * @return
     */
    public JTextArea getLogViewer() {
        return logViewer;
    }
    
    /**
     * Favorite List Model Setter
     *
     * @return
     */
    public void setFavoriteListModel(ComboBoxModel s) {
        this.favoriteListModel = s;
        this.favoriteSelector.setModel(this.favoriteListModel);
    }    

    /**
     * Favorite List Model Getter
     *
     * @return
     */
    public ComboBoxModel getFavoriteListModel() {
        return favoriteListModel;
    }
    
    public void resetLogView() {
        if (this.loggerThread != null && this.loggerThread.isAlive()) {
            this.loggerThread.interrupt();
        }

        this.log = (Log) this.logListModel.getElementAt(this.logList.getSelectedIndex());
        log.setRefreshInterval(this.configWindow.refreshInterval);
        log.setLimit(this.configWindow.contentLimit);
        log.setTextField(this.logViewer);
        
        this.configWindow.setLog(this.log);

        try {
            loggerThread = new Thread(log);
            loggerThread.start();
        } catch (Exception e) {
            System.out.println("Thread Error: " + e.getMessage());
        }        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        favoriteSelector = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        logList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        logViewer = new javax.swing.JTextArea();
        refreshLogList = new javax.swing.JButton();
        refreshLogView = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        favoritesMenu = new javax.swing.JMenu();
        manageFavoritesMenu = new javax.swing.JMenuItem();
        addFavoriteMenu = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        configMenu = new javax.swing.JMenuItem();

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        favoriteSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                favoriteSelectorActionPerformed(evt);
            }
        });

        logList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                logListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(logList);

        logViewer.setBackground(new java.awt.Color(0, 0, 0));
        logViewer.setColumns(20);
        logViewer.setForeground(new java.awt.Color(153, 255, 153));
        logViewer.setRows(5);
        Font font = new Font("Courier", Font.PLAIN, 14);
        logViewer.setFont(font);
        jScrollPane2.setViewportView(logViewer);

        refreshLogList.setText("Refresh");
        refreshLogList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshLogListActionPerformed(evt);
            }
        });

        refreshLogView.setText("Refresh");
        refreshLogView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshLogViewActionPerformed(evt);
            }
        });

        favoritesMenu.setText("Favorites");

        manageFavoritesMenu.setText("Manage Favorites");
        manageFavoritesMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageFavoritesMenuActionPerformed(evt);
            }
        });
        favoritesMenu.add(manageFavoritesMenu);

        addFavoriteMenu.setText("Add Favorite");
        addFavoriteMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFavoriteMenuActionPerformed(evt);
            }
        });
        favoritesMenu.add(addFavoriteMenu);

        jMenuBar1.add(favoritesMenu);

        editMenu.setText("Edit");

        configMenu.setText("Log Settings");
        configMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configMenuActionPerformed(evt);
            }
        });
        editMenu.add(configMenu);

        jMenuBar1.add(editMenu);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(favoriteSelector, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 362, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(refreshLogList))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 702, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(refreshLogView)
                                .add(0, 611, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(favoriteSelector, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(refreshLogList)
                    .add(refreshLogView))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Event for when a log is selected
     *
     * If the log thread is running, kill it and then prep the thread for the
     * selected file and kick it off
     *
     * @param evt
     */
    private void logListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_logListValueChanged
        if (evt.getValueIsAdjusting()) {
            return;
        }

        this.resetLogView();
    }//GEN-LAST:event_logListValueChanged

    /**
     * Event for when a different favorite is selected Set the list model,
     * Set the favorite and then build the list of log files for the
     * specified favorite
     *
     * @param evt
     */
    private void favoriteSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_favoriteSelectorActionPerformed
        if (this.logs == null) {
            return;
        }

        Favorite favorite = (Favorite) this.favoriteSelector.getSelectedItem();
        this.logs.setListModel(this.getLogListModel());
        this.logs.setFavorite(favorite);
        this.logs.buildLogs();
    }//GEN-LAST:event_favoriteSelectorActionPerformed

    private void refreshLogListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshLogListActionPerformed
        this.logs.buildLogs();
    }//GEN-LAST:event_refreshLogListActionPerformed

    private void refreshLogViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshLogViewActionPerformed
        this.resetLogView();
    }//GEN-LAST:event_refreshLogViewActionPerformed

    private void manageFavoritesMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageFavoritesMenuActionPerformed
        this.favoritesWindow.setVisible(true);
    }//GEN-LAST:event_manageFavoritesMenuActionPerformed

    private void addFavoriteMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFavoriteMenuActionPerformed
        this.favoritesWindow.setVisible(true);
        this.favoritesWindow.showAddWindow();
    }//GEN-LAST:event_addFavoriteMenuActionPerformed

    private void configMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configMenuActionPerformed
        this.configWindow.setVisible(true);
    }//GEN-LAST:event_configMenuActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addFavoriteMenu;
    private javax.swing.JMenuItem configMenu;
    private javax.swing.JMenu editMenu;
    private javax.swing.JComboBox favoriteSelector;
    private javax.swing.JMenu favoritesMenu;
    private javax.swing.JButton jButton1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList logList;
    private javax.swing.JTextArea logViewer;
    private javax.swing.JMenuItem manageFavoritesMenu;
    private javax.swing.JButton refreshLogList;
    private javax.swing.JButton refreshLogView;
    // End of variables declaration//GEN-END:variables
}
