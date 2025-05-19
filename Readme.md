# Hotel Reservation System

## Presentation 
https://www.canva.com/design/DAGk9XTeK74/8bbLqMqMVA8K_7-M_QqGeg/edit?utm_content=DAGk9XTeK74&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton

## Description
A hotel reservation management application built in Java, utilizing Apache POI for Excel-based data storage and a console-based interface. It supports creating, viewing, updating, and deleting reservations, managing users and rooms, importing/exporting data in Excel format, and generating reports.

## Author
Batyr Batyrov

## Project Goals
- Provide efficient management of reservations, rooms, and users through a console interface.
- Implement role-based access (administrator and user).
- Support data import/export and report generation.

## Requirements
- User authentication via login and password.
- Role-based access: administrator (full access) and user (limited access).
- Create, view, update, and delete reservations.
- Display available rooms for selected dates.
- Manage room prices (administrators only).
- Manage users: add, update roles, delete (administrators only).
- Import and export reservation and user data in Excel format.
- Generate reports on system status (user, reservation, and room statistics).
- Console-based interface with user interaction.
- Log all operations to a file.

## Installation and Setup

### Required Software
- Java 17 or higher
- Apache POI library
- Maven

### Installation Steps
- **Project Build:**
  - Clone the repository: `git clone https://github.com/Batyr21a/Hotel_Reservation_System`
  - Navigate to the project folder: `cd Hotel_Reservation_System`
  - Build the project: `mvn clean install`
- **Run the Application:**
  - Run the application: `mvn exec:java -Dexec.mainClass="com.example.HotelApp"`
- **Test Credentials:**
  - Administrator: login `admin`, password `admin`
  - User: login `user`, password `user`

## Project Structure
- `com.example`: Main package containing application logic.
  - `ExcelUtils.java`: Handles Excel file operations.
  - `HotelApp.java`: Main application class with console interface.
  - `ReservationService.java`: Manages reservation operations.
  - `UserService.java`: Manages user operations.
- `resources`: Configuration and log files (e.g., logs/hotel.log).

## Key Features
- **Authentication:** Login with role-based access.
- **Reservations:** Create, view, update, and delete reservations via console.
- **Room Management:** Update prices (administrators only).
- **User Management:** Add, update roles, delete (administrators only).
- **Import/Export:** Save and load data in Excel format.
- **Reports:** Statistics on users, reservations, and rooms.
- **Logging:** Operations logged to `logs/hotel.log`.

## Documentation

### Algorithms
- **Available Room Search:** Checks Excel data to exclude booked rooms for specified dates.
- **Excel Processing:** Validates data format and handles file I/O.

## Data Structures
- **In-Memory:** `List<Map<String, Object>>` for temporary data storage from Excel.
- **File-Based:** Excel files (`users.xlsx`, `rooms.xlsx`, `reservations.xlsx`) for persistent storage.

## Modules
- **ExcelUtils:** Manages reading and writing to Excel files.
- **ReservationService:** Handles reservation creation, updates, and deletions.
- **UserService:** Manages user authentication and administration.
- **HotelApp:** Controls the console-based user interface.

## Challenges and Solutions
- **Challenge:** Incorrect Excel format during import.
  - **Solution:** Validate headers and data types in `ExcelUtils`, handle exceptions.
- **Challenge:** File access errors.
  - **Solution:** Implement try-with-resources for file handling and log errors.

## Test Cases
- **Invalid password login:** Displays an error message.
- **Booking an occupied room:** Reservation is rejected.
- **Importing invalid Excel:** Shows an error.
- **Updating room price:** Reflected in the Excel file.

## Screenshots
![Снимок экрана 2025-05-15 120542](https://github.com/user-attachments/assets/7af29ba3-0d81-4090-a5f8-e52d3d66d716)

![Снимок экрана 2025-05-15 120731](https://github.com/user-attachments/assets/1c48aac8-8421-43bb-babd-1cbeb71b6e3e)

![Снимок экрана 2025-05-15 120828](https://github.com/user-attachments/assets/21d3f044-5552-4e62-821b-795c4b4d46e1)

![Снимок экрана 2025-05-15 121134](https://github.com/user-attachments/assets/f8152ef5-c94e-4cd4-bab4-0ea1ef898b11)

![Снимок экрана 2025-05-15 121154](https://github.com/user-attachments/assets/3bfc1f68-13ff-42be-9abf-5136b390c72b)

![Снимок экрана 2025-05-15 121301](https://github.com/user-attachments/assets/48248516-f0ad-44f9-a7a2-5c8fb2d99e4b)

![Снимок экрана 2025-05-15 121753](https://github.com/user-attachments/assets/1c119977-7cf1-4a00-b34c-f2d03b450bec)

![Снимок экрана 2025-05-15 122113](https://github.com/user-attachments/assets/d2b86a75-f19f-425e-8671-d13a7ab2bb28)

![Снимок экрана 2025-05-15 122217](https://github.com/user-attachments/assets/a8cbe555-a299-406b-9a33-4b83e65b1d12)

![Снимок экрана 2025-05-15 122242](https://github.com/user-attachments/assets/25009522-e65a-4e58-8024-f8fe63a02ae8)

![Снимок экрана 2025-05-15 122608](https://github.com/user-attachments/assets/9ac31ec3-54ad-401b-9808-94c778e07487)

![Снимок экрана 2025-05-15 122713](https://github.com/user-attachments/assets/eb17600a-bd88-45f2-8f5a-a9e6dcef96ca)

![Снимок экрана 2025-05-15 122803](https://github.com/user-attachments/assets/377d178d-1db7-4680-83cc-64723c244efc)

![image](https://github.com/user-attachments/assets/43f8c44c-0ff6-477d-85a6-f63e6f2392a6)































