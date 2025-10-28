package net.risesoft.dto.platform;

import lombok.Getter;
import lombok.Setter;

/**
 * 组织
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Getter
@Setter
public class CreateOrganizationDTO extends CreateOrgUnitBaseDTO {

    private static final long serialVersionUID = 7911871659478634602L;

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