# Enhanced Precision Calculator

A Java-based calculator application built with a modern Swing Graphical User Interface (GUI). It uses `BigDecimal` for all its arithmetic operations, ensuring exact precision and avoiding common floating-point errors.

## Features

- **High Precision Arithmetic**: Uses `BigDecimal` to eliminate floating-point inaccuracies (e.g., `0.1 + 0.2` exactly equals `0.3`).
- **Standard Operations**: Addition, subtraction, multiplication, division, modulo, and square root.
- **Scientific Operations**: Power (`x^y`), natural logarithm (`ln`), base-10 logarithm (`log10`), and trigonometric functions (`sin`, `cos`, `tan`).
- **Unit Conversions**: Temperature conversions between Celsius, Fahrenheit, and Kelvin.
- **Currency Conversion**: Convert between various supported currencies using a built-in conversion rates map.
- **Clean Swing GUI**: A user-friendly graphical interface with a dedicated menu bar for advanced scientific and conversion tools.

## Prerequisites

- **Java Development Kit (JDK)**: Version 8 or higher must be installed on your machine.
- Verify your Java installation by opening a terminal/command prompt and running:
  ```bash
  java -version
  javac -version
  ```

## How to Compile and Run

1. **Open a terminal or command prompt**.
2. **Navigate to the source folder** containing the Java files:
   ```bash
   cd path/to/project/src
   ```
   *(Make sure to replace `path/to/project/src` with the actual path where you saved the code).*

3. **Compile the Java file**:
   ```bash
   javac Calculator.java
   ```

4. **Run the application**:
   ```bash
   java Calculator
   ```

## Usage

- **Basic Math**: Use the on-screen buttons to perform standard calculations.
- **Scientific Mode**: Click on the **Scientific** menu at the top of the window to access advanced mathematical operations.
- **Conversions**: Click on the **Conversion** menu to convert temperatures or open the currency converter dialog.

## Architecture

- **`Calculator`**: The main class that sets up the GUI and handles user interactions.
- **`ArithmeticEngine`**: Inner class that handles all math logic safely via `BigDecimal`.
- **`UnitConverter`**: Inner class for handling unit and currency conversions.
