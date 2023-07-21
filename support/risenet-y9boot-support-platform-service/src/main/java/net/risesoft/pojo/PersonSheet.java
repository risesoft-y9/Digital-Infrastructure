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
public class PersonSheet implements Serializable {

    private static final long serialVersionUID = -4544289922271404524L;
    private String dn;
    private String duty;
    private String email;
    private String loginName;
    private String mobile;
    private String name;
    private int num;
    private String officeFax;
    private String officePhone;
    private Integer tabIndex;

}
