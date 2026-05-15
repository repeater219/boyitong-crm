package com.boyitong.service;

import com.boyitong.dto.StatsVO;
import com.boyitong.entity.Customer;
import com.boyitong.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    private StatsServiceImpl statsService;

    @BeforeEach
    void setUp() {
        statsService = new StatsServiceImpl(customerRepository);
    }

    @Test
    void getStats_WithMultipleCities_ShouldReturnCorrectDistribution() {
        Customer c1 = createCustomer("柳州", "商铺转让", "张三", 100.0);
        Customer c2 = createCustomer("柳州", "餐饮转让", "张三", 200.0);
        Customer c3 = createCustomer("鄂尔多斯", "商铺转让", "李四", 50.0);

        when(customerRepository.findAll()).thenReturn(List.of(c1, c2, c3));

        StatsVO stats = statsService.getStats();

        assertEquals(3, stats.getTotalCustomers());
        assertEquals(2, stats.getCityCount());
        assertEquals(2, stats.getCategoryCount());
        assertEquals(2, stats.getSalespersonCount());

        // Verify city distribution
        assertTrue(stats.getCityDistribution().stream()
                .anyMatch(m -> m.get("name").equals("柳州") && m.get("value").equals(2L)));
        assertTrue(stats.getCityDistribution().stream()
                .anyMatch(m -> m.get("name").equals("鄂尔多斯") && m.get("value").equals(1L)));
    }

    @Test
    void getStats_WithNullFields_ShouldHandleGracefully() {
        Customer c = new Customer();
        c.setCity(null);
        c.setCategory(null);
        c.setSalesperson(null);
        c.setSize(null);

        when(customerRepository.findAll()).thenReturn(List.of(c));

        StatsVO stats = statsService.getStats();

        assertEquals(1, stats.getTotalCustomers());
        assertEquals(0, stats.getCityDistribution().size());
        assertEquals(0, stats.getCategoryDistribution().size());
        assertEquals(0, stats.getSalespersonRanking().size());
    }

    @Test
    void getStats_WithEmptyData_ShouldReturnZeros() {
        when(customerRepository.findAll()).thenReturn(List.of());

        StatsVO stats = statsService.getStats();

        assertEquals(0, stats.getTotalCustomers());
        assertEquals(0, stats.getCityCount());
        assertEquals(0, stats.getCategoryCount());
        assertEquals(0, stats.getSalespersonCount());
    }

    @Test
    void getStats_SalespersonRanking_ShouldBeSortedDescending() {
        Customer c1 = createCustomer("柳州", "A", "张三", 100.0);
        Customer c2 = createCustomer("柳州", "A", "张三", 100.0);
        Customer c3 = createCustomer("柳州", "A", "张三", 100.0);
        Customer c4 = createCustomer("柳州", "A", "李四", 100.0);
        Customer c5 = createCustomer("柳州", "A", "李四", 100.0);
        Customer c6 = createCustomer("柳州", "A", "王五", 100.0);

        when(customerRepository.findAll()).thenReturn(List.of(c1, c2, c3, c4, c5, c6));

        StatsVO stats = statsService.getStats();

        List<java.util.Map<String, Object>> ranking = stats.getSalespersonRanking();
        assertEquals("张三", ranking.get(0).get("name"));
        assertEquals(3L, ranking.get(0).get("value"));
        assertEquals("李四", ranking.get(1).get("name"));
        assertEquals(2L, ranking.get(1).get("value"));
        assertEquals("王五", ranking.get(2).get("name"));
        assertEquals(1L, ranking.get(2).get("value"));
    }

    @Test
    void getStats_AreaDistribution_ShouldBucketCorrectly() {
        Customer c1 = createCustomer("柳州", "A", "张三", 30.0);   // ≤50
        Customer c2 = createCustomer("柳州", "A", "张三", 75.0);   // 51-100
        Customer c3 = createCustomer("柳州", "A", "张三", 150.0);  // 101-200
        Customer c4 = createCustomer("柳州", "A", "张三", 300.0);  // 201-500
        Customer c5 = createCustomer("柳州", "A", "张三", 750.0);  // 501-1000
        Customer c6 = createCustomer("柳州", "A", "张三", 2000.0); // >1000
        Customer c7 = createCustomer("柳州", "A", "张三", null);   // null, should be skipped

        when(customerRepository.findAll()).thenReturn(List.of(c1, c2, c3, c4, c5, c6, c7));

        StatsVO stats = statsService.getStats();

        List<java.util.Map<String, Object>> areaDist = stats.getAreaDistribution();
        assertEquals(1L, areaDist.get(0).get("value")); // ≤50
        assertEquals(1L, areaDist.get(1).get("value")); // 51-100
        assertEquals(1L, areaDist.get(2).get("value")); // 101-200
        assertEquals(1L, areaDist.get(3).get("value")); // 201-500
        assertEquals(1L, areaDist.get(4).get("value")); // 501-1000
        assertEquals(1L, areaDist.get(5).get("value")); // >1000
    }

    private Customer createCustomer(String city, String category, String salesperson, Double size) {
        Customer c = new Customer();
        c.setCity(city);
        c.setCategory(category);
        c.setSalesperson(salesperson);
        c.setSize(size);
        return c;
    }
}