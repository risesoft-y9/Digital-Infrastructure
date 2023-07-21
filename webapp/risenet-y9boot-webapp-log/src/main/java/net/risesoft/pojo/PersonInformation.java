package net.risesoft.pojo;

import java.io.Serializable;

public class PersonInformation implements Serializable {
    private static final long serialVersionUID = 8092547953292121627L;

    private String fullPath;
    private String name;
    private String loginName;
    private String sex;
    private String mobile;
    private String email;

    public PersonInformation() {}

    public PersonInformation(String fullPath, String name, String loginName, String sex, String mobile, String email) {
        super();
        this.fullPath = fullPath;
        this.name = name;
        this.loginName = loginName;
        this.sex = sex;
        this.mobile = mobile;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getFullPath() {
        return fullPath;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getMobile() {
        return mobile;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "PersonInformation [fullPath=" + fullPath + ", name=" + name + ", loginName=" + loginName + ", sex=" + sex + ", mobile=" + mobile + ", email=" + email + "]";
    }

}
