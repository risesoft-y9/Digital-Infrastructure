package com.fabriceci.fmc.model;

import java.util.List;

public class ErrorItem {

    private String id;
    private int code;
    private String title;
    private ErrorMeta meta;

    public ErrorItem(String title) {
        this(title, null);
    }

    public ErrorItem(String message, List<String> arguments) {
        this.id = "server";
        this.code = 500;
        this.title = message;
        this.meta = new ErrorMeta();
        if (arguments != null) {
            this.meta.setArguments(arguments);
        }
    }

    public int getCode() {
        return code;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String message) {
        this.title = message;
    }
}
