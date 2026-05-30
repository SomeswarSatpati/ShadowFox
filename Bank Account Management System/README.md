# Bank Account Management System

A Java-based Bank Account Management System built with Maven and MySQL. 

## Prerequisites

To run this project on your local machine, ensure you have the following installed:
- **Java Development Kit (JDK) 24** (or compatible version)
- **Apache Maven** (for dependency management and building)
- **MySQL Server** (for the database)

## Database Setup

This application uses a MySQL database to store transaction records. Follow these steps to set up the database before running the application:

1. **Start MySQL Server** on your machine.
2. **Create the Database:**
   Log into your MySQL console and create a new database named `banking_db`:
   ```sql
   CREATE DATABASE banking_db;
   USE banking_db;
   ```
3. **Create the Transactions Table:**
   Run the following SQL command to create the necessary table structure:
   ```sql
   CREATE TABLE transactions (
       id INT AUTO_INCREMENT PRIMARY KEY,
       account_id VARCHAR(50) NOT NULL,
       type VARCHAR(20) NOT NULL,
       amount DOUBLE NOT NULL,
       timestamp DATETIME NOT NULL
   );
   ```
4. **Configure Database Credentials:**
   The application connects to the database using the following default configuration in `src/main/java/com/shadowfox/banking/management/DatabaseConnection.java`:
   - **URL**: `jdbc:mysql://localhost:3306/banking_db`
   - **Username**: `root`

   **Important**: Before running the program, open `DatabaseConnection.java` and update the `PASSWORD` constant (e.g., `private static final String PASSWORD = "your_mysql_password";`) to match your local MySQL password.

## Running the Application

1. **Open the Project:**
   Place the project directory anywhere on your computer and open a terminal in the root folder (where the `pom.xml` file is located).

2. **Build the Project with Maven:**
   Run the following command to download dependencies and compile the code:
   ```bash
   mvn clean install
   ```

3. **Run the Application:**
   After building successfully, run the main application class using Maven:
   ```bash
   mvn exec:java -Dexec.mainClass="com.shadowfox.banking.management.Main"
   ```
   *Alternatively, you can open the project in an IDE (such as IntelliJ IDEA, Eclipse, or VS Code) and run the `Main.java` class directly.*

## Technologies Used
- Java 24
- Maven
- MySQL Connector/J
- JUnit 5 & Mockito (for Testing)
