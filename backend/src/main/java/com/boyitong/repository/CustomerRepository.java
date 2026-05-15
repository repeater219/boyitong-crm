package com.boyitong.repository;

import com.boyitong.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    List<Customer> findByAssignedTo(String assignedTo);
    List<Customer> findByAssignedToIsNullOrAssignedTo(String assignedTo);
    List<Customer> findByPhoneContaining(String phone);
    List<Customer> findByAddressContaining(String address);
}