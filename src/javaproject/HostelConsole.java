package javaproject;

import java.sql.*;
import java.util.Scanner;

public class HostelConsole {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- Hostel Management System ---");
            System.out.println("1. View Dashboard (Stats)");
            System.out.println("2. Add Student");
            System.out.println("3. View All Students");
            System.out.println("4. Update Room Number");
            System.out.println("5. Remove Student");
            System.out.println("6. Search Student by ID");
            System.out.println("7. Exit");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: showDashboard(); break;
                case 2: addStudent(); break;
                case 3: viewStudents(); break;
                case 4: updateRoom(); break;
                case 5: removeStudent(); break;
                case 6: searchStudent(); break;
                case 7: 
                    System.out.println("Exiting System...");
                    System.exit(0);
                default: 
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // 1. DASHBOARD
    private static void showDashboard() {
        System.out.println("\n--- Dashboard Statistics ---");
        System.out.println("Total Students: " + getCount("SELECT COUNT(*) FROM students"));
        System.out.println("Fees Paid:      " + getCount("SELECT COUNT(*) FROM students WHERE fee_status = 'Paid'"));
        System.out.println("Fees Unpaid:    " + getCount("SELECT COUNT(*) FROM students WHERE fee_status = 'Unpaid'"));
    }

    // 2. ADD STUDENT
    private static void addStudent() {
        try (Connection con = DBConnection.getConnection()) {
            System.out.print("Enter ID: "); int id = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter Name: "); String name = scanner.nextLine();
            System.out.print("Enter Room Number: "); int room = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter Contact: "); String contact = scanner.nextLine();
            System.out.print("Enter Fee Status (Paid/Unpaid): "); String fee = scanner.nextLine();

            PreparedStatement ps = con.prepareStatement("INSERT INTO students VALUES(?,?,?,?,?)");
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setInt(3, room);
            ps.setString(4, contact);
            ps.setString(5, fee);
            ps.executeUpdate();
            System.out.println("Student record added successfully!");
        } catch (Exception e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }

    // 3. VIEW ALL STUDENTS
    private static void viewStudents() {
        try (Connection con = DBConnection.getConnection()) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM students");
            System.out.println("\nID | Name | Room | Contact | Fee Status");
            System.out.println("---------------------------------------");
            while (rs.next()) {
                System.out.printf("%d | %s | %d | %s | %s\n", 
                    rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getString(5));
            }
        } catch (Exception e) {
            System.out.println("Error fetching data: " + e.getMessage());
        }
    }

    // 4. UPDATE ROOM
    private static void updateRoom() {
        try (Connection con = DBConnection.getConnection()) {
            System.out.print("Enter Student ID to update: "); int id = scanner.nextInt();
            System.out.print("Enter New Room Number: "); int room = scanner.nextInt();

            PreparedStatement ps = con.prepareStatement("UPDATE students SET room_number=? WHERE id=?");
            ps.setInt(1, room);
            ps.setInt(2, id);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Room updated successfully.");
            else System.out.println("Student ID not found.");
        } catch (Exception e) {
            System.out.println("Error updating room: " + e.getMessage());
        }
    }

    // 5. REMOVE STUDENT
    private static void removeStudent() {
        try (Connection con = DBConnection.getConnection()) {
            System.out.print("Enter Student ID to delete: "); int id = scanner.nextInt();

            PreparedStatement ps = con.prepareStatement("DELETE FROM students WHERE id=?");
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Student removed successfully.");
            else System.out.println("Student ID not found.");
        } catch (Exception e) {
            System.out.println("Error removing student: " + e.getMessage());
        }
    }

    // 6. SEARCH
    private static void searchStudent() {
        try (Connection con = DBConnection.getConnection()) {
            System.out.print("Enter Student ID to search: "); int id = scanner.nextInt();

            PreparedStatement ps = con.prepareStatement("SELECT * FROM students WHERE id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("\nResult Found:");
                System.out.println("Name: " + rs.getString(2));
                System.out.println("Room: " + rs.getInt(3));
                System.out.println("Contact: " + rs.getString(4));
                System.out.println("Fee Status: " + rs.getString(5));
            } else {
                System.out.println("No student found with ID: " + id);
            }
        } catch (Exception e) {
            System.out.println("Error searching student: " + e.getMessage());
        }
    }

    // HELPER: Get counts for dashboard
    private static String getCount(String query) {
        try (Connection con = DBConnection.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery(query);
            return rs.next() ? String.valueOf(rs.getInt(1)) : "0";
        } catch (Exception e) {
            return "0";
        }
    }
}