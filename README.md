# Hotel Reservation System

A modern, user-friendly hotel reservation management application built with a graphical user interface (GUI) and a robust PostgreSQL database. 
This system streamlines hotel operations by providing secure authentication, role-based access, and comprehensive CRUD functionality for users, 
rooms, and bookings. It also supports data import/export and detailed report generation.

**Author**: Batyr Batyrov

---

## Features

- **Secure Authentication**: Login with username and password, ensuring only authorized access.
- **Role-Based Access Control**: Separate roles for admins (full control) and users (limited access).
- **Booking Management**: Create, view, update, and delete bookings with ease.
- **Room Availability**: Check available rooms by date using an intuitive calendar interface.
- **Room Price Management**: Admins can set and update room prices dynamically.
- **User Management**: Admins can manage user accounts.
- **Data Import/Export**: Import and export bookings in CSV format for seamless data handling.
- **Report Generation**: Generate detailed system status reports for operational insights.
- **Graphical Interface**: A clean and modern GUI with a booking calendar for visual scheduling.
- **Operation Logging**: All actions are logged for auditing and troubleshooting.

---

## Objectives

1. Implement full **CRUD operations** for users, rooms, and bookings.
2. Provide secure **authentication** and **role-based access** to ensure data integrity.
3. Enable **data import/export** and **report generation** for efficient hotel management.

---

## Requirements

1. **User Authentication**: Secure login system with username and password.
2. **Role Separation**: Distinct admin and user roles with appropriate permissions.
3. **Booking CRUD**: Create, read, update, and delete bookings.
4. **Room Availability Check**: Display available rooms for selected dates.
5. **Room Price Management**: Admins can manage room pricing.
6. **User Management**: Admins can manage user accounts.
7. **CSV Import/Export**: Support for importing/exporting booking data in CSV format.
8. **System Reports**: Generate reports on system status and bookings.
9. **Graphical Interface**: A user-friendly GUI with a booking calendar.
10. **Operation Logging**: Log all system operations for transparency.

---

## Documentation

### Algorithms

- **Room Availability Search**: Utilizes an SQL query to exclude booked rooms for a given date range:

  ```sql
  SELECT * FROM rooms
  WHERE room_id NOT IN (
      SELECT room_id FROM bookings
      WHERE check_in <= :end_date AND check_out >= :start_date
  );Booking Validation: Checks for date conflicts before creating a new booking.
  
Report Generation: Aggregates data from bookings and rooms to produce summary reports.
Data Structures
ArrayLists: Used in memory to store lists of users, rooms, and bookings for quick access.
Database Tables:
users: Stores user credentials and roles.
rooms: Stores room details (e.g., room number, price, type).
bookings: Stores booking information (e.g., user, room, check-in/out dates).
logs: Stores operation logs with timestamps and descriptions.
Modules
UserDao: Handles database operations for user management (e.g., login, CRUD).
RoomDao: Manages room-related database operations (e.g., availability, pricing).
BookingDao: Manages booking-related database operations.
DataService: Handles CSV import/export and report generation.
MainMenuController: Controls the main GUI, including navigation and calendar display.
LogService: Logs all operations to the database for auditing.
Challenges
PostgreSQL Configuration: Ensuring proper database setup and connection pooling.
CSV Error Handling: Validating and processing malformed CSV files gracefully.
Concurrent Bookings: Preventing double bookings through transaction locking.
GUI Responsiveness: Optimizing the calendar interface for large datasets.
