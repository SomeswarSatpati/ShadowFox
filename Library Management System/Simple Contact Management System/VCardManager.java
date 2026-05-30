import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VCardManager {

    /**
     * Exports a list of Contacts to a .vcf file.
     */
    public static void exportContacts(List<Contact> contacts, File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Contact contact : contacts) {
                writer.println("BEGIN:VCARD");
                writer.println("VERSION:3.0");
                writer.println("FN:" + contact.getName());
                writer.println("TEL:" + contact.getPhone());
                writer.println("EMAIL:" + contact.getEmail());
                writer.println("END:VCARD");
            }
        }
    }

    /**
     * Imports contacts from a .vcf file.
     */
    public static List<Contact> importContacts(File file) throws IOException {
        List<Contact> importedContacts = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String currentName = null;
            String currentPhone = null;
            String currentEmail = null;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                if (line.equals("BEGIN:VCARD")) {
                    currentName = null;
                    currentPhone = null;
                    currentEmail = null;
                } else if (line.startsWith("FN:")) {
                    currentName = line.substring(3).trim();
                } else if (line.startsWith("TEL:")) {
                    currentPhone = line.substring(4).trim();
                } else if (line.startsWith("EMAIL:")) {
                    currentEmail = line.substring(6).trim();
                } else if (line.equals("END:VCARD")) {
                    // Reached the end of a contact block, try to create and add it
                    if (currentName != null && currentPhone != null && currentEmail != null) {
                        try {
                            Contact contact = new Contact(currentName, currentPhone, currentEmail);
                            importedContacts.add(contact);
                        } catch (IllegalArgumentException e) {
                            // Skip invalid contacts during import, or log them
                            System.err.println("Failed to import contact: " + currentName + " due to validation error: " + e.getMessage());
                        }
                    }
                }
            }
        }
        
        return importedContacts;
    }
}
