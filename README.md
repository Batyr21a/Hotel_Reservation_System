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
     
CREATE DATABASE hotel;
Ensure the user postgres with password 12345 is configured (or update settings in DatabaseConnector.java).
Project Build:

Clone the repository:
```git clone https://github.com/Batyr21a/Hotel_Reservation_System```

Navigate to the project folder:
```cd Hotel_Reservation_System```

Build the project:
```mvn clean install```

Run the Application:
```mvn exec:java -Dexec.mainClass="com.example.HotelReservationApp"```

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

  
## Screenshots
Login Window:

![image](https://github.com/user-attachments/assets/ba5a82d4-6097-4a12-a213-c7be06115ab2)

Main Menu:

![image](https://github.com/user-attachments/assets/fa3da1a2-d130-475a-831a-0a50adf757cb)

Reservation Calendar:

![image](https://github.com/user-attachments/assets/b8f9f56b-35e1-4704-8c79-6827370a19e0)

Reservation:

![image](https://github.com/user-attachments/assets/4b1a5afb-c741-467c-bbe4-8de230e77fb5)

Change price list:

![image](https://github.com/user-attachments/assets/9fa0c73b-e3e0-4520-a88b-f95f3135a64f)

All reservation:

![image](https://github.com/user-attachments/assets/f184f9a4-437b-4137-9a59-4905c6b3b01d)

Manage role:

![image](https://github.com/user-attachments/assets/acdba34d-771b-4d57-b1bc-f099fa02c0b4)

Reports:

![image](https://github.com/user-attachments/assets/0295f642-f62b-45b4-bced-b1cc7513726e)

Logs:

![image](https://github.com/user-attachments/assets/ab6d7d82-be57-4247-b570-9419ba37f0ac)

### It was role admin and you can see user's role:
Menu:

![image](https://github.com/user-attachments/assets/4f99c2cd-a8e1-4de6-b2d4-4d60c3cc93c9)


