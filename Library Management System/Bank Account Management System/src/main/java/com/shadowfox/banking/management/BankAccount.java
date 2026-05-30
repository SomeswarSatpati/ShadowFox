package com.shadowfox.banking.management;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BankAccount {
    private String accountId;
    private double balance;
    private List<Transaction> transactions;
    private DatabaseConnection databaseConnection;

    public BankAccount(String accountId, double initialBalance, DatabaseConnection databaseConnection) {
        this.accountId = accountId;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
        this.databaseConnection = databaseConnection;
    }

    public synchronized void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        balance += amount;
        Transaction transaction = new Transaction("DEPOSIT", amount, LocalDateTime.now());
        transactions.add(transaction);
        
        if (databaseConnection != null) {
            databaseConnection.saveTransaction(accountId, transaction);
        }
    }

    public synchronized void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be positive");
        }
        if (amount > balance) {
            throw new InsufficientFundsException("Withdrawal failed: insufficient funds. Balance: " + balance + ", Requested: " + amount);
        }
        balance -= amount;
        Transaction transaction = new Transaction("WITHDRAWAL", amount, LocalDateTime.now());
        transactions.add(transaction);

        if (databaseConnection != null) {
            databaseConnection.saveTransaction(accountId, transaction);
        }
    }

    public synchronized double getBalance() {
        return balance;
    }

    public synchronized List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public synchronized void printMiniStatement() {
        System.out.println("Mini Statement for Account: " + accountId);
        System.out.println("Current Balance: ₹" + balance);
        System.out.println("Recent Transactions:");
        for (Transaction t : transactions) {
            System.out.println(t.toString());
        }
    }
}
