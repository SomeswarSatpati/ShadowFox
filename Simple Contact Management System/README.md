# Simple Contact Management System

A desktop application built using **Java Swing** that allows you to manage your contacts (Add, View, Update, Delete). It features data validation, duplicate checks, real-time search filtering, and VCard (.vcf) Export/Import.

## 💾 Database Information

**No external database (like MySQL, PostgreSQL, or SQLite) is required.** 
The application utilizes **In-Memory Storage** (using an `ArrayList`) to store contacts while the application is running. 

If you want to save your contacts permanently so you don't lose them when closing the app, use the **Export to VCard** and **Import from VCard** buttons located at the bottom of the interface!

## 🚀 How to Run the Application

Since this application is written in standard Java and uses the built-in Java Swing UI framework, you do not need any build tools like Maven or Gradle.

### Prerequisites
Make sure you have the Java Development Kit (JDK) installed. You can verify your Java version by opening your terminal and typing:
```bash
java -version
```

### Steps to Compile and Run

1. **Open your Terminal (or Command Prompt).**
2. **Navigate** to the folder containing the `.java` files. Replace the path below with wherever you downloaded the project:
   ```bash
   cd path/to/Simple-Contact-Management-System
   ```
3. **Compile all the Java files** using `javac`:
   ```bash
   javac *.java
   ```
4. **Run the Application** using `java`:
   ```bash
   java ContactManagerGUI
   ```

A window will pop up automatically, allowing you to start managing your contacts!

## ✨ Features
* **Regex Validation:** Email strings and Phone Numbers are verified.
* **Duplicate Detection:** Prevents two contacts from sharing the same phone number.
* **Dynamic Search:** Case-insensitive search bar that filters the table as you type.
* **Data Binding:** Double-click any row on the grid to immediately bring up the update dialog.
* **VCard Parser:** Export/Import `.vcf` files utilizing a custom standard parser strategy.
