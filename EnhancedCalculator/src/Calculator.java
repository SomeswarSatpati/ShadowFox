import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Calculator extends JFrame implements ActionListener {
    private final ArithmeticEngine arithmetic = new ArithmeticEngine();
    private final UnitConverter converter = new UnitConverter();

    private JTextField displayField;
    private BigDecimal firstOperand = null;
    private String currentOperator = "";
    private boolean isNewInput = true;

    public Calculator() {
        setTitle("Enhanced Precision Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 700);
        setLayout(new BorderLayout());

        displayField = new JTextField("0");
        displayField.setEditable(false);
        displayField.setFont(new Font("Arial", Font.BOLD, 36));
        displayField.setHorizontalAlignment(JTextField.RIGHT);
        displayField.setBackground(new Color(255, 251, 238));
        displayField.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(displayField, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] buttons = {
            "C", "sqrt", "%", "/",
            "7", "8", "9", "x",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "+/-", "0", ".", "="
        };

        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 22));
            button.setFocusPainted(false);
            if ("C".equals(text)) {
                button.setForeground(Color.RED);
            } else if ("+-x/=".contains(text)) {
                button.setForeground(new Color(0, 102, 204));
            }
            button.addActionListener(this);
            buttonPanel.add(button);
        }
        add(buttonPanel, BorderLayout.CENTER);
        
        setupMenu();
        setLocationRelativeTo(null);
    }

    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu scientificMenu = new JMenu("Scientific");
        String[] sciOps = {"Power (x^y)", "ln", "log10", "sin", "cos", "tan"};
        for (String op : sciOps) {
            JMenuItem item = new JMenuItem(op);
            item.addActionListener(e -> handleScientific(op));
            scientificMenu.add(item);
        }
        
        JMenu conversionMenu = new JMenu("Conversion");
        String[] convOps = {"C -> F", "F -> C", "C -> K", "K -> C", "F -> K", "K -> F", "Currency"};
        for (String op : convOps) {
            JMenuItem item = new JMenuItem(op);
            item.addActionListener(e -> handleConversion(op));
            conversionMenu.add(item);
        }

        menuBar.add(scientificMenu);
        menuBar.add(conversionMenu);
        setJMenuBar(menuBar);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new Calculator().setVisible(true);
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        try {
            if ("0123456789".contains(command)) {
                if (isNewInput) {
                    displayField.setText(command);
                    isNewInput = false;
                } else {
                    displayField.setText(displayField.getText() + command);
                }
            } else if (".".equals(command)) {
                if (isNewInput) {
                    displayField.setText("0.");
                    isNewInput = false;
                } else if (!displayField.getText().contains(".")) {
                    displayField.setText(displayField.getText() + ".");
                }
            } else if ("C".equals(command)) {
                displayField.setText("0");
                firstOperand = null;
                currentOperator = "";
                isNewInput = true;
            } else if ("+/-".equals(command)) {
                if (!displayField.getText().equals("0")) {
                    if (displayField.getText().startsWith("-")) {
                        displayField.setText(displayField.getText().substring(1));
                    } else {
                        displayField.setText("-" + displayField.getText());
                    }
                }
            } else if ("sqrt".equals(command)) {
                BigDecimal val = new BigDecimal(displayField.getText());
                displayField.setText(arithmetic.format(arithmetic.squareRoot(val)));
                isNewInput = true;
            } else if ("=".equals(command)) {
                calculateResult();
            } else if ("+".equals(command) || "-".equals(command) || "x".equals(command) || "/".equals(command) || "%".equals(command)) {
                if (!currentOperator.isEmpty() && !isNewInput) {
                    calculateResult();
                }
                firstOperand = new BigDecimal(displayField.getText());
                currentOperator = command;
                isNewInput = true;
            }
        } catch (Exception ex) {
            displayField.setText("Error");
            isNewInput = true;
        }
    }

    private void calculateResult() {
        if (firstOperand == null || currentOperator.isEmpty()) return;
        try {
            BigDecimal secondOperand = new BigDecimal(displayField.getText());
            BigDecimal result = BigDecimal.ZERO;
            switch (currentOperator) {
                case "+": result = arithmetic.add(firstOperand, secondOperand); break;
                case "-": result = arithmetic.subtract(firstOperand, secondOperand); break;
                case "x": result = arithmetic.multiply(firstOperand, secondOperand); break;
                case "/": result = arithmetic.divide(firstOperand, secondOperand); break;
                case "%": result = arithmetic.modulo(firstOperand, secondOperand); break;
                case "x^y": result = arithmetic.power(firstOperand, secondOperand); break;
            }
            displayField.setText(arithmetic.format(result));
            firstOperand = null;
            currentOperator = "";
            isNewInput = true;
        } catch (Exception e) {
            displayField.setText("Error");
            isNewInput = true;
        }
    }

    private void handleScientific(String op) {
        try {
            if ("Power (x^y)".equals(op)) {
                firstOperand = new BigDecimal(displayField.getText());
                currentOperator = "x^y";
                isNewInput = true;
                return;
            }
            
            BigDecimal val = new BigDecimal(displayField.getText());
            BigDecimal result = BigDecimal.ZERO;
            switch (op) {
                case "ln": result = arithmetic.naturalLog(val); break;
                case "log10": result = arithmetic.log10(val); break;
                case "sin": result = arithmetic.sine(val); break;
                case "cos": result = arithmetic.cosine(val); break;
                case "tan": result = arithmetic.tangent(val); break;
            }
            displayField.setText(arithmetic.format(result));
            isNewInput = true;
        } catch (Exception e) {
            displayField.setText("Error: " + e.getMessage());
            isNewInput = true;
        }
    }

    private void handleConversion(String op) {
        try {
            if ("Currency".equals(op)) {
                handleCurrencyConversion();
                return;
            }
            
            BigDecimal val = new BigDecimal(displayField.getText());
            BigDecimal result = BigDecimal.ZERO;
            switch (op) {
                case "C -> F": result = converter.celsiusToFahrenheit(val); break;
                case "F -> C": result = converter.fahrenheitToCelsius(val); break;
                case "C -> K": result = converter.celsiusToKelvin(val); break;
                case "K -> C": result = converter.kelvinToCelsius(val); break;
                case "F -> K": result = converter.fahrenheitToKelvin(val); break;
                case "K -> F": result = converter.kelvinToFahrenheit(val); break;
            }
            displayField.setText(arithmetic.format(result));
            isNewInput = true;
        } catch (Exception e) {
            displayField.setText("Error: " + e.getMessage());
            isNewInput = true;
        }
    }

    private void handleCurrencyConversion() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Amount:"));
        JTextField amountField = new JTextField(displayField.getText());
        panel.add(amountField);
        
        panel.add(new JLabel("From:"));
        JComboBox<String> fromBox = new JComboBox<>(converter.getSupportedCurrencyCodes().toArray(new String[0]));
        panel.add(fromBox);
        
        panel.add(new JLabel("To:"));
        JComboBox<String> toBox = new JComboBox<>(converter.getSupportedCurrencyCodes().toArray(new String[0]));
        panel.add(toBox);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Currency Conversion", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                BigDecimal amt = new BigDecimal(amountField.getText());
                String from = (String) fromBox.getSelectedItem();
                String to = (String) toBox.getSelectedItem();
                BigDecimal converted = converter.convertCurrency(amt, from, to);
                displayField.setText(arithmetic.format(converted));
                isNewInput = true;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Conversion Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    //  INNER CLASS: ArithmeticEngine
    //  All arithmetic & scientific math via BigDecimal.
    //  OOP: Encapsulation -- math logic is hidden here, not scattered.
    // =========================================================================
    static class ArithmeticEngine {
        private static final MathContext PRECISION     = new MathContext(10, RoundingMode.HALF_UP);
        private static final int         DISPLAY_SCALE = 10;

        public BigDecimal add(BigDecimal a, BigDecimal b)      { return a.add(b); }
        public BigDecimal subtract(BigDecimal a, BigDecimal b) { return a.subtract(b); }
        public BigDecimal multiply(BigDecimal a, BigDecimal b) { return a.multiply(b); }

        public BigDecimal divide(BigDecimal a, BigDecimal b) {
            if (b.compareTo(BigDecimal.ZERO) == 0)
                throw new ArithmeticException("Division by zero is undefined.");
            return a.divide(b, DISPLAY_SCALE, RoundingMode.HALF_UP);
        }

        public BigDecimal modulo(BigDecimal a, BigDecimal b) {
            if (b.compareTo(BigDecimal.ZERO) == 0)
                throw new ArithmeticException("Modulo by zero is undefined.");
            return a.remainder(b);
        }

        public BigDecimal squareRoot(BigDecimal a) {
            if (a.compareTo(BigDecimal.ZERO) < 0)
                throw new ArithmeticException("Square root of a negative number is not real.");
            return a.sqrt(PRECISION); // Java 9+; use Math.sqrt() for Java 8
        }

        /** Integer exponents: exact via BigDecimal.pow(). Fractional: delegates to Math.pow(). */
        public BigDecimal power(BigDecimal base, BigDecimal exponent) {
            try {
                return base.pow(exponent.intValueExact(), PRECISION);
            } catch (ArithmeticException e) {
                return new BigDecimal(Math.pow(base.doubleValue(), exponent.doubleValue()), PRECISION);
            }
        }

        public BigDecimal naturalLog(BigDecimal a) {
            if (a.compareTo(BigDecimal.ZERO) <= 0)
                throw new ArithmeticException("Logarithm is only defined for positive numbers.");
            return new BigDecimal(Math.log(a.doubleValue()), PRECISION);
        }

        public BigDecimal log10(BigDecimal a) {
            if (a.compareTo(BigDecimal.ZERO) <= 0)
                throw new ArithmeticException("Logarithm is only defined for positive numbers.");
            return new BigDecimal(Math.log10(a.doubleValue()), PRECISION);
        }

        public BigDecimal sine(BigDecimal degrees) {
            return new BigDecimal(Math.sin(Math.toRadians(degrees.doubleValue())), PRECISION);
        }

        public BigDecimal cosine(BigDecimal degrees) {
            return new BigDecimal(Math.cos(Math.toRadians(degrees.doubleValue())), PRECISION);
        }

        public BigDecimal tangent(BigDecimal degrees) {
            double d = degrees.doubleValue() % 360;
            if (d == 90 || d == 270)
                throw new ArithmeticException("tan(" + d + " deg) is undefined.");
            return new BigDecimal(Math.tan(Math.toRadians(d)), PRECISION);
        }

        public String format(BigDecimal v) { return v.stripTrailingZeros().toPlainString(); }
    }

    // =========================================================================
    //  INNER CLASS: UnitConverter
    //  OOP: Abstraction -- caller says "convert 100C to F", not "apply formula".
    // =========================================================================
    static class UnitConverter {
        private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);

        // Map design: adding a new currency = 1 new line. No new methods needed.
        // Production note: fetch rates from a live API (Fixer.io, Open Exchange Rates).
        private static final Map<String, BigDecimal> RATES_FROM_USD = new LinkedHashMap<>();
        static {
            RATES_FROM_USD.put("USD", BigDecimal.ONE);
            RATES_FROM_USD.put("INR", new BigDecimal("83.50"));
            RATES_FROM_USD.put("EUR", new BigDecimal("0.92"));
            RATES_FROM_USD.put("GBP", new BigDecimal("0.79"));
            RATES_FROM_USD.put("JPY", new BigDecimal("149.50"));
            RATES_FROM_USD.put("AUD", new BigDecimal("1.53"));
            RATES_FROM_USD.put("CAD", new BigDecimal("1.36"));
            RATES_FROM_USD.put("CNY", new BigDecimal("7.24"));
        }

        public BigDecimal celsiusToFahrenheit(BigDecimal c) {
            return c.multiply(new BigDecimal("9")).divide(new BigDecimal("5"), MC)
                    .add(new BigDecimal("32"));
        }
        public BigDecimal fahrenheitToCelsius(BigDecimal f) {
            return f.subtract(new BigDecimal("32")).multiply(new BigDecimal("5"))
                    .divide(new BigDecimal("9"), MC);
        }
        public BigDecimal celsiusToKelvin(BigDecimal c)    { return c.add(new BigDecimal("273.15")); }
        public BigDecimal kelvinToCelsius(BigDecimal k) {
            BigDecimal r = k.subtract(new BigDecimal("273.15"));
            if (r.compareTo(new BigDecimal("-273.15")) < 0)
                throw new ArithmeticException("Temperature below absolute zero (0 K) is impossible.");
            return r;
        }
        public BigDecimal fahrenheitToKelvin(BigDecimal f) { return celsiusToKelvin(fahrenheitToCelsius(f)); }
        public BigDecimal kelvinToFahrenheit(BigDecimal k) { return celsiusToFahrenheit(kelvinToCelsius(k)); }

        /**
         * Two-step USD pivot: source -> USD -> target.
         * Requires only N rates for N currencies (not N*N pairs).
         */
        public BigDecimal convertCurrency(BigDecimal amt, String from, String to) {
            from = from.toUpperCase().trim(); to = to.toUpperCase().trim();
            if (!RATES_FROM_USD.containsKey(from)) throw new IllegalArgumentException("Unsupported: " + from);
            if (!RATES_FROM_USD.containsKey(to))   throw new IllegalArgumentException("Unsupported: " + to);
            BigDecimal inUSD = amt.divide(RATES_FROM_USD.get(from), MC);
            return inUSD.multiply(RATES_FROM_USD.get(to)).setScale(2, RoundingMode.HALF_UP);
        }

        public String getSupportedCurrencies() {
            StringBuilder sb = new StringBuilder();
            int i = 1;
            for (Map.Entry<String, BigDecimal> e : RATES_FROM_USD.entrySet())
                sb.append(String.format("  %d. %-4s (1 USD = %s)%n", i++, e.getKey(), e.getValue().toPlainString()));
            return sb.toString();
        }

        public Set<String> getSupportedCurrencyCodes() { return RATES_FROM_USD.keySet(); }
    }
}