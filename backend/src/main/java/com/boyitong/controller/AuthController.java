package com.boyitong.controller;

import com.boyitong.common.Result;
import com.boyitong.dto.LoginRequest;
import com.boyitong.dto.LoginResponse;
import com.boyitong.entity.User;
import com.boyitong.repository.UserRepository;
import com.boyitong.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getId());
        LoginResponse response = new LoginResponse(token, user.getUsername(),
                user.getDisplayName(), user.getRole(), user.getAvatarUrl(), user.getId());
        return Result.success(response);
    }

    @GetMapping("/me")
    public Result<LoginResponse> me(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return Result.success(new LoginResponse(token, username, user.getDisplayName(), role, user.getAvatarUrl(), user.getId()));
    }
}