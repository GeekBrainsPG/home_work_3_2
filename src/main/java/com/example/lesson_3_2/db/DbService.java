package com.example.lesson_3_2.db;

import com.example.lesson_3_2.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DbService {

    private DbService() {};

    public static void createUserTableIfNotExists(Connection connect) {
        try {
            final Statement statement = connect.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " login TEXT NOT NULL," +
                    " password TEXT NOT NULL," +
                    " nick TEXT NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void dropUserTable(Connection connect) {
        try {
            final Statement statement = connect.createStatement();
            statement.executeUpdate("DROP TABLE IF EXISTS users");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void populateMockUserData(Connection connect) {
        try (final PreparedStatement ps = connect.prepareStatement("INSERT INTO users(login, password, nick) VALUES (?, ?, ?)")) {
            List<User> users = List.of(
                    new User("login0", "pass0", "nick0"),
                    new User("login1", "pass1", "nick1"),
                    new User("login2", "pass2", "nick2"));

            users.forEach(user -> {
                try {
                    ps.setString(1, user.getLogin());
                    ps.setString(2, user.getPassword());
                    ps.setString(3, user.getNick());
                    ps.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
