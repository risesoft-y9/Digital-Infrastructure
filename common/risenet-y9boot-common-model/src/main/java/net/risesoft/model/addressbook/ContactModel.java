package net.risesoft.model.addressbook;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

import net.risesoft.enums.platform.OrgTypeEnum;

@Data
public class ContactModel implements Serializable {
    private static final long serialVersionUID = -2726298750435709819L;

    private String id;
    private Date createTime;
    private Date updateTime;
    private String contactGroupId;
    private String name;
    private String mobile;
    private String officePhone;
    private String email;
    private String company;
    private String address;
    private String remarks;
    private Integer tabIndex;
    private String orgType = OrgTypeEnum.CONTACT.getEnName();
}
