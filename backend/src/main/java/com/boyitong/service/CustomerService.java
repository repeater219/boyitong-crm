package com.boyitong.service;

import com.boyitong.entity.Customer;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerService {
    List<Customer> findAll();
    Customer findById(Long id);
    List<Customer> importExcel(MultipartFile file, String city);
}