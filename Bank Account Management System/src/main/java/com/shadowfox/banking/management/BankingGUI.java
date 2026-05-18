package com.shadowfox.banking.management;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

public class BankingGUI extends JFrame {

    private BankAccount currentAccount;
    private DatabaseConnection databaseConnection;

    // UI Components
    private JTextField accountIdField;
    private JTextField initialBalanceField;
    private JButton loginButton;
    private JButton viewAllButton;

    private JLabel balanceLabel;
    private JTextArea statementArea;

    private JTextField amountField;
    private JButton depositButton;
    private JButton withdrawButton;
    private JButton refreshStatementButton;

    public BankingGUI(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        setTitle("Bank Account Management System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // Top Panel for Account Setup/Login
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Account Setup"));

        topPanel.add(new JLabel("Account ID:"));
        accountIdField = new JTextField(10);
        topPanel.add(accountIdField);

        topPanel.add(new JLabel("Initial Balance:"));
        initialBalanceField = new JTextField(8);
        topPanel.add(initialBalanceField);

        loginButton = new JButton("Login / Create");
        topPanel.add(loginButton);

        viewAllButton = new JButton("View All DB Accounts");
        topPanel.add(viewAllButton);

        add(topPanel, BorderLayout.NORTH);

        // Center Panel for Balance and Statement
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        balanceLabel = new JLabel("Current Balance: ₹0.00");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        centerPanel.add(balanceLabel, BorderLayout.NORTH);

        statementArea = new JTextArea();
        statementArea.setEditable(false);
        statementArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(statementArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Transaction History"));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel for Operations
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Operations"));

        bottomPanel.add(new JLabel("Amount: ₹"));
        amountField = new JTextField(8);
        bottomPanel.add(amountField);

        depositButton = new JButton("Deposit");
        withdrawButton = new JButton("Withdraw");
        refreshStatementButton = new JButton("Refresh Statement");

        bottomPanel.add(depositButton);
        bottomPanel.add(withdrawButton);
        bottomPanel.add(refreshStatementButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // Disable operations until logged in
        setOperationsEnabled(false);

        // Add Listeners
        setupListeners();
    }

    private void setupListeners() {
        loginButton.addActionListener(e -> {
            String accountId = accountIdField.getText().trim();
            String balanceStr = initialBalanceField.getText().trim();

            if (accountId.isEmpty() || balanceStr.isEmpty()) {
                showError("Please enter both Account ID and Initial Balance.");
                return;
            }

            try {
                double initialBalance = Double.parseDouble(balanceStr);
                if (initialBalance < 0) {
                    showError("Initial balance cannot be negative.");
                    return;
                }
                
                // Initialize the account
                currentAccount = new BankAccount(accountId, initialBalance, databaseConnection);
                
                setOperationsEnabled(true);
                updateUIState();
                statementArea.setText("Account " + accountId + " initialized with balance ₹" + initialBalance + "\n");
                accountIdField.setEnabled(false);
                initialBalanceField.setEnabled(false);
                loginButton.setEnabled(false);
                
            } catch (NumberFormatException ex) {
                showError("Please enter a valid numeric value for Initial Balance.");
            }
        });

        depositButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                currentAccount.deposit(amount);
                updateUIState();
                amountField.setText("");
            } catch (NumberFormatException ex) {
                showError("Please enter a valid amount.");
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });

        withdrawButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                currentAccount.withdraw(amount);
                updateUIState();
                amountField.setText("");
            } catch (NumberFormatException ex) {
                showError("Please enter a valid amount.");
            } catch (IllegalArgumentException | InsufficientFundsException ex) {
                showError(ex.getMessage());
            }
        });

        refreshStatementButton.addActionListener(e -> updateUIState());

        viewAllButton.addActionListener(e -> {
            Map<String, Double> allAccounts = databaseConnection.getAllAccounts();
            if (allAccounts == null || allAccounts.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No accounts found in database or connection failed.", "Database Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create Table Data
            String[] columnNames = {"Account ID", " Transaction (₹)"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
            for (Map.Entry<String, Double> entry : allAccounts.entrySet()) {
                tableModel.addRow(new Object[]{entry.getKey(), String.format("%.2f", entry.getValue())});
            }

            JTable table = new JTable(tableModel);
            table.setEnabled(false); // Make it read-only
            
            JDialog dialog = new JDialog(this, "All Database Accounts", true);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);
            dialog.add(new JScrollPane(table));
            dialog.setVisible(true);
        });
    }

    private void setOperationsEnabled(boolean enabled) {
        amountField.setEnabled(enabled);
        depositButton.setEnabled(enabled);
        withdrawButton.setEnabled(enabled);
        refreshStatementButton.setEnabled(enabled);
    }

    private void updateUIState() {
        if (currentAccount != null) {
            balanceLabel.setText(String.format("Current Balance: ₹%.2f", currentAccount.getBalance()));
            
            StringBuilder sb = new StringBuilder();
            List<Transaction> transactions = currentAccount.getTransactions();
            if (transactions.isEmpty()) {
                sb.append("No transactions yet.\n");
            } else {
                for (Transaction t : transactions) {
                    sb.append(t.toString()).append("\n");
                }
            }
            statementArea.setText(sb.toString());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
