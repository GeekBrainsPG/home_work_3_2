package com.example.lesson_3_2.server;

import com.example.lesson_3_2.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.example.lesson_3_2.Command.*;

public class ClientHandler {

    private final Socket socket;
    private final ChatServer chatServer;
    private final DataInputStream in;
    private final DataOutputStream out;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM HH:mm");

    private String nick;

    public ClientHandler(Socket socket, ChatServer chatServer) {
        try {
            this.nick = "";
            this.socket = socket;
            this.chatServer = chatServer;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    authenticate();
                    readMessage();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

        private void closeConnection() {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (socket != null) {
                try {
                    socket.close();
                    chatServer.unsubscribe(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    private void readMessage() {
        while (true) {
            try {
                final String message = in.readUTF();

                if (Command.isCommand(message)) {
                    if (message.startsWith(Command.getCommandPrefix())) {
                        if (Command.getCommandByText(message) == END) {
                            break;
                        }
                        if (Command.getCommandByText(message) == PRIVATE_MESSAGE) {
                            final String[] split = message.split(" ");
                            final String nickTo = split[1];
                            final int amountOfSymbolsToSubstring = PRIVATE_MESSAGE.getCommand().length() + 2 + nickTo.length();
                            chatServer.sendMessageToClient(this, nick, message.substring(amountOfSymbolsToSubstring));
                        }

                        continue;
                    }
                }

                chatServer.broadcast(nick + ": " + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void authenticate() {
        while (true) {
            try {
                final String message = in.readUTF();

                if (Command.getCommandByText(message) == AUTH) {
                    final String[] split = message.split(" ");
                    String login = split[1];
                    String password = split[2];

                    String nick = chatServer.getAuthService().getNickByLoginAndPassword(login, password);

                    if (nick != null) {
                        if (chatServer.isNickBusy(nick)) {
                            sendMessage("User already authorized");
                            continue;
                        }
                        sendMessage(Command.AUTHOK, nick);
                        this.nick = nick;
                        chatServer.broadcast("User " + nick + " is online");
                        chatServer.subscribe(this);

                        break;
                    } else {
                        sendMessage("Incorrect login and password");
                    }
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void sendMessage(Command command, String message) {
        try {
            out.writeUTF(command.getCommand() + " " + message);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(LocalDateTime.now().format(FORMATTER) + ": " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNick() {
        return nick;
    }
}
