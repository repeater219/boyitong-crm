package com.boyitong.controller;

import com.boyitong.common.Result;
import com.boyitong.dto.UserVO;
import com.boyitong.entity.User;
import com.boyitong.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** 返回所有用户列表（轻量，不含密码） */
    @GetMapping
    public Result<List<UserVO>> getAllUsers() {
        List<UserVO> users = userRepository.findAll().stream()
                .map(u -> new UserVO(u.getId(), u.getUsername(), u.getDisplayName(), u.getRole()))
                .toList();
        return Result.success(users);
    }

    /** 返回所有业务员列表 */
    @GetMapping("/salespersons")
    public Result<List<UserVO>> getSalespersons() {
        List<UserVO> users = userRepository.findAll().stream()
                .filter(u -> "USER".equals(u.getRole()))
                .map(u -> new UserVO(u.getId(), u.getUsername(), u.getDisplayName(), u.getRole()))
                .toList();
        return Result.success(users);
    }
}