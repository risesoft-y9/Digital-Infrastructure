package net.risesoft.vo.resource;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.enums.TreeTypeEnum;
import net.risesoft.model.platform.System;
import net.risesoft.vo.TreeNodeVO;

/**
 * 系统树节点vo
 *
 * @author shidaobang
 * @date 2023/12/21
 */
@Getter
@Setter
public class SystemTreeNodeVO extends TreeNodeVO {

    private static final long serialVersionUID = -7329269439077778947L;

    /** 系统中文名 */
    private String cnName;
    /** 系统id */
    private String systemId;
    /** 租户id */
    private String tenantId;

    public static SystemTreeNodeVO convertSystem(System system) {
        SystemTreeNodeVO systemTreeNodeVO = new SystemTreeNodeVO();
        systemTreeNodeVO.setId(system.getId());
        systemTreeNodeVO.setName(system.getName());
        systemTreeNodeVO.setCnName(system.getCnName());
        systemTreeNodeVO.setParentId(null);
        systemTreeNodeVO.setTabIndex(system.getTabIndex());
        systemTreeNodeVO.setHasChild(true);
        systemTreeNodeVO.setNodeType("SYSTEM");
        systemTreeNodeVO.setSystemId(system.getId());
        systemTreeNodeVO.setTenantId(system.getTenantId());
        return systemTreeNodeVO;
    }

    public static List<SystemTreeNodeVO> convertY9SystemList(List<System> systemList) {
        List<SystemTreeNodeVO> roleTreeNodeVOList = new ArrayList<>();
        for (System system : systemList) {
            roleTreeNodeVOList.add(convertSystem(system));
        }
        return roleTreeNodeVOList;
    }

    @Override
    public TreeTypeEnum getTreeType() {
        return TreeTypeEnum.SYSTEM;
    }
}
