package com.boyitong.repository;

import com.boyitong.entity.FollowUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowUpRepository extends JpaRepository<FollowUp, Long> {
    List<FollowUp> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<FollowUp> findBySalespersonOrderByCreatedAtDesc(String salesperson);
    long countByCustomerId(Long customerId);
}