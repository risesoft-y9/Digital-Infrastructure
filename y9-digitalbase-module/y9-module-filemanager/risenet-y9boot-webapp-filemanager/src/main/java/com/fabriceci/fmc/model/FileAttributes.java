package com.fabriceci.fmc.model;

public class FileAttributes {

    private String name;
    private String path;
    private int readable = 1;
    private int writable = 1;
    private Long created;
    private Long modified;
    private Integer height;
    private Integer width;
    private Long size;
    private String content;
    private Long files;

    private Long folders;

    public String getContent() {
        return content;
    }

    public Long getCreated() {
        return created;
    }

    public Long getFiles() {
        return files;
    }

    public Long getFolders() {
        return folders;
    }

    public Integer getHeight() {
        return height;
    }

    public Long getModified() {
        return modified;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public int getReadable() {
        return readable;
    }

    public Long getSize() {
        return size;
    }

    public Integer getWidth() {
        return width;
    }

    public int getWritable() {
        return writable;
    }

    public boolean isReadable() {
        return this.readable == 1;
    }

    public boolean isWritable() {
        return this.writable == 1;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public void setFiles(Long files) {
        this.files = files;
    }

    public void setFolders(Long folders) {
        this.folders = folders;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setReadable(int readable) {
        this.readable = readable;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public void setWritable(int writable) {
        this.writable = writable;
    }
}
