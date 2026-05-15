package com.boyitong.controller;

import com.boyitong.entity.Customer;
import com.boyitong.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
        // Set up security context for PUT/DELETE tests
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
        );

        // Insert 3 test customers
        Customer c1 = new Customer();
        c1.setCity("柳州");
        c1.setArea("柳北区");
        c1.setAddress("测试地址A");
        c1.setCategory("商铺转让");
        c1.setSize(100.0);
        c1.setPhone("13800138000");
        c1.setSalesperson("张三");
        customerRepository.save(c1);

        Customer c2 = new Customer();
        c2.setCity("柳州");
        c2.setArea("柳南区");
        c2.setAddress("测试地址B 餐饮旺铺");
        c2.setCategory("餐饮转让");
        c2.setSize(200.0);
        c2.setPhone("13900139000");
        c2.setSalesperson("张三");
        customerRepository.save(c2);

        Customer c3 = new Customer();
        c3.setCity("鄂尔多斯");
        c3.setArea("东胜区");
        c3.setAddress("测试地址C");
        c3.setCategory("店铺转让");
        c3.setSize(50.0);
        c3.setPhone("15000150000");
        c3.setSalesperson("李四");
        customerRepository.save(c3);
    }

    @Test
    void listCustomers_ShouldReturnAll() throws Exception {
        mockMvc.perform(get("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(jsonPath("$.data.content.length()").value(3));
    }

    @Test
    void listCustomers_ShouldFilterByCity() throws Exception {
        mockMvc.perform(get("/api/customers")
                        .param("city", "柳州")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    @Test
    void listCustomers_ShouldFilterByArea() throws Exception {
        mockMvc.perform(get("/api/customers")
                        .param("area", "柳北")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void listCustomers_ShouldFilterByCategory() throws Exception {
        mockMvc.perform(get("/api/customers")
                        .param("category", "餐饮")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void listCustomers_ShouldFilterBySalesperson() throws Exception {
        mockMvc.perform(get("/api/customers")
                        .param("salesperson", "李四")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void listCustomers_ShouldFilterBySizeRange() throws Exception {
        mockMvc.perform(get("/api/customers")
                        .param("minSize", "80")
                        .param("maxSize", "150")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void listCustomers_ShouldSearchByKeywordInAddress() throws Exception {
        mockMvc.perform(get("/api/customers")
                        .param("keyword", "餐饮")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void listCustomers_WithCombinedFilters() throws Exception {
        mockMvc.perform(get("/api/customers")
                        .param("city", "柳州")
                        .param("salesperson", "张三")
                        .param("minSize", "50")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    @Test
    void listCustomers_WithNoMatch_ShouldReturnEmpty() throws Exception {
        mockMvc.perform(get("/api/customers")
                        .param("city", "不存在")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(0))
                .andExpect(jsonPath("$.data.content.length()").value(0));
    }

    @Test
    void listCustomers_ShouldRespectPagination() throws Exception {
        mockMvc.perform(get("/api/customers")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(2));
    }

    @Test
    void listCustomers_ShouldSortBySizeDesc() throws Exception {
        mockMvc.perform(get("/api/customers")
                        .param("sortBy", "size")
                        .param("sortDir", "desc")
                        .param("size", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].size").value(200.0));
    }

    @Test
    void getCustomerById_ShouldReturnCustomer() throws Exception {
        Customer saved = customerRepository.findAll().get(0);

        mockMvc.perform(get("/api/customers/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(saved.getId()));
    }

    @Test
    void getCustomerById_WhenNotExists_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/customers/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void getStats_ShouldReturnCorrectCounts() throws Exception {
        mockMvc.perform(get("/api/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalCustomers").value(3))
                .andExpect(jsonPath("$.data.cityCount").value(2));
    }

    @Test
    void getStats_ShouldHaveCategoryDistribution() throws Exception {
        mockMvc.perform(get("/api/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.categoryDistribution").isArray());
    }

    @Test
    void getStats_ShouldHaveSalespersonRanking() throws Exception {
        mockMvc.perform(get("/api/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.salespersonRanking").isArray());
    }

    @Test
    void updateCustomer_ShouldModifyFields() throws Exception {
        Customer saved = customerRepository.findAll().get(0);

        mockMvc.perform(put("/api/customers/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"area\":\"新区域\",\"address\":\"新地址\",\"salesperson\":\"新销售\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.area").value("新区域"))
                .andExpect(jsonPath("$.data.address").value("新地址"))
                .andExpect(jsonPath("$.data.salesperson").value("新销售"));
    }

    @Test
    void deleteCustomer_ShouldRemoveRecord() throws Exception {
        Customer saved = customerRepository.findAll().get(0);
        long id = saved.getId();

        mockMvc.perform(delete("/api/customers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/customers/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCustomer_WhenNotExists_ShouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/customers/{id}", 9999L))
                .andExpect(status().isNotFound());
    }
}