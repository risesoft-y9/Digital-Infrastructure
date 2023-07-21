package net.risesoft.model.addressbook;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

import net.risesoft.enums.OrgTypeEnum;

@Data
public class ContactGroupModel implements Serializable {
    private static final long serialVersionUID = 546814757688315860L;

    private String id;
    private Date createTime;
    private Date updateTime;
    private String personId;
    private String name;
    private Integer tabIndex = 0;
    private String orgType = OrgTypeEnum.CONTACT_GROUP.getEnName();
}
