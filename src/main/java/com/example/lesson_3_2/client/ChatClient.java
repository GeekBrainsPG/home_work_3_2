package com.example.lesson_3_2.client;

import com.example.lesson_3_2.Command;
import com.example.lesson_3_2.MessengerController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.concurrent.*;

import static com.example.lesson_3_2.Command.*;

public class ChatClient {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private MessengerController controller;
    private boolean isUserAuthorized;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public ChatClient(MessengerController controller) {
        this.controller = controller;

        try {
            socket = new Socket("127.0.0.1", 4000);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            executorService.execute(new AuthChecker(this));
            executorService.execute(new AuthMessageChecker(this));
            executorService.execute(new MessageReader(this));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void closeConnection() {
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

    public boolean isUserAuthorized() {
        return isUserAuthorized;
    }

    public MessengerController getController() {
        return controller;
    }

    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public void setUserAuthorized(boolean userAuthorized) {
        isUserAuthorized = userAuthorized;
    }
}

class AuthChecker implements Runnable {

    private final ChatClient chatClient;

    public AuthChecker(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(12000);

            boolean isUserAuthorized = chatClient.isUserAuthorized();

            System.out.println("Is user authorized: " + isUserAuthorized);

            if (!isUserAuthorized) {
                chatClient.getController().exitChat();
                chatClient.closeConnection();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class AuthMessageChecker implements Runnable {

    private final ChatClient chatClient;

    public AuthMessageChecker(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String authMessage = chatClient.getIn().readUTF();

                if (getCommandByText(authMessage) == AUTHOK) {
                    String nick = authMessage.split(" ")[1];

                    chatClient.getController().addMessage("User " + nick + " successfully authorized");
                    chatClient.getController().setAuth(true);
                    chatClient.setUserAuthorized(true);

                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

class MessageReader implements Runnable {

    private final ChatClient chatClient;

    public MessageReader(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public void run() {
        while (true) {
            try {
                final String message = chatClient.getIn().readUTF();

                if (Command.isCommand(message)) {
                    Command command = getCommandByText(message);

                    if (command == END) {
                        chatClient.getController().setAuth(false);
                        break;
                    }

                    if (command == CLIENTS) {
                        String[] clients = message.replace(CLIENTS.getCommand() + " ", "").split(" ");

                        chatClient.getController().updateClientsList(clients);
                    }
                }

                chatClient.getController().addMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
