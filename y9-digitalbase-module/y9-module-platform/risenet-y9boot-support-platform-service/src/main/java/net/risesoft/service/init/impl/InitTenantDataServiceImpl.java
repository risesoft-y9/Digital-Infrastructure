package net.risesoft.service.init.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.consts.OptionClassConsts;
import net.risesoft.entity.dictionary.Y9OptionClass;
import net.risesoft.entity.org.Y9Job;
import net.risesoft.entity.org.Y9Manager;
import net.risesoft.entity.org.Y9Organization;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.service.dictionary.Y9OptionClassService;
import net.risesoft.service.dictionary.Y9OptionValueService;
import net.risesoft.service.init.InitTenantDataService;
import net.risesoft.service.org.Y9JobService;
import net.risesoft.service.org.Y9ManagerService;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.service.relation.Y9PersonsToPositionsService;

/**
 * @author shidaobang
 * @date 2023/10/08
 * @since 9.6.3
 */
@Transactional(value = "rsTenantTransactionManager")
@Service
@RequiredArgsConstructor
public class InitTenantDataServiceImpl implements InitTenantDataService {

    private final Y9OrganizationService y9OrganizationService;
    private final Y9ManagerService y9ManagerService;
    private final Y9OptionClassService y9OptionClassService;
    private final Y9OptionValueService y9OptionValueService;
    private final Y9JobService y9JobService;
    private final Y9PersonService y9PersonService;
    private final Y9PositionService y9PositionService;
    private final Y9PersonsToPositionsService y9PersonsToPositionsService;

    public void creatOptionValue(String type) {
        if (OptionClassConsts.DUTY.equals(type)) {
            createDutyValues(type);
            return;
        }

        if (OptionClassConsts.DUTY_LEVEL.equals(type)) {
            createDutyLevelValues(type);
            return;
        }

        if (OptionClassConsts.DUTY_TYPE.equals(type)) {
            createDutyTypeValues(type);
            return;
        }

        if (OptionClassConsts.OFFICIAL_TYPE.equals(type)) {
            createOfficialTypeValues(type);
            return;
        }

        if (OptionClassConsts.ORGANIZATION_TYPE.equals(type)) {
            createOrganizationTypeValues(type);
            return;
        }

        if (OptionClassConsts.PRINCIPAL_ID_TYPE.equals(type)) {
            createPrincipalIDTypeValues(type);
            return;
        }

        if (OptionClassConsts.DEPARTMENT_PROP_CATEGORY.equals(type)) {
            createDepartmentPropCategoryValues(type);
            return;
        }

        if (OptionClassConsts.DATA_CATALOG_TREE_TYPE.equals(type)) {
            createDataCatalogTreeType(type);
            return;
        }
    }

    private void createDataCatalogTreeType(String type) {
        y9OptionValueService.create("normal", "常规", type);
    }

    private void createAuditManager(String parentId) {
        if (!y9ManagerService.existsByLoginName(InitDataConsts.DEFAULT_AUDIT_MANAGER)) {
            Y9Manager auditManager = new Y9Manager();
            auditManager.setParentId(parentId);
            auditManager.setName(ManagerLevelEnum.AUDIT_MANAGER.getName());
            auditManager.setLoginName(InitDataConsts.DEFAULT_AUDIT_MANAGER);
            auditManager.setGlobalManager(true);
            auditManager.setManagerLevel(ManagerLevelEnum.AUDIT_MANAGER);
            auditManager.setUserHostIp("");
            auditManager.setLastReviewLogTime(new Date());
            auditManager.setLastModifyPasswordTime(new Date());
            y9ManagerService.saveOrUpdate(auditManager);
        }
    }

    private void createDepartmentPropCategoryValues(String type) {
        y9OptionValueService.create("1", "管理员", type);
        y9OptionValueService.create("2", "主管领导", type);
        y9OptionValueService.create("3", "部门领导", type);
        y9OptionValueService.create("4", "副部门领导", type);
        y9OptionValueService.create("5", "部门收发员", type);
        y9OptionValueService.create("6", "秘书", type);
    }

    private void createDutyLevelValues(String type) {
        y9OptionValueService.create("1", "国家主席、副主席、总理级", type);
        y9OptionValueService.create("2", "副总理、国务委员级", type);
        y9OptionValueService.create("3", "部、省级", type);
        y9OptionValueService.create("5", "司、局、地厅级", type);
        y9OptionValueService.create("6", "副司、副局、副地、副厅级", type);
        y9OptionValueService.create("7", "县、处级", type);
        y9OptionValueService.create("8", "副县、副处级", type);
        y9OptionValueService.create("9", "正科级", type);
        y9OptionValueService.create("10", "副科级", type);
        y9OptionValueService.create("11", "科员级", type);
        y9OptionValueService.create("12", "办事员级", type);
        y9OptionValueService.create("14", "县长", type);
    }

    private void createDutyTypeValues(String type) {
        y9OptionValueService.create("1", "领导", type);
        y9OptionValueService.create("2", "公务员", type);
    }

    private void createDutyValues(String type) {
        y9OptionValueService.create("001A", "书记", type);
        y9OptionValueService.create("001B", "副书记", type);
        y9OptionValueService.create("002A", "委员", type);
        y9OptionValueService.create("002K", "常务委员", type);
        y9OptionValueService.create("004A", "主任", type);
        y9OptionValueService.create("004B", "副主任", type);
        y9OptionValueService.create("010A", "巡视员", type);
        y9OptionValueService.create("011A", "调研员", type);
        y9OptionValueService.create("011B", "副调研员", type);
        y9OptionValueService.create("015A", "纪检员", type);
        y9OptionValueService.create("083Q", "总工程师", type);
        y9OptionValueService.create("083R", "副总工程师", type);
        y9OptionValueService.create("102A", "秘书长", type);
        y9OptionValueService.create("102B", "副秘书长", type);
        y9OptionValueService.create("105A", "检察长", type);
        y9OptionValueService.create("105B", "副检察长", type);
        y9OptionValueService.create("201A", "主席", type);
        y9OptionValueService.create("201B", "副主席", type);
        y9OptionValueService.create("211A", "部长", type);
        y9OptionValueService.create("211B", "副部长", type);
        y9OptionValueService.create("211J", "部长助理", type);
        y9OptionValueService.create("212A", "审计长", type);
        y9OptionValueService.create("212B", "副审计长", type);
        y9OptionValueService.create("214A", "署长", type);
        y9OptionValueService.create("214B", "副署长", type);
        y9OptionValueService.create("215A", "关长", type);
        y9OptionValueService.create("215B", "副关长", type);
        y9OptionValueService.create("216A", "局长", type);
        y9OptionValueService.create("216B", "副局长", type);
        y9OptionValueService.create("219A", "处长", type);
        y9OptionValueService.create("219B", "副处长", type);
        y9OptionValueService.create("220A", "科长", type);
        y9OptionValueService.create("220B", "副科长", type);
        y9OptionValueService.create("221S", "主任科员", type);
        y9OptionValueService.create("221T", "副主任科员", type);
        y9OptionValueService.create("221A", "科员", type);
        y9OptionValueService.create("224A", "办事员", type);
        y9OptionValueService.create("251A", "省长", type);
        y9OptionValueService.create("251B", "副省长", type);
        y9OptionValueService.create("252A", "市长", type);
        y9OptionValueService.create("252B", "副市长", type);
        y9OptionValueService.create("252J", "市长助理", type);
        y9OptionValueService.create("254A", "州长", type);
        y9OptionValueService.create("254B", "副州长", type);
        y9OptionValueService.create("255A", "区长", type);
        y9OptionValueService.create("255B", "副区长", type);
        y9OptionValueService.create("256A", "盟长", type);
        y9OptionValueService.create("256B", "副盟长", type);
        y9OptionValueService.create("257A", "专员", type);
        y9OptionValueService.create("257B", "副专员", type);
        y9OptionValueService.create("258A", "县长", type);
        y9OptionValueService.create("258B", "副县长", type);
        y9OptionValueService.create("259A", "旗长", type);
        y9OptionValueService.create("259B", "副旗长", type);
        y9OptionValueService.create("260A", "镇长", type);
        y9OptionValueService.create("260B", "副镇长", type);
        y9OptionValueService.create("261A", "乡长", type);
        y9OptionValueService.create("261B", "副乡长", type);
        y9OptionValueService.create("262A", "村长", type);
        y9OptionValueService.create("262B", "副村长", type);
        y9OptionValueService.create("410A", "会长", type);
        y9OptionValueService.create("410B", "副会长", type);
        y9OptionValueService.create("405A", "理事长", type);
        y9OptionValueService.create("405B", "副理事长", type);
        y9OptionValueService.create("416A", "所长", type);
        y9OptionValueService.create("416B", "副所长", type);
        y9OptionValueService.create("417A", "院长", type);
        y9OptionValueService.create("417B", "副院长", type);
        y9OptionValueService.create("418A", "校长", type);
        y9OptionValueService.create("418B", "副校长", type);
        y9OptionValueService.create("438A", "站长", type);
        y9OptionValueService.create("438B", "副站长", type);
        y9OptionValueService.create("801A", "参谋长", type);
        y9OptionValueService.create("802A", "参谋", type);
        y9OptionValueService.create("851A", "政委", type);
        y9OptionValueService.create("840A", "大队长", type);
        y9OptionValueService.create("840B", "副大队长", type);
        y9OptionValueService.create("841A", "队长", type);
        y9OptionValueService.create("841B", "副队长", type);
        y9OptionValueService.create("842A", "负责人", type);
    }

    private void createOfficialTypeValues(String type) {
        y9OptionValueService.create("1", "公务员", type);
        y9OptionValueService.create("2", "行政编", type);
        y9OptionValueService.create("3", "事业编", type);
        y9OptionValueService.create("4", "企业编", type);
        y9OptionValueService.create("5", "其他", type);
    }

    @Transactional(readOnly = false)
    public void createOptionClass(String className, String type) {
        Optional<Y9OptionClass> optionClass = y9OptionClassService.findByType(type);
        if (!optionClass.isPresent()) {
            Y9OptionClass option = new Y9OptionClass();
            option.setName(className);
            option.setType(type);
            y9OptionClassService.saveOptionClass(option);
            creatOptionValue(type);
        }
    }

    private void createOrganizationTypeValues(String type) {
        y9OptionValueService.create("11", "公司", type);
        y9OptionValueService.create("13", "非公司制企业法人", type);
        y9OptionValueService.create("15", "企业分支机构", type);
        y9OptionValueService.create("17", "个人独资企业、合伙企业", type);
        y9OptionValueService.create("19", "其它企业", type);
        y9OptionValueService.create("31", "中国共产党", type);
        y9OptionValueService.create("32", "国家权力机关法人", type);
        y9OptionValueService.create("33", "国家行政机关法人", type);
        y9OptionValueService.create("34", "国家司法机关法人", type);
        y9OptionValueService.create("35", "政协组织", type);
        y9OptionValueService.create("36", "民主党派", type);
        y9OptionValueService.create("37", "人民解放军、武警部队", type);
        y9OptionValueService.create("39", "其他机关", type);
        y9OptionValueService.create("51", "事业单位法人", type);
        y9OptionValueService.create("53", "事业单位分支、派出机构", type);
        y9OptionValueService.create("59", "其它事业单位", type);
        y9OptionValueService.create("71", "社会团体法人", type);
        y9OptionValueService.create("73", "社会团体分支、代表机构", type);
        y9OptionValueService.create("79", "其它社会团体", type);
        y9OptionValueService.create("91", "民办非企业单位", type);
        y9OptionValueService.create("93", "基金会", type);
        y9OptionValueService.create("94", "宗教活动场所", type);
        y9OptionValueService.create("95", "农村居民委员会", type);
        y9OptionValueService.create("96", "城市居民委员会", type);
        y9OptionValueService.create("97", "自定义区", type);
        y9OptionValueService.create("99", "其它", type);
    }

