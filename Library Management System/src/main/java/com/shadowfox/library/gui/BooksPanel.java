package com.shadowfox.library.gui;

import com.shadowfox.library.dao.BookDAO;
import com.shadowfox.library.model.Book;
import com.shadowfox.library.service.GoogleBooksAPI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BooksPanel extends JPanel {
    private final BookDAO bookDAO;
    private final GoogleBooksAPI googleBooksAPI;
    private DefaultTableModel tableModel;
    private JTable booksTable;

    public BooksPanel(BookDAO bookDAO, GoogleBooksAPI googleBooksAPI) {
        this.bookDAO = bookDAO;
        this.googleBooksAPI = googleBooksAPI;
        setLayout(new BorderLayout());

        // Top Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel isbnLabel = new JLabel("Fetch via ISBN:");
        JTextField isbnField = new JTextField(15);
        JButton fetchButton = new JButton("Fetch & Add Book");

        controlPanel.add(isbnLabel);
        controlPanel.add(isbnField);
        controlPanel.add(fetchButton);
        
        JButton refreshButton = new JButton("Refresh Table");
        controlPanel.add(refreshButton);

        add(controlPanel, BorderLayout.NORTH);

        // Table Data
        String[] columnNames = {"ID", "ISBN", "Title", "Author", "Published Year"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only
            }
        };
        booksTable = new JTable(tableModel);
        booksTable.setFillsViewportHeight(true);
        add(new JScrollPane(booksTable), BorderLayout.CENTER);

        // Actions
        fetchButton.addActionListener(e -> {
            String isbn = isbnField.getText().trim();
            if (isbn.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an ISBN.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            fetchButton.setEnabled(false);
            fetchButton.setText("Fetching...");

            // Run in background thread to avoid freezing GUI
            SwingWorker<Book, Void> worker = new SwingWorker<>() {
                @Override
                protected Book doInBackground() {
                    return googleBooksAPI.fetchBookByIsbn(isbn);
                }

                @Override
                protected void done() {
                    try {
                        Book book = get();
                        if (book != null) {
                            if (bookDAO.addBook(book)) {
                                JOptionPane.showMessageDialog(BooksPanel.this, "Successfully added: " + book.getTitle(), "Success", JOptionPane.INFORMATION_MESSAGE);
                                isbnField.setText("");
                                refreshTable();
                            } else {
                                JOptionPane.showMessageDialog(BooksPanel.this, "Failed to add book. It might already exist.", "Database Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(BooksPanel.this, "Book not found on Google Books.", "API Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(BooksPanel.this, "Error fetching book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        fetchButton.setEnabled(true);
                        fetchButton.setText("Fetch & Add Book");
                    }
                }
            };
            worker.execute();
        });

        refreshButton.addActionListener(e -> refreshTable());

        // Initial Load
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0); // clear existing data
        List<Book> books = bookDAO.getAllBooks();
        for (Book b : books) {
            Object[] row = {
                b.getId(),
                b.getIsbn(),
                b.getTitle(),
                b.getAuthor() != null ? b.getAuthor().getName() : "Unknown",
                b.getPublishedYear() != null ? b.getPublishedYear() : "N/A"
            };
            tableModel.addRow(row);
        }
    }
}
