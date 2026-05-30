package com.shadowfox.banking.management;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Run the GUI on the Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            // Initialize database connection
            // Note: If MySQL is not running or credentials don't match, 
            // the DatabaseConnection class handles the SQLException internally
            // by printing to System.err, allowing the app to run in-memory mode.
            DatabaseConnection dbConnection = new DatabaseConnection();
            
            // Launch GUI
            BankingGUI gui = new BankingGUI(dbConnection);
            gui.setVisible(true);
        });
    }
}
