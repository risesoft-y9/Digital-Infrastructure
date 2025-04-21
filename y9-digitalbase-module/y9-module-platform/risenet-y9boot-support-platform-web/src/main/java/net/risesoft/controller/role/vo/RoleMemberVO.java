package net.risesoft.controller.role.vo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.relation.Y9OrgBasesToRoles;
import net.risesoft.util.Y9OrgUtil;

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
    private String id;

    /** 是否为负角色关联 */
    private Boolean negative;

    /** 组织节点id */
    private String orgUnitId;

    /** 组织节点dn */
    private String orgUnitDn;

    /** 组织节点名称 */
    private String orgUnitName;

    /** 组织节点名称路径 > 分隔 */
    private String orgUnitNamePath;

    /** 组织节点类型 */
    private String orgTypeStr;

    /** 创建时间 */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    public static RoleMemberVO of(Y9OrgBasesToRoles y9OrgBasesToRoles, Y9OrgBase y9OrgBase) {
        RoleMemberVO roleMemberVO = new RoleMemberVO();
        roleMemberVO.setId(y9OrgBasesToRoles.getId());
        roleMemberVO.setOrgUnitId(y9OrgBasesToRoles.getOrgId());
        roleMemberVO.setOrgUnitName(y9OrgBase.getName());
        roleMemberVO.setOrgTypeStr(y9OrgBase.getOrgType().getName());
        roleMemberVO.setOrgUnitDn(y9OrgBase.getDn());
        roleMemberVO.setOrgUnitNamePath(Y9OrgUtil.dnToNamePath(y9OrgBase.getDn()));
        roleMemberVO.setNegative(y9OrgBasesToRoles.getNegative());
        roleMemberVO.setCreateTime(y9OrgBasesToRoles.getCreateTime());
        return roleMemberVO;
    }

}
