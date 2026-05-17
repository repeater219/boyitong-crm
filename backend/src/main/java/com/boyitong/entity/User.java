package com.boyitong.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    /** 角色: ADMIN / USER */
    @Column(nullable = false)
    private String role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_entity_id")
    private Role roleEntity;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    public User() {}

    public User(String username, String password, String role, String displayName) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.displayName = displayName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Role getRoleEntity() { return roleEntity; }
    public void setRoleEntity(Role roleEntity) { this.roleEntity = roleEntity; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}