package com.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.Scanner;
import java.util.stream.StreamSupport;

public class UserService {
    private final ExcelUtils excelUtils;
    private final Scanner scanner = new Scanner(System.in);

    public UserService(ExcelUtils excelUtils) {
        this.excelUtils = excelUtils;
    }

    public boolean login(String login, String password, int[] userId, String[] role) throws IOException {
        if (login.isEmpty() || password.isEmpty()) return false;
        try (Workbook wb = new XSSFWorkbook(new FileInputStream(ExcelUtils.USERS_FILE))) {
            Sheet sheet = wb.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                Cell loginCell = row.getCell(1);
                Cell passwordCell = row.getCell(2);
                if (loginCell != null && passwordCell != null &&
                        login.equals(loginCell.getStringCellValue()) &&
                        password.equals(passwordCell.getStringCellValue())) {
                    Cell idCell = row.getCell(0);
                    if (idCell != null && idCell.getCellType() == CellType.NUMERIC) {
                        userId[0] = (int) idCell.getNumericCellValue();
                        role[0] = row.getCell(3).getStringCellValue();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean register(String login, String password) throws IOException {
        if (login.isEmpty() || password.isEmpty()) return false;
        try (Workbook wb = new XSSFWorkbook(new FileInputStream(ExcelUtils.USERS_FILE))) {
            Sheet sheet = wb.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() > 1 &&
                    StreamSupport.stream(sheet.spliterator(), false)
                            .skip(1)
                            .anyMatch(row -> {
                                Cell loginCell = row.getCell(1);
                                return loginCell != null && login.equals(loginCell.getStringCellValue());
                            }))
                return false;
            int maxId = StreamSupport.stream(sheet.spliterator(), false)
                    .skip(1)
                    .filter(row -> row.getCell(0) != null && row.getCell(0).getCellType() == CellType.NUMERIC)
                    .mapToInt(row -> (int) row.getCell(0).getNumericCellValue())
                    .max()
                    .orElse(0);
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            row.createCell(0).setCellValue(maxId + 1);
            row.createCell(1).setCellValue(login);
            row.createCell(2).setCellValue(password);
            row.createCell(3).setCellValue("user");
            try (FileOutputStream fos = new FileOutputStream(ExcelUtils.USERS_FILE)) {
                wb.write(fos);
            }
            return true;
        }
    }

    public void manageUsers(int currentUserId) throws IOException {
        while (true) {
            System.out.println("\n1. List users\n2. Delete user\n3. Back");
            String choice = scanner.nextLine().trim();
            if (choice.equals("3")) break;
            switch (choice) {
                case "1" -> ExcelUtils.readExcel(ExcelUtils.USERS_FILE)
                        .forEach(u -> System.out.printf("ID: %d, Username: %s, Role: %s%n",
                                Double.valueOf(u.get("id").toString()).intValue(), u.get("login"), u.get("role")));
                case "2" -> {
                    System.out.print("User ID: ");
                    try {
                        int id = Integer.parseInt(scanner.nextLine().trim());
                        if (id == currentUserId) {
                            System.out.println("You cannot delete yourself");
                            continue;
                        }
                        deleteUser(id);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid user ID");
                    }
                }
                default -> System.out.println("Invalid option");
            }
        }
    }

    private void deleteUser(int userId) throws IOException {
        try (Workbook wb = new XSSFWorkbook(new FileInputStream(ExcelUtils.USERS_FILE))) {
            Sheet sheet = wb.getSheetAt(0);
            Sheet temp = wb.createSheet("Temp");
            Row header = temp.createRow(0);
            String[] headers = {"id", "login", "password", "role"};
            for (int i = 0; i < headers.length; i++) header.createCell(i).setCellValue(headers[i]);
            int rowNum = 1;
            boolean found = false;
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if ((int) row.getCell(0).getNumericCellValue() == userId) {
                    found = true;
                    continue;
                }
                Row newRow = temp.createRow(rowNum++);
                for (int i = 0; i < 4; i++) newRow.createCell(i).setCellValue(ExcelUtils.getCellString(row.getCell(i)));
            }
            wb.removeSheetAt(wb.getSheetIndex("Users"));
            wb.setSheetName(wb.getSheetIndex("Temp"), "Users");
            try (FileOutputStream fos = new FileOutputStream(ExcelUtils.USERS_FILE)) {
                wb.write(fos);
            }
            if (found) deleteUserReservations(userId);
        }
    }

    private void deleteUserReservations(int userId) throws IOException {
        try (Workbook wb = new XSSFWorkbook(new FileInputStream(ExcelUtils.RESERVATIONS_FILE))) {
            Sheet sheet = wb.getSheetAt(0);
            Sheet temp = wb.createSheet("Temp");
            Row header = temp.createRow(0);
            String[] headers = {"id", "user_id", "room_number", "check_in", "check_out", "guest_name", "guest_surname"};
            for (int i = 0; i < headers.length; i++) header.createCell(i).setCellValue(headers[i]);
            int rowNum = 1;
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if ((int) row.getCell(1).getNumericCellValue() == userId) continue;
                Row newRow = temp.createRow(rowNum++);
                for (int i = 0; i < 7; i++) newRow.createCell(i).setCellValue(ExcelUtils.getCellString(row.getCell(i)));
            }
            wb.removeSheetAt(wb.getSheetIndex("Reservations"));
            wb.setSheetName(wb.getSheetIndex("Temp"), "Reservations");
            try (FileOutputStream fos = new FileOutputStream(ExcelUtils.RESERVATIONS_FILE)) {
                wb.write(fos);
            }
        }
    }
}