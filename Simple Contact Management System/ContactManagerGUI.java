import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactManagerGUI extends JFrame {
    
    private List<Contact> contacts = new ArrayList<>();
    private ContactTableModel tableModel;
    private JTable contactTable;
    private TableRowSorter<ContactTableModel> rowSorter;
    
    public ContactManagerGUI() {
        setTitle("Simple Contact Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Setup UI
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        
        // --- Top Panel: Search ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        
        JLabel searchLabel = new JLabel("Search by Name:");
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");
        
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim().toLowerCase();
            if (!query.isEmpty()) {
                rowSorter.setRowFilter(new RowFilter<ContactTableModel, Integer>() {
                    @Override
                    public boolean include(Entry<? extends ContactTableModel, ? extends Integer> entry) {
                        ContactTableModel model = entry.getModel();
                        Contact contact = model.getContactAt(entry.getIdentifier());
                        return contact.getName().toLowerCase().contains(query);
                    }
                });
            } else {
                rowSorter.setRowFilter(null);
            }
        });
        
        clearButton.addActionListener(e -> {
            searchField.setText("");
            rowSorter.setRowFilter(null);
        });
        
        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(clearButton);
        add(topPanel, BorderLayout.NORTH);
        
        // --- Center Panel: Table ---
        tableModel = new ContactTableModel(contacts);
        contactTable = new JTable(tableModel);
        contactTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactTable.setRowHeight(25);
        
        rowSorter = new TableRowSorter<>(tableModel);
        contactTable.setRowSorter(rowSorter);
        
        JScrollPane scrollPane = new JScrollPane(contactTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        // Double-click to update
        contactTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    updateSelectedContact();
                }
            }
        });
        
        // --- Bottom Panel: Actions ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton addButton = new JButton("Add Contact");
        JButton updateButton = new JButton("Update Contact");
        JButton deleteButton = new JButton("Delete Contact");
        JButton exportButton = new JButton("Export to VCard");
        JButton importButton = new JButton("Import from VCard");
        
        addButton.addActionListener(e -> addContact());
        updateButton.addActionListener(e -> updateSelectedContact());
        deleteButton.addActionListener(e -> deleteSelectedContact());
        exportButton.addActionListener(e -> exportContacts());
        importButton.addActionListener(e -> importContacts());
        
        bottomPanel.add(addButton);
        bottomPanel.add(updateButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(exportButton);
        bottomPanel.add(importButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    // --- Actions ---
    
    private void addContact() {
        ContactDialog dialog = new ContactDialog(this, "Add Contact", null);
        dialog.setVisible(true);
        
        if (dialog.isApproved()) {
            Contact newContact = dialog.getContact();
            if (isPhoneDuplicate(newContact.getPhone(), -1)) {
                JOptionPane.showMessageDialog(this, "A contact with this phone number already exists.", "Duplicate Phone", JOptionPane.ERROR_MESSAGE);
                return;
            }
            contacts.add(newContact);
            tableModel.fireTableDataChanged();
        }
    }
    
    private void updateSelectedContact() {
        int viewRow = contactTable.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a contact to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = contactTable.convertRowIndexToModel(viewRow);
        Contact contactToUpdate = contacts.get(modelRow);
        
        ContactDialog dialog = new ContactDialog(this, "Update Contact", contactToUpdate);
        dialog.setVisible(true);
        
        if (dialog.isApproved()) {
            Contact updatedData = dialog.getContact();
            
            if (isPhoneDuplicate(updatedData.getPhone(), contactToUpdate.getId())) {
                JOptionPane.showMessageDialog(this, "A contact with this phone number already exists.", "Duplicate Phone", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            contactToUpdate.setName(updatedData.getName());
            contactToUpdate.setPhone(updatedData.getPhone());
            contactToUpdate.setEmail(updatedData.getEmail());
            tableModel.fireTableRowsUpdated(modelRow, modelRow);
        }
    }
    
    private void deleteSelectedContact() {
        int viewRow = contactTable.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a contact to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = contactTable.convertRowIndexToModel(viewRow);
        Contact contactToDelete = contacts.get(modelRow);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete contact:\n" + contactToDelete.getName() + "?", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            contacts.remove(modelRow);
            tableModel.fireTableDataChanged();
        }
    }
    
    private void exportContacts() {
        if (contacts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No contacts to export.", "Empty List", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save VCard File");
        fileChooser.setSelectedFile(new File("contacts.vcf"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                VCardManager.exportContacts(contacts, file);
                JOptionPane.showMessageDialog(this, "Contacts exported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error exporting file: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void importContacts() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open VCard File");
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                List<Contact> imported = VCardManager.importContacts(file);
                int addedCount = 0;
                
                for (Contact c : imported) {
                    if (!isPhoneDuplicate(c.getPhone(), -1)) {
                        contacts.add(c);
                        addedCount++;
                    }
                }
                
                tableModel.fireTableDataChanged();
                JOptionPane.showMessageDialog(this, "Imported " + addedCount + " contacts.\n(Duplicates were skipped)", "Import Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error importing file: " + ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean isPhoneDuplicate(String phone, int excludeId) {
        for (Contact c : contacts) {
            if (c.getId() != excludeId && c.getPhone().equals(phone)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        // Set System L&F
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignore
        }
        
        SwingUtilities.invokeLater(() -> {
            new ContactManagerGUI().setVisible(true);
        });
    }

    // --- Inner Classes ---
    
    /**
     * Table Model for binding List<Contact> to JTable
     */
    class ContactTableModel extends AbstractTableModel {
        private final String[] columnNames = {"ID", "Name", "Phone", "Email"};
        private final List<Contact> contactList;

        public ContactTableModel(List<Contact> contactList) {
            this.contactList = contactList;
        }
        
        public Contact getContactAt(int row) {
            return contactList.get(row);
        }

        @Override
        public int getRowCount() {
            return contactList.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Contact c = contactList.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> c.getId();
                case 1 -> c.getName();
                case 2 -> c.getPhone();
                case 3 -> c.getEmail();
                default -> null;
            };
        }
    }
    
    /**
     * Dialog for Adding and Updating Contacts
     */
    class ContactDialog extends JDialog {
        private JTextField nameField;
        private JTextField phoneField;
        private JTextField emailField;
        
        private boolean approved = false;
        private Contact resultContact;
        
        public ContactDialog(JFrame parent, String title, Contact initialData) {
            super(parent, title, true);
            setSize(350, 250);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());
            
            JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            formPanel.add(new JLabel("Name:"));
            nameField = new JTextField();
            formPanel.add(nameField);
            
            formPanel.add(new JLabel("Phone:"));
            phoneField = new JTextField();
            formPanel.add(phoneField);
            
            formPanel.add(new JLabel("Email:"));
            emailField = new JTextField();
            formPanel.add(emailField);
            
            if (initialData != null) {
                nameField.setText(initialData.getName());
                phoneField.setText(initialData.getPhone());
                emailField.setText(initialData.getEmail());
            }
            
            add(formPanel, BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton saveButton = new JButton("Save");
            JButton cancelButton = new JButton("Cancel");
            
            saveButton.addActionListener(e -> {
                try {
                    // This will trigger validation in the Contact constructor/setters
                    resultContact = new Contact(
                            nameField.getText(), 
                            phoneField.getText(), 
                            emailField.getText()
                    );
                    approved = true;
                    dispose();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            cancelButton.addActionListener(e -> dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            add(buttonPanel, BorderLayout.SOUTH);
        }
        
        public boolean isApproved() {
            return approved;
        }
        
        public Contact getContact() {
            return resultContact;
        }
    }
}
