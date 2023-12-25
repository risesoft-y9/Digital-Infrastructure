package net.risesoft.controller.org.vo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.controller.TreeNodeVO;
import net.risesoft.controller.TreeTypeEnum;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.enums.platform.SexEnum;

/**
 * 组织树节点vo
 *
 * @author shidaobang
 * @date 2023/12/21
 */
@Getter
@Setter
public class OrgTreeNodeVO extends TreeNodeVO {
    
    private static final long serialVersionUID = 3542372289025268472L;

    private Boolean disabled;
    
    private String guidPath;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SexEnum sex;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean original;
    
    @Override
    public TreeTypeEnum getTreeType() {
        return TreeTypeEnum.ORG;
    }

    public static OrgTreeNodeVO convertY9OrgBase(Y9OrgBase y9OrgBase) {
        OrgTreeNodeVO orgTreeNodeVO = new OrgTreeNodeVO();
        orgTreeNodeVO.setId(y9OrgBase.getId());
        orgTreeNodeVO.setGuidPath(y9OrgBase.getGuidPath());
        orgTreeNodeVO.setDisabled(y9OrgBase.getDisabled());
        orgTreeNodeVO.setName(y9OrgBase.getName());
        orgTreeNodeVO.setParentId(y9OrgBase.getParentId());
        orgTreeNodeVO.setTabIndex(y9OrgBase.getTabIndex());
        orgTreeNodeVO.setHasChild(true);
        orgTreeNodeVO.setNodeType(y9OrgBase.getOrgType().getValue());

        if (y9OrgBase instanceof Y9Person) {
            orgTreeNodeVO.setSex(((Y9Person) y9OrgBase).getSex());
            orgTreeNodeVO.setOriginal(((Y9Person) y9OrgBase).getOriginal());
        }
        return orgTreeNodeVO;
    }

    public static List<OrgTreeNodeVO> convertY9OrgBaseList(List<? extends Y9OrgBase> y9ResourceBaseList) {
        List<OrgTreeNodeVO> roleTreeNodeVOList = new ArrayList<>();
        for (Y9OrgBase y9ResourceBase : y9ResourceBaseList) {
            roleTreeNodeVOList.add(convertY9OrgBase(y9ResourceBase));
        }
        return roleTreeNodeVOList;
    }
}
