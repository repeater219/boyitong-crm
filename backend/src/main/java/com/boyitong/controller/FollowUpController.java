package com.boyitong.controller;

import com.boyitong.common.Result;
import com.boyitong.entity.FollowUp;
import com.boyitong.repository.FollowUpRepository;
import com.boyitong.service.AuditLogService;
import com.boyitong.service.UserResolver;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow-ups")
public class FollowUpController {

    private final FollowUpRepository followUpRepository;
    private final AuditLogService auditLogService;
    private final UserResolver userResolver;

    public FollowUpController(FollowUpRepository followUpRepository, AuditLogService auditLogService, UserResolver userResolver) {
        this.followUpRepository = followUpRepository;
        this.auditLogService = auditLogService;
        this.userResolver = userResolver;
    }

    @GetMapping("/customer/{customerId}")
    public Result<List<FollowUp>> getByCustomer(@PathVariable Long customerId) {
        return Result.success(followUpRepository.findByCustomerIdOrderByCreatedAtDesc(customerId));
    }

    @PostMapping
    public Result<FollowUp> create(@RequestBody FollowUp followUp, Authentication auth) {
        followUp.setId(null);
        followUp.setSalesperson(auth.getName());
        followUp.setSalespersonUserId(userResolver.getUserId(auth.getName()));
        FollowUp saved = followUpRepository.save(followUp);
        auditLogService.log(auth.getName(), "FOLLOW_UP", "Customer", String.valueOf(followUp.getCustomerId()),
                "跟进客户 #" + followUp.getCustomerId() + ": " + followUp.getContent());
        return Result.success(saved);
    }
}