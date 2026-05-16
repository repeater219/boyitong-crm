package com.boyitong.controller;

import com.boyitong.common.Result;
import com.boyitong.entity.User;
import com.boyitong.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public Result<User> getProfile(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping
    public Result<User> updateProfile(@RequestBody Map<String, String> body, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (body.containsKey("displayName")) {
            user.setDisplayName(body.get("displayName"));
        }
        userRepository.save(user);
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/password")
    public ResponseEntity<Result<Void>> changePassword(@RequestBody Map<String, String> body, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (oldPassword == null || newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Result.error(400, "密码长度不能少于6位"));
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body(Result.error(400, "原密码错误"));
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return ResponseEntity.ok(Result.success());
    }

    @PostMapping("/avatar")
    public Result<User> uploadAvatar(@RequestParam("file") MultipartFile file, Authentication auth) throws IOException {
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // Store as Base64 data URL directly in DB — no files on disk
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error(400, "只支持图片文件");
        }
        String base64 = Base64.getEncoder().encodeToString(file.getBytes());
        String dataUrl = "data:" + contentType + ";base64," + base64;

        user.setAvatarUrl(dataUrl);
        userRepository.save(user);
        user.setPassword(null);
        return Result.success(user);
    }
}