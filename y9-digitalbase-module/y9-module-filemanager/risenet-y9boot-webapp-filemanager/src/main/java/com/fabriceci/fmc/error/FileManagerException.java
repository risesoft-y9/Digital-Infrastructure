package com.fabriceci.fmc.error;

import java.util.List;

public class FileManagerException extends Exception {

    private List<String> arguments;

    public FileManagerException(String message) {
        super(message);
    }

    public FileManagerException(String message, List<String> arguments) {
        super(message);
        this.arguments = arguments;
    }

    public FileManagerException(String message, List<String> arguments, Throwable cause) {
        super(message, cause);
        this.arguments = arguments;
    }

    public FileManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public List<String> getArguments() {
        return arguments;
    }
}