package com.example.lesson_3_2.server;

import com.example.lesson_3_2.Command;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.lesson_3_2.Command.CLIENTS;

public class ChatServer {

    private final Map<String, ClientHandler> clients;
    private final AuthService authService;

    public ChatServer() {
        clients = new HashMap();
        authService = new DbAuthService();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(4000)) {
            while (true) {
                System.out.println("Waiting for client connection");
                final Socket socket = serverSocket.accept();
                new ClientHandler(socket, this);
                System.out.println("Client get connected");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void subscribe(ClientHandler client) {
        clients.put(client.getNick(), client);
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client.getNick());
        broadcastClientList();
    }

    public boolean isNickBusy(String nick) {
        return clients.containsKey(nick);
    }

    public AuthService getAuthService() {
        return authService;
    }

    public void broadcast(String message) {
        for (ClientHandler client : clients.values()) {
            client.sendMessage(message);
        }
    }

    public void sendMessageToClient(ClientHandler from, String nickTo, String message) {
        ClientHandler clientTo = clients.get(nickTo);

        if (clientTo != null) {
            clientTo.sendMessage("From " + from.getNick() + ": " + message);
            from.sendMessage("User " + nickTo + ": " + message);

            return;
        }

        from.sendMessage("User with nick " + nickTo + " doesn't exists");
    }

    public void broadcastClientList() {
        String message = clients.values().stream()
                .map(ClientHandler::getNick)
                .collect(Collectors.joining(" "));

//        StringBuilder message = new StringBuilder("/clients ");
//        clients.values().forEach(client -> message.append(client.getNick()).append(" "));
//        broadcast(message.toString());

        broadcast(CLIENTS, message);
        broadcast(message);
    }

    private void broadcast(Command command, String message) {
        for (ClientHandler client : clients.values()) {
            client.sendMessage(command, message);
        }
    }
}
