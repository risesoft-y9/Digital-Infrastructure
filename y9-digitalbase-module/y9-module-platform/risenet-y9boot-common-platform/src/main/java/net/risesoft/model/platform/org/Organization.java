package net.risesoft.model.platform.org;

import lombok.Data;

/**
 * 组织
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
public class Organization extends OrgUnit {

    private static final long serialVersionUID = 1095290600377048717L;

    /**
     * 英文名称
     */
    private String enName;

    /**
     * 机构代码
     */
    private String organizationCode;

    /**
     * 组织机构类型
     */
    private String organizationType;

    /**
     * 机构类型,0=实体组织，1=虚拟组织
     */
    private Boolean virtual = false;

}