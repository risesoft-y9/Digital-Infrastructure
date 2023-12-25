package net.risesoft.controller.resource.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.risesoft.controller.TreeNodeVO;
import net.risesoft.controller.TreeTypeEnum;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;

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

    private String appId;
    
    @Override
    public TreeTypeEnum getTreeType() {
        return TreeTypeEnum.RESOURCE;
    }

    public static ResourceTreeNodeVO convertY9ResourceBase(Y9ResourceBase y9ResourceBase) {
        ResourceTreeNodeVO resourceTreeNodeVO = new ResourceTreeNodeVO();
        resourceTreeNodeVO.setId(y9ResourceBase.getId());
        resourceTreeNodeVO.setAppId(y9ResourceBase.getAppId());
        resourceTreeNodeVO.setName(y9ResourceBase.getName());
        resourceTreeNodeVO.setParentId(y9ResourceBase.getParentId());
        resourceTreeNodeVO.setTabIndex(y9ResourceBase.getTabIndex());
        resourceTreeNodeVO.setHasChild(true);
        resourceTreeNodeVO.setNodeType(y9ResourceBase.getResourceType().toString());
        return resourceTreeNodeVO;
    }

    public static List<ResourceTreeNodeVO> convertY9ResourceBaseList(List<? extends Y9ResourceBase> y9ResourceBaseList) {
        List<ResourceTreeNodeVO> roleTreeNodeVOList = new ArrayList<>();
        for (Y9ResourceBase y9ResourceBase : y9ResourceBaseList) {
            roleTreeNodeVOList.add(convertY9ResourceBase(y9ResourceBase));
        }
        return roleTreeNodeVOList;
    }
}
