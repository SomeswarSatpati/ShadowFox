package com.shadowfox.library.gui;

import com.shadowfox.library.dao.BookDAO;
import com.shadowfox.library.dao.UserDAO;
import com.shadowfox.library.service.GoogleBooksAPI;
import com.shadowfox.library.service.LibraryService;

import javax.swing.*;

public class LibraryGUI extends JFrame {

    public LibraryGUI() {
        setTitle("Library Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize backend components
        BookDAO bookDAO = new BookDAO();
        UserDAO userDAO = new UserDAO();
        GoogleBooksAPI googleBooksAPI = new GoogleBooksAPI();
        LibraryService libraryService = new LibraryService();

        // Create main tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add Panels
        tabbedPane.addTab("Books", new BooksPanel(bookDAO, googleBooksAPI));
        tabbedPane.addTab("Users", new UsersPanel(userDAO));
        tabbedPane.addTab("Transactions", new TransactionPanel(libraryService));

        add(tabbedPane);
    }

    public static void main(String[] args) {
        // Ensure GUI runs on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LibraryGUI().setVisible(true);
        });
    }
}
