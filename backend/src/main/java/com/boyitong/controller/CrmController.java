package com.boyitong.controller;

import com.boyitong.common.Result;
import com.boyitong.entity.*;
import com.boyitong.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/crm")
public class CrmController {

    private final ProductRepository productRepo;
    private final OpportunityRepository oppRepo;
    private final ContractRepository contractRepo;
    private final PaymentRepository paymentRepo;
    private final ContactRepository contactRepo;
    private final AnnouncementRepository announcementRepo;

    public CrmController(ProductRepository productRepo, OpportunityRepository oppRepo,
                         ContractRepository contractRepo, PaymentRepository paymentRepo,
                         ContactRepository contactRepo, AnnouncementRepository announcementRepo) {
        this.productRepo = productRepo;
        this.oppRepo = oppRepo;
        this.contractRepo = contractRepo;
        this.paymentRepo = paymentRepo;
        this.contactRepo = contactRepo;
        this.announcementRepo = announcementRepo;
    }

    // ========= PRODUCTS =========
    @GetMapping("/products") public Result<List<Product>> getProducts() { return Result.success(productRepo.findAll()); }
    @PostMapping("/products") public Result<Product> createProduct(@RequestBody Product p) { return Result.success(productRepo.save(p)); }
    @DeleteMapping("/products/{id}") public Result<Void> deleteProduct(@PathVariable Long id) { productRepo.deleteById(id); return Result.success(); }

    // ========= OPPORTUNITIES =========
    @GetMapping("/opportunities") public Result<List<Opportunity>> getOpps(Authentication auth) { return Result.success(oppRepo.findBySalespersonOrderByCreatedAtDesc(auth.getName())); }
    @PostMapping("/opportunities") public Result<Opportunity> createOpp(@RequestBody Opportunity o, Authentication auth) { o.setSalesperson(auth.getName()); o.setWinRate(calcWinRate(o.getStage())); return Result.success(oppRepo.save(o)); }
    @PutMapping("/opportunities/{id}") public Result<Opportunity> updateOpp(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Opportunity o = oppRepo.findById(id).orElseThrow();
        if (body.containsKey("stage")) { o.setStage((String)body.get("stage")); o.setWinRate(calcWinRate((String)body.get("stage"))); }
        if (body.containsKey("amount")) o.setAmount(Double.valueOf(body.get("amount").toString()));
        return Result.success(oppRepo.save(o));
    }
    private double calcWinRate(String stage) { return switch(stage) { case "INTENT" -> 10; case "PROPOSAL" -> 30; case "QUOTATION" -> 50; case "NEGOTIATION" -> 70; case "WON" -> 100; default -> 0; }; }

    // ========= CONTRACTS =========
    @GetMapping("/contracts") public Result<List<Contract>> getContracts() { return Result.success(contractRepo.findAll()); }
    @PostMapping("/contracts") public Result<Contract> createContract(@RequestBody Contract c) {
        c.setContractNo("CT-" + System.currentTimeMillis() % 1000000); return Result.success(contractRepo.save(c));
    }
    @PutMapping("/contracts/{id}/status") public Result<Contract> updateContractStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Contract c = contractRepo.findById(id).orElseThrow(); c.setStatus(body.get("status")); return Result.success(contractRepo.save(c));
    }

    // ========= PAYMENTS =========
    @GetMapping("/payments") public Result<List<Payment>> getPayments(@RequestParam Long contractId) { return Result.success(paymentRepo.findByContractId(contractId)); }
    @PostMapping("/payments") public Result<Payment> createPayment(@RequestBody Payment p) { return Result.success(paymentRepo.save(p)); }

    // ========= CONTACTS =========
    @GetMapping("/contacts") public Result<List<Contact>> getContacts(@RequestParam Long customerId) { return Result.success(contactRepo.findByCustomerId(customerId)); }
    @PostMapping("/contacts") public Result<Contact> createContact(@RequestBody Contact c) { return Result.success(contactRepo.save(c)); }

    // ========= ANNOUNCEMENTS =========
    @GetMapping("/announcements") public Result<List<Announcement>> getAnnouncements() { return Result.success(announcementRepo.findAllByOrderByPinnedDescCreatedAtDesc()); }
    @PostMapping("/announcements") public Result<Announcement> createAnnouncement(@RequestBody Announcement a, Authentication auth) { a.setAuthor(auth.getName()); return Result.success(announcementRepo.save(a)); }
    @DeleteMapping("/announcements/{id}") public Result<Void> deleteAnnouncement(@PathVariable Long id) { announcementRepo.deleteById(id); return Result.success(); }
}