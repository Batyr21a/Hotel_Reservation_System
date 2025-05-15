package com.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

public class ExcelUtils {
    public static final String USERS_FILE = "users.xlsx";
    public static final String ROOMS_FILE = "rooms.xlsx";
    public static final String RESERVATIONS_FILE = "reservations.xlsx";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static void initializeFiles() throws IOException {
        initFile(USERS_FILE, "Users", new String[]{"id", "login", "password", "role"},
                Map.of(1, List.of(1, "admin", "admin", "admin")));
        initFile(ROOMS_FILE, "Rooms", new String[]{"room_number", "type", "price", "currency"},
                generateRooms());
        initFile(RESERVATIONS_FILE, "Reservations",
                new String[]{"id", "user_id", "room_number", "check_in", "check_out", "guest_name", "guest_surname"},
                null);
    }

    private static void initFile(String fileName, String sheetName, String[] headers, Map<Integer, List<Object>> data) throws IOException {
        if (new File(fileName).exists()) return;
        try (Workbook wb = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(fileName)) {
            Sheet sheet = wb.createSheet(sheetName);
            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }
            if (data != null) {
                data.forEach((rowNum, values) -> {
                    Row row = sheet.createRow(rowNum);
                    for (int i = 0; i < values.size(); i++) {
                        Cell cell = row.createCell(i);
                        Object value = values.get(i);
                        if (value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                });
            }
            wb.write(fos);
        }
    }

    private static Map<Integer, List<Object>> generateRooms() {
        Map<Integer, List<Object>> rooms = new HashMap<>();
        int rowNum = 1;
        for (int floor = 2; floor <= 5; floor++) {
            int base = floor * 100;
            rooms.put(rowNum++, List.of(base + 1, "Lux", 100.00, "USD"));
            rooms.put(rowNum++, List.of(base + 2, "Double", 60.00, "USD"));
            rooms.put(rowNum++, List.of(base + 3, "Double", 60.00, "USD"));
            rooms.put(rowNum++, List.of(base + 4, "Twin", 70.00, "USD"));
            rooms.put(rowNum++, List.of(base + 5, "Twin", 70.00, "USD"));
            rooms.put(rowNum++, List.of(base + 6, "Single", 50.00, "USD"));
            rooms.put(rowNum++, List.of(base + 7, "Single", 50.00, "USD"));
            rooms.put(rowNum++, List.of(base + 8, "Single", 50.00, "USD"));
        }
        return rooms;
    }

    public static List<Map<String, Object>> readExcel(String fileName) throws IOException {
        List<Map<String, Object>> data = new ArrayList<>();
        try (Workbook wb = new XSSFWorkbook(new FileInputStream(fileName))) {
            Sheet sheet = wb.getSheetAt(0);
            Row header = sheet.getRow(0);
            if (header == null) return data;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                Map<String, Object> rowData = new HashMap<>();
                
                for (int i = 0; i < header.getLastCellNum(); i++) {
                    Cell headerCell = header.getCell(i);
                    if (headerCell == null) continue;
                    
                    String key = headerCell.getStringCellValue();
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    
                    try {
                        switch (cell.getCellType()) {
                            case NUMERIC:
                                if (key.equals("room_number") || key.equals("id") || key.equals("user_id")) {
                                    rowData.put(key, (int) cell.getNumericCellValue());
                                } 
                                else if (cell.getCellType() == CellType.NUMERIC) {
                                    if (DateUtil.isCellDateFormatted(cell)) {
                                        rowData.put(key, cell.getLocalDateTimeCellValue().toLocalDate().format(DATE_FORMATTER));
                                    } 
                                    else {
                                        rowData.put(key, cell.getNumericCellValue());
                                    }
                                }
                                break;
                            case STRING:
                                rowData.put(key, cell.getStringCellValue());
                                break;
                            default:
                                rowData.put(key, "");
                        }
                    } catch (Exception e) {
                        rowData.put(key, "");
                    }
                }
                data.add(rowData);
            }
        }
        return data;
    }

    public static String getCellString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getLocalDateTimeCellValue().toLocalDate().format(DATE_FORMATTER)
                    : String.valueOf((int) cell.getNumericCellValue());
            default -> "";
        };
    }

    public static boolean isValidReservationHeader(Row header) {
        return Stream.of("id", "user_id", "room_number", "check_in", "check_out", "guest_name", "guest_surname")
                .allMatch(h -> header.getCell(header.getLastCellNum() - 7 + header.getFirstCellNum()) != null &&
                        h.equals(header.getCell(header.getLastCellNum() - 7 + header.getFirstCellNum()).getStringCellValue()));
    }
}