package com.boyitong.service;

import com.boyitong.entity.Permission;
import com.boyitong.entity.Role;
import com.boyitong.entity.User;
import com.boyitong.repository.PermissionRepository;
import com.boyitong.repository.RoleRepository;
import com.boyitong.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
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
        if (userRepository.count() > 0) {
            log.info("Users already exist, skipping user initialization");
            return;
        }

        // Create permissions
        Permission customerRead = permissionRepository.save(new Permission("CUSTOMER_READ", "查看客户"));
        Permission customerWrite = permissionRepository.save(new Permission("CUSTOMER_WRITE", "编辑客户"));
        Permission customerDelete = permissionRepository.save(new Permission("CUSTOMER_DELETE", "删除客户"));
        Permission customerAssign = permissionRepository.save(new Permission("CUSTOMER_ASSIGN", "分配客户"));
        Permission productWrite = permissionRepository.save(new Permission("PRODUCT_WRITE", "管理产品"));
        Permission announcementWrite = permissionRepository.save(new Permission("ANNOUNCEMENT_WRITE", "管理公告"));
        Permission uploadApprove = permissionRepository.save(new Permission("UPLOAD_APPROVE", "审核上传"));
        Permission auditLogView = permissionRepository.save(new Permission("AUDIT_LOG_VIEW", "查看日志"));
        Permission allDataView = permissionRepository.save(new Permission("ALL_DATA_VIEW", "查看全部数据"));
        Permission exportData = permissionRepository.save(new Permission("EXPORT_DATA", "导出数据"));

        // Create roles
        Role adminRole = new Role("ADMIN");
        adminRole.setPermissions(Set.of(customerRead, customerWrite, customerDelete, customerAssign,
                productWrite, announcementWrite, uploadApprove, auditLogView, allDataView, exportData));
        roleRepository.save(adminRole);

        Role userRole = new Role("USER");
        userRole.setPermissions(Set.of(customerRead, exportData));
        roleRepository.save(userRole);

        // Default admin
        User admin = new User("admin", passwordEncoder.encode("admin123"), "ADMIN", "管理员");
        admin.setRoleEntity(adminRole);
        userRepository.save(admin);

        // Default salespersons
        User zhangrui = new User("zhangrui", passwordEncoder.encode("123456"), "USER", "张睿");
        zhangrui.setRoleEntity(userRole);
        userRepository.save(zhangrui);

        User wangxian = new User("wangxian", passwordEncoder.encode("123456"), "USER", "王鲜");
        wangxian.setRoleEntity(userRole);
        userRepository.save(wangxian);

        log.info("Created default users with RBAC: admin, zhangrui, wangxian");
    }
}