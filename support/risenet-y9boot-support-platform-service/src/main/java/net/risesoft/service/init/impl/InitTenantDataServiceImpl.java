package net.risesoft.service.init.impl;

import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.entity.Y9Manager;
import net.risesoft.entity.Y9OptionClass;
import net.risesoft.entity.Y9Organization;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.service.dictionary.Y9OptionClassService;
import net.risesoft.service.dictionary.Y9OptionValueService;
import net.risesoft.service.init.InitTenantDataService;
import net.risesoft.service.org.Y9JobService;
import net.risesoft.service.org.Y9ManagerService;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.service.org.Y9PersonService;

/**
 * @author shidaobang
 * @date 2023/10/08
 * @since 9.6.3
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class InitTenantDataServiceImpl implements InitTenantDataService {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private final Y9OrganizationService y9OrganizationService;
    private final Y9ManagerService y9ManagerService;
    private final Y9OptionClassService y9OptionClassService;
    private final Y9OptionValueService y9OptionValueService;
    private final Y9JobService y9JobService;
    private final Y9PersonService y9PersonService;

    @Override
    @Transactional(readOnly = false)
    public void initAll(String tenantId) {
        // 租户的示例数据
        // FIXME 是否需要？
        this.initJob();
        this.initOrgUnit();

        // 租户必要的数据
        this.initOptionClass();
        this.initManagers();
    }

    private void initOrgUnit() {
        boolean organizationNotExists = y9OrganizationService.list(false).isEmpty();
        if (organizationNotExists) {
            Y9Organization y9Organization = y9OrganizationService.create("组织", Boolean.FALSE);
            y9PersonService.create(y9Organization.getId(), "业务用户", "user", "13511111111");
        }
    }

    private void initJob() {
        boolean jobNotExists = y9JobService.count() == 0;
        if (jobNotExists) {
            y9JobService.create("普通职位", "001");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void initManagers() {
        // 新建租户三员及他们所在的虚拟组织
        boolean virtualOrganizationNotExists = y9OrganizationService.list(true).isEmpty();
        if (virtualOrganizationNotExists) {
            Y9Organization y9Organization = y9OrganizationService.create("虚拟组织", Boolean.TRUE);
            createSystemManager(y9Organization.getId());
            createSecurityManager(y9Organization.getId());
            createAuditManager(y9Organization.getId());
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void initOptionClass() {
        if (!y9OptionClassService.hasData()) {
            // 有数据不再进行初始化
            createOptionClass("职务", "duty");
            createOptionClass("职级", "dutyLevel");
            createOptionClass("编制类型", "officialType");
            createOptionClass("机构类型", "organizationType");
            createOptionClass("证件类型", "principalIDType");
        }
    }

    @Transactional(readOnly = false)
    public void createOptionClass(String className, String type) {
        Y9OptionClass option = new Y9OptionClass();
        option.setName(className);
        option.setType(type);
        y9OptionClassService.saveOptionClass(option);
        creatOptionValue(type);
    }

    public void creatOptionValue(String type) {
        if ("duty".equals(type)) {
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
            return;
        }

        if ("dutyLevel".equals(type)) {
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
            return;
        }

        if ("dutyType".equals(type)) {
            y9OptionValueService.create("1", "领导", type);
            y9OptionValueService.create("2", "公务员", type);
            return;
        }

        if ("officialType".equals(type)) {
            y9OptionValueService.create("1", "公务员", type);
            y9OptionValueService.create("2", "行政编", type);
            y9OptionValueService.create("3", "事业编", type);
            y9OptionValueService.create("4", "企业编", type);
            y9OptionValueService.create("5", "其他", type);
            return;
        }

        if ("organizationType".equals(type)) {
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
            return;
        }

        if ("principalIDType".equals(type)) {
            y9OptionValueService.create("10", "身份证", type);
            y9OptionValueService.create("11", "护照", type);
            y9OptionValueService.create("12", "户口簿", type);
            y9OptionValueService.create("13", "军人证", type);
            return;
        }
    }

    private void createAuditManager(String parentId) {
        if (!y9ManagerService.existsByLoginName(InitDataConsts.DEFAULT_AUDIT_MANAGER)) {
            Y9Manager auditManager = new Y9Manager();
            auditManager.setParentId(parentId);
            auditManager.setName(ManagerLevelEnum.AUDIT_MANAGER.getName());
            auditManager.setLoginName(InitDataConsts.DEFAULT_AUDIT_MANAGER);
            auditManager.setGlobalManager(true);
            auditManager.setManagerLevel(ManagerLevelEnum.AUDIT_MANAGER);
            auditManager.setPwdCycle(Y9Manager.DEFAULT_PWD_CYCLE);
            auditManager.setUserHostIp("");
            auditManager.setCheckTime(DATE_FORMAT.format(new Date()));
            auditManager.setModifyPwdTime(DATE_FORMAT.format(new Date()));
            auditManager.setCheckCycle(Y9Manager.DEFAULT_PWD_CYCLE);
            y9ManagerService.saveOrUpdate(auditManager);
        }
    }

    private void createSecurityManager(String parentId) {
        if (!y9ManagerService.existsByLoginName(InitDataConsts.DEFAULT_SECURITY_MANAGER)) {
            Y9Manager securityManager = new Y9Manager();
            securityManager.setParentId(parentId);
            securityManager.setName(ManagerLevelEnum.SECURITY_MANAGER.getName());
            securityManager.setLoginName(InitDataConsts.DEFAULT_SECURITY_MANAGER);
            securityManager.setGlobalManager(true);
            securityManager.setManagerLevel(ManagerLevelEnum.SECURITY_MANAGER);
            securityManager.setPwdCycle(Y9Manager.DEFAULT_PWD_CYCLE);
            securityManager.setUserHostIp("");
            securityManager.setCheckTime(DATE_FORMAT.format(new Date()));
            securityManager.setModifyPwdTime(DATE_FORMAT.format(new Date()));
            securityManager.setCheckCycle(Y9Manager.DEFAULT_PWD_CYCLE);
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
            systemManager.setPwdCycle(Y9Manager.DEFAULT_PWD_CYCLE);
            systemManager.setUserHostIp("");
            systemManager.setCheckTime(DATE_FORMAT.format(new Date()));
            systemManager.setModifyPwdTime(DATE_FORMAT.format(new Date()));
            systemManager.setCheckCycle(0);
            y9ManagerService.saveOrUpdate(systemManager);
        }
    }
}
