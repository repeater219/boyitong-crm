package com.boyitong.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * PostgreSQL 迁移：将 @Lob 映射的 OID 类型列转为 TEXT
 * 解决 "Unable to access lob stream" 错误
 */
@Component
@Order(0)
public class DatabaseMigration implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseMigration.class);
    private final JdbcTemplate jdbc;

    public DatabaseMigration(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(String... args) {
        try {
            // 检查是否 PostgreSQL
            String dbUrl = jdbc.getDataSource().getConnection().getMetaData().getURL();
            if (!dbUrl.startsWith("jdbc:postgresql")) {
                log.info("Not PostgreSQL, skipping migration");
                return;
            }
        } catch (SQLException e) {
            log.warn("Failed to check database type, skipping migration");
            return;
        }

        log.info("Running PostgreSQL column type migrations...");

        // app_users.avatar_url: OID → TEXT
        try {
            jdbc.execute("ALTER TABLE app_users ALTER COLUMN avatar_url TYPE TEXT");
            log.info("Migrated app_users.avatar_url: OID → TEXT");
        } catch (Exception e) {
            log.info("app_users.avatar_url migration skipped: {}", e.getMessage());
        }

        log.info("Database migration complete");
    }
}