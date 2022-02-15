package com.example.lesson_3_2;

import com.example.lesson_3_2.client.ChatClient;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.util.Arrays;
import java.util.List;

public class MessengerController {

    @FXML
    public ListView<String> clientList;

    @FXML
    private HBox loginBox;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private HBox messageBox;

    @FXML
    private TextArea messengerTextArea;

    @FXML
    private TextField messengerInputField;

    @FXML
    public Button submitButton;

    private final ChatClient client;

    public MessengerController() {
        client = new ChatClient(this);
    }

    public void onSubmitButton() {
        final String message = messengerInputField.getText();

        if (message != null && !message.isEmpty()) {
            client.sendMessage(message);
            messengerInputField.clear();
            messengerInputField.requestFocus();
        }
    }

    public void onExitClick(ActionEvent actionEvent) {
        this.exitChat();
    }

    public void addMessage(String message) {
        messengerTextArea.appendText(message + "\n");
    }

    public void btnAuthClick(ActionEvent actionEvent) {
        client.sendMessage("/auth " + loginField.getText() + " " + passwordField.getText());
    }

    public void selectClient(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            String message = messengerInputField.getText();
            ObservableList client = clientList.getSelectionModel().getSelectedItems();

            messengerInputField.setText(Command.PRIVATE_MESSAGE.getCommand() + " " + client + " " + message);
            messengerInputField.requestFocus();
            messengerInputField.selectEnd();
        }
    }

    public void setAuth(boolean isAuthSuccess) {
        loginBox.setVisible(!isAuthSuccess);
        messageBox.setVisible(isAuthSuccess);
    }

    public void updateClientsList(String[] clients) {
        clientList.getItems().clear();
        clientList.getItems().addAll(clients);
    }

    public void exitChat() {
        System.exit(0);
    }
}