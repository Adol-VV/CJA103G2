package com.momento.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Database Schema Fixer
 * 
 * 用於在應用程式啟動時自動修正資料庫 Schema 設定。
 * 主要解決新功能 Requirements (如草稿) 與舊有 DB Constraint 不符的問題。
 */
@Component
public class DatabaseSchemaFixer implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("DatabaseSchemaFixer: Starts checking schema...");

        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement()) {

            // Fix 1: Make EMP_ID nullable (For Drafts)
            try {
                // MySQL Syntax
                System.out.println("DatabaseSchemaFixer: Altering EVENT.EMP_ID to NULL...");
                stmt.executeUpdate("ALTER TABLE EVENT MODIFY COLUMN EMP_ID INT NULL");
                System.out.println("DatabaseSchemaFixer: EVENT.EMP_ID is now nullable.");
            } catch (Exception e) {
                System.err.println("DatabaseSchemaFixer: Failed to alter EMP_ID. " + e.getMessage());
            }

            // Fix 2: Make TYPE_ID nullable (For Drafts)
            try {
                System.out.println("DatabaseSchemaFixer: Altering EVENT.TYPE_ID to NULL...");
                stmt.executeUpdate("ALTER TABLE EVENT MODIFY COLUMN TYPE_ID INT NULL");
                System.out.println("DatabaseSchemaFixer: EVENT.TYPE_ID is now nullable.");
            } catch (Exception e) {
                System.err.println("DatabaseSchemaFixer: Failed to alter TYPE_ID. " + e.getMessage());
            }
            // Fix 2: Make PLACE nullable (For Drafts)
            try {
                System.out.println("DatabaseSchemaFixer: Altering EVENT.PLACE to NULL...");
                stmt.executeUpdate("ALTER TABLE EVENT MODIFY COLUMN PLACE VARCHAR(200) NULL");
                System.out.println("DatabaseSchemaFixer: EVENT.PLACE is now nullable.");
            } catch (Exception e) {
                System.err.println("DatabaseSchemaFixer: Failed to alter PLACE. " + e.getMessage());
            }

            // Fix 3: Make Time fields nullable
            try {
                System.out.println("DatabaseSchemaFixer: Altering EVENT time fields to NULL...");
                stmt.executeUpdate("ALTER TABLE EVENT MODIFY COLUMN STARTED_AT DATETIME NULL");
                stmt.executeUpdate("ALTER TABLE EVENT MODIFY COLUMN ENDED_AT DATETIME NULL");
                stmt.executeUpdate("ALTER TABLE EVENT MODIFY COLUMN EVENT_AT DATETIME NULL");
                System.out.println("DatabaseSchemaFixer: EVENT time fields are now nullable.");
            } catch (Exception e) {
                System.err.println("DatabaseSchemaFixer: Failed to alter time fields. " + e.getMessage());
            }

            // Fix 4: Make PUBLISHED_AT nullable
            try {
                System.out.println("DatabaseSchemaFixer: Altering EVENT.PUBLISHED_AT to NULL...");
                stmt.executeUpdate("ALTER TABLE EVENT MODIFY COLUMN PUBLISHED_AT DATETIME NULL");
                System.out.println("DatabaseSchemaFixer: EVENT.PUBLISHED_AT is now nullable.");
            } catch (Exception e) {
                System.err.println("DatabaseSchemaFixer: Failed to alter PUBLISHED_AT. " + e.getMessage());
            }

            // Fix 5: ORG_NOTIFY schema fixes
            try {
                System.out.println("DatabaseSchemaFixer: Altering ORG_NOTIFY.IS_READ...");
                // Setting default 0 for IS_READ
                stmt.executeUpdate("ALTER TABLE ORG_NOTIFY MODIFY COLUMN IS_READ TINYINT DEFAULT 0");
                System.out.println("DatabaseSchemaFixer: ORG_NOTIFY.IS_READ updated.");
            } catch (Exception e) {
                System.err.println("DatabaseSchemaFixer: Failed to alter ORG_NOTIFY. " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("DatabaseSchemaFixer: Error during execution. " + e.getMessage());
        }
    }
}
