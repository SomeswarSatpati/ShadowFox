import java.util.regex.Pattern;

public class Contact {
    private static int nextId = 1;

    private int id;
    private String name;
    private String phone;
    private String email;

    // Regex for basic validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9\\-\\s]+$");

    public Contact(String name, String phone, String email) {
        this.id = nextId++;
        setName(name);
        setPhone(phone);
        setEmail(email);
    }
    
    // Used when importing to manually set ID to avoid conflicts if needed, or just let it auto-increment
    public Contact(int id, String name, String phone, String email) {
        this.id = id;
        if (id >= nextId) {
            nextId = id + 1;
        }
        setName(name);
        setPhone(phone);
        setEmail(email);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        this.name = name.trim();
    }

    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be empty.");
        }
        String trimmedPhone = phone.trim();
        if (!PHONE_PATTERN.matcher(trimmedPhone).matches()) {
            throw new IllegalArgumentException("Invalid phone number format. Only numbers, +, -, and spaces are allowed.");
        }
        this.phone = trimmedPhone;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        String trimmedEmail = email.trim();
        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        this.email = trimmedEmail;
    }

    @Override
    public String toString() {
        return name;
    }
}
