package net.risesoft.vo.resource;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.enums.TreeTypeEnum;
import net.risesoft.enums.platform.TreeNodeType;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.model.platform.System;
import net.risesoft.model.platform.resource.Resource;
import net.risesoft.vo.TreeNodeVO;

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

    /**
     * 应用id
     */
    private String appId;
    /**
     * 系统id
     */
    private String systemId;
    /**
     * 是否启用
     */
    private Boolean enabled;
    /**
     * 租户id
     */
    private String tenantId;
    /**
     * 授权是否继承
     */
    private Boolean inherit;

    public static ResourceTreeNodeVO convertResource(Resource resource) {
        ResourceTreeNodeVO resourceTreeNodeVO = new ResourceTreeNodeVO();
        resourceTreeNodeVO.setId(resource.getId());
        resourceTreeNodeVO.setAppId(resource.getAppId());
        resourceTreeNodeVO.setName(resource.getName());
        if (ResourceTypeEnum.APP.equals(resource.getResourceType())) {
            resourceTreeNodeVO.setParentId(resource.getSystemId());
        } else {
            resourceTreeNodeVO.setParentId(resource.getParentId());
        }
        resourceTreeNodeVO.setTabIndex(resource.getTabIndex());
        resourceTreeNodeVO.setHasChild(true);
        resourceTreeNodeVO.setNodeType(resource.getResourceType().toString());
        resourceTreeNodeVO.setSystemId(resource.getSystemId());
        resourceTreeNodeVO.setEnabled(resource.getEnabled());
        resourceTreeNodeVO.setInherit(resource.getInherit());
        return resourceTreeNodeVO;
    }

    public static List<ResourceTreeNodeVO> convertResource(List<? extends Resource> resourceList) {
        List<ResourceTreeNodeVO> roleTreeNodeVOList = new ArrayList<>();
        for (Resource resource : resourceList) {
            roleTreeNodeVOList.add(convertResource(resource));
        }
        return roleTreeNodeVOList;
    }

    public static ResourceTreeNodeVO convertSystem(System system) {
        ResourceTreeNodeVO resourceTreeNodeVO = new ResourceTreeNodeVO();
        resourceTreeNodeVO.setId(system.getId());
        resourceTreeNodeVO.setAppId(null);
        resourceTreeNodeVO.setName(system.getCnName());
        resourceTreeNodeVO.setParentId(null);
        resourceTreeNodeVO.setTabIndex(system.getTabIndex());
        resourceTreeNodeVO.setHasChild(true);
        resourceTreeNodeVO.setNodeType(TreeNodeType.SYSTEM.toString());
        resourceTreeNodeVO.setSystemId(null);
        resourceTreeNodeVO.setEnabled(system.getEnabled());
        resourceTreeNodeVO.setTenantId(system.getTenantId());
        return resourceTreeNodeVO;
    }

    public static List<ResourceTreeNodeVO> convertSystem(List<System> systemList) {
        List<ResourceTreeNodeVO> resourceTreeNodeVOList = new ArrayList<>();
        for (System system : systemList) {
            resourceTreeNodeVOList.add(convertSystem(system));
        }
        return resourceTreeNodeVOList;
    }

    @Override
    public TreeTypeEnum getTreeType() {
        return TreeTypeEnum.RESOURCE;
    }
}
