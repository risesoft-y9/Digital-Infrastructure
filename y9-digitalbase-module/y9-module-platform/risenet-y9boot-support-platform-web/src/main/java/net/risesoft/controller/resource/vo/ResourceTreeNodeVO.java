package net.risesoft.controller.resource.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.pojo.TreeNodeVO;
import net.risesoft.enums.TreeTypeEnum;
import net.risesoft.enums.platform.ResourceTypeEnum;
import net.risesoft.enums.platform.TreeNodeType;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.entity.resource.Y9System;

/**
 * 资源树节点vo
 *
 * @author shidaobang
 * @date 2023/12/21
 */
@Getter
@Setter
public class ResourceTreeNodeVO extends TreeNodeVO {

    private static final long serialVersionUID = 6086483154815612739L;

    /** 应用id */
    private String appId;
    /** 系统id */
    private String systemId;
    /** 是否启用 */
    private Boolean enabled;
    /** 租户id */
    private String tenantId;

    public static ResourceTreeNodeVO convertY9ResourceBase(Y9ResourceBase y9ResourceBase) {
        ResourceTreeNodeVO resourceTreeNodeVO = new ResourceTreeNodeVO();
        resourceTreeNodeVO.setId(y9ResourceBase.getId());
        resourceTreeNodeVO.setAppId(y9ResourceBase.getAppId());
        resourceTreeNodeVO.setName(y9ResourceBase.getName());
        if (ResourceTypeEnum.APP.equals(y9ResourceBase.getResourceType())) {
            resourceTreeNodeVO.setParentId(y9ResourceBase.getSystemId());
        } else {
            resourceTreeNodeVO.setParentId(y9ResourceBase.getParentId());
        }
        resourceTreeNodeVO.setTabIndex(y9ResourceBase.getTabIndex());
        resourceTreeNodeVO.setHasChild(true);
        resourceTreeNodeVO.setNodeType(y9ResourceBase.getResourceType().toString());
        resourceTreeNodeVO.setSystemId(y9ResourceBase.getSystemId());
        resourceTreeNodeVO.setEnabled(y9ResourceBase.getEnabled());
        return resourceTreeNodeVO;
    }

    public static List<ResourceTreeNodeVO>
    convertY9ResourceBaseList(List<? extends Y9ResourceBase> y9ResourceBaseList) {
        List<ResourceTreeNodeVO> roleTreeNodeVOList = new ArrayList<>();
        for (Y9ResourceBase y9ResourceBase : y9ResourceBaseList) {
            roleTreeNodeVOList.add(convertY9ResourceBase(y9ResourceBase));
        }
        return roleTreeNodeVOList;
    }

    public static ResourceTreeNodeVO convertY9System(Y9System y9System) {
        ResourceTreeNodeVO resourceTreeNodeVO = new ResourceTreeNodeVO();
        resourceTreeNodeVO.setId(y9System.getId());
        resourceTreeNodeVO.setAppId(null);
        resourceTreeNodeVO.setName(y9System.getCnName());
        resourceTreeNodeVO.setParentId(null);
        resourceTreeNodeVO.setTabIndex(y9System.getTabIndex());
        resourceTreeNodeVO.setHasChild(true);
        resourceTreeNodeVO.setNodeType(TreeNodeType.SYSTEM.toString());
        resourceTreeNodeVO.setSystemId(null);
        resourceTreeNodeVO.setEnabled(y9System.getEnabled());
        resourceTreeNodeVO.setTenantId(y9System.getTenantId());
        return resourceTreeNodeVO;
    }

    public static List<ResourceTreeNodeVO> convertY9SystemList(List<Y9System> y9SystemList) {
        List<ResourceTreeNodeVO> resourceTreeNodeVOList = new ArrayList<>();
        for (Y9System y9System : y9SystemList) {
            resourceTreeNodeVOList.add(convertY9System(y9System));
        }
        return resourceTreeNodeVOList;
    }

    @Override
    public TreeTypeEnum getTreeType() {
        return TreeTypeEnum.RESOURCE;
    }
}
