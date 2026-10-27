
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class Library {
    String id, title, author;
    boolean borrowed;

    public Library(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.borrowed = false;
    }
}

public class SystemLibrary extends JFrame implements Runnable {

    private ArrayList<Library> books = new ArrayList<>();

    private JTextField txtId, txtTitle, txtAuthor;
    private JLabel lblStatus, lblCount;
    private JTextArea txtDisplay;
    private JButton btnAdd, btnDisplay, btnBorrow, btnReturn, btnSearch;
    private Thread thread;

    public SystemLibrary() {
        setTitle("System Library Management");
        setSize(650, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

    
        JLabel lblHeader = new JLabel(
                "SYSTEM LIBRARY MANAGEMENT", JLabel.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 22));
        add(lblHeader, BorderLayout.NORTH);

        
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(
                BorderFactory.createTitledBorder("Book Information"));

        inputPanel.add(new JLabel("Book ID:"));
        txtId = new JTextField();
        inputPanel.add(txtId);

        inputPanel.add(new JLabel("Book Title:"));
        txtTitle = new JTextField();
        inputPanel.add(txtTitle);

        inputPanel.add(new JLabel("Author:"));
        txtAuthor = new JTextField();
        inputPanel.add(txtAuthor);

        
        btnAdd = new JButton("Add Book");
        btnDisplay = new JButton("Display Books");
        btnBorrow = new JButton("Borrow Book");
        btnReturn = new JButton("Return Book");
        btnSearch = new JButton("Search Book");

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDisplay);
        buttonPanel.add(btnBorrow);
        buttonPanel.add(btnReturn);
        buttonPanel.add(btnSearch);

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        
        txtDisplay = new JTextArea();
        txtDisplay.setEditable(false);
        txtDisplay.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(txtDisplay);

        lblStatus = new JLabel("Status: System Ready");
        lblCount = new JLabel("Total Books: 0");

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        bottomPanel.add(lblStatus);
        bottomPanel.add(lblCount);

        add(topPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);

        remove(topPanel);
        remove(scrollPane);
        add(centerPanel, BorderLayout.CENTER);

        
        btnAdd.addActionListener(e -> addBook());

        
        btnDisplay.addActionListener(e -> displayBooks());

        
        btnBorrow.addActionListener(e -> borrowBook());

        
        btnReturn.addActionListener(e -> returnBook());

        
        btnSearch.addActionListener(e -> searchBook());

        
        thread = new Thread(this);
        thread.start();

        setVisible(true);
    }

    private void addBook() {
        String id = txtId.getText().trim();
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();

        if (id.isEmpty() || title.isEmpty() || author.isEmpty()) {
            lblStatus.setText("Status: Please complete all fields.");
            return;
        }

        if (findBook(id) != null) {
            lblStatus.setText("Status: Book ID already exists.");
            return;
        }

        books.add(new Library(id, title, author));

        lblStatus.setText("Status: Book added successfully!");
        clearFields();
        updateCount();
        displayBooks();
    }

    private void displayBooks() {
        txtDisplay.setText("");

        if (books.isEmpty()) {
            txtDisplay.setText("No books available.");
            return;
        }

        for (Library book : books) {
            txtDisplay.append(
                    "Book ID: " + book.id + "\n"
                    + "Title: " + book.title + "\n"
                    + "Author: " + book.author + "\n"
                    + "Status: "
                    + (book.borrowed ? "Borrowed" : "Available")
                    + "\n------------------------------\n"
            );
        }

        lblStatus.setText("Status: Displaying all books.");
        updateCount();
    }

    private void borrowBook() {
        String id = txtId.getText().trim();
        Library book = findBook(id);

        if (book == null) {
            lblStatus.setText("Status: Book ID not found.");
        } else if (book.borrowed) {
            lblStatus.setText("Status: Book is already borrowed.");
        } else {
            book.borrowed = true;
            lblStatus.setText("Status: Book borrowed successfully!");
            displayBooks();
        }
    }

    private void returnBook() {
        String id = txtId.getText().trim();
        Library book = findBook(id);

        if (book == null) {
            lblStatus.setText("Status: Book ID not found.");
        } else if (!book.borrowed) {
            lblStatus.setText("Status: Book is already available.");
        } else {
            book.borrowed = false;
            lblStatus.setText("Status: Book returned successfully!");
            displayBooks();
        }
    }

    private void searchBook() {
        String search = txtId.getText().trim();
        boolean found = false;

        txtDisplay.setText("");

        for (Library book : books) {
            if (book.id.equalsIgnoreCase(search)
                    || book.title.toLowerCase()
                    .contains(search.toLowerCase())) {

                txtDisplay.append(
                        "Book ID: " + book.id + "\n"
                        + "Title: " + book.title + "\n"
                        + "Author: " + book.author + "\n"
                        + "Status: "
                        + (book.borrowed ? "Borrowed" : "Available")
                        + "\n------------------------------\n"
                );
                found = true;
            }
        }

        lblStatus.setText(found
                ? "Status: Book found."
                : "Status: Book not found.");
    }

    private Library findBook(String id) {
        for (Library book : books) {
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
    }

    private void updateCount() {
        lblCount.setText("Total Books: " + books.size());
    }

    
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                SwingUtilities.invokeLater(() ->
                        lblStatus.setText(
                                "Status: System Running | Library Ready"));

                Thread.sleep(5000);

                SwingUtilities.invokeLater(() ->
                        lblStatus.setText(
                                "Status: System Active | Ready for input"));

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SystemLibrary());
    }
}
