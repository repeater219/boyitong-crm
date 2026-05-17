package com.boyitong.controller;

import com.boyitong.common.Result;
import com.boyitong.dto.UserVO;
import com.boyitong.entity.Role;
import com.boyitong.entity.User;
import com.boyitong.repository.RoleRepository;
import com.boyitong.repository.UserRepository;
import com.boyitong.service.UserResolver;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserResolver userResolver;

    public UserController(UserRepository userRepository, RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder, UserResolver userResolver) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userResolver = userResolver;
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
                .filter(u -> "USER".equals(u.getRole()) || "MANAGER".equals(u.getRole()))
                .map(u -> new UserVO(u.getId(), u.getUsername(), u.getDisplayName(), u.getRole()))
                .toList();
        return Result.success(users);
    }

    /** 返回所有角色列表 */
    @GetMapping("/roles")
    public Result<List<Role>> getRoles() {
        return Result.success(roleRepository.findAll());
    }

    /** 管理员新增用户 */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Result<UserVO> createUser(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String displayName = body.get("displayName");
        String roleName = body.get("role");

        if (username == null || password == null || roleName == null) {
            return Result.error(400, "用户名、密码、角色不能为空");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            return Result.error(400, "用户名已存在");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("角色不存在: " + roleName));

        User user = new User(username, passwordEncoder.encode(password), roleName, displayName);
        user.setRoleEntity(role);
        userRepository.save(user);
        userResolver.refresh();

        return Result.success(new UserVO(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole()));
    }

    /** 管理员更新用户（修改显示名、密码、角色） */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Result<UserVO> updateUser(@PathVariable Long id, @RequestBody Map<String, String> body) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (body.containsKey("displayName")) {
            user.setDisplayName(body.get("displayName"));
        }
        if (body.containsKey("password") && body.get("password") != null && !body.get("password").isBlank()) {
            user.setPassword(passwordEncoder.encode(body.get("password")));
        }
        if (body.containsKey("role")) {
            String roleName = body.get("role");
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("角色不存在: " + roleName));
            user.setRole(roleName);
            user.setRoleEntity(role);
        }

        userRepository.save(user);
        userResolver.refresh();

        return Result.success(new UserVO(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole()));
    }

    /** 管理员删除用户 */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if ("admin".equals(user.getUsername())) {
            return Result.error(400, "不能删除管理员账号");
        }
        userRepository.deleteById(id);
        userResolver.refresh();
        return Result.success();
    }
}