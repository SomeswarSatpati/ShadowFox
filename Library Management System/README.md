# Library Management System

A robust Java-based Library Management System that uses MySQL for data persistence, implements the DAO pattern, utilizes `PreparedStatement` to prevent SQL Injection, and features Google Books API integration.

## Features
- **User Accounts:** Manage library users.
- **Book Management:** Add and manage books and authors.
- **Borrowing System:** Checkout books and calculate overdue fines using `java.time` APIs.
- **Book Recommendations:** Recommend popular books to users.
- **Google Books Integration:** Automatically fetch book details by ISBN using the modern `java.net.http.HttpClient`.

## Prerequisites
- **Java 17 or higher**
- **Maven**
- **MySQL Server** (Running locally or remotely)

## Database Setup (Crucial Step)

Before running the application, you **must** configure the MySQL database.

1. Open your MySQL client (e.g., MySQL Workbench, DBeaver, or command line).
2. Connect to your MySQL server as `root` (or a user with permissions to create databases).
3. Open the `schema.sql` file located in the root directory of this project.
4. Execute the entire `schema.sql` script. This will:
   - Create the `library_db` database.
   - Create the `authors`, `books`, `users`, and `borrowed_books` tables.
   - Insert some initial sample data so you can test the application immediately.

## Application Configuration

By default, the application is configured to connect to MySQL on `localhost:3306` with the username `root` and password `root`. 

If your MySQL credentials are different:
1. Open `src/main/java/com/shadowfox/library/util/DatabaseConnection.java`.
2. Modify the `URL`, `USER`, and `PASSWORD` constants to match your MySQL setup.

## How to Run

### Using Maven (Command Line)

1. Open a terminal (Command Prompt, PowerShell, or bash) in the project's root directory (where `pom.xml` is located).
2. Compile the project:
   ```bash
   mvn clean compile
   ```
3. Run the application using the `exec-maven-plugin` (or directly via `java -cp` after packaging):
   ```bash
   mvn exec:java -Dexec.mainClass="com.shadowfox.library.Main"
   ```

### Using an IDE (IntelliJ IDEA, Eclipse, VS Code) - Recommended

1. Open the project folder in your IDE as a Maven project.
2. The IDE will automatically download the required dependencies (`mysql-connector-j` and `gson`).
3. Locate `src/main/java/com/shadowfox/library/gui/LibraryGUI.java`.
4. Right-click and select **Run 'LibraryGUI.main()'** to launch the graphical interface.

*Note: If you prefer the old terminal version, you can still run `Main.java` instead.*

## Usage Instructions (GUI)

Once the application is running, a window will appear with three tabs:

1. **Books Tab:** View the entire library catalogue. Use the "Fetch via ISBN" field to automatically download book data from Google Books and save it to your database.
2. **Users Tab:** View registered users or register new users.
3. **Transactions Tab:** Enter a User ID and Book ISBN to borrow or return books. The Return section includes a checkbox to simulate a late return so you can see the automatic fine calculation in action.
