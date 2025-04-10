import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private ArrayList<Book> books = new ArrayList<>();
    private Scanner sc = new Scanner(System.in);
    private final String FILE_NAME = "books.txt";

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        loadBooksFromFile(); // Load books from the file at the start
        while (true) {
            System.out.println("\n=== Library Management System ===");
            System.out.println("1. Add Book");
            System.out.println("2. Display Books");
            System.out.println("3. Search Book");
            System.out.println("4. Issue Book");
            System.out.println("5. Return Book");
            System.out.println("6. View Issued Books");
            System.out.println("7. Delete Book");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline
            switch (choice) {
                case 1:
                    addBook();
                    break;
                case 2:
                    displayBooks();
                    break;
                case 3:
                    searchBook();
                    break;
                case 4:
                    issueBook();
                    break;
                case 5:
                    returnBook();
                    break;
                case 6:
                    viewIssuedBooks();
                    break;
                case 7:
                    deleteBook();
                    break;
                case 8:
                    saveBooksToFile(); // Save books to the file before exiting
                    System.exit(0);
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private void addBook() {
        System.out.print("Enter Book ISBN: ");
        long isbn = sc.nextLong();
        sc.nextLine(); // Consume newline
        System.out.print("Enter Book Title: ");
        String title = sc.nextLine();
        System.out.print("Enter Author: ");
        String author = sc.nextLine();
        books.add(new Book(isbn, title, author));
        System.out.println("Book added successfully!");
        saveBooksToFile(); // Save changes to the file
    }

    private void displayBooks() {
        if (books.isEmpty()) {
            System.out.println("No books available.");
            return;
        }
        printBookHeader();
        for (Book book : books) {
            printBookDetails(book);
        }
    }

    private void searchBook() {
        System.out.print("Enter title or author to search: ");
        String searchTerm = sc.nextLine().toLowerCase();
        boolean found = false;
        printBookHeader();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(searchTerm) || book.getAuthor().toLowerCase().contains(searchTerm)) {
                printBookDetails(book);
                found = true;
            }
        }
        if (!found)
            System.out.println("No books found.");
    }

    private void issueBook() {
        System.out.print("Enter Book ISBN to Issue: ");
        long isbn = sc.nextLong();
        for (Book book : books) {
            if (book.getId() == isbn && !book.isIssued()) {
                book.setIssued(true);
                System.out.println("Book issued successfully!");
                saveBooksToFile(); // Save changes to the file
                return;
            }
        }
        System.out.println("Book not found or already issued.");
    }

    private void returnBook() {
        System.out.print("Enter Book ISBN to Return: ");
        long isbn = sc.nextLong();
        for (Book book : books) {
            if (book.getId() == isbn && book.isIssued()) {
                book.setIssued(false);
                System.out.println("Book returned successfully!");
                saveBooksToFile(); // Save changes to the file
                return;
            }
        }
        System.out.println("Book not found or not issued.");
    }

    private void viewIssuedBooks() {
        boolean found = false;
        printBookHeader();
        for (Book book : books) {
            if (book.isIssued()) {
                printBookDetails(book);
                found = true;
            }
        }
        if (!found)
            System.out.println("No books are currently issued.");
    }

    private void deleteBook() {
        System.out.print("Enter Book ISBN to Delete: ");
        long isbn = sc.nextLong();
        for (Book book : books) {
            if (book.getId() == isbn) {
                books.remove(book);
                System.out.println("Book deleted successfully!");
                saveBooksToFile(); // Save changes to the file
                return;
            }
        }
        System.out.println("Book not found.");
    }

    private void printBookHeader() {
        System.out.println("\nISBN\t\tTitle\t\tAuthor\t\tStatus");
        System.out.println("------------------------------------------------");
    }

    private void printBookDetails(Book book) {
        System.out.printf("%d\t%s\t\t%s\t\t%s%n", book.getId(), book.getTitle(), book.getAuthor(),
                book.isIssued() ? "Issued" : "Available");
    }

    private void loadBooksFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                long isbn = Long.parseLong(parts[0]);
                String title = parts[1];
                String author = parts[2];
                boolean isIssued = Boolean.parseBoolean(parts[3]);
                books.add(new Book(isbn, title, author, isIssued));
            }
        } catch (FileNotFoundException e) {
            System.out.println("No existing data found. Starting fresh.");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private void saveBooksToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Book book : books) {
                pw.println(book.getId() + "," + book.getTitle() + "," + book.getAuthor() + "," + book.isIssued());
            }
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }
}