package net.risesoft.model.processadmin;

import java.io.Serializable;

import lombok.Data;

@Data
public class ProcessDefinitionModel implements Serializable {

    private static final long serialVersionUID = -2797024298337897009L;

    private String Id;

    private String category;

    private String name;

    private String key;

    private String description;

    private int version;

    private String resourceName;

    private String deploymentId;

    private String diagramResourceName;

    private boolean suspended;

    private String engineVersion;

}
