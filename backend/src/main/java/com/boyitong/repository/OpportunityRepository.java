package com.boyitong.repository;
import com.boyitong.entity.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {
    List<Opportunity> findBySalespersonOrderByCreatedAtDesc(String salesperson);
    List<Opportunity> findByCustomerId(Long customerId);
    List<Opportunity> findByStage(String stage);
}