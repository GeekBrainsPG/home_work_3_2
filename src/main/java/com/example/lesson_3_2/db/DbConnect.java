package com.example.lesson_3_2.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnect {

    private static DbConnect instance;
    private static Connection connection;

    private DbConnect() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:messenger.db");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static DbConnect connect() {
        if (instance == null) {
            instance = new DbConnect();

            System.out.println("Connection with database was created!");
        }

        return instance;
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
