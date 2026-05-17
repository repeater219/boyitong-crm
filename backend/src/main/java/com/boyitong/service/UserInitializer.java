package com.boyitong.service;

import com.boyitong.entity.Permission;
import com.boyitong.entity.Role;
import com.boyitong.entity.User;
import com.boyitong.entity.User;
import com.boyitong.repository.PermissionRepository;
import com.boyitong.repository.RoleRepository;
import com.boyitong.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Order(1)
public class UserInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(UserInitializer.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public UserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        // Always initialize permissions and roles
        Permission customerRead = findOrCreate("CUSTOMER_READ", "查看客户");
        Permission customerWrite = findOrCreate("CUSTOMER_WRITE", "编辑客户");
        Permission customerDelete = findOrCreate("CUSTOMER_DELETE", "删除客户");
        Permission customerAssign = findOrCreate("CUSTOMER_ASSIGN", "分配客户");
        Permission productWrite = findOrCreate("PRODUCT_WRITE", "管理产品");
        Permission announcementWrite = findOrCreate("ANNOUNCEMENT_WRITE", "管理公告");
        Permission uploadApprove = findOrCreate("UPLOAD_APPROVE", "审核上传");
        Permission auditLogView = findOrCreate("AUDIT_LOG_VIEW", "查看日志");
        Permission allDataView = findOrCreate("ALL_DATA_VIEW", "查看全部数据");
        Permission exportData = findOrCreate("EXPORT_DATA", "导出数据");

        // Create/update roles
        createRoleIfNotExists("ADMIN", Set.of(customerRead, customerWrite, customerDelete, customerAssign,
                productWrite, announcementWrite, uploadApprove, auditLogView, allDataView, exportData));

        createRoleIfNotExists("MANAGER", Set.of(customerRead, customerWrite, customerAssign,
                allDataView, exportData, auditLogView));

        createRoleIfNotExists("USER", Set.of(customerRead, exportData));

        // Create default users if none exist
        if (userRepository.count() > 0) {
            // Ensure existing users have roleEntity set
            Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
            Role userRole = roleRepository.findByName("USER").orElse(null);
            for (User u : userRepository.findAll()) {
                if (u.getRoleEntity() == null) {
                    if ("ADMIN".equals(u.getRole()) && adminRole != null) {
                        u.setRoleEntity(adminRole);
                    } else if (userRole != null) {
                        u.setRoleEntity(userRole);
                    }
                    userRepository.save(u);
                }
            }
            log.info("Roles and permissions ensured for existing users");
            return;
        }

        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
        Role userRole = roleRepository.findByName("USER").orElseThrow();

        User admin = new User("admin", passwordEncoder.encode("admin123"), "ADMIN", "管理员");
        admin.setRoleEntity(adminRole);
        userRepository.save(admin);

        User zhangrui = new User("zhangrui", passwordEncoder.encode("123456"), "USER", "张睿");
        zhangrui.setRoleEntity(userRole);
        userRepository.save(zhangrui);

        User wangxian = new User("wangxian", passwordEncoder.encode("123456"), "USER", "王鲜");
        wangxian.setRoleEntity(userRole);
        userRepository.save(wangxian);

        log.info("Created default users with RBAC: admin, zhangrui, wangxian");
    }

    private Permission findOrCreate(String name, String description) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> permissionRepository.save(new Permission(name, description)));
    }

    private void createRoleIfNotExists(String name, Set<Permission> permissions) {
        if (roleRepository.findByName(name).isEmpty()) {
            Role role = new Role(name);
            role.setPermissions(permissions);
            roleRepository.save(role);
            log.info("Created role: {}", name);
        }
    }
}