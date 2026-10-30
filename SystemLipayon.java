import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

class LipayonItem {
    String id;
    String title;
    String author;
    boolean borrowed;

    public LipayonItem(String id, String title, String author, boolean borrowed) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.borrowed = borrowed;
    }
}

public class SystemLipayon extends JFrame {

    private ArrayList<LipayonItem> books = new ArrayList<>();

    private JTextField txtId;
    private JTextField txtTitle;
    private JTextField txtAuthor;

    private JLabel lblStatus;
    private JLabel lblCount;

    private JTable table;
    private DefaultTableModel tableModel;

    private static final String FILE_NAME = "Book.txt";

    // Dark Theme Color Palette
    private final Color BG_DARK = new Color(30, 30, 30);
    private final Color PANEL_DARK = new Color(45, 45, 48);
    private final Color TEXT_LIGHT = new Color(220, 220, 220);
    private final Color FIELD_BG = new Color(60, 60, 65);
    private final Color BUTTON_BG = new Color(63, 63, 70);
    private final Color BUTTON_HOVER = new Color(85, 85, 95);
    private final Color TABLE_GRID = new Color(55, 55, 60);

    public SystemLipayon() {
        setTitle("Library Management System");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BG_DARK);
        setResizable(false);

        // Load existing books from file
        loadBooksFromFile();

        // 1. HEADER PANEL
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PANEL_DARK);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel lblHeader = new JLabel("SYSTEM LIBRARY MANAGEMENT", JLabel.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 20));
        lblHeader.setForeground(TEXT_LIGHT);
        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        // 2. LEFT CONTROL PANEL (Inputs & Buttons)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(PANEL_DARK);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(TEXT_LIGHT, 1),
                        "Book Information",
                        0, 0, new Font("Arial", Font.BOLD, 12), TEXT_LIGHT
                )
        ));
        leftPanel.setPreferredSize(new Dimension(280, 0));

        // Form Fields
        txtId = createStyledTextField();
        txtTitle = createStyledTextField();
        txtAuthor = createStyledTextField();

        leftPanel.add(createFieldLabel("Book ID:"));
        leftPanel.add(txtId);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(createFieldLabel("Book Title:"));
        leftPanel.add(txtTitle);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(createFieldLabel("Author:"));
        leftPanel.add(txtAuthor);
        leftPanel.add(Box.createVerticalStrut(15));

        // Action Buttons
        JButton btnAdd = createStyledButton("Add Book");
        JButton btnDisplay = createStyledButton("Refresh/Display");
        JButton btnBorrow = createStyledButton("Borrow Book");
        JButton btnReturn = createStyledButton("Return Book");
        JButton btnSearch = createStyledButton("Search Book");
        JButton btnClear = createStyledButton("Clear Fields");

        JPanel buttonGrid = new JPanel(new GridLayout(3, 2, 6, 6));
        buttonGrid.setBackground(PANEL_DARK);
        buttonGrid.add(btnAdd);
        buttonGrid.add(btnDisplay);
        buttonGrid.add(btnBorrow);
        buttonGrid.add(btnReturn);
        buttonGrid.add(btnSearch);
        buttonGrid.add(btnClear);

        leftPanel.add(buttonGrid);
        add(leftPanel, BorderLayout.WEST);

        // 3. CENTER PANEL (JTable for Main Objects)
        String[] columnNames = {"Book ID", "Title", "Author", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        table = new JTable(tableModel);
        table.setBackground(FIELD_BG);
        table.setForeground(TEXT_LIGHT);
        table.setGridColor(TABLE_GRID);
        table.setSelectionBackground(new Color(0, 122, 204));
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(PANEL_DARK);
        table.getTableHeader().setForeground(TEXT_LIGHT);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Center alignment for status column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(BG_DARK);
        scrollPane.setBorder(BorderFactory.createLineBorder(TABLE_GRID));
        add(scrollPane, BorderLayout.CENTER);

        // 4. BOTTOM STATUS BAR
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(PANEL_DARK);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        lblStatus = new JLabel("Status: System Ready");
        lblStatus.setForeground(TEXT_LIGHT);
        lblCount = new JLabel("Total Books: 0");
        lblCount.setForeground(TEXT_LIGHT);

        bottomPanel.add(lblStatus, BorderLayout.WEST);
        bottomPanel.add(lblCount, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Populate table initially
        refreshTable();

        // BUTTON LISTENERS
        btnAdd.addActionListener(e -> addBook());
        btnDisplay.addActionListener(e -> {
            refreshTable();
            lblStatus.setText("Status: Displaying all books.");
        });
        btnBorrow.addActionListener(e -> borrowBook());
        btnReturn.addActionListener(e -> returnBook());
        btnSearch.addActionListener(e -> searchBook());
        btnClear.addActionListener(e -> clearFields());

        // Table row click listener to autofill fields
        table.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                txtTitle.setText(tableModel.getValueAt(selectedRow, 1).toString());
                txtAuthor.setText(tableModel.getValueAt(selectedRow, 2).toString());
            }
        });

        setVisible(true);
    }

    // --- HELPER UI METHODS ---
    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setBackground(FIELD_BG);
        tf.setForeground(TEXT_LIGHT);
        tf.setCaretColor(TEXT_LIGHT);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        tf.setBorder(BorderFactory.createLineBorder(TABLE_GRID));
        return tf;
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_LIGHT);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        return lbl;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(BUTTON_BG);
        btn.setForeground(TEXT_LIGHT);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(TABLE_GRID));
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(BUTTON_HOVER);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(BUTTON_BG);
            }
        });
        return btn;
    }

    // --- BUSINESS LOGIC & FILE I/O ---

    private void loadBooksFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    books.add(new LipayonItem(parts[0], parts[1], parts[2], Boolean.parseBoolean(parts[3])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveBooksToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (LipayonItem book : books) {
                writer.println(book.id + "|" + book.title + "|" + book.author + "|" + book.borrowed);
            }
        } catch (IOException e) {
            lblStatus.setText("Status: Error saving to file!");
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (LipayonItem book : books) {
            tableModel.addRow(new Object[]{
                    book.id,
                    book.title,
                    book.author,
                    book.borrowed ? "Borrowed" : "Available"
            });
        }
        updateCount();
    }

    private void addBook() {
        String id = txtId.getText().trim();
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();

        if (id.isEmpty() || title.isEmpty() || author.isEmpty()) {
            lblStatus.setText("Status: Complete all fields!");
            return;
        }

        if (findBook(id) != null) {
            lblStatus.setText("Status: Book ID already exists!");
            return;
        }

        books.add(new LipayonItem(id, title, author, false));
        saveBooksToFile();
        refreshTable();
        clearFields();
        lblStatus.setText("Status: Book added successfully!");
    }

    private void borrowBook() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            lblStatus.setText("Status: Enter or select Book ID!");
            return;
        }

        LipayonItem book = findBook(id);
        if (book == null) {
            lblStatus.setText("Status: Book not found!");
        } else if (book.borrowed) {
            lblStatus.setText("Status: Already borrowed!");
        } else {
            book.borrowed = true;
            saveBooksToFile();
            refreshTable();
            lblStatus.setText("Status: Book borrowed successfully!");
        }
    }

    private void returnBook() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            lblStatus.setText("Status: Enter or select Book ID!");
            return;
        }

        LipayonItem book = findBook(id);
        if (book == null) {
            lblStatus.setText("Status: Book not found!");
        } else if (!book.borrowed) {
            lblStatus.setText("Status: Book is already available!");
        } else {
            book.borrowed = false;
            saveBooksToFile();
            refreshTable();
            lblStatus.setText("Status: Book returned successfully!");
        }
    }

    private void searchBook() {
        String search = txtId.getText().trim();
        if (search.isEmpty()) {
            lblStatus.setText("Status: Enter search keyword in Book ID!");
            return;
        }

        tableModel.setRowCount(0);
        boolean found = false;

        for (LipayonItem book : books) {
            if (book.id.equalsIgnoreCase(search) ||
                    book.title.toLowerCase().contains(search.toLowerCase()) ||
                    book.author.toLowerCase().contains(search.toLowerCase())) {
                tableModel.addRow(new Object[]{
                        book.id,
                        book.title,
                        book.author,
                        book.borrowed ? "Borrowed" : "Available"
                });
                found = true;
            }
        }

        if (found) {
            lblStatus.setText("Status: Search results displayed.");
        } else {
            lblStatus.setText("Status: No matching books found!");
        }
    }

    private LipayonItem findBook(String id) {
        for (LipayonItem book : books) {
            if (book.id.equalsIgnoreCase(id)) {
                return book;
            }
        }
        return null;
    }

    private void clearFields() {
        txtId.setText("");
        txtTitle.setText("");
        txtAuthor.setText("");
        table.clearSelection();
    }

    private void updateCount() {
        lblCount.setText("Total Books: " + books.size());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SystemLipayon());
    }
}