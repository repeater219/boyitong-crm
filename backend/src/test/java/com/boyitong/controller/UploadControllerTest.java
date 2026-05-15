package com.boyitong.controller;

import com.boyitong.entity.Customer;
import com.boyitong.entity.UploadRecord;
import com.boyitong.entity.User;
import com.boyitong.repository.CustomerRepository;
import com.boyitong.repository.UploadRecordRepository;
import com.boyitong.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UploadRecordRepository uploadRecordRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        uploadRecordRepository.deleteAll();
        customerRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.save(new User("admin", passwordEncoder.encode("admin123"), "ADMIN", "管理员"));
        userRepository.save(new User("zhangrui", passwordEncoder.encode("123456"), "USER", "张睿"));
    }

    private String login(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        return body.split("\"token\":\"")[1].split("\"")[0];
    }

    @Test
    void submitUpload_AsUser_ShouldCreatePendingRecord() throws Exception {
        String token = login("zhangrui", "123456");
        MockMultipartFile file = new MockMultipartFile("file", "柳州.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                createMinimalExcel());

        mockMvc.perform(multipart("/api/uploads/submit")
                        .file(file)
                        .param("city", "柳州")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void approveUpload_AsAdmin_ShouldImportData() throws Exception {
        // First create a pending upload
        String userToken = login("zhangrui", "123456");
        MockMultipartFile file = new MockMultipartFile("file", "柳州.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                createMinimalExcel());

        mockMvc.perform(multipart("/api/uploads/submit")
                .file(file).param("city", "柳州")
                .header("Authorization", "Bearer " + userToken));

        // Approve as admin
        adminToken = login("admin", "admin123");
        UploadRecord record = uploadRecordRepository.findAll().get(0);

        mockMvc.perform(post("/api/uploads/{id}/approve", record.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\":\"数据无误\"}")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void rejectUpload_AsAdmin_ShouldUpdateStatus() throws Exception {
        String userToken = login("zhangrui", "123456");
        MockMultipartFile file = new MockMultipartFile("file", "柳州.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                createMinimalExcel());

        mockMvc.perform(multipart("/api/uploads/submit")
                .file(file).param("city", "柳州")
                .header("Authorization", "Bearer " + userToken));

        adminToken = login("admin", "admin123");
        UploadRecord record = uploadRecordRepository.findAll().get(0);

        mockMvc.perform(post("/api/uploads/{id}/reject", record.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\":\"数据格式有误\"}")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void myUploads_ShouldReturnUserRecords() throws Exception {
        String token = login("zhangrui", "123456");
        mockMvc.perform(get("/api/uploads/my")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private byte[] createMinimalExcel() {
        try {
            org.apache.poi.xssf.usermodel.XSSFWorkbook workbook =
                    new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Sheet1");
            sheet.createRow(0).createCell(0).setCellValue("日期");
            sheet.createRow(0).createCell(1).setCellValue("区域");
            sheet.createRow(0).createCell(2).setCellValue("地址");
            sheet.createRow(0).createCell(3).setCellValue("板块");
            sheet.createRow(0).createCell(4).setCellValue("面积");
            sheet.createRow(0).createCell(5).setCellValue("电话");
            sheet.createRow(0).createCell(6).setCellValue("到期");
            sheet.createRow(0).createCell(7).setCellValue("销售");
            sheet.createRow(0).createCell(8).setCellValue("备注");
            sheet.createRow(1).createCell(0).setCellValue("1.01");
            sheet.createRow(1).createCell(1).setCellValue("测试区");
            sheet.createRow(1).createCell(2).setCellValue("测试地址");
            sheet.createRow(1).createCell(3).setCellValue("商铺转让");
            sheet.createRow(1).createCell(4).setCellValue(100);
            sheet.createRow(1).createCell(5).setCellValue("13800138000");
            sheet.createRow(1).createCell(6).setCellValue("");
            sheet.createRow(1).createCell(7).setCellValue("测试销售");
            sheet.createRow(1).createCell(8).setCellValue("");
            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}