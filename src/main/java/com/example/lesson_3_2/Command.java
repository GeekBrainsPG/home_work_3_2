package com.example.lesson_3_2;

import java.util.Arrays;

public enum Command {

    AUTHOK("/authok"),
    AUTH("/auth"),
    PRIVATE_MESSAGE("/w"),
    END("/end"),
    CLIENTS("/clients");

    private String command;

    Command(String command) {
        this.command = command;
    }

    public static boolean isCommand(String message) {
        return message.startsWith(getCommandPrefix());
    }

    public String getCommand() {
        return command;
    }

    public static Command getCommandByText(String text) {
        return Arrays
                .stream(values())
                .filter(cmd -> text.startsWith(cmd.getCommand()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Such command doesnt exists " + text));
    }

    public static String getCommandPrefix() {
        return "/";
    }
}
