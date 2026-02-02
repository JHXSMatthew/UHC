package com.github.JHXSMatthew.Utils;

import com.github.JHXSMatthew.Config.Config;
import com.huskehhh.mysql.mysql.MySQL;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库初始化工具类
 * 用于自动创建和验证 UHC 数据库表结构
 */
public class DatabaseInitializer {

    private final MySQL mysql;
    private final String tableName;

    public DatabaseInitializer() {
        this.mysql = new MySQL(Config.SQL_ADDRESS, String.valueOf(Config.SQL_PORT),
                Config.SQL_DB_NAME, Config.SQL_USER_NAME, Config.SQL_PASSWORD);
        this.tableName = Config.SQL_TABLE_NAME;
    }

    /**
     * 初始化数据库表
     */
    public void initializeTables(Connection connection) {
        try {
            Statement s = connection.createStatement();
            
            // 创建 UHC 统计表
            String createTableSQL = "CREATE TABLE IF NOT EXISTS `" + tableName + "` (" +
                    "`id` INT AUTO_INCREMENT PRIMARY KEY," +
                    "`Name` VARCHAR(64) NOT NULL UNIQUE," +
                    "`Games` INT DEFAULT 0," +
                    "`Wins` INT DEFAULT 0," +
                    "`Kills` INT DEFAULT 0," +
                    "`Deaths` INT DEFAULT 0," +
                    "`Stacks` INT DEFAULT 0," +
                    "`Points` INT DEFAULT 0," +
                    "`LastPlayed` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "INDEX `idx_uhc_name` (`Name`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
            
            s.execute(createTableSQL);
            s.close();
            
            System.out.println("[UHC] 数据库表初始化完成: " + tableName);
        } catch (SQLException e) {
            System.err.println("[UHC] 数据库表初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 验证表是否存在
     */
    public boolean validateTableExists(Connection connection) {
        try {
            Statement s = connection.createStatement();
            // 检查表是否存在于当前数据库中
            String checkSQL = "SELECT COUNT(*) FROM information_schema.tables " +
                    "WHERE table_schema = DATABASE() AND table_name = '" + tableName + "'";
            
            var resultSet = s.executeQuery(checkSQL);
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                s.close();
                return count > 0;
            }
            s.close();
        } catch (SQLException e) {
            System.err.println("[UHC] 验证表存在性时出错: " + e.getMessage());
        }
        return false;
    }
}