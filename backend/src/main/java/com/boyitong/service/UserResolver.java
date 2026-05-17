package com.boyitong.service;

import com.boyitong.entity.User;
import com.boyitong.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 内存缓存工具类，提供 username ↔ id ↔ displayName 的快速查找。
 * 启动时加载所有用户，运行时保持同步。
 */
@Component
public class UserResolver {

    private static final Logger log = LoggerFactory.getLogger(UserResolver.class);
    private final UserRepository userRepository;

    /** username → User */
    private volatile Map<String, User> usernameMap = Collections.emptyMap();
    /** id → User */
    private volatile Map<Long, User> idMap = Collections.emptyMap();

    public UserResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void refresh() {
        Map<String, User> uMap = new HashMap<>();
        Map<Long, User> iMap = new HashMap<>();
        for (User u : userRepository.findAll()) {
            uMap.put(u.getUsername(), u);
            iMap.put(u.getId(), u);
        }
        this.usernameMap = uMap;
        this.idMap = iMap;
        log.info("UserResolver loaded {} users", uMap.size());
    }

    /** 根据 username 获取 userId */
    public Long getUserId(String username) {
        User u = usernameMap.get(username);
        return u != null ? u.getId() : null;
    }

    /** 根据 userId 获取 username */
    public String getUsername(Long userId) {
        User u = idMap.get(userId);
        return u != null ? u.getUsername() : null;
    }

    /** 根据 userId 获取 displayName，fallback 到 usernameFallback */
    public String resolveDisplayName(Long userId, String usernameFallback) {
        if (userId != null) {
            User u = idMap.get(userId);
            if (u != null && u.getDisplayName() != null) return u.getDisplayName();
        }
        if (usernameFallback != null) {
            User u = usernameMap.get(usernameFallback);
            if (u != null && u.getDisplayName() != null) return u.getDisplayName();
        }
        return usernameFallback;
    }

    /** 根据 username 获取 displayName */
    public String getDisplayName(String username) {
        User u = usernameMap.get(username);
        return u != null ? u.getDisplayName() : username;
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(usernameMap.get(username));
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(idMap.get(id));
    }
}