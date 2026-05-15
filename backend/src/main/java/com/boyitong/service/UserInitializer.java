package com.boyitong.service;

import com.boyitong.entity.User;
import com.boyitong.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(UserInitializer.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Users already exist, skipping user initialization");
            return;
        }

        // Default admin
        userRepository.save(new User("admin", passwordEncoder.encode("admin123"), "ADMIN", "管理员"));
        // Default salesperson
        userRepository.save(new User("zhangrui", passwordEncoder.encode("123456"), "USER", "张睿"));
        userRepository.save(new User("wangxian", passwordEncoder.encode("123456"), "USER", "王鲜"));

        log.info("Created default users: admin, zhangrui, wangxian");
    }
}