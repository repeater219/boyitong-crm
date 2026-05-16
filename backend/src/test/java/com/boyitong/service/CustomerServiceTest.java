package com.boyitong.service;

import com.boyitong.entity.Customer;
import com.boyitong.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    private CustomerRepository customerRepository;
    private ImportService importService;
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        // Use a real ImportService instance; mock parseExcel behavior by overriding if needed
        importService = new ImportService();
        customerService = new CustomerServiceImpl(customerRepository, importService);
    }

    @Test
    void findById_WhenExists_ShouldReturnCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCity("柳州");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Customer result = customerService.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("柳州", result.getCity());
    }

    @Test
    void findById_WhenNotExists_ShouldThrowException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> customerService.findById(99L));
    }

    @Test
    void findAll_ShouldReturnAllCustomers() {
        when(customerRepository.findAll()).thenReturn(List.of(new Customer(), new Customer()));

        List<Customer> result = customerService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void searchCustomers_ShouldReturnPagedResults() {
        Customer customer = new Customer();
        customer.setCity("柳州");
        Page<Customer> page = new PageImpl<>(List.of(customer));

        when(customerRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        Page<Customer> result = customerService.searchCustomers(
                "柳州", null, null, null, null, null, null, null,
                0, 20, "id", "asc");

        assertEquals(1, result.getContent().size());
        assertEquals("柳州", result.getContent().get(0).getCity());
    }
}