package com.boyitong.security;

import org.springframework.security.core.Authentication;

/** 权限检查工具类 */
public class SecurityUtils {

    /** 当前用户是否是管理员（ROLE_ADMIN） */
    public static boolean isAdmin(Authentication auth) {
        return hasRole(auth, "ROLE_ADMIN");
    }

    /** 当前用户是否有查看全部数据的权限 */
    public static boolean canViewAllData(Authentication auth) {
        return hasRole(auth, "ROLE_ADMIN") || hasAuthority(auth, "ALL_DATA_VIEW");
    }

    /** 当前用户是否有管理产品权限 */
    public static boolean canManageProduct(Authentication auth) {
        return hasRole(auth, "ROLE_ADMIN") || hasAuthority(auth, "PRODUCT_WRITE");
    }

    /** 当前用户是否有管理公告权限 */
    public static boolean canManageAnnouncement(Authentication auth) {
        return hasRole(auth, "ROLE_ADMIN") || hasAuthority(auth, "ANNOUNCEMENT_WRITE");
    }

    public static boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals(role));
    }

    public static boolean hasAuthority(Authentication auth, String authority) {
        return auth.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals(authority));
    }
}