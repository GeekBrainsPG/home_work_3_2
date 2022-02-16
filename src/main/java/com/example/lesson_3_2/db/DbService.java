package com.example.lesson_3_2.db;

import com.example.lesson_3_2.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbService {

    private DbService() {};

    public static void createUserTableIfNotExists(Connection connect) {
        try {
            final Statement statement = connect.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " login TEXT NOT NULL," +
                    " pass TEXT NOT NULL," +
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
        try (final PreparedStatement ps = connect.prepareStatement("INSERT INTO users(login, pass, nick) VALUES (?, ?, ?)")) {
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

    public static List<User> getAllUsers(Connection connect) {
        final List<User> users = new ArrayList<>();

        try (final ResultSet rs = connect.createStatement().executeQuery("SELECT * FROM users")) {
            while (rs.next()) {
                User user = new User(rs.getString(1), rs.getString(2), rs.getString(3));

                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public static User getUserByLoginAndPassword(Connection connect, String login, String password) {
        try (PreparedStatement ps = connect.prepareStatement("SELECT * FROM users WHERE login = ? and pass = ? LIMIT 1")) {
            ps.setString(1, login);
            ps.setString(2, password);

            final ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                return new User(rs.getString(2), rs.getString(3), rs.getString(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
