package com.example.lesson_3_2.server;

public class ServerRunner {

    public static void main(String[] args) {
        final ChatServer chatServer = new ChatServer();
        chatServer.start();
    }

}
