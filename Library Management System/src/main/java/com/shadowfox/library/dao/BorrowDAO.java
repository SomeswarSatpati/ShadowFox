package com.shadowfox.library.dao;

import com.shadowfox.library.model.BorrowedBook;
import com.shadowfox.library.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {

    public boolean borrowBook(int userId, int bookId, LocalDate borrowDate, LocalDate dueDate) {
        String sql = "INSERT INTO borrowed_books (user_id, book_id, borrow_date, due_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            stmt.setDate(3, Date.valueOf(borrowDate));
            stmt.setDate(4, Date.valueOf(dueDate));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error borrowing book: " + e.getMessage());
            return false;
        }
    }

    public BorrowedBook getActiveBorrowRecord(int userId, int bookId) {
        String sql = "SELECT * FROM borrowed_books WHERE user_id = ? AND book_id = ? AND return_date IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBorrowedBook(rs);
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching borrow record: " + e.getMessage());
        }
        return null;
    }

    public boolean returnBook(int borrowId, LocalDate returnDate, double fineAmount) {
        String sql = "UPDATE borrowed_books SET return_date = ?, fine_amount = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(returnDate));
            stmt.setDouble(2, fineAmount);
            stmt.setInt(3, borrowId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error returning book: " + e.getMessage());
            return false;
        }
    }

    public List<Integer> getMostPopularBookIds(int limit) {
        List<Integer> bookIds = new ArrayList<>();
        String sql = "SELECT book_id, COUNT(*) as borrow_count FROM borrowed_books GROUP BY book_id ORDER BY borrow_count DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                bookIds.add(rs.getInt("book_id"));
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching popular books: " + e.getMessage());
        }
        return bookIds;
    }

    private BorrowedBook mapResultSetToBorrowedBook(ResultSet rs) throws SQLException {
        BorrowedBook bb = new BorrowedBook();
        bb.setId(rs.getInt("id"));
        bb.setUserId(rs.getInt("user_id"));
        bb.setBookId(rs.getInt("book_id"));
        bb.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
        bb.setDueDate(rs.getDate("due_date").toLocalDate());
        
        Date returnDate = rs.getDate("return_date");
        if (returnDate != null) {
            bb.setReturnDate(returnDate.toLocalDate());
        }
        bb.setFineAmount(rs.getDouble("fine_amount"));
        return bb;
    }
}
