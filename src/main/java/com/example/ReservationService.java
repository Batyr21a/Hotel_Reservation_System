package com.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ReservationService {
    private final ExcelUtils excelUtils;
    private final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public ReservationService(ExcelUtils excelUtils) {
        this.excelUtils = excelUtils;
    }

    public void viewAvailableRooms(String checkInStr, String checkOutStr) throws IOException {
        if (checkInStr.isEmpty() || checkOutStr.isEmpty()) return;
        try {
            LocalDate checkIn = LocalDate.parse(checkInStr, DATE_FORMATTER);
            LocalDate checkOut = LocalDate.parse(checkOutStr, DATE_FORMATTER);
            if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) return;
            List<Map<String, Object>> rooms = ExcelUtils.readExcel(ExcelUtils.ROOMS_FILE);
            List<Map<String, Object>> reservations = ExcelUtils.readExcel(ExcelUtils.RESERVATIONS_FILE);
            rooms.stream()
                    .filter(room -> {
                        try {
                            double roomNum = Double.parseDouble(room.get("room_number").toString());
                            return reservations.stream().noneMatch(res -> {
                                try {
                                    double resRoomNum = Double.parseDouble(res.get("room_number").toString());
                                    return (int) resRoomNum == (int) roomNum &&
                                            !LocalDate.parse(res.get("check_out").toString(), DATE_FORMATTER).isBefore(checkIn) &&
                                            !LocalDate.parse(res.get("check_in").toString(), DATE_FORMATTER).isAfter(checkOut);
                                } catch (Exception e) {
                                    return false;
                                }
                            });
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .forEach(room -> {
                        try {
                            double roomNum = Double.parseDouble(room.get("room_number").toString());
                            double price = Double.parseDouble(room.get("price").toString());
                            System.out.printf("Room %d (%s) - $%.2f%n",
                                    (int) roomNum, room.get("type"), price);
                        } catch (Exception e) {
                            System.out.printf("Error displaying room: %s%n", e.getMessage());
                        }
                    });
        } catch (Exception e) {
            System.out.println("Ошибка в формате даты: " + e.getMessage());
        }
    }

    public boolean makeReservation(int userId, String roomNumberStr, String checkInStr, String checkOutStr,
                                   String guestName, String guestSurname) throws IOException {
        
        try {
            System.out.println("Debug: Starting reservation process...");
            if (roomNumberStr.isEmpty() || checkInStr.isEmpty() || checkOutStr.isEmpty() ||
                    guestName.isEmpty() || guestSurname.isEmpty()) {
                return false;
            }
            int roomNumber;
            try {
                roomNumber = Integer.parseInt(roomNumberStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid room number format");
                return false;
            }
            LocalDate checkIn, checkOut;
            try {
                checkIn = LocalDate.parse(checkInStr, DATE_FORMATTER);
                checkOut = LocalDate.parse(checkOutStr, DATE_FORMATTER);
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use dd.MM.yyyy");
                return false;
            }
            if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
                return false;
            }
            // System.out.println("Debug: Room number: " + roomNumber);
            // System.out.println("Debug: Check-in date: " + checkIn);
            // System.out.println("Debug: Check-out date: " + checkOut);
            List<Map<String, Object>> reservations = ExcelUtils.readExcel(ExcelUtils.RESERVATIONS_FILE);
            boolean isBooked = reservations.stream()
                    .anyMatch(res -> {
                        try {
                            int resRoom = Integer.parseInt(res.get("room_number").toString());
                            if (resRoom != roomNumber) {
                                return false;
                            }
                            LocalDate roomCheckOut = LocalDate.parse(res.get("check_out").toString(), DATE_FORMATTER);
                            LocalDate roomCheckIn = LocalDate.parse(res.get("check_in").toString(), DATE_FORMATTER);
                            return !roomCheckOut.isBefore(checkIn) && !roomCheckIn.isAfter(checkOut);
                        } catch (Exception e) {
                            return false;
                        }
                    });
            if (isBooked) {
                System.out.println("Debug: Room is already booked for the selected dates.");
                return false;
            }
            System.out.println("Debug: Room is available. Proceeding to create reservation...");
            try (Workbook wb = new XSSFWorkbook(new FileInputStream(ExcelUtils.RESERVATIONS_FILE))) {
                Sheet sheet = wb.getSheetAt(0);
                int maxId = 0;
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;
                    try {
                        Cell idCell = row.getCell(0);
                        if (idCell != null && idCell.getCellType() == CellType.NUMERIC) {
                            int id = (int) idCell.getNumericCellValue();
                            maxId = Math.max(maxId, id);
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
                Row row = sheet.createRow(sheet.getLastRowNum() + 1);
                row.createCell(0, CellType.NUMERIC).setCellValue(maxId + 1);
                row.createCell(1, CellType.NUMERIC).setCellValue(userId);
                row.createCell(2, CellType.NUMERIC).setCellValue(roomNumber);
                row.createCell(3, CellType.STRING).setCellValue(checkIn.format(DATE_FORMATTER));
                row.createCell(4, CellType.STRING).setCellValue(checkOut.format(DATE_FORMATTER));
                row.createCell(5, CellType.STRING).setCellValue(guestName);
                row.createCell(6, CellType.STRING).setCellValue(guestSurname);
                try (FileOutputStream fos = new FileOutputStream(ExcelUtils.RESERVATIONS_FILE)) {
                    wb.write(fos);
                }
                return true;
            }
        } catch (Exception e) {
            System.out.println("Ошибка при бронировании: " + e.getMessage());
            return false;
        }
    }

    public void viewReservations(int userId) throws IOException {
        List<Map<String, Object>> reservations = ExcelUtils.readExcel(ExcelUtils.RESERVATIONS_FILE);

        int dynamicId = 1;
        for (Map<String, Object> res : reservations) {
            if ((int) Double.parseDouble(res.get("user_id").toString()) == userId) {
                System.out.printf("ID: %d, Room: %d, Check-in: %s, Check-out: %s, Guest: %s %s%n",
                        dynamicId++,
                        (int) Double.parseDouble(res.get("room_number").toString()),
                        res.get("check_in"),
                        res.get("check_out"),
                        res.get("guest_name"),
                        res.get("guest_surname"));
            }
        }
    }

    public boolean updateReservation(int userId, String idStr, String roomNumberStr, String checkInStr,
                                     String checkOutStr, String guestName, String guestSurname) throws IOException {
        try {
            if (idStr.isEmpty() || roomNumberStr.isEmpty() || checkInStr.isEmpty() ||
                    checkOutStr.isEmpty() || guestName.isEmpty() || guestSurname.isEmpty()) {
                return false;
            }
            int reservationId = Integer.parseInt(idStr);
            int roomNumber = Integer.parseInt(roomNumberStr);
            LocalDate checkIn = LocalDate.parse(checkInStr, DATE_FORMATTER);
            LocalDate checkOut = LocalDate.parse(checkOutStr, DATE_FORMATTER);
            if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
                return false;
            }
            List<Map<String, Object>> rooms = ExcelUtils.readExcel(ExcelUtils.ROOMS_FILE);
            boolean roomExists = rooms.stream()
                    .anyMatch(r -> Integer.parseInt(r.get("room_number").toString()) == roomNumber);
            if (!roomExists) {
                return false;
            }
            List<Map<String, Object>> reservations = ExcelUtils.readExcel(ExcelUtils.RESERVATIONS_FILE);
            boolean isBooked = reservations.stream()
                    .anyMatch(res -> {
                        try {
                            if (Double.parseDouble(res.get("id").toString()) == reservationId) {
                                return false;
                            }
                            int resRoom = Integer.parseInt(res.get("room_number").toString());
                            if (resRoom == roomNumber) {
                                LocalDate resCheckOut = LocalDate.parse(res.get("check_out").toString(), DATE_FORMATTER);
                                LocalDate resCheckIn = LocalDate.parse(res.get("check_in").toString(), DATE_FORMATTER);
                                return !resCheckOut.isBefore(checkIn) && !resCheckIn.isAfter(checkOut);
                            }
                            return false;
                        } catch (Exception e) {
                            return false;
                        }
                    });
            if (isBooked) {
                return false;
            }
            try (Workbook wb = new XSSFWorkbook(new FileInputStream(ExcelUtils.RESERVATIONS_FILE))) {
                Sheet sheet = wb.getSheetAt(0);
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;
                    if ((int) row.getCell(0).getNumericCellValue() == reservationId &&
                            (int) row.getCell(1).getNumericCellValue() == userId) {
                        row.getCell(2).setCellValue(roomNumber);
                        row.getCell(3).setCellValue(checkIn.format(DATE_FORMATTER));
                        row.getCell(4).setCellValue(checkOut.format(DATE_FORMATTER));
                        row.getCell(5).setCellValue(guestName);
                        row.getCell(6).setCellValue(guestSurname);
                        try (FileOutputStream fos = new FileOutputStream(ExcelUtils.RESERVATIONS_FILE)) {
                            wb.write(fos);
                        }
                        return true;
                    }
                }
                return false;
            }
        } catch (Exception e) {
            System.out.println("Ошибка при обновлении брони: " + e.getMessage());
            return false;
        }
    }

    public boolean cancelReservation(int userId, String role, String idStr) throws IOException {
        try {
            int reservationId = Integer.parseInt(idStr);
            try (Workbook wb = new XSSFWorkbook(new FileInputStream(ExcelUtils.RESERVATIONS_FILE))) {
                Sheet sheet = wb.getSheetAt(0);
                Sheet temp = wb.createSheet("Temp");
                Row header = temp.createRow(0);
              
                for (int i = 0; i < 7; i++) {
                    header.createCell(i).setCellValue(
                        switch (i) {
                            case 0 -> "id";
                            case 1 -> "user_id";
                            case 2 -> "room_number";
                            case 3 -> "check_in";
                            case 4 -> "check_out";
                            case 5 -> "guest_name";
                            case 6 -> "guest_surname";
                            default -> "";
                        }
                    );
                }
                int rowNum = 1;
                boolean found = false;
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;
                    int id = (int) row.getCell(0).getNumericCellValue();
                    int resUserId = (int) row.getCell(1).getNumericCellValue();
                    if (id == reservationId && (resUserId == userId || "admin".equals(role))) {
                        found = true;
                        continue;
                    }
                    Row newRow = temp.createRow(rowNum++);
                    for (int i = 0; i < 7; i++) newRow.createCell(i).setCellValue(excelUtils.getCellString(row.getCell(i)));
                }
                wb.removeSheetAt(wb.getSheetIndex("Reservations"));
                wb.setSheetName(wb.getSheetIndex("Temp"), "Reservations");
                try (FileOutputStream fos = new FileOutputStream(ExcelUtils.RESERVATIONS_FILE)) {
                    wb.write(fos);
                }
                return found;
            }
        } catch (Exception e) {
            System.out.println("Ошибка при отмене брони: " + e.getMessage());
            return false;
        }
    }

    public void viewAllReservations() throws IOException {
        ExcelUtils.readExcel(ExcelUtils.RESERVATIONS_FILE)
                .forEach(res -> System.out.printf("ID: %d, User: %d, Room: %d, Check-in: %s, Check-out: %s, Guest: %s %s%n",
                        Double.valueOf(res.get("id").toString()).intValue(),
                        Double.valueOf(res.get("user_id").toString()).intValue(),
                        Double.valueOf(res.get("room_number").toString()).intValue(),
                        res.get("check_in"), res.get("check_out"), res.get("guest_name"), res.get("guest_surname")));
    }

    public boolean exportReservations(int userId, String role, String fileName) throws IOException {
        if (fileName.isEmpty()) return false;
        String exportFile = fileName.endsWith(".xlsx") ? fileName : fileName + ".xlsx";
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Reservations");
            Row header = sheet.createRow(0);
           
            for (int i = 0; i < 7; i++) {
                header.createCell(i).setCellValue(
                    switch (i) {
                        case 0 -> "id";
                        case 1 -> "user_id";
                        case 2 -> "room_number";
                        case 3 -> "check_in";
                        case 4 -> "check_out";
                        case 5 -> "guest_name";
                        case 6 -> "guest_surname";
                        default -> "";
                    }
                );
            }
            List<Map<String, Object>> reservations = excelUtils.readExcel(ExcelUtils.RESERVATIONS_FILE)
                    .stream()
                    .filter(res -> "admin".equals(role) || Double.parseDouble(res.get("user_id").toString()) == userId)
                    .toList();
            int rowNum = 1;
            for (Map<String, Object> res : reservations) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(Double.parseDouble(res.get("id").toString()));
                row.createCell(1).setCellValue(Double.parseDouble(res.get("user_id").toString()));
                row.createCell(2).setCellValue(Double.parseDouble(res.get("room_number").toString()));
                row.createCell(3).setCellValue((String) res.get("check_in"));
                row.createCell(4).setCellValue((String) res.get("check_out"));
                row.createCell(5).setCellValue((String) res.get("guest_name"));
                row.createCell(6).setCellValue((String) res.get("guest_surname"));
            }
            try (FileOutputStream fos = new FileOutputStream(exportFile)) {
                wb.write(fos);
            }
            return true;
        } catch (IOException e) {
            System.out.println("Ошибка при экспорте: " + e.getMessage());
            return false;
        }
    }

    public void importReservations(int userId, String role, String fileName) throws IOException {
        if (fileName.isEmpty()) return;
        try (Workbook wb = new XSSFWorkbook(new FileInputStream(fileName))) {
            Sheet sheet = wb.getSheetAt(0);
            if (!excelUtils.isValidReservationHeader(sheet.getRow(0))) return;
            List<Map<String, Object>> users = excelUtils.readExcel(ExcelUtils.USERS_FILE);
            List<Map<String, Object>> rooms = excelUtils.readExcel(ExcelUtils.ROOMS_FILE);
            try (Workbook resWb = new XSSFWorkbook(new FileInputStream(ExcelUtils.RESERVATIONS_FILE))) {
                Sheet resSheet = resWb.getSheetAt(0);
                int maxId = StreamSupport.stream(resSheet.spliterator(), false)
                        .skip(1)
                        .mapToInt(row -> (int) row.getCell(0).getNumericCellValue())
                        .max()
                        .orElse(0);
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;
                    try {
                        int resUserId = (int) row.getCell(1).getNumericCellValue();
                        int roomNumber = (int) row.getCell(2).getNumericCellValue();
                        String checkInStr = excelUtils.getCellString(row.getCell(3));
                        String checkOutStr = excelUtils.getCellString(row.getCell(4));
                        String guestName = excelUtils.getCellString(row.getCell(5));
                        String guestSurname = excelUtils.getCellString(row.getCell(6));
                        if (checkInStr.isEmpty() || checkOutStr.isEmpty() || guestName.isEmpty() || guestSurname.isEmpty() ||
                                LocalDate.parse(checkOutStr, DATE_FORMATTER).isBefore(LocalDate.parse(checkInStr, DATE_FORMATTER)) ||
                                users.stream().noneMatch(u -> Double.parseDouble(u.get("id").toString()) == resUserId) ||
                                rooms.stream().noneMatch(r -> Double.parseDouble(r.get("room_number").toString()) == roomNumber) ||
                                (!"admin".equals(role) && resUserId != userId) ||
                                excelUtils.readExcel(ExcelUtils.RESERVATIONS_FILE).stream().anyMatch(res ->
                                        Double.parseDouble(res.get("room_number").toString()) == roomNumber &&
                                                !LocalDate.parse((String) res.get("check_out"), DATE_FORMATTER).isBefore(LocalDate.parse(checkInStr, DATE_FORMATTER)) &&
                                                !LocalDate.parse((String) res.get("check_in"), DATE_FORMATTER).isAfter(LocalDate.parse(checkOutStr, DATE_FORMATTER))))
                            continue;
                        Row newRow = resSheet.createRow(resSheet.getLastRowNum() + 1);
                        newRow.createCell(0).setCellValue(++maxId);
                        newRow.createCell(1).setCellValue(resUserId);
                        newRow.createCell(2).setCellValue(roomNumber);
                        newRow.createCell(3).setCellValue(checkInStr);
                        newRow.createCell(4).setCellValue(checkOutStr);
                        newRow.createCell(5).setCellValue(guestName);
                        newRow.createCell(6).setCellValue(guestSurname);
                    } catch (Exception e) {
                        continue;
                    }
                }
                try (FileOutputStream fos = new FileOutputStream(ExcelUtils.RESERVATIONS_FILE)) {
                    resWb.write(fos);
                }
            }
        }
    }

    public void generateReport() throws IOException {
        List<Map<String, Object>> users = ExcelUtils.readExcel(ExcelUtils.USERS_FILE);
        List<Map<String, Object>> reservations = ExcelUtils.readExcel(ExcelUtils.RESERVATIONS_FILE);
        List<Map<String, Object>> rooms = ExcelUtils.readExcel(ExcelUtils.ROOMS_FILE);
        System.out.printf("Users: %d, Reservations: %d, Active users: %d%n",
                users.size(), reservations.size(),
                reservations.stream().map(r -> Double.parseDouble(r.get("user_id").toString())).distinct().count());
        Map<String, Long> roomTypes = reservations.stream()
                .collect(Collectors.groupingBy(res -> rooms.stream()
                        .filter(r -> Double.parseDouble(r.get("room_number").toString()) == Double.parseDouble(res.get("room_number").toString()))
                        .map(r -> (String) r.get("type"))
                        .findFirst()
                        .orElse("Unknown"), Collectors.counting()));
        roomTypes.forEach((type, count) -> System.out.printf("- %s: %d%n", type, count));
    }
}