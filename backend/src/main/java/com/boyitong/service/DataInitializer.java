package com.boyitong.service;

import com.boyitong.entity.Customer;
import com.boyitong.repository.CustomerRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final CustomerRepository customerRepository;

    public DataInitializer(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (customerRepository.count() > 0) {
            log.info("Database already contains data, skipping initialization");
            return;
        }

        log.info("Initializing data from Excel files...");

        try {
            ClassPathResource liuzhou = new ClassPathResource("data/柳州.xlsx");
            List<Customer> liuzhouCustomers = parseExcel(liuzhou.getInputStream(), "柳州", true);
            customerRepository.saveAll(liuzhouCustomers);
            log.info("Imported {} customers from 柳州", liuzhouCustomers.size());
        } catch (Exception e) {
            log.warn("Failed to import 柳州.xlsx: {}", e.getMessage());
        }

        try {
            ClassPathResource ordos = new ClassPathResource("data/鄂尔多斯.xlsx");
            List<Customer> ordosCustomers = parseExcel(ordos.getInputStream(), "鄂尔多斯", false);
            customerRepository.saveAll(ordosCustomers);
            log.info("Imported {} customers from 鄂尔多斯", ordosCustomers.size());
        } catch (Exception e) {
            log.warn("Failed to import 鄂尔多斯.xlsx: {}", e.getMessage());
        }

        log.info("Data initialization complete. Total customers: {}", customerRepository.count());
    }

    private List<Customer> parseExcel(InputStream is, String city, boolean isLiuzhou) {
        List<Customer> customers = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
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
        } catch (Exception e) {
            throw new RuntimeException("Excel parse error: " + e.getMessage(), e);
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