package addressbook;

import user.User;

import java.sql.*;
import java.util.Scanner;

public class Addressbook {
    private static Scanner scanner = new Scanner(System.in);
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/addressbook";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "priyansh26";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("MySQL JDBC Driver not found.");
            return;
        }

        while (true) {
            printMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    addUser();
                    break;
                case 2:
                    deleteUser();
                    break;
                case 3:
                    searchUser();
                    break;
                case 4:
                    updateUser();
                    break;
                case 5:
                    System.out.println("Exiting the Address Book. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }

    private static void printMenu() {
        System.out.println("Address Book Menu:");
        System.out.println("1. Add User");
        System.out.println("2. Delete User");
        System.out.println("3. Search User");
        System.out.println("4. Update User");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void addUser() {
        System.out.print("Enter name: ");
        String name;
        do {
            name = scanner.nextLine();
            if (containsNumbers(name)) {
                System.out.println("Name cannot contain numbers. Please enter a valid name.");
            }
        } while (containsNumbers(name));

        String phoneNumber;
        do {
            System.out.print("Enter phone number: ");
            phoneNumber = scanner.nextLine();
        } while (!isValidPhoneNumber(phoneNumber));

        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        System.out.print("Enter city: ");
        String city = scanner.nextLine();
        System.out.print("Enter country: ");
        String country = scanner.nextLine();

        String aadharCardNumber;
        do {
            System.out.print("Enter Aadhar card number: ");
            aadharCardNumber = scanner.nextLine();
        } while (!isAadharCardNumberValid(aadharCardNumber) || !isAadharCardNumberUnique(aadharCardNumber));

        try (Connection connection = getConnection()) {
            String sql = "INSERT INTO users (name, phone_number, address, city, country, aadhar_card_number) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, name);
                statement.setString(2, phoneNumber);
                statement.setString(3, address);
                statement.setString(4, city);
                statement.setString(5, country);
                statement.setString(6, aadharCardNumber);

                statement.executeUpdate();
            }
            System.out.println("User added successfully!\n");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding user to the database.\n");
        }
    }

    private static boolean containsNumbers(String input) {
        return input.matches(".*\\d.*");
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber.matches("\\d{10}")) {
            return true;
        } else {
            System.out.println("Phone number must be exactly 10 digits long.");
            return false;
        }
    }

    private static boolean isAadharCardNumberValid(String aadharCardNumber) {
        if (aadharCardNumber.length() == 12 && aadharCardNumber.matches("\\d{12}")) {
            return true;
        } else {
            System.out.println("Aadhar card number must be exactly 12 digits long.");
            return false;
        }
    }

    private static boolean isAadharCardNumberUnique(String aadharCardNumber) {
        try (Connection connection = getConnection()) {
            String sql = "SELECT COUNT(*) FROM users WHERE aadhar_card_number = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, aadharCardNumber);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next() && resultSet.getInt(1) > 0) {
                        System.out.println("Aadhar card number must be unique.");
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error checking Aadhar card number uniqueness in the database.\n");
        }

        return true;
    }

    private static void deleteUser() {
        System.out.print("Enter the Aadhar card number of the user to delete: ");
        String aadharCardNumberToDelete = scanner.nextLine();

        try (Connection connection = getConnection()) {
            String sql = "DELETE FROM users WHERE aadhar_card_number = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, aadharCardNumberToDelete);

                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("User deleted successfully!\n");
                } else {
                    System.out.println("User not found in the database.\n");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error deleting user from the database.\n");
        }
    }

    private static void searchUser() {
        System.out.println("Search Options:");
        System.out.println("1. Search by Name");
        System.out.println("2. Search by Phone Number");
        System.out.println("3. Search by Address");
        System.out.println("4. Search by City");
        System.out.println("5. Search by Country");
        System.out.println("6. Search by Aadhar Card Number");
        System.out.print("Enter your choice: ");

        int searchChoice = scanner.nextInt();
        scanner.nextLine(); 

        System.out.print("Enter the value to search: ");
        String valueToSearch = scanner.nextLine();

        try (Connection connection = getConnection()) {
            String sql = ""; 

            switch (searchChoice) {
                case 1:
                    sql = "SELECT * FROM users WHERE name = ?";
                    break;
                case 2:
                    sql = "SELECT * FROM users WHERE phone_number = ?";
                    break;
                case 3:
                    sql = "SELECT * FROM users WHERE address = ?";
                    break;
                case 4:
                    sql = "SELECT * FROM users WHERE city = ?";
                    break;
                case 5:
                    sql = "SELECT * FROM users WHERE country = ?";
                    break;
                case 6:
                    sql = "SELECT * FROM users WHERE aadhar_card_number = ?";
                    break;
                default:
                    System.out.println("Invalid search choice.");
                    return;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, valueToSearch);

                try (ResultSet resultSet = statement.executeQuery()) {
                    boolean found = false;

                    while (resultSet.next()) {
                        found = true;
                        User user = new User(
                                resultSet.getString("name"),
                                resultSet.getString("phone_number"),
                                resultSet.getString("address"),
                                resultSet.getString("city"),
                                resultSet.getString("country"),
                                resultSet.getString("aadhar_card_number")
                        );

                        System.out.println("User found:");
                        System.out.println(user);
                        System.out.println();
                    }

                    if (!found) {
                        System.out.println("No users found in the database.\n");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error searching for users in the database.\n");
        }
    }

    private static void updateUser() {
        System.out.print("Enter the Aadhar card number of the user to update: ");
        String aadharCardNumberToUpdate = scanner.nextLine();

        try (Connection connection = getConnection()) {
            String sql = "SELECT * FROM users WHERE aadhar_card_number = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, aadharCardNumberToUpdate);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("User found. Enter new details:");

                        System.out.print("Enter new name: ");
                        String newName;
                        do {
                            newName = scanner.nextLine();
                            if (containsNumbers(newName)) {
                                System.out.println("Name cannot contain numbers. Please enter a valid name.");
                            }
                        } while (containsNumbers(newName));

                        String newPhoneNumber;
                        do {
                            System.out.print("Enter new phone number: ");
                            newPhoneNumber = scanner.nextLine();
                        } while (!isValidPhoneNumber(newPhoneNumber));

                        System.out.print("Enter new address: ");
                        String newAddress = scanner.nextLine();
                        System.out.print("Enter new city: ");
                        String newCity = scanner.nextLine();
                        System.out.print("Enter new country: ");
                        String newCountry = scanner.nextLine();

                        String newAadharCardNumber;
                        do {
                            System.out.print("Enter new Aadhar card number: ");
                            newAadharCardNumber = scanner.nextLine();
                        } while (!isAadharCardNumberValid(newAadharCardNumber) || !isAadharCardNumberUnique(newAadharCardNumber));

                        sql = "UPDATE users SET name=?, phone_number=?, address=?, city=?, country=?, aadhar_card_number=? WHERE aadhar_card_number=?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(sql)) {
                            updateStatement.setString(1, newName);
                            updateStatement.setString(2, newPhoneNumber);
                            updateStatement.setString(3, newAddress);
                            updateStatement.setString(4, newCity);
                            updateStatement.setString(5, newCountry);
                            updateStatement.setString(6, newAadharCardNumber);
                            updateStatement.setString(7, aadharCardNumberToUpdate);

                            int rowsUpdated = updateStatement.executeUpdate();
                            if (rowsUpdated > 0) {
                                System.out.println("User updated successfully!\n");
                            } else {
                                System.out.println("Error updating user.\n");
                            }
                        }
                    } else {
                        System.out.println("User not found in the database.\n");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating user information in the database.\n");
        }
    }
}
