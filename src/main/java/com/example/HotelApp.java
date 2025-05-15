package com.example;

import java.io.IOException;
import java.util.Scanner;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class HotelApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService;
    private static final ReservationService reservationService;
    private static int[] userId = {-1};
    private static String[] role = {""};

    static {
        UserService tempUserService = null;
        ReservationService tempReservationService = null;
        try {
            ExcelUtils.initializeFiles();
            ExcelUtils excelUtils = new ExcelUtils();
            tempUserService = new UserService(excelUtils);
            tempReservationService = new ReservationService(excelUtils);
        } catch (IOException e) {
            System.err.println("Failed to initialize services: " + e.getMessage());
            System.exit(1);
        }
        userService = tempUserService;
        reservationService = tempReservationService;
    }

    public static void main(String[] args) {
        while (true) {
            try {
                if (userId[0] == -1) showLoginMenu();
                else showMainMenu();
            } catch (IOException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private static void showLoginMenu() throws IOException {
        System.out.println("\n+-------------------------------+");
        System.out.println("|      HOTEL SYSTEM LOGIN       |");
        System.out.println("+-------------------------------+");
        System.out.println("|  1.  Login                    |");
        System.out.println("|  2.  Register                 |");
        System.out.println("|  3.  Exit                     |");
        System.out.println("+-------------------------------+");
        System.out.print("Select an option (1-3): ");



        switch (scanner.nextLine().trim()) {
            case "1" -> {
                System.out.print("Username: ");
                String login = scanner.nextLine().trim();
                System.out.print("Password: ");
                String password = scanner.nextLine().trim();
                if (userService.login(login, password, userId, role))
                    System.out.println("Login successful");
                else
                    System.out.println("Invalid username or password");
            }
            case "2" -> {
                System.out.print("Username: ");
                String login = scanner.nextLine().trim();
                System.out.print("Password: ");
                String password = scanner.nextLine().trim();
                System.out.println(userService.register(login, password) ? "Registration successful" : "Username already exists");
            }
            case "3" -> System.exit(0);
            default -> System.out.println("Invalid option");
        }
    }

    private static void showMainMenu() throws IOException {
        System.out.println("\n+-------------------------------------------+");
        System.out.println("|         HOTEL RESERVATION SYSTEM          |");
        System.out.println("+-------------------------------------------+");
        System.out.println("|  1.  View Available Rooms                 |");
        System.out.println("|  2.  Make Reservation                     |");
        System.out.println("|  3.  My Reservations                      |");
        System.out.println("|  4.  Update Reservation                   |");
        System.out.println("|  5.  Cancel Reservation                   |");
        System.out.println("|  6.  Export Reservations                  |");
        System.out.println("|  7.  Import Reservations                  |");
        System.out.println("|  8.  Generate Report                      |");
        if (role[0].equals("admin")) {
            System.out.println("|  9.  All Reservations                     |");
            System.out.println("| 10.  Manage Users                         |");
        }
        System.out.println("| 11.  Logout                               |");
        System.out.println("+-------------------------------------------+");
        System.out.print("Select an option (1-11): ");


        switch (scanner.nextLine().trim()) {
            case "1" -> {
                System.out.print("Check-in date (dd.MM.yyyy): ");
                String checkIn = scanner.nextLine().trim();
                System.out.print("Check-out date (dd.MM.yyyy): ");
                String checkOut = scanner.nextLine().trim();
                reservationService.viewAvailableRooms(checkIn, checkOut);
            }
            case "2" -> {
                System.out.print("Room number: ");
                String room = scanner.nextLine().trim();
                System.out.print("Check-in date (dd.MM.yyyy): ");
                String checkIn = scanner.nextLine().trim();
                System.out.print("Check-out date (dd.MM.yyyy): ");
                String checkOut = scanner.nextLine().trim();
                System.out.print("Guest first name: ");
                String name = scanner.nextLine().trim();
                System.out.print("Guest last name: ");
                String surname = scanner.nextLine().trim();
                System.out.println(reservationService.makeReservation(userId[0], room, checkIn, checkOut, name, surname)
                        ? "Reservation created successfully" : "Failed to create reservation");
            }
            case "3" -> reservationService.viewReservations(userId[0]);
            case "4" -> {
                System.out.print("Reservation ID: ");
                String id = scanner.nextLine().trim();
                System.out.print("Room number: ");
                String room = scanner.nextLine().trim();
                System.out.print("Check-in date (dd.MM.yyyy): ");
                String checkIn = scanner.nextLine().trim();
                System.out.print("Check-out date (dd.MM.yyyy): ");
                String checkOut = scanner.nextLine().trim();
                System.out.print("Guest first name: ");
                String name = scanner.nextLine().trim();
                System.out.print("Guest last name: ");
                String surname = scanner.nextLine().trim();
                System.out.println(reservationService.updateReservation(userId[0], id, room, checkIn, checkOut, name, surname)
                        ? "Reservation updated successfully" : "Failed to update reservation");
            }
            case "5" -> {
                System.out.print("Reservation ID: ");
                String id = scanner.nextLine().trim();
                System.out.println(reservationService.cancelReservation(userId[0], role[0], id)
                        ? "Reservation cancelled successfully" : "Failed to cancel reservation");
            }
            case "6" -> {
                System.out.print("File name: ");
                String file = scanner.nextLine().trim();
                System.out.println(reservationService.exportReservations(userId[0], role[0], file)
                        ? "Reservations exported successfully" : "Failed to export reservations");
            }
            case "7" -> {
                System.out.print("File name: ");
                String file = scanner.nextLine().trim();
                reservationService.importReservations(userId[0], role[0], file);
                System.out.println("Import completed");
            }
            case "8" -> reservationService.generateReport();
            case "9" -> {
                if (role[0].equals("admin")) reservationService.viewAllReservations();
                else System.out.println("Access denied");
            }
            case "10" -> {
                if (role[0].equals("admin")) userService.manageUsers(userId[0]);
                else System.out.println("Access denied");
            }
            case "11" -> {
                userId[0] = -1;
                role[0] = "";
                System.out.println("Logged out successfully");
            }
        }
    }
}