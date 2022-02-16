package com.example.lesson_3_2.server;

import com.example.lesson_3_2.db.DbConnect;
import com.example.lesson_3_2.db.DbService;

public class ServerRunner {

    public static void main(String[] args) {
        DbConnect connect = DbConnect.connect();
        DbService.dropUserTable(connect.getConnection());
        DbService.createUserTableIfNotExists(connect.getConnection());
        DbService.populateMockUserData(connect.getConnection());

        final ChatServer chatServer = new ChatServer();
        chatServer.start();
    }

}
