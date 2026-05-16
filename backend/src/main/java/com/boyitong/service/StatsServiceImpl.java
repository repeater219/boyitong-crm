package com.boyitong.service;

import com.boyitong.dto.StatsVO;
import com.boyitong.entity.Customer;
import com.boyitong.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsServiceImpl implements StatsService {

    private final CustomerRepository customerRepository;

    public StatsServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public StatsVO getStats() {
        return buildStats(customerRepository.findAll());
    }

    @Override
    public StatsVO getStatsForUser(String username) {
        return buildStats(customerRepository.findByAssignedTo(username));
    }

    private StatsVO buildStats(List<Customer> all) {

        StatsVO stats = new StatsVO();
        stats.setTotalCustomers(all.size());

        // City distribution
        Map<String, Long> cityMap = all.stream()
                .filter(c -> c.getCity() != null && !c.getCity().isBlank())
                .collect(Collectors.groupingBy(Customer::getCity, Collectors.counting()));
        stats.setCityCount(cityMap.size());
        stats.setCityDistribution(toListMap(cityMap));

        // Category distribution
        Map<String, Long> categoryMap = all.stream()
                .filter(c -> c.getCategory() != null && !c.getCategory().isBlank())
                .collect(Collectors.groupingBy(Customer::getCategory, Collectors.counting()));
        stats.setCategoryCount(categoryMap.size());
        stats.setCategoryDistribution(toListMap(categoryMap));

        // Salesperson ranking
        Map<String, Long> salesMap = all.stream()
                .filter(c -> c.getSalesperson() != null && !c.getSalesperson().isBlank())
                .collect(Collectors.groupingBy(Customer::getSalesperson, Collectors.counting()));
        stats.setSalespersonCount(salesMap.size());
        stats.setSalespersonRanking(toListMapSorted(salesMap));

        // Area distribution (buckets)
        stats.setAreaDistribution(getAreaDistribution(all));

        return stats;
    }

    private List<Map<String, Object>> toListMap(Map<String, Long> map) {
        return map.entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("name", e.getKey());
                    m.put("value", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> toListMapSorted(Map<String, Long> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("name", e.getKey());
                    m.put("value", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getAreaDistribution(List<Customer> all) {
        long lt50 = 0, lt100 = 0, lt200 = 0, lt500 = 0, lt1000 = 0, gt1000 = 0;

        for (Customer c : all) {
            if (c.getSize() == null) continue;
            double s = c.getSize();
            if (s <= 50) lt50++;
            else if (s <= 100) lt100++;
            else if (s <= 200) lt200++;
            else if (s <= 500) lt500++;
            else if (s <= 1000) lt1000++;
            else gt1000++;
        }

        List<Map<String, Object>> result = new ArrayList<>();
        result.add(Map.of("name", "≤50m²", "value", lt50));
        result.add(Map.of("name", "51-100m²", "value", lt100));
        result.add(Map.of("name", "101-200m²", "value", lt200));
        result.add(Map.of("name", "201-500m²", "value", lt500));
        result.add(Map.of("name", "501-1000m²", "value", lt1000));
        result.add(Map.of("name", ">1000m²", "value", gt1000));
        return result;
    }
}