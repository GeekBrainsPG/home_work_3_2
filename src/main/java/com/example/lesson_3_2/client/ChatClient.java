package com.example.lesson_3_2.client;

import com.example.lesson_3_2.Command;
import com.example.lesson_3_2.MessengerController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import static com.example.lesson_3_2.Command.*;

public class ChatClient {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private MessengerController controller;
    private boolean isUserAuthorized;

    public ChatClient(MessengerController controller) {
        this.controller = controller;

        try {
            socket = new Socket("127.0.0.1", 4000);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    Thread.sleep(12000);

                    System.out.println("Is user authorized: " + isUserAuthorized);

                    if (!isUserAuthorized) {
                        controller.exitChat();
                        closeConnection();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            new Thread(() -> {
                try {
                    while (true) {
                        String authMessage = in.readUTF();

                        if (getCommandByText(authMessage) == AUTHOK) {
                            String nick = authMessage.split(" ")[1];

                            controller.addMessage("User " + nick + " successfully authorized");
                            controller.setAuth(true);
                            isUserAuthorized = true;
                            break;
                        }
                    }

                    while (true) {
                        final String message = in.readUTF();

                        if (Command.isCommand(message)) {
                            Command command = getCommandByText(message);

                            if (command == END) {
                                controller.setAuth(false);
                                break;
                            }

                            if (command == CLIENTS) {
                                String[] clients = message.replace(CLIENTS.getCommand() + " ", "").split(" ");

                                controller.updateClientsList(clients);
                            }
                        }

                        controller.addMessage(message);
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException exception) {
            exception.printStackTrace();
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
