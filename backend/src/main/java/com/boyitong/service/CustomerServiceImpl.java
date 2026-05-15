package com.boyitong.service;

import com.boyitong.entity.Customer;
import com.boyitong.entity.CustomerSpecification;
import com.boyitong.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ImportService importService;

    public CustomerServiceImpl(CustomerRepository customerRepository, ImportService importService) {
        this.customerRepository = customerRepository;
        this.importService = importService;
    }

    public CustomerRepository getCustomerRepository() {
        return customerRepository;
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id));
    }

    @Override
    public List<Customer> importExcel(MultipartFile file, String city) {
        List<Customer> customers = importService.parseExcel(file, city);
        return customerRepository.saveAll(customers);
    }

    public Page<Customer> searchCustomers(
            String city, String area, String category,
            Double minSize, Double maxSize,
            String salesperson, String keyword,
            int page, int size, String sortBy, String sortDir) {

        Specification<Customer> spec = CustomerSpecification.withFilters(
                city, area, category, minSize, maxSize, salesperson, keyword);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return customerRepository.findAll(spec, pageable);
    }

    public List<Object[]> getCityDistribution() {
        String query = "SELECT c.city, COUNT(c) FROM Customer c GROUP BY c.city";
        return customerRepository.findAll()
                .stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Customer::getCity, java.util.stream.Collectors.counting()))
                .entrySet()
                .stream()
                .map(e -> new Object[]{e.getKey(), e.getValue()})
                .toList();
    }
}