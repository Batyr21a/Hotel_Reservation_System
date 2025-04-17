# Hotel Reservation System

## Description
A hotel reservation management application built in Java, using JavaFX for the graphical interface and PostgreSQL for data storage. It supports creating, viewing, updating, and deleting reservations, managing users and rooms, importing/exporting data in CSV format, and generating reports.

**Author**: Batyr Batyrov

## Project Goals
- Provide convenient management of reservations, rooms, and users through a graphical interface.
- Implement role-based access (administrator and user).
- Support data import/export and report generation.

## Requirements
1. User authentication via login and password.
2. Role-based access: administrator (full access) and user (limited access).
3. Create, view, update, and delete reservations.
4. Display available rooms for selected dates.
5. Manage room prices (administrators only).
6. Manage users: add, update roles, delete (administrators only).
7. Import and export reservation and user data in CSV format.
8. Generate reports on system status (room, reservation, and occupancy statistics).
9. Graphical interface with a reservation calendar.
10. Log all operations to a file.

## Installation and Setup

### Required Software
- Java 17 or higher
- PostgreSQL 13 or higher
- Maven
- PostgreSQL JDBC driver

### Installation Steps
1. **Database Setup**:
   - Install PostgreSQL.
   - Create a database:
     ```sql
CREATE DATABASE hotel;

Ensure the user postgres with password 12345 is configured (or update settings in DatabaseConnector.java).
Project Build:

Clone the repository:
git clone https://github.com/Batyr21a/Hotel_Reservation_System

Navigate to the project folder:
cd Hotel_Reservation_System

Build the project:
mvn clean install

Run the Application:
mvn exec:java -Dexec.mainClass="com.example.HotelReservationApp"

Test Credentials:
Administrator: login admin, password admin.
User: login user, password user.

### Project Structure
com.example.model: Data classes (User, Room, Reservation).
com.example.dao: Database access classes (UserDAO, RoomDAO, ReservationDAO).
com.example.service: Business logic (AuthService, DataService).
com.example.ui: Interface controllers (MainMenuController, ReservationCalendarController).
com.example.util: Utilities (DatabaseConnector, CsvHandler).
resources: Interface files (FXML).

### Key Features
Authentication: Login with role-based access.
Reservations: Create, view, and delete via a calendar.
Room Management: Update prices (administrators only).
User Management: Add, update roles, delete (administrators only).
Import/Export: Save and load data in CSV format.
Reports: Statistics on rooms, reservations, and occupancy.
Logging: Operations logged to logs/hotel.log.

### Documentation
Algorithms
Available Room Search: SQL query excludes booked rooms for given dates.
CSV Processing: Escapes special characters, validates format.
## Data Structures
- **In-Memory**: `ArrayList` for `User`, `Room`, `Reservation` objects.
- **Database**: Tables `users`, `rooms`, `reservations` with foreign keys.

## Modules
- `UserDAO`: Handles user database operations.
- `CsvHandler`: Manages CSV import/export.
- `MainMenuController`: Controls the main interface window.

## Challenges and Solutions
- **Challenge**: Incorrect CSV format during import.  
  **Solution**: Validate headers and fields in `CsvHandler`, use `CsvHandlerException`.
- **Challenge**: Database connection errors.  
  **Solution**: Transactions with rollback and error logging.

## Test Cases
- Invalid password login: Displays an error message.
- Booking an occupied room: Reservation is rejected.
- Importing invalid CSV: Shows an error.
- Updating room price: Reflected in the table.
