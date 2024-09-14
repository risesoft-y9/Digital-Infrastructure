package com.fabriceci.fmc.model;

public class InitiateData {

    private String id = "/";

    private String type = "initiate";

    private InitiateAttributes attributes;

    public InitiateAttributes getAttributes() {
        return attributes;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setAttributes(InitiateAttributes attributes) {
        this.attributes = attributes;
    }
}
