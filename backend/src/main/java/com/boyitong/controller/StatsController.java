package com.boyitong.controller;

import com.boyitong.common.Result;
import com.boyitong.dto.StatsVO;
import com.boyitong.entity.Customer;
import com.boyitong.repository.CustomerRepository;
import com.boyitong.security.SecurityUtils;
import com.boyitong.service.StatsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;
    private final CustomerRepository customerRepository;

    public StatsController(StatsService statsService, CustomerRepository customerRepository) {
        this.statsService = statsService;
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public Result<StatsVO> getStats() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            if (!SecurityUtils.canViewAllData(auth)) {
                return Result.success(statsService.getStatsForUser(auth.getName()));
            }
        }
        return Result.success(statsService.getStats());
    }

    @GetMapping("/trends")
    public Result<Map<String, Object>> getTrends() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Customer> all;
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            if (!SecurityUtils.canViewAllData(auth)) {
                all = customerRepository.findByAssignedTo(auth.getName());
            } else {
                all = customerRepository.findAll();
            }
        } else {
            all = customerRepository.findAll();
        }

        Map<String, Long> statusCount = all.stream()
                .filter(c -> c.getStatus() != null)
                .collect(Collectors.groupingBy(Customer::getStatus, Collectors.counting()));

        // Simple sales funnel
        long total = all.size();
        long following = statusCount.getOrDefault("FOLLOWING", 0L);
        long negotiating = statusCount.getOrDefault("NEGOTIATING", 0L);
        long won = statusCount.getOrDefault("WON", 0L);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("statusDistribution", statusCount.entrySet().stream()
                .map(e -> Map.of("name", e.getKey(), "value", e.getValue()))
                .toList());

        Map<String, Object> funnel = new LinkedHashMap<>();
        funnel.put("total", total);
        funnel.put("following", following);
        funnel.put("negotiating", negotiating);
        funnel.put("won", won);
        funnel.put("conversionRate", total > 0 ? Math.round((double) won / total * 10000) / 100.0 : 0);
        result.put("funnel", funnel);

        return Result.success(result);
    }
}