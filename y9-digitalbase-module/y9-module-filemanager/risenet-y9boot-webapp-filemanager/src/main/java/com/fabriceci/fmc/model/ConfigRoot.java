package com.fabriceci.fmc.model;

public class ConfigRoot {

    private ConfigSecurity security;

    private ConfigUpload upload;

    public ConfigSecurity getSecurity() {
        return security;
    }

    public ConfigUpload getUpload() {
        return upload;
    }

    public void setSecurity(ConfigSecurity security) {
        this.security = security;
    }

    public void setUpload(ConfigUpload upload) {
        this.upload = upload;
    }
}
