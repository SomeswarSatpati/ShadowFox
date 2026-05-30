package com.shadowfox.library.gui;

import com.shadowfox.library.service.LibraryService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class TransactionPanel extends JPanel {
    private final LibraryService libraryService;

    public TransactionPanel(LibraryService libraryService) {
        this.libraryService = libraryService;
        setLayout(new BorderLayout());

        // Center Panel for Forms
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Borrow Section
        JLabel borrowTitle = new JLabel("Borrow a Book");
        borrowTitle.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(borrowTitle, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1;
        formPanel.add(new JLabel("User ID:"), gbc);
        JTextField borrowUserIdField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(borrowUserIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Book ISBN:"), gbc);
        JTextField borrowIsbnField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(borrowIsbnField, gbc);

        JButton borrowButton = new JButton("Borrow Book");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(borrowButton, gbc);

        // Separator
        gbc.gridy = 4;
        formPanel.add(new JSeparator(), gbc);

        // Return Section
        JLabel returnTitle = new JLabel("Return a Book");
        returnTitle.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 5;
        formPanel.add(returnTitle, gbc);

        gbc.gridwidth = 1; gbc.gridy = 6;
        formPanel.add(new JLabel("User ID:"), gbc);
        JTextField returnUserIdField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(returnUserIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Book ISBN:"), gbc);
        JTextField returnIsbnField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(returnIsbnField, gbc);

        JCheckBox simulateLateBox = new JCheckBox("Simulate 20 Days Late (for Fines)");
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        formPanel.add(simulateLateBox, gbc);

        JButton returnButton = new JButton("Return Book");
        gbc.gridy = 9;
        formPanel.add(returnButton, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Result Area
        JTextArea resultArea = new JTextArea(8, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Transaction Results / Recommendations"));
        add(scrollPane, BorderLayout.SOUTH);

        // Recommendations Button
        JButton recButton = new JButton("Show Recommendations");
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(recButton);
        add(topPanel, BorderLayout.NORTH);

        // Actions
        borrowButton.addActionListener(e -> {
            try {
                int userId = Integer.parseInt(borrowUserIdField.getText().trim());
                String isbn = borrowIsbnField.getText().trim();
                String result = libraryService.borrowBook(userId, isbn);
                resultArea.setText(result);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "User ID must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        returnButton.addActionListener(e -> {
            try {
                int userId = Integer.parseInt(returnUserIdField.getText().trim());
                String isbn = returnIsbnField.getText().trim();
                
                LocalDate returnDate = LocalDate.now();
                if (simulateLateBox.isSelected()) {
                    returnDate = LocalDate.now().plusDays(14 + 20); // 14 days borrow time + 20 days late
                }

                String result = libraryService.returnBook(userId, isbn, returnDate);
                resultArea.setText(result);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "User ID must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        recButton.addActionListener(e -> {
            String recs = libraryService.getRecommendationsString();
            resultArea.setText(recs);
        });
    }
}
