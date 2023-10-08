package net.risesoft.y9public.service.tenant.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.OrgLevelConsts;
import net.risesoft.exception.TenantErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.y9.util.Y9Assert;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9public.entity.tenant.Y9DataSource;
import net.risesoft.y9public.entity.tenant.Y9Tenant;
import net.risesoft.y9public.manager.tenant.Y9DataSourceManager;
import net.risesoft.y9public.manager.tenant.Y9TenantManager;
import net.risesoft.y9public.repository.tenant.Y9TenantRepository;
import net.risesoft.y9public.service.tenant.Y9TenantService;
import net.risesoft.y9public.service.user.Y9UserService;
import net.risesoft.y9public.specification.Y9TenantSpecification;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service(value = "tenantService")
@Transactional(value = "rsPublicTransactionManager")
@Slf4j
@RequiredArgsConstructor
public class Y9TenantServiceImpl implements Y9TenantService {

    private final Y9TenantRepository y9TenantRepository;

    private final Y9UserService y9UserService;
    private final Y9DataSourceManager y9DataSourceManager;
    private final Y9TenantManager y9TenantManager;

    @Override
    @Transactional(readOnly = false)
    public Y9Tenant changDefaultDataSourceId(String id, String datasourceId) {
        Y9Tenant y9Tenant = this.getById(id);
        y9Tenant.setDefaultDataSourceId(datasourceId);
        return y9TenantRepository.save(y9Tenant);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Tenant changeDisabled(String id) {
        Y9Tenant y9Tenant = this.getById(id);
        y9Tenant.setEnabled(!y9Tenant.getEnabled());
        return y9TenantRepository.save(y9Tenant);
    }

    @Override
    public long countByShortName(String shortName) {
        return y9TenantRepository.countByShortName(shortName);
    }

    @Override
    public long countByShortNameAndIdIsNot(String shortName, String tenantId) {
        return y9TenantRepository.countByShortNameAndIdIsNot(shortName, tenantId);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Tenant createTenant(String tenantName, String tenantShortName, String dataSourceId) {
        Y9Tenant y9Tenant = new Y9Tenant();
        y9Tenant.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        y9Tenant.setName(tenantName);
        y9Tenant.setShortName(tenantShortName);
        y9Tenant.setTenantType(3);
        y9Tenant.setEnabled(Boolean.TRUE);
        y9Tenant.setDefaultDataSourceId(dataSourceId);
        Integer maxTabIndex = getMaxTableIndex();
        y9Tenant.setTabIndex(maxTabIndex);
        return save(y9Tenant);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        // TODO 删除关联数据 deleteToResourceAndRoleNode(id);
        y9UserService.deleteByTenantId(id);
        y9TenantRepository.deleteById(id);
    }

    @Override
    public Optional<Y9Tenant> findById(String id) {
        return y9TenantRepository.findById(id);
    }

    @Override
    public Y9Tenant getById(String id) {
        return y9TenantManager.getById(id);
    }

    @Override
    public Integer getMaxTableIndex() {
        return y9TenantRepository.findTopByOrderByTabIndexDesc().map(y9Tenant -> y9Tenant.getTabIndex() + 1).orElse(0);
    }

    @Override
    public List<Y9Tenant> listAll() {
        return y9TenantRepository.findAll(Sort.by(Direction.DESC, "tenantType"));
    }

    @Override
    public List<Y9Tenant> listByGuidPathLike(String guidPath) {
        return y9TenantRepository.findByGuidPathContaining(guidPath);
    }

    @Override
    public List<Y9Tenant> listByParentIdAndTenantType(String parentId, Integer tenantType) {
        if (StringUtils.isBlank(parentId)) {
            return y9TenantRepository.findByTenantTypeAndParentIdIsNullOrderByTabIndexAsc(tenantType);
        }
        return y9TenantRepository.findByParentIdOrderByTabIndexAsc(parentId);
    }

    @Override
    public Optional<Y9Tenant> findByShortName(String shortName) {
        return y9TenantRepository.findByShortName(shortName);
    }

    @Override
    public Optional<Y9Tenant> findByTenantName(String tenantName) {
        return y9TenantRepository.findByName(tenantName);
    }

    @Override
    public List<Y9Tenant> listByTenantType(Integer tenantType) {
        return y9TenantRepository.findByTenantTypeOrderByTabIndexAsc(tenantType);
    }

    @Override
    public List<Y9Tenant> listByTenantType(String name, Integer tenantType) {
        Y9TenantSpecification<Y9Tenant> spec = new Y9TenantSpecification<>(name, null, tenantType);
        return y9TenantRepository.findAll(spec, Sort.by(Direction.ASC, "tabIndex"));
    }

    @Override
    public List<Y9Tenant> listByTenantTypeIn(Integer tenantType, Integer tenantType2) {
        List<Integer> types = new ArrayList<>();
        if (null != tenantType) {
            types.add(tenantType);
        }
        if (null != tenantType2) {
            types.add(tenantType2);
        }
        return y9TenantRepository.findByTenantTypeIn(types);
    }

    @Override
    @Transactional(readOnly = false)
    public void move(String id, String parentId) {
        Y9Tenant y9Tenant = this.getById(id);

        if (StringUtils.isNotBlank(parentId)) {
            List<Y9Tenant> tenants = this.listByGuidPathLike(y9Tenant.getGuidPath());
            List<String> tenantIds = tenants.stream().map(Y9Tenant::getId).collect(Collectors.toList());

            // 不能将租户移动到本身或子租户中
            Y9Assert.notNull(tenantIds.contains(parentId), TenantErrorCodeEnum.MOVE_TO_SUB_TENANT_NOT_PERMITTED);
        }

        y9Tenant.setParentId(parentId);
        this.save(y9Tenant);
    }

    @Override
    public Page<Y9Tenant> page(int page, int rows) {
        Pageable pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Direction.DESC, "createTime"));
        return y9TenantRepository.findAll(pageable);
    }

