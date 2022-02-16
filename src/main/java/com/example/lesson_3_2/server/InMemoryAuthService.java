package com.example.lesson_3_2.server;

import com.example.lesson_3_2.db.DbConnect;
import com.example.lesson_3_2.db.DbService;
import com.example.lesson_3_2.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InMemoryAuthService implements AuthService {

    private final List<User> users;

    public InMemoryAuthService() {
//        this.users = new ArrayList<>();

//        for (int i = 0; i < 9; i++) {
//            users.add(new UserData("login" + i, "pass" + i, "nick" + i));
//        }

        this.users = DbService.getAllUsers(DbConnect.connect().getConnection());
    }

    private strictfp class UserData {
        private final String login;
        private final String password;
        private final String nick;


        public UserData(String login, String password, String nick) {
            this.login = login;
            this.password = password;
            this.nick = nick;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public String getNick() {
            return nick;
        }
    }

    @Override
    public String getNickByLoginAndPassword(String login, String password) {
        User user = DbService.getUserByLoginAndPassword(DbConnect.connect().getConnection(), login, password);

        if (user != null) {
            return user.getNick();
        }
//        for (User user : users) {
//            if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
//                return user.getNick();
//            }
//        }

        return null;
    }
}
