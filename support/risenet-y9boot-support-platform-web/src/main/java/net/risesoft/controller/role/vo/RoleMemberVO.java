package net.risesoft.controller.role.vo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.relation.Y9OrgBasesToRoles;
import net.risesoft.enums.platform.OrgTypeEnum;

/**
 * 角色关联组织节点
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Getter
@Setter
public class RoleMemberVO implements Serializable {

    private static final long serialVersionUID = -6419413051796171825L;

    /** id */
    private Integer id;

    /** 是否为负角色关联 */
    private String negative;

    /** 组织节点id */
    private String orgUnitId;

    /** 组织节点dn */
    private String unitDn;

    /** 组织节点名称 */
    private String unitName;

    /** 组织节点类型 */
    private String unitTypeName;

    public static RoleMemberVO of(Y9OrgBasesToRoles y9OrgBasesToRoles, Y9OrgBase y9OrgBase) {
        RoleMemberVO roleMemberVO = new RoleMemberVO();
        roleMemberVO.setId(y9OrgBasesToRoles.getId());
        roleMemberVO.setOrgUnitId(y9OrgBasesToRoles.getOrgId());
        roleMemberVO.setUnitName(y9OrgBase.getName());
        roleMemberVO.setUnitTypeName(OrgTypeEnum.ORG_TYPE_MAP.get(y9OrgBase.getOrgType()));
        roleMemberVO.setUnitDn(y9OrgBase.getDn());
        roleMemberVO.setNegative(Boolean.TRUE.equals(y9OrgBasesToRoles.getNegative()) ? "是" : "否");
        return roleMemberVO;
    }

}
