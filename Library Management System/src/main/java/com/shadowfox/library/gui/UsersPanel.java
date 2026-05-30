package com.shadowfox.library.gui;

import com.shadowfox.library.dao.UserDAO;
import com.shadowfox.library.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class UsersPanel extends JPanel {
    private final UserDAO userDAO;
    private DefaultTableModel tableModel;
    private JTable usersTable;

    public UsersPanel(UserDAO userDAO) {
        this.userDAO = userDAO;
        setLayout(new BorderLayout());

        // Top Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(12);
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(12);
        JButton addButton = new JButton("Add User");
        JButton refreshButton = new JButton("Refresh Table");

        controlPanel.add(nameLabel);
        controlPanel.add(nameField);
        controlPanel.add(emailLabel);
        controlPanel.add(emailField);
        controlPanel.add(addButton);
        controlPanel.add(refreshButton);

        add(controlPanel, BorderLayout.NORTH);

        // Table Data
        String[] columnNames = {"ID", "Name", "Email", "Join Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        usersTable = new JTable(tableModel);
        usersTable.setFillsViewportHeight(true);
        add(new JScrollPane(usersTable), BorderLayout.CENTER);

        // Actions
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            
            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Email are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setJoinDate(LocalDate.now());

            if (userDAO.addUser(user)) {
                JOptionPane.showMessageDialog(this, "User added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                nameField.setText("");
                emailField.setText("");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add user. Email might exist.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshButton.addActionListener(e -> refreshTable());

        // Initial Load
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<User> users = userDAO.getAllUsers();
        for (User u : users) {
            Object[] row = {
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getJoinDate()
            };
            tableModel.addRow(row);
        }
    }
}
