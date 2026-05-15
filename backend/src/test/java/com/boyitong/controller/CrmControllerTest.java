package com.boyitong.controller;

import com.boyitong.entity.*;
import com.boyitong.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CrmControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ProductRepository productRepo;
    @Autowired private OpportunityRepository oppRepo;
    @Autowired private ContractRepository contractRepo;
    @Autowired private PaymentRepository paymentRepo;
    @Autowired private ContactRepository contactRepo;
    @Autowired private AnnouncementRepository announcementRepo;

    @BeforeEach
    void setUp() {
        productRepo.deleteAll();
        oppRepo.deleteAll();
        contractRepo.deleteAll();
        paymentRepo.deleteAll();
        contactRepo.deleteAll();
        announcementRepo.deleteAll();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
    }

    // ========= PRODUCTS =========
    @Test
    void createProduct_ShouldReturnSavedProduct() throws Exception {
        mockMvc.perform(post("/api/crm/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"测试产品\",\"category\":\"测试类\",\"unit\":\"个\",\"price\":99.9}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("测试产品"))
                .andExpect(jsonPath("$.data.price").value(99.9));
    }

    @Test
    void getProducts_ShouldReturnList() throws Exception {
        Product p = new Product(); p.setName("产品A"); p.setCategory("类1"); p.setPrice(100.0);
        productRepo.save(p);
        mockMvc.perform(get("/api/crm/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void deleteProduct_ShouldRemoveRecord() throws Exception {
        Product p = new Product(); p.setName("待删除"); productRepo.save(p);
        mockMvc.perform(delete("/api/crm/products/" + p.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(get("/api/crm/products"))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    // ========= OPPORTUNITIES =========
    @Test
    void createOpportunity_ShouldSetWinRateBasedOnStage() throws Exception {
        mockMvc.perform(post("/api/crm/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"测试商机\",\"customerId\":1,\"amount\":50000,\"stage\":\"INTENT\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.winRate").value(10.0));
    }

    @Test
    void createOpportunity_NegotiationStage_ShouldSet70WinRate() throws Exception {
        mockMvc.perform(post("/api/crm/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"谈判中商机\",\"customerId\":1,\"amount\":100000,\"stage\":\"NEGOTIATION\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.winRate").value(70.0));
    }

    @Test
    void updateOpportunityStage_ShouldUpdateWinRate() throws Exception {
        Opportunity o = new Opportunity(); o.setName("原商机"); o.setStage("INTENT"); o.setWinRate(10.0); o.setSalesperson("admin");
        o = oppRepo.save(o);

        mockMvc.perform(put("/api/crm/opportunities/" + o.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stage\":\"QUOTATION\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stage").value("QUOTATION"))
                .andExpect(jsonPath("$.data.winRate").value(50.0));
    }

    @Test
    void getOpportunities_ShouldReturnOnlyUserOwned() throws Exception {
        Opportunity o1 = new Opportunity(); o1.setName("我的商机"); o1.setSalesperson("admin"); oppRepo.save(o1);
        Opportunity o2 = new Opportunity(); o2.setName("别人的商机"); o2.setSalesperson("other"); oppRepo.save(o2);
        mockMvc.perform(get("/api/crm/opportunities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("我的商机"));
    }

    // ========= CONTRACTS =========
    @Test
    void createContract_ShouldGenerateContractNo() throws Exception {
        mockMvc.perform(post("/api/crm/contracts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"测试合同\",\"amount\":20000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.contractNo").isString());
    }

    @Test
    void updateContractStatus_ShouldChangeStatus() throws Exception {
        Contract c = new Contract(); c.setName("合同"); c.setContractNo("CT-001"); c.setStatus("DRAFT");
        c = contractRepo.save(c);
        mockMvc.perform(put("/api/crm/contracts/" + c.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    // ========= PAYMENTS =========
    @Test
    void createAndGetPayments_ShouldWork() throws Exception {
        Contract c = new Contract(); c.setName("合同"); c.setContractNo("CT-002"); c = contractRepo.save(c);
        mockMvc.perform(post("/api/crm/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"contractId\":" + c.getId() + ",\"amount\":5000,\"status\":\"PENDING\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/crm/payments").param("contractId", String.valueOf(c.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    // ========= CONTACTS =========
    @Test
    void createAndGetContacts_ShouldWork() throws Exception {
        mockMvc.perform(post("/api/crm/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":1,\"name\":\"张三\",\"phone\":\"13800138000\",\"position\":\"经理\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/crm/contacts").param("customerId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("张三"));
    }

    // ========= ANNOUNCEMENTS =========
    @Test
    void createAnnouncement_ShouldSetAuthor() throws Exception {
        mockMvc.perform(post("/api/crm/announcements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"测试公告\",\"content\":\"公告内容\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.author").value("admin"))
                .andExpect(jsonPath("$.data.title").value("测试公告"));
    }

    @Test
    void getAnnouncements_ShouldReturnAll() throws Exception {
        Announcement a = new Announcement(); a.setTitle("公告"); a.setContent("内容"); a.setAuthor("admin");
        announcementRepo.save(a);
        mockMvc.perform(get("/api/crm/announcements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void deleteAnnouncement_ShouldRemove() throws Exception {
        Announcement a = new Announcement(); a.setTitle("待删"); a.setContent("内容"); a.setAuthor("admin");
        a = announcementRepo.save(a);
        mockMvc.perform(delete("/api/crm/announcements/" + a.getId()))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/crm/announcements"))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    // ========= EDGE CASES =========
    @Test
    void createProduct_WithEmptyName_ShouldStillSave() throws Exception {
        mockMvc.perform(post("/api/crm/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"price\":100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void createOpportunity_WithWonStage_ShouldSet100WinRate() throws Exception {
        mockMvc.perform(post("/api/crm/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"已赢单\",\"customerId\":1,\"stage\":\"WON\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.winRate").value(100.0));
    }

    @Test
    void updateContractStatus_ToTerminated_ShouldWork() throws Exception {
        Contract c = new Contract(); c.setName("合同"); c.setContractNo("CT-003"); c.setStatus("ACTIVE");
        c = contractRepo.save(c);
        mockMvc.perform(put("/api/crm/contracts/" + c.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"TERMINATED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("TERMINATED"));
    }
}