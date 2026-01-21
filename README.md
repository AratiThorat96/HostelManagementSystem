Hostel Management System (HMS)
A robust, console and GUI-based Java application designed to manage student residents, room assignments, and fee records using a professional layered architecture (DAO pattern).


📌 Project Objective
The primary objective of this project is to provide an automated system for hostel administrators to:
Maintain a digital record of all student residents.
Monitor room occupancy and availability in real-time.
Track financial status (Paid/Unpaid fees) to ensure operational efficiency.
Minimize manual paperwork and data entry errors through a structured database.


📝 Task Description
The system is built to handle the full lifecycle of hostel management through the following functional modules:
Dashboard Statistics: Provides an instant summary of total students and a breakdown of fee collection status.
Student Registration: Allows the addition of new students with unique IDs, contact details, and assigned room numbers.
Record Management: * View: Displays a formatted list of all registered students.
Search: Quickly retrieves specific student details using their unique ID.
Update: Enables room transfers by updating room numbers for existing records.
Delete: Removes student records upon checkout or completion of their stay.
Data Persistence: All information is stored permanently in a MySQL database, ensuring data is not lost when the application is closed.



💡 Concepts and Technologies Used
1. Architecture: Layered DAO Pattern
The project follows a separation of concerns strategy to make the code maintainable and scalable:
com.acc.model: Contains Data Transfer Objects (DTOs) representing the student entity.
com.acc.dao: Data Access Object layer containing all SQL logic.
com.acc.services: The Business Logic Layer that processes data before it reaches the UI or Database.
com.acc.util: Utility classes for database connectivity.
com.acc.main: The Presentation Layer (Console and Swing GUI).


2. Core Technologies
Java SE (Standard Edition): The primary programming language used for core logic.
Java Swing: Used for building the Professional "Layered Edition" Graphical User Interface.
JDBC (Java Database Connectivity): The API used to connect the Java application with the SQL database.
MySQL: The Relational Database Management System (RDBMS) used for data storage.

4. Key Java Concepts
Object-Oriented Programming (OOP): Extensive use of Encapsulation, Classes, and Objects.
Collections Framework: Use of ArrayList and Vector to manage lists of data retrieved from the database.
Exception Handling: Robust try-catch blocks to handle SQL errors and invalid user inputs.
Multithreading: Used in the GUI (SwingUtilities.invokeLater) to ensure a responsive user experience.


🚀 How to Run
Database Setup: Execute the SQL script provided in the project folder to create the hostel_db and students table.
Configuration: Update the DBConnection.java file with your MySQL username and password.
Library: Ensure the mysql-connector-java.jar is added to your project's Build Path in Eclipse.
Execution: Run HostelApp.java for the console version or HostelGUI.java for the graphical version.
