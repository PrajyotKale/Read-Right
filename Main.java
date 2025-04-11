package com.library;
import java.io.*;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    private Scanner sc = new Scanner(System.in);
    private String currentRole = ""; // To store the role of the logged-in user

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library";
    private static final String DB_USER = "root"; // Replace with your MySQL username
    private static final String DB_PASSWORD = ""; // Replace with your MySQL password

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found.", e);
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        if (!authenticate()) {
            System.out.println("Authentication failed. Exiting...");
            return;
        }

        while (true) {
            System.out.println("\n=== Library Management System ===");
            System.out.println("1. Display Books");
            System.out.println("2. Search Book");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. View Issued Books");
            System.out.println("6. Exit");

            if (currentRole.equals("Admin")) {
                System.out.println("7. Add Book");
                System.out.println("8. Delete Book");
                System.out.println("9. Create New User");
                System.out.println("10. Delete User");
            }

            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    displayBooks();
                    break;
                case 2:
                    searchBook();
                    break;
                case 3:
                    issueBook();
                    break;
                case 4:
                    returnBook();
                    break;
                case 5:
                    viewIssuedBooks();
                    break;
                case 6:
                    System.exit(0);
                    break;
                case 7:
                    if (currentRole.equals("Admin")) addBook();
                    else System.out.println("Unauthorized action.");
                    break;
                case 8:
                    if (currentRole.equals("Admin")) deleteBook();
                    else System.out.println("Unauthorized action.");
                    break;
                case 9:
                    if (currentRole.equals("Admin")) createNewUser();
                    else System.out.println("Unauthorized action.");
                    break;
                case 10:
                    if (currentRole.equals("Admin")) deleteUser();
                    else System.out.println("Unauthorized action.");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private boolean authenticate() {
        System.out.println("=== Login ===");
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        String query = "SELECT role FROM users WHERE username = ? AND password = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                currentRole = rs.getString("role");
                System.out.println("Logged in as " + currentRole + ".");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error during authentication: " + e.getMessage());
        }
        return false;
    }

    private void createNewUser() {
        System.out.print("Enter new username: ");
        String username = sc.nextLine();
        System.out.print("Enter new password: ");
        String password = sc.nextLine();
        String role = "User";

        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.executeUpdate();
            System.out.println("New user created successfully!");
        } catch (SQLException e) {
            System.out.println("Error creating new user: " + e.getMessage());
        }
    }

    private void deleteUser() {
        System.out.print("Enter the username to delete: ");
        String usernameToDelete = sc.nextLine();

        String query = "DELETE FROM users WHERE username = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, usernameToDelete);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User deleted successfully!");
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
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

        String query = "INSERT INTO books (isbn, title, author, is_issued) VALUES (?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setLong(1, isbn);
            ps.setString(2, title);
            ps.setString(3, author);
            ps.setBoolean(4, false); // New books are not issued by default
            ps.executeUpdate();
            System.out.println("Book added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    private void deleteBook() {
        System.out.print("Enter Book ISBN to Delete: ");
        long isbn = sc.nextLong();

        String query = "DELETE FROM books WHERE isbn = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setLong(1, isbn);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Book deleted successfully!");
            } else {
                System.out.println("Book not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting book: " + e.getMessage());
        }
    }

    private void displayBooks() {
        String query = "SELECT * FROM books";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            boolean found = false;
            printBookHeader();
            while (rs.next()) {
                long isbn = rs.getLong("isbn");
                String title = rs.getString("title");
                String author = rs.getString("author");
                boolean isIssued = rs.getBoolean("is_issued");
                System.out.printf("%d\t%s\t\t%s\t\t%s%n", isbn, title, author, isIssued ? "Issued" : "Available");
                found = true;
            }
            if (!found) {
                System.out.println("No books available.");
            }
        } catch (SQLException e) {
            System.out.println("Error displaying books: " + e.getMessage());
        }
    }

    private void searchBook() {
        System.out.print("Enter the title or author to search: ");
        String keyword = sc.nextLine().toLowerCase();

        String query = "SELECT * FROM books WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            boolean found = false;
            System.out.println("\nSearch Results:");
            printBookHeader();
            while (rs.next()) {
                long isbn = rs.getLong("isbn");
                String title = rs.getString("title");
                String author = rs.getString("author");
                boolean isIssued = rs.getBoolean("is_issued");
                System.out.printf("%d\t%s\t\t%s\t\t%s%n", isbn, title, author, isIssued ? "Issued" : "Available");
                found = true;
            }

            if (!found) {
                System.out.println("No books found matching the search criteria.");
            }
        } catch (SQLException e) {
            System.out.println("Error searching books: " + e.getMessage());
        }
    }

    private void issueBook() {
        System.out.print("Enter Book ISBN to Issue: ");
        long isbn = sc.nextLong();

        String query = "UPDATE books SET is_issued = ? WHERE isbn = ? AND is_issued = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setBoolean(1, true);
            ps.setLong(2, isbn);
            ps.setBoolean(3, false); // Only issue books that are not already issued
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Book issued successfully!");
            } else {
                System.out.println("Book not found or already issued.");
            }
        } catch (SQLException e) {
            System.out.println("Error issuing book: " + e.getMessage());
        }
    }

    private void returnBook() {
        System.out.print("Enter Book ISBN to Return: ");
        long isbn = sc.nextLong();

        String query = "UPDATE books SET is_issued = ? WHERE isbn = ? AND is_issued = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setBoolean(1, false);
            ps.setLong(2, isbn);
            ps.setBoolean(3, true); // Only return books that are currently issued
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Book returned successfully!");
            } else {
                System.out.println("Book not found or not issued.");
            }
        } catch (SQLException e) {
            System.out.println("Error returning book: " + e.getMessage());
        }
    }

    private void viewIssuedBooks() {
        String query = "SELECT * FROM books WHERE is_issued = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setBoolean(1, true); // Fetch only issued books
            ResultSet rs = ps.executeQuery();

            boolean found = false;
            printBookHeader();
            while (rs.next()) {
                long isbn = rs.getLong("isbn");
                String title = rs.getString("title");
                String author = rs.getString("author");
                System.out.printf("%d\t%s\t\t%s\t\tIssued%n", isbn, title, author);
                found = true;
            }
            if (!found) {
                System.out.println("No books are currently issued.");
            }
        } catch (SQLException e) {
            System.out.println("Error viewing issued books: " + e.getMessage());
        }
    }

    private void printBookHeader() {
        System.out.println("\nISBN\t\tTitle\t\tAuthor\t\tStatus");
        System.out.println("------------------------------------------------");
    }
}
