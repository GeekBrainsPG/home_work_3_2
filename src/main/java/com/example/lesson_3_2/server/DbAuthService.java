package com.example.lesson_3_2.server;

import com.example.lesson_3_2.db.DbConnect;
import com.example.lesson_3_2.db.DbService;
import com.example.lesson_3_2.model.User;

public class DbAuthService implements AuthService {

    public DbAuthService() {}

    @Override
    public String getNickByLoginAndPassword(String login, String password) {
        User user = DbService.getUserByLoginAndPassword(DbConnect.connect().getConnection(), login, password);

        if (user != null) {
            return user.getNick();
        }

        return null;
    }
}
