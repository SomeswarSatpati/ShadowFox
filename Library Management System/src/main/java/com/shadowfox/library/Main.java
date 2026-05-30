package com.shadowfox.library;

import com.shadowfox.library.dao.BookDAO;
import com.shadowfox.library.dao.UserDAO;
import com.shadowfox.library.model.Book;
import com.shadowfox.library.model.User;
import com.shadowfox.library.service.GoogleBooksAPI;
import com.shadowfox.library.service.LibraryService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserDAO userDAO = new UserDAO();
    private static final BookDAO bookDAO = new BookDAO();
    private static final GoogleBooksAPI googleBooksAPI = new GoogleBooksAPI();
    private static final LibraryService libraryService = new LibraryService();

    public static void main(String[] args) {
        System.out.println("Welcome to the Library Management System!");
        System.out.println("Make sure you have configured MySQL and executed schema.sql");

        while (true) {
            printMenu();
            int choice = getIntInput();

            switch (choice) {
                case 1:
                    addUser();
                    break;
                case 2:
                    fetchAndAddBook();
                    break;
                case 3:
                    borrowBook();
                    break;
                case 4:
                    returnBook();
                    break;
                case 5:
                    libraryService.displayRecommendations();
                    break;
                case 6:
                    viewAllBooks();
                    break;
                case 7:
                    viewAllUsers();
                    break;
                case 8:
                    System.out.println("Exiting the application. Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please select a number between 1 and 8.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n=== Library Menu ===");
        System.out.println("1. Add a New User");
        System.out.println("2. Fetch & Add Book via ISBN (Google Books API)");
        System.out.println("3. Borrow a Book");
        System.out.println("4. Return a Book (Simulate Late Return)");
        System.out.println("5. Get Book Recommendations");
        System.out.println("6. View All Books");
        System.out.println("7. View All Users");
        System.out.println("8. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void addUser() {
        System.out.print("Enter user name: ");
        String name = scanner.nextLine();
        System.out.print("Enter user email: ");
        String email = scanner.nextLine();

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setJoinDate(LocalDate.now());

        if (userDAO.addUser(user)) {
            System.out.println("User added successfully.");
        } else {
            System.out.println("Failed to add user (Email might already exist).");
        }
    }

    private static void fetchAndAddBook() {
        System.out.print("Enter ISBN to fetch (e.g., 9780134685991 for Effective Java): ");
        String isbn = scanner.nextLine();

        System.out.println("Fetching details from Google Books API...");
        Book book = googleBooksAPI.fetchBookByIsbn(isbn);
        
        if (book != null) {
            System.out.println("Found Book: " + book.getTitle() + " by " + book.getAuthor().getName());
            if (bookDAO.addBook(book)) {
                System.out.println("Book successfully added to the database.");
            } else {
                System.out.println("Failed to add book (ISBN might already exist).");
            }
        } else {
            System.out.println("Book not found via API.");
        }
    }

    private static void borrowBook() {
        System.out.print("Enter User ID: ");
        int userId = getIntInput();
        System.out.print("Enter Book ISBN: ");
        String isbn = scanner.nextLine();

        System.out.println(libraryService.borrowBook(userId, isbn));
    }

    private static void returnBook() {
        System.out.print("Enter User ID: ");
        int userId = getIntInput();
        System.out.print("Enter Book ISBN: ");
        String isbn = scanner.nextLine();
        
        System.out.print("Simulate a late return? (y/n): ");
        String simulate = scanner.nextLine();
        
        LocalDate returnDate = LocalDate.now();
        if (simulate.equalsIgnoreCase("y")) {
             System.out.print("Enter number of days late: ");
             int daysLate = getIntInput();
             returnDate = LocalDate.now().plusDays(14 + daysLate);
        }

        System.out.println(libraryService.returnBook(userId, isbn, returnDate));
    }

    private static void viewAllBooks() {
        List<Book> books = bookDAO.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books in the library.");
        } else {
            for (Book b : books) {
                System.out.println(b);
            }
        }
    }

    private static void viewAllUsers() {
        List<User> users = userDAO.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users registered.");
        } else {
            for (User u : users) {
                System.out.println(u);
            }
        }
    }

    private static int getIntInput() {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
}