    private void createPrincipalIDTypeValues(String type) {
        y9OptionValueService.create("10", "身份证", type);
        y9OptionValueService.create("11", "护照", type);
        y9OptionValueService.create("12", "户口簿", type);
        y9OptionValueService.create("13", "军人证", type);
    }

    private void createSecurityManager(String parentId) {
        if (!y9ManagerService.existsByLoginName(InitDataConsts.DEFAULT_SECURITY_MANAGER)) {
            Y9Manager securityManager = new Y9Manager();
            securityManager.setParentId(parentId);
            securityManager.setName(ManagerLevelEnum.SECURITY_MANAGER.getName());
            securityManager.setLoginName(InitDataConsts.DEFAULT_SECURITY_MANAGER);
            securityManager.setGlobalManager(true);
            securityManager.setManagerLevel(ManagerLevelEnum.SECURITY_MANAGER);
            securityManager.setUserHostIp("");
            securityManager.setLastReviewLogTime(new Date());
            securityManager.setLastModifyPasswordTime(new Date());
            y9ManagerService.saveOrUpdate(securityManager);
        }
    }

    private void createSystemManager(String parentId) {
        if (!y9ManagerService.existsByLoginName(InitDataConsts.DEFAULT_SYSTEM_MANAGER)) {
            Y9Manager systemManager = new Y9Manager();
            systemManager.setParentId(parentId);
            systemManager.setName(ManagerLevelEnum.SYSTEM_MANAGER.getName());
            systemManager.setLoginName(InitDataConsts.DEFAULT_SYSTEM_MANAGER);
            systemManager.setGlobalManager(true);
            systemManager.setManagerLevel(ManagerLevelEnum.SYSTEM_MANAGER);
            systemManager.setUserHostIp("");
            systemManager.setLastReviewLogTime(new Date());
            systemManager.setLastModifyPasswordTime(new Date());
            y9ManagerService.saveOrUpdate(systemManager);
        }
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void initAll(String tenantId) {
        // 租户必要的数据
        this.initOptionClass();
        this.initManagers();

        // 租户的示例数据
        // FIXME 是否需要？
        this.initOrg();
    }

    @Override
    public void initManagers() {
        // 新建租户三员及他们所在的虚拟组织
        boolean virtualOrganizationNotExists = y9OrganizationService.list(true, false).isEmpty();
        if (virtualOrganizationNotExists) {
            Y9Organization y9Organization = y9OrganizationService.create("虚拟组织", Boolean.TRUE);
            createSystemManager(y9Organization.getId());
            createSecurityManager(y9Organization.getId());
            createAuditManager(y9Organization.getId());
        }
    }

    @Override
    public void initOptionClass() {
        createOptionClass("职务", OptionClassConsts.DUTY);
        createOptionClass("职级", OptionClassConsts.DUTY_LEVEL);
        createOptionClass("编制类型", OptionClassConsts.OFFICIAL_TYPE);
        createOptionClass("机构类型", OptionClassConsts.ORGANIZATION_TYPE);
        createOptionClass("证件类型", OptionClassConsts.PRINCIPAL_ID_TYPE);
        createOptionClass("部门属性类型", OptionClassConsts.DEPARTMENT_PROP_CATEGORY);
        createOptionClass("数据目录树类型", OptionClassConsts.DATA_CATALOG_TREE_TYPE);
    }

    private void initOrg() {
        boolean organizationNotExists = y9OrganizationService.list(false, false).isEmpty();
        if (organizationNotExists) {
            Y9Job y9Job = y9JobService.create("无", "001");

            Y9Organization y9Organization = y9OrganizationService.create("组织", Boolean.FALSE);

            Y9Person y9Person = y9PersonService.create(y9Organization.getId(), "业务用户", "user", "13511111111");

            Y9Position y9Position = y9PositionService.create(y9Organization.getId(), y9Job.getId());

            y9PersonsToPositionsService.addPositions(y9Person.getId(), new String[] {y9Position.getId()});
        }
    }
}
