package com.shadowfox.library.service;

import com.shadowfox.library.dao.BookDAO;
import com.shadowfox.library.dao.BorrowDAO;
import com.shadowfox.library.dao.UserDAO;
import com.shadowfox.library.model.Book;
import com.shadowfox.library.model.BorrowedBook;
import com.shadowfox.library.model.User;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class LibraryService {

    private final BookDAO bookDAO;
    private final UserDAO userDAO;
    private final BorrowDAO borrowDAO;
    
    private static final int BORROW_DAYS = 14;
    private static final double FINE_PER_DAY = 0.50; // $0.50 per day overdue

    public LibraryService() {
        this.bookDAO = new BookDAO();
        this.userDAO = new UserDAO();
        this.borrowDAO = new BorrowDAO();
    }

    public String borrowBook(int userId, String isbn) {
        User user = userDAO.getUserById(userId);
        if (user == null) {
            return "Error: User not found.";
        }

        Book book = bookDAO.getBookByIsbn(isbn);
        if (book == null) {
            return "Error: Book not found in library.";
        }

        BorrowedBook activeRecord = borrowDAO.getActiveBorrowRecord(userId, book.getId());
        if (activeRecord != null) {
             return "Error: User already has this book checked out.";
        }

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(BORROW_DAYS);

        boolean success = borrowDAO.borrowBook(userId, book.getId(), borrowDate, dueDate);
        if (success) {
            return "Successfully borrowed '" + book.getTitle() + "'. Due date: " + dueDate;
        }
        return "Error: Failed to borrow book due to database error.";
    }

    public String returnBook(int userId, String isbn, LocalDate returnDateOverride) {
        Book book = bookDAO.getBookByIsbn(isbn);
        if (book == null) {
            return "Error: Book not found.";
        }

        BorrowedBook activeRecord = borrowDAO.getActiveBorrowRecord(userId, book.getId());
        if (activeRecord == null) {
            return "Error: No active borrow record found for this user and book.";
        }

        LocalDate returnDate = returnDateOverride != null ? returnDateOverride : LocalDate.now();
        double fineAmount = calculateFine(activeRecord.getDueDate(), returnDate);

        boolean success = borrowDAO.returnBook(activeRecord.getId(), returnDate, fineAmount);
        if (success) {
            if (fineAmount > 0) {
                return String.format("Book returned successfully. An overdue fine of $%.2f has been applied.", fineAmount);
            } else {
                return "Book returned on time. No fine.";
            }
        }
        return "Error: Failed to return book due to database error.";
    }

    private double calculateFine(LocalDate dueDate, LocalDate returnDate) {
        if (returnDate.isAfter(dueDate)) {
            long daysOverdue = ChronoUnit.DAYS.between(dueDate, returnDate);
            return daysOverdue * FINE_PER_DAY;
        }
        return 0.0;
    }

    public String getRecommendationsString() {
        List<Integer> popularIds = borrowDAO.getMostPopularBookIds(5);
        if (popularIds.isEmpty()) {
            return "Not enough data to generate recommendations. (Try borrowing some books!)";
        }

        StringBuilder sb = new StringBuilder("--- Recommended Books (Most Borrowed) ---\n");
        List<Book> allBooks = bookDAO.getAllBooks();
        
        for (Integer bookId : popularIds) {
            for (Book b : allBooks) {
                if (b.getId() == bookId) {
                    sb.append("- ").append(b.getTitle()).append(" by ").append(b.getAuthor().getName()).append("\n");
                    break;
                }
            }
        }
        return sb.toString();
    }
    
    // Kept for CLI compatibility
    public void displayRecommendations() {
        System.out.println(getRecommendationsString());
    }
}
