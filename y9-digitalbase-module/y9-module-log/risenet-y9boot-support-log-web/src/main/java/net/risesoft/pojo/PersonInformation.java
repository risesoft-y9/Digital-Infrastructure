package net.risesoft.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class PersonInformation implements Serializable {
    private static final long serialVersionUID = 8092547953292121627L;

    private String name;

    private String loginName;

    private String fullPath;

    private String sex;

    private String mobile;

    private String email;

}
