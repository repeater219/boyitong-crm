package com.boyitong.service;

import com.boyitong.entity.Customer;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ImportServiceTest {

    private final ImportService importService = new ImportService();

    @Test
    void detectCity_FromFileName_ShouldDetectLiuzhou() {
        assertEquals("柳州", importService.detectCity("柳州.xlsx"));
        assertEquals("柳州", importService.detectCity("柳州数据2024.xlsx"));
    }

    @Test
    void detectCity_FromFileName_ShouldDetectOrdos() {
        assertEquals("鄂尔多斯", importService.detectCity("鄂尔多斯.xlsx"));
        assertEquals("鄂尔多斯", importService.detectCity("鄂尔多斯市数据.xlsx"));
    }

    @Test
    void detectCity_UnknownCity_ShouldReturnNameWithoutExtension() {
        assertEquals("北京", importService.detectCity("北京.xlsx"));
        assertEquals("测试城市", importService.detectCity("测试城市.xlsx"));
    }

    @Test
    void detectCity_NullFileName_ShouldReturnUnknown() {
        assertEquals("未知", importService.detectCity(null));
    }

    @Test
    void detectCity_NoExtension_ShouldReturnFullName() {
        assertEquals("Shanghai", importService.detectCity("Shanghai"));
    }

    @Test
    void parseExcel_LiuzhouFormat_ShouldParseCorrectly() throws Exception {
        byte[] excelBytes = createLiuzhouExcel();
        MultipartFile file = new MockMultipartFile("file", "柳州.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelBytes);

        List<Customer> customers = importService.parseExcel(file, "柳州");

        assertEquals(2, customers.size());

        Customer c1 = customers.get(0);
        assertEquals("柳州", c1.getCity());
        assertEquals("1.02", c1.getDate());
        assertEquals("柳北区", c1.getArea());
        assertEquals("急转 营业中精装餐饮店", c1.getAddress());
        assertEquals("商铺转让", c1.getCategory());
        assertEquals(400.0, c1.getSize());
        assertEquals("18177219998", c1.getPhone());
        assertEquals("", c1.getExpiryDate());
        assertEquals("王鲜", c1.getSalesperson());
        assertEquals("", c1.getRemarks());

        Customer c2 = customers.get(1);
        assertEquals("柳州", c2.getCity());
        assertEquals("1.05", c2.getDate());
        assertEquals("柳南", c2.getArea());
        assertEquals("张睿", c2.getSalesperson());
    }

    @Test
    void parseExcel_OrdosFormat_ShouldParseCorrectly() throws Exception {
        byte[] excelBytes = createOrdosExcel();
        MultipartFile file = new MockMultipartFile("file", "鄂尔多斯.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelBytes);

        List<Customer> customers = importService.parseExcel(file, "鄂尔多斯");

        assertEquals(2, customers.size());

        Customer c1 = customers.get(0);
        assertEquals("鄂尔多斯", c1.getCity());
        assertEquals("1.04", c1.getDate());
        assertEquals("鄂拖旗", c1.getArea());
        assertEquals("整体转让 棋盘井临街旺铺", c1.getAddress());
        assertEquals("店面转让", c1.getCategory());
        assertEquals(80.0, c1.getSize());
        assertEquals("15934004944", c1.getPhone());
        assertEquals("小宁0278", c1.getSalesperson());
        assertNull(c1.getRemarks());
        assertEquals("", c1.getAccountName() == null ? "" : c1.getAccountName());

        Customer c2 = customers.get(1);
        assertEquals("鄂尔多斯", c2.getCity());
        assertEquals("东胜", c2.getArea());
    }

    @Test
    void parseExcel_EmptyFile_ShouldReturnEmptyList() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        workbook.createSheet("Sheet1");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();

        MultipartFile file = new MockMultipartFile("file", "empty.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bos.toByteArray());

        List<Customer> customers = importService.parseExcel(file, "柳州");

        assertTrue(customers.isEmpty());
    }

    @Test
    void parseExcel_NumericValues_ShouldHandleCorrectly() throws Exception {
        byte[] excelBytes = createExcelWithNumericVariants();
        MultipartFile file = new MockMultipartFile("file", "numeric.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelBytes);

        List<Customer> customers = importService.parseExcel(file, "柳州");

        assertEquals(1, customers.size());
        Customer c = customers.get(0);
        // 2000 should be parsed as a number
        assertNotNull(c.getSize());
        assertEquals(2000.0, c.getSize());
        // Phone should be preserved as string even if numeric
        assertEquals("18177219998", c.getPhone());
    }

    private byte[] createLiuzhouExcel() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        // Header row
        Row header = sheet.createRow(0);
        String[] headers = {"日期", "区域", "地址", "板块", "面积", "电话", "到期", "销售", "备注"};
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        // Data row 1
        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("1.02");
        row1.createCell(1).setCellValue("柳北区");
        row1.createCell(2).setCellValue("急转 营业中精装餐饮店");
        row1.createCell(3).setCellValue("商铺转让");
        row1.createCell(4).setCellValue(400.0);
        row1.createCell(5).setCellValue("18177219998");
        row1.createCell(6).setCellValue(""); // expiry date empty
        row1.createCell(7).setCellValue("王鲜");
        row1.createCell(8).setCellValue(""); // remarks empty

        // Data row 2
        Row row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("1.05");
        row2.createCell(1).setCellValue("柳南");
        row2.createCell(2).setCellValue("豪华装修大型娱乐KTV");
        row2.createCell(3).setCellValue("商铺转让");
        row2.createCell(4).setCellValue(200.0);
        row2.createCell(5).setCellValue("13471210725");
        row2.createCell(6).setCellValue("");
        row2.createCell(7).setCellValue("张睿");
        row2.createCell(8).setCellValue("");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        return bos.toByteArray();
    }

    private byte[] createOrdosExcel() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        Row header = sheet.createRow(0);
        String[] headers = {"日期", "区域", "地址", "行业/板块", "面积", "电话", "到期日期", "销售名", "账号名"};
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("1.04");
        row1.createCell(1).setCellValue("鄂拖旗");
        row1.createCell(2).setCellValue("整体转让 棋盘井临街旺铺");
        row1.createCell(3).setCellValue("店面转让");
        row1.createCell(4).setCellValue(80.0);
        row1.createCell(5).setCellValue("15934004944");
        row1.createCell(6).setCellValue("");
        row1.createCell(7).setCellValue("小宁0278");
        row1.createCell(8).setCellValue("");

        Row row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("1.04");
        row2.createCell(1).setCellValue("东胜");
        row2.createCell(2).setCellValue("整体转让 东胜区政务中心附近");
        row2.createCell(3).setCellValue("店面转让");
        row2.createCell(4).setCellValue(130.0);
        row2.createCell(5).setCellValue("15894913631");
        row2.createCell(6).setCellValue("");
        row2.createCell(7).setCellValue("小宁0278");
        row2.createCell(8).setCellValue("");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        return bos.toByteArray();
    }

    private byte[] createExcelWithNumericVariants() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        Row header = sheet.createRow(0);
        String[] headers = {"日期", "区域", "地址", "板块", "面积", "电话", "到期", "销售", "备注"};
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("1.02");
        row1.createCell(1).setCellValue("柳北区");
        row1.createCell(2).setCellValue("测试");
        row1.createCell(3).setCellValue("商铺转让");
        row1.createCell(4).setCellValue(2000.0); // large number
        row1.createCell(5).setCellValue(18177219998L); // phone as numeric
        row1.createCell(6).setCellValue("");
        row1.createCell(7).setCellValue("张三");
        row1.createCell(8).setCellValue("");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        return bos.toByteArray();
    }
}