package net.risesoft.vo.org;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.enums.TreeTypeEnum;
import net.risesoft.enums.platform.org.OrgTreeTypeEnum;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.enums.platform.org.SexEnum;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.vo.TreeNodeVO;

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

    /** 是否禁用 */
    private Boolean disabled;

    /** id路径 */
    private String guidPath;

    /** 成员计数 可以是岗位和人员计数 */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long memberCount;

    /** 人员性别 */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SexEnum sex;

    /** 人员是否为新增的账号 */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean original;

    public static OrgTreeNodeVO convertY9OrgBase(Y9OrgBase y9OrgBase, OrgTreeTypeEnum treeType, boolean countMember,
        CompositeOrgBaseService compositeOrgBaseService) {
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
            orgTreeNodeVO.setSex(((Y9Person)y9OrgBase).getSex());
            orgTreeNodeVO.setOriginal(((Y9Person)y9OrgBase).getOriginal());
        }
        if (countMember && (OrgTypeEnum.DEPARTMENT.equals(y9OrgBase.getOrgType())
            || OrgTypeEnum.ORGANIZATION.equals(y9OrgBase.getOrgType()))) {
            orgTreeNodeVO.setMemberCount(compositeOrgBaseService.countByGuidPath(y9OrgBase.getGuidPath(), treeType));
        }
        return orgTreeNodeVO;
    }

    public static List<OrgTreeNodeVO> convertY9OrgBaseList(List<? extends Y9OrgBase> y9ResourceBaseList,
        OrgTreeTypeEnum treeType, boolean countMember, CompositeOrgBaseService compositeOrgBaseService) {
        List<OrgTreeNodeVO> roleTreeNodeVOList = new ArrayList<>();
        for (Y9OrgBase y9OrgBase : y9ResourceBaseList) {
            roleTreeNodeVOList.add(convertY9OrgBase(y9OrgBase, treeType, countMember, compositeOrgBaseService));
        }
        return roleTreeNodeVOList;
    }

    @Override
    public TreeTypeEnum getTreeType() {
        return TreeTypeEnum.ORG;
    }
}
