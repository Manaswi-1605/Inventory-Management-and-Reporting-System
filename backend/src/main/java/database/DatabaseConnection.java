package com.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/stockmanagement";
    private static final String USER = "root";
    private static final String PASSWORD = "skrillex";

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connected to MySQL!");
            return conn;
        } catch (SQLException e) {
            System.out.println("❌ Connection Failed!");
            e.printStackTrace();
            return null;
        }
    }

}
