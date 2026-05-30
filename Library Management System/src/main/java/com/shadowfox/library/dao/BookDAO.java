package com.shadowfox.library.dao;

import com.shadowfox.library.model.Author;
import com.shadowfox.library.model.Book;
import com.shadowfox.library.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public int ensureAuthorExists(String authorName) throws SQLException {
        String checkSql = "SELECT id FROM authors WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, authorName);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }

        String insertSql = "INSERT INTO authors (name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, authorName);
            insertStmt.executeUpdate();
            ResultSet rs = insertStmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Failed to create author.");
        }
    }

    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (isbn, title, author_id, published_year) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int authorId = ensureAuthorExists(book.getAuthor().getName());

            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setInt(3, authorId);
            if (book.getPublishedYear() != null) {
                stmt.setInt(4, book.getPublishedYear());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error adding book: " + e.getMessage());
            return false;
        }
    }

    public Book getBookByIsbn(String isbn) {
        String sql = "SELECT b.*, a.name AS author_name FROM books b JOIN authors a ON b.author_id = a.id WHERE b.isbn = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBook(rs);
            }
        } catch (SQLException e) {
             System.err.println("Database error fetching book: " + e.getMessage());
        }
        return null;
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, a.name AS author_name FROM books b JOIN authors a ON b.author_id = a.id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
             System.err.println("Database error fetching books: " + e.getMessage());
        }
        return books;
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setAuthorId(rs.getInt("author_id"));
        
        int year = rs.getInt("published_year");
        book.setPublishedYear(rs.wasNull() ? null : year);

        Author author = new Author(rs.getInt("author_id"), rs.getString("author_name"));
        book.setAuthor(author);

        return book;
    }
}
