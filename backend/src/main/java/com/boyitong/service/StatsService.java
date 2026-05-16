package com.boyitong.service;

import com.boyitong.dto.StatsVO;

public interface StatsService {
    StatsVO getStats();
    StatsVO getStatsForUser(String username);
}