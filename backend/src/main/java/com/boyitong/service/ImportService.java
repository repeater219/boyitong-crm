package com.boyitong.service;

import com.boyitong.entity.Customer;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ImportService {

    /** Auto-detect city from file name (e.g. "柳州.xlsx" or "鄂尔多斯数据.xlsx") */
    public String detectCity(String fileName) {
        if (fileName == null) return "未知";
        // Try to match known cities
        String name = fileName.replaceAll("\\.[^.]+$", ""); // remove extension
        if (name.contains("柳州")) return "柳州";
        if (name.contains("鄂尔多斯")) return "鄂尔多斯";
        return name;
    }

    public List<Customer> parseExcel(MultipartFile file, String city) {
        try (InputStream is = file.getInputStream()) {
            return parseExcel(is, city);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage(), e);
        }
    }

    public List<Customer> parseExcel(byte[] fileData, String city) {
        return parseExcel(new ByteArrayInputStream(fileData), city);
    }

    private List<Customer> parseExcel(InputStream is, String city) {
        List<Customer> customers = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean isLiuzhou = "柳州".equals(city);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Customer c = new Customer();
                c.setCity(city);

                if (isLiuzhou) {
                    c.setDate(getCellValue(row.getCell(0)));
                    c.setArea(getCellValue(row.getCell(1)));
                    c.setAddress(getCellValue(row.getCell(2)));
                    c.setCategory(getCellValue(row.getCell(3)));
                    c.setSize(parseDouble(getCellValue(row.getCell(4))));
                    c.setPhone(getCellValue(row.getCell(5)));
                    c.setExpiryDate(getCellValue(row.getCell(6)));
                    c.setSalesperson(getCellValue(row.getCell(7)));
                    c.setRemarks(getCellValue(row.getCell(8)));
                } else {
                    c.setDate(getCellValue(row.getCell(0)));
                    c.setArea(getCellValue(row.getCell(1)));
                    c.setAddress(getCellValue(row.getCell(2)));
                    c.setCategory(getCellValue(row.getCell(3)));
                    c.setSize(parseDouble(getCellValue(row.getCell(4))));
                    c.setPhone(getCellValue(row.getCell(5)));
                    c.setExpiryDate(getCellValue(row.getCell(6)));
                    c.setSalesperson(getCellValue(row.getCell(7)));
                    c.setAccountName(getCellValue(row.getCell(8)));
                }

                customers.add(c);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage(), e);
        }

        return customers;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                double val = cell.getNumericCellValue();
                if (val == Math.floor(val) && !Double.isInfinite(val)) {
                    yield String.valueOf((long) val);
                }
                yield String.valueOf(val);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private Double parseDouble(String val) {
        if (val == null || val.isBlank()) return null;
        try {
            return Double.parseDouble(val.replaceAll("[^\\d.]", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}