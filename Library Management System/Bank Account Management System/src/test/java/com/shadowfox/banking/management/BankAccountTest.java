package com.shadowfox.banking.management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BankAccountTest {

    @Mock
    private DatabaseConnection databaseConnection;

    private BankAccount bankAccount;

    @BeforeEach
    void setUp() {
        bankAccount = new BankAccount("ACC-123", 1000.0, databaseConnection);
    }

    // Baseline tests
    @Test
    void testDeposit() {
        bankAccount.deposit(500.0);
        assertEquals(1500.0, bankAccount.getBalance(), "Balance should increase by 500");
        verify(databaseConnection, times(1)).saveTransaction(eq("ACC-123"), any(Transaction.class));
    }

    @Test
    void testWithdraw() {
        bankAccount.withdraw(200.0);
        assertEquals(800.0, bankAccount.getBalance(), "Balance should decrease by 200");
        verify(databaseConnection, times(1)).saveTransaction(eq("ACC-123"), any(Transaction.class));
    }

    @Test
    void testBalanceInquiry() {
        assertEquals(1000.0, bankAccount.getBalance(), "Initial balance should be 1000");
    }

    // Negative tests
    @Test
    void testWithdrawMoreThanBalance() {
        Exception exception = assertThrows(InsufficientFundsException.class, () -> {
            bankAccount.withdraw(1200.0);
        });
        assertTrue(exception.getMessage().contains("insufficient funds"));
        verify(databaseConnection, never()).saveTransaction(anyString(), any(Transaction.class));
    }

    @Test
    void testDepositNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            bankAccount.deposit(-50.0);
        });
    }

    @Test
    void testWithdrawNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            bankAccount.withdraw(-50.0);
        });
    }

    // Tier 1: Transaction History
    @Test
    void testTransactionHistorySizeIncreases() {
        assertEquals(0, bankAccount.getTransactions().size(), "Initially no transactions");
        
        bankAccount.deposit(100.0);
        assertEquals(1, bankAccount.getTransactions().size(), "One transaction after deposit");
        assertEquals("DEPOSIT", bankAccount.getTransactions().get(0).getType());

        bankAccount.withdraw(50.0);
        assertEquals(2, bankAccount.getTransactions().size(), "Two transactions after withdrawal");
        assertEquals("WITHDRAWAL", bankAccount.getTransactions().get(1).getType());
    }

    @Test
    void testPrintMiniStatement() {
        bankAccount.deposit(100.0);
        bankAccount.withdraw(50.0);
        // Ensure no exceptions are thrown during print
        assertDoesNotThrow(() -> bankAccount.printMiniStatement());
    }

    // Tier 2: Concurrency Safety
    @Test
    void testConcurrencySafetyOnWithdraw() throws InterruptedException {
        // Initial balance is 1000
        int numberOfThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        Runnable withdrawTask = () -> {
            try {
                bankAccount.withdraw(500.0);
            } catch (Exception e) {
                // Ignore for this test
            } finally {
                latch.countDown();
            }
        };

        // Two threads trying to withdraw 500 at the same time
        executorService.submit(withdrawTask);
        executorService.submit(withdrawTask);

        latch.await();

        // Since balance was 1000, two withdrawals of 500 should leave exactly 0.
        assertEquals(0.0, bankAccount.getBalance(), "Final balance should be 0.0 after two 500 withdrawals from 1000");
        assertEquals(2, bankAccount.getTransactions().size());
        verify(databaseConnection, times(2)).saveTransaction(eq("ACC-123"), any(Transaction.class));
    }

    @Test
    void testConcurrencySafetyWithInsufficientFunds() throws InterruptedException {
        // Initial balance is 1000
        int numberOfThreads = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        Runnable withdrawTask = () -> {
            try {
                bankAccount.withdraw(500.0);
            } catch (InsufficientFundsException e) {
                // Expected for the 3rd thread
            } finally {
                latch.countDown();
            }
        };

        // Three threads trying to withdraw 500. Only two should succeed.
        executorService.submit(withdrawTask);
        executorService.submit(withdrawTask);
        executorService.submit(withdrawTask);

        latch.await();

        assertEquals(0.0, bankAccount.getBalance(), "Final balance should be 0.0 after multiple withdrawals");
        // Only two transactions should be successfully added
        assertEquals(2, bankAccount.getTransactions().size());
        verify(databaseConnection, times(2)).saveTransaction(eq("ACC-123"), any(Transaction.class));
    }
}