    @Override
    public Page<Y9Tenant> pageByNameAndEnabled(int page, int rows, String name, Integer enabled) {
        Pageable pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Direction.ASC, "tabIndex"));
        Y9TenantSpecification<Y9Tenant> spec = new Y9TenantSpecification<>(name, enabled, null);
        return y9TenantRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Tenant save(Y9Tenant y9Tenant) {
        if (StringUtils.isBlank(y9Tenant.getId())) {
            y9Tenant.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            y9Tenant.setTabIndex(getMaxTableIndex());
        }
        String parentId = y9Tenant.getParentId();
        if (StringUtils.isNotBlank(parentId)) {
            Y9Tenant parent = this.getById(parentId);
            if (parent != null) {
                y9Tenant.setParentId(parentId);
                y9Tenant.setNamePath(parent.getNamePath() + OrgLevelConsts.SEPARATOR + y9Tenant.getShortName());
                y9Tenant.setGuidPath(parent.getGuidPath() + OrgLevelConsts.SEPARATOR + y9Tenant.getId());
            }
        } else {
            y9Tenant.setParentId(null);
            y9Tenant.setNamePath(y9Tenant.getShortName());
            y9Tenant.setGuidPath(y9Tenant.getId());
        }
        return y9TenantRepository.save(y9Tenant);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Tenant saveOrUpdate(Y9Tenant y9Tenant, Integer tenantType) {
        Y9Assert.isTrue(isNameAvailable(y9Tenant.getName(), y9Tenant.getId()), TenantErrorCodeEnum.NAME_HAS_BEEN_USED,
            y9Tenant.getName());
        Y9Assert.isTrue(isShortNameAvailable(y9Tenant.getShortName(), y9Tenant.getId()),
            TenantErrorCodeEnum.SHORT_NAME_HAS_BEEN_USED, y9Tenant.getShortName());

        if (StringUtils.isNotBlank(y9Tenant.getId())) {
            Y9Tenant oldTenant = y9TenantRepository.findById(y9Tenant.getId()).orElse(null);
            if (oldTenant != null) {
                Y9BeanUtil.copyProperties(y9Tenant, oldTenant);
                return save(oldTenant);
            }
        }
        y9Tenant.setTenantType(tenantType);
        Y9DataSource y9DataSource = null;
        try {
            y9DataSource = y9DataSourceManager.createTenantDefaultDataSource(y9Tenant.getShortName(), tenantType, null);
            y9Tenant.setDefaultDataSourceId(y9DataSource.getId());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            if (y9DataSource != null) {
                y9DataSourceManager.dropTenantDefaultDataSource(y9DataSource.getId(), y9Tenant.getShortName());
            }
        }
        return save(y9Tenant);
    }

    private boolean isShortNameAvailable(String shortName, String id) {
        Optional<Y9Tenant> y9TenantOptional = y9TenantRepository.findByShortName(shortName);

        if (y9TenantOptional.isEmpty()) {
            // 不存在同名的租户肯定可用
            return true;
        }

        // 编辑租户时没修改英文名称同样认为可用
        return y9TenantOptional.get().getId().equals(id);
    }

    private boolean isNameAvailable(String name, String id) {
        Optional<Y9Tenant> y9TenantOptional = y9TenantRepository.findByName(name);

        if (y9TenantOptional.isEmpty()) {
            // 不存在同名的租户肯定可用
            return true;
        }

        // 编辑租户时没修改中文名称同样认为可用
        return y9TenantOptional.get().getId().equals(id);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveTenantOrders(String[] tenantIds) {
        for (int i = 0; i < tenantIds.length; i++) {
            Y9Tenant y9Tenant = this.getById(tenantIds[i]);
            y9Tenant.setTabIndex(i);
            y9TenantRepository.save(y9Tenant);
        }
    }

}
