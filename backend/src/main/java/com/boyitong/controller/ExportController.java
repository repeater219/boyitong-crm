package com.boyitong.controller;

import com.boyitong.entity.Customer;
import com.boyitong.entity.CustomerSpecification;
import com.boyitong.repository.CustomerRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final CustomerRepository customerRepository;

    public ExportController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/customers")
    public void exportCustomers(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minSize,
            @RequestParam(required = false) Double maxSize,
            @RequestParam(required = false) String salesperson,
            @RequestParam(required = false) String keyword,
            HttpServletResponse response) throws Exception {

        Specification<Customer> spec = com.boyitong.entity.CustomerSpecification.withFilters(
                city, area, category, minSize, maxSize, salesperson, keyword, null);

        // USER 角色只能导出分配给自己的客户
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(g -> g.getAuthority().equals("ROLE_ADMIN"));
            if (!isAdmin) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("assignedTo"), auth.getName()));
            }
        }

        List<Customer> customers = customerRepository.findAll(spec);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=customers.xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("客户数据");

            // Header
            String[] headers = {"ID", "城市", "日期", "区域", "地址", "行业/板块", "面积", "电话", "到期日期", "销售员", "备注"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Data
            int rowNum = 1;
            for (Customer c : customers) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(c.getId() != null ? c.getId() : 0);
                row.createCell(1).setCellValue(c.getCity() != null ? c.getCity() : "");
                row.createCell(2).setCellValue(c.getDate() != null ? c.getDate() : "");
                row.createCell(3).setCellValue(c.getArea() != null ? c.getArea() : "");
                row.createCell(4).setCellValue(c.getAddress() != null ? c.getAddress() : "");
                row.createCell(5).setCellValue(c.getCategory() != null ? c.getCategory() : "");
                row.createCell(6).setCellValue(c.getSize() != null ? c.getSize() : 0);
                row.createCell(7).setCellValue(c.getPhone() != null ? c.getPhone() : "");
                row.createCell(8).setCellValue(c.getExpiryDate() != null ? c.getExpiryDate() : "");
                row.createCell(9).setCellValue(c.getSalesperson() != null ? c.getSalesperson() : "");
                row.createCell(10).setCellValue(c.getRemarks() != null ? c.getRemarks() : "");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            OutputStream os = response.getOutputStream();
            workbook.write(os);
            os.flush();
        }
    }
}