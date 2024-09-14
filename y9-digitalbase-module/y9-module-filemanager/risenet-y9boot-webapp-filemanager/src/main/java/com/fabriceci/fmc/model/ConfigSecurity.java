package com.fabriceci.fmc.model;

public class ConfigSecurity {
    private boolean readOnly;

    private ConfigExtensions extensions;

    public ConfigExtensions getExtensions() {
        return extensions;
    }

    public boolean getReadOnly() {
        return readOnly;
    }

    public void setExtensions(ConfigExtensions extensions) {
        this.extensions = extensions;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
