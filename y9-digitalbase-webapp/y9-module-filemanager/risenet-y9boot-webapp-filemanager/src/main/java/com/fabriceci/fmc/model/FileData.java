package com.fabriceci.fmc.model;

public class FileData {

    private String id;
    private FileType type;
    private FileAttributes attributes;

    public FileAttributes getAttributes() {
        return attributes;
    }

    public String getId() {
        return id;
    }

    public FileType getType() {
        return type;
    }

    public void setAttributes(FileAttributes attributes) {
        this.attributes = attributes;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(FileType type) {
        this.type = type;
    }
}
