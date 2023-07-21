package net.risesoft.pojo;

import java.io.Serializable;

import lombok.Data;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Data
public class PersonInformation implements Serializable {
    
    private static final long serialVersionUID = 8092547953292121627L;
    
    private String email;
    private String fullPath;
    private String loginName;
    private String mobile;
    private String name;
    private String sex;
    
}
