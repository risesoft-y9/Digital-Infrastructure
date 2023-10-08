package net.risesoft.service.init.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.DefaultIdConsts;
import net.risesoft.service.dictionary.Y9OptionClassService;
import net.risesoft.service.init.InitTenantDataService;
import net.risesoft.service.org.Y9JobService;
import net.risesoft.service.org.Y9ManagerService;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author shidaobang
 * @date 2023/10/08
 * @since 9.6.3
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class InitTenantDataServiceImpl implements InitTenantDataService {

    private final Y9OrganizationService y9OrganizationService;
    private final Y9ManagerService y9ManagerService;
    private final Y9OptionClassService y9OptionClassService;
    private final Y9JobService y9JobService;
    private final Y9PersonService y9PersonService;

    @Override
    public void init(String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        // 租户的示例数据
        // FIXME 是否需要？
        y9OrganizationService.create(tenantId, "组织", Boolean.FALSE);
        y9JobService.create("普通职位", "001");
        y9PersonService.create(tenantId, "业务用户", "user", "13511111111");

        // 新建租户三员及他们所在的虚拟组织
        y9OrganizationService.create(DefaultIdConsts.ORGANIZATION_VIRTUAL_ID, "虚拟组织", Boolean.TRUE);
        y9ManagerService.createSystemManager(DefaultIdConsts.SYSTEM_MANAGER_ID,
            DefaultIdConsts.ORGANIZATION_VIRTUAL_ID);
        y9ManagerService.createSecurityManager(DefaultIdConsts.SECURITY_MANAGER_ID,
            DefaultIdConsts.ORGANIZATION_VIRTUAL_ID);
        y9ManagerService.createAuditManager(DefaultIdConsts.AUDIT_MANAGER_ID, DefaultIdConsts.ORGANIZATION_VIRTUAL_ID);

        // 数据字典
        y9OptionClassService.initOptionClass();
    }
}
