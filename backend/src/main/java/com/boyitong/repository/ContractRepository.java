package com.boyitong.repository;
import com.boyitong.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findBySalespersonOrderByCreatedAtDesc(String salesperson);
    List<Contract> findByCustomerId(Long customerId);
    Contract findByContractNo(String contractNo);
}