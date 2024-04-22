package com.fabriceci.fmc.model;

import java.util.ArrayList;
import java.util.List;

public class ErrorResponse {

    private List<ErrorItem> errors = new ArrayList<>();

    public ErrorResponse(ErrorItem item) {
        errors.add(item);
    }

    public ErrorResponse(List<ErrorItem> items) {
        errors.addAll(items);
    }

    public List<ErrorItem> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorItem> errors) {
        this.errors = errors;
    }
}
