package com.fabriceci.fmc.model;

public class ConfigExtensions {

    private String policy;

    private boolean ignoreCase = true;

    private String[] restrictions;

    public boolean getIgnoreCase() {
        return ignoreCase;
    }

    public String getPolicy() {
        return policy;
    }

    public String[] getRestrictions() {
        return restrictions;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public void setRestrictions(String[] restrictions) {
        this.restrictions = restrictions;
    }
}