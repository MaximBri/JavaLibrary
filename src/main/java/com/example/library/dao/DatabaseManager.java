package com.example.library.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:h2:./librarydb;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    static {
        try {
            // Инициализация драйвера
            Class.forName("org.h2.Driver");
            // Создание базы данных при первом запуске
            createDatabaseIfNotExists();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Не удалось загрузить драйвер H2", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static void createDatabaseIfNotExists() {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {
            // Создаем базовую таблицу для публикаций
            stmt.execute("CREATE TABLE IF NOT EXISTS publications (" +
                    "id BIGINT PRIMARY KEY, " +
                    "title VARCHAR(255) NOT NULL, " +
                    "publisher VARCHAR(255), " +
                    "publish_date DATE, " +
                    "reserved BOOLEAN DEFAULT FALSE, " +
                    "type VARCHAR(20) NOT NULL" +
                    ")");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании базы данных", e);
        }
    }
}
