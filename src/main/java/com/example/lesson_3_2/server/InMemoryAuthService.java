package com.example.lesson_3_2.server;

import java.util.ArrayList;
import java.util.List;

public class InMemoryAuthService implements AuthService {

    private final List<UserData> users;

    public InMemoryAuthService() {
        this.users = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            users.add(new UserData("login" + i, "pass" + i, "nick" + i));
        }
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
        for (UserData user : users) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                return user.getNick();
            }
        }

        return null;
    }
}
