package net.risesoft.vo.org;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.enums.TreeTypeEnum;
import net.risesoft.enums.platform.org.OrgTreeTypeEnum;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.enums.platform.org.SexEnum;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Person;
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

    public static OrgTreeNodeVO convertOrgUnit(OrgUnit orgUnit, OrgTreeTypeEnum treeType, boolean countMember,
        CompositeOrgBaseService compositeOrgBaseService) {
        OrgTreeNodeVO orgTreeNodeVO = new OrgTreeNodeVO();
        orgTreeNodeVO.setId(orgUnit.getId());
        orgTreeNodeVO.setGuidPath(orgUnit.getGuidPath());
        orgTreeNodeVO.setDisabled(orgUnit.getDisabled());
        orgTreeNodeVO.setName(orgUnit.getName());
        orgTreeNodeVO.setParentId(orgUnit.getParentId());
        orgTreeNodeVO.setTabIndex(orgUnit.getTabIndex());
        orgTreeNodeVO.setHasChild(true);
        orgTreeNodeVO.setNodeType(orgUnit.getOrgType().getValue());

        if (orgUnit instanceof Person) {
            orgTreeNodeVO.setSex(((Person)orgUnit).getSex());
            orgTreeNodeVO.setOriginal(((Person)orgUnit).isOriginal());
        }
        if (countMember && (OrgTypeEnum.DEPARTMENT.equals(orgUnit.getOrgType())
            || OrgTypeEnum.ORGANIZATION.equals(orgUnit.getOrgType()))) {
            orgTreeNodeVO.setMemberCount(compositeOrgBaseService.countByGuidPath(orgUnit.getGuidPath(), treeType));
        }
        return orgTreeNodeVO;
    }

    public static List<OrgTreeNodeVO> convertOrgUnitList(List<? extends OrgUnit> orgUnitList, OrgTreeTypeEnum treeType,
        boolean countMember, CompositeOrgBaseService compositeOrgBaseService) {
        List<OrgTreeNodeVO> roleTreeNodeVOList = new ArrayList<>();
        for (OrgUnit orgUnit : orgUnitList) {
            roleTreeNodeVOList.add(convertOrgUnit(orgUnit, treeType, countMember, compositeOrgBaseService));
        }
        return roleTreeNodeVOList;
    }

    @Override
    public TreeTypeEnum getTreeType() {
        return TreeTypeEnum.ORG;
    }
}
