package com.fabriceci.fmc.model;

import java.util.ArrayList;
import java.util.List;

public class ErrorMeta {

    private List<String> arguments = new ArrayList<>();

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }
}
