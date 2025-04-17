package com.example;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.example.Reservation.Reservation;
import com.example.User.User;

public class CsvHandler {
    private static final Logger LOGGER = Logger.getLogger(CsvHandler.class.getName());
    private static final String CSV_DELIMITER = ",";
    private static final String NEWLINE = System.getProperty("line.separator");

    public static class CsvHandlerException extends Exception {
        public CsvHandlerException(String message) {
            super(message);
        }

        public CsvHandlerException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static void exportReservations(List<Reservation> reservations, String fileName) throws CsvHandlerException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("id,user_id,room_number,check_in,check_out,guest_name,guest_surname");
            
            for (Reservation res : reservations) {
                writer.printf("%d%s%d%s%d%s%s%s%s%s%s%s%s%n",
                    res.getId(), CSV_DELIMITER,
                    res.getUserId(), CSV_DELIMITER,
                    res.getRoomNumber(), CSV_DELIMITER,
                    res.getCheckIn(), CSV_DELIMITER,
                    res.getCheckOut(), CSV_DELIMITER,
                    escapeCsvField(res.getGuestName()), CSV_DELIMITER,
                    escapeCsvField(res.getGuestSurname()));
            }
            LOGGER.info("Successfully exported " + reservations.size() + " reservations to " + fileName);
        } catch (IOException e) {
            LOGGER.severe("Error exporting reservations: " + e.getMessage());
            throw new CsvHandlerException("Failed to export reservations", e);
        }
    }

    public static List<Reservation> importReservations(String fileName) throws CsvHandlerException {
        List<Reservation> reservations = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String header = reader.readLine();
            if (header == null || !isValidReservationHeader(header)) {
                throw new CsvHandlerException("Invalid CSV format: missing or incorrect header");
            }

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    String[] parts = line.split(CSV_DELIMITER);
                    if (parts.length != 7) {
                        throw new CsvHandlerException("Invalid number of fields at line " + lineNumber);
                    }

                    reservations.add(new Reservation(
                        Integer.parseInt(parts[0].trim()),
                        Integer.parseInt(parts[1].trim()),
                        Integer.parseInt(parts[2].trim()),
                        LocalDate.parse(parts[3].trim()),
                        LocalDate.parse(parts[4].trim()),
                        unescapeCsvField(parts[5].trim()),
                        unescapeCsvField(parts[6].trim())
                    ));
                } catch (NumberFormatException e) {
                    throw new CsvHandlerException("Invalid number format at line " + lineNumber, e);
                } catch (DateTimeParseException e) {
                    throw new CsvHandlerException("Invalid date format at line " + lineNumber, e);
                }
            }
            LOGGER.info("Successfully imported " + reservations.size() + " reservations from " + fileName);
        } catch (IOException e) {
            LOGGER.severe("Error importing reservations: " + e.getMessage());
            throw new CsvHandlerException("Failed to import reservations", e);
        }
        return reservations;
    }

    public static void exportUsers(List<User> users, String fileName) throws CsvHandlerException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("id,login,role");
            for (User user : users) {
                writer.printf("%d%s%s%s%s%n",
                    user.getId(), CSV_DELIMITER,
                    escapeCsvField(user.getLogin()), CSV_DELIMITER,
                    escapeCsvField(user.getRole()));
            }
            LOGGER.info("Successfully exported " + users.size() + " users to " + fileName);
        } catch (IOException e) {
            LOGGER.severe("Error exporting users: " + e.getMessage());
            throw new CsvHandlerException("Failed to export users", e);
        }
    }

    public static List<User> importUsers(String fileName) throws CsvHandlerException {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String header = reader.readLine();
            if (header == null || !isValidUserHeader(header)) {
                throw new CsvHandlerException("Invalid CSV format: missing or incorrect header");
            }

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    String[] parts = line.split(CSV_DELIMITER);
                    if (parts.length != 3) {
                        throw new CsvHandlerException("Invalid number of fields at line " + lineNumber);
                    }

                    users.add(new User(
                        Integer.parseInt(parts[0].trim()),
                        unescapeCsvField(parts[1].trim()),
                        unescapeCsvField(parts[2].trim())
                    ));
                } catch (NumberFormatException e) {
                    throw new CsvHandlerException("Invalid number format at line " + lineNumber, e);
                }
            }
            LOGGER.info("Successfully imported " + users.size() + " users from " + fileName);
        } catch (IOException e) {
            LOGGER.severe("Error importing users: " + e.getMessage());
            throw new CsvHandlerException("Failed to import users", e);
        }
        return users;
    }

    private static boolean isValidReservationHeader(String header) {
        return "id,user_id,room_number,check_in,check_out,guest_name,guest_surname"
            .equalsIgnoreCase(header.trim());
    }

    private static boolean isValidUserHeader(String header) {
        return "id,login,role".equalsIgnoreCase(header.trim());
    }

    private static String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        if (field.contains(CSV_DELIMITER) || field.contains("\"") || field.contains(NEWLINE)) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    private static String unescapeCsvField(String field) {
        if (field == null || field.isEmpty()) {
            return "";
        }
        if (field.startsWith("\"") && field.endsWith("\"")) {
            return field.substring(1, field.length() - 1).replace("\"\"", "\"");
        }
        return field;
    }
}
