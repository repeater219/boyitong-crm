package com.boyitong.dto;

public class LoginResponse {
    private String token;
    private String username;
    private String displayName;
    private String role;
    private String avatarUrl;
    private Long userId;

    public LoginResponse(String token, String username, String displayName, String role, String avatarUrl, Long userId) {
        this.token = token;
        this.username = username;
        this.displayName = displayName;
        this.role = role;
        this.avatarUrl = avatarUrl;
        this.userId = userId;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public String getRole() { return role; }
    public String getAvatarUrl() { return avatarUrl; }
    public Long getUserId() { return userId; }
}