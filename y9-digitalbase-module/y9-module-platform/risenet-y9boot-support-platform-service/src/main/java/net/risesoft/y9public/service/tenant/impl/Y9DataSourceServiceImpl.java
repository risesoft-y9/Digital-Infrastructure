package net.risesoft.y9public.service.tenant.impl;

import static net.risesoft.consts.JpaPublicConsts.PUBLIC_TRANSACTION_MANAGER;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.google.common.collect.Maps;
import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.platform.DataSourceTypeEnum;
import net.risesoft.exception.DataSourceErrorCodeEnum;
import net.risesoft.model.platform.tenant.DataSourceInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.util.Y9PublishServiceUtil;
import net.risesoft.y9.pubsub.constant.Y9CommonEventConst;
import net.risesoft.y9.pubsub.message.Y9MessageCommon;
import net.risesoft.y9.util.Y9AssertUtil;
import net.risesoft.y9.util.base64.Y9Base64Util;
import net.risesoft.y9public.entity.tenant.Y9DataSource;
import net.risesoft.y9public.entity.tenant.Y9TenantSystem;
import net.risesoft.y9public.manager.resource.Y9SystemManager;
import net.risesoft.y9public.manager.tenant.Y9DataSourceManager;
import net.risesoft.y9public.repository.tenant.Y9DataSourceRepository;
import net.risesoft.y9public.repository.tenant.Y9TenantSystemRepository;
import net.risesoft.y9public.service.tenant.Y9DataSourceService;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service(value = "dataSourceService")
@RequiredArgsConstructor
public class Y9DataSourceServiceImpl implements Y9DataSourceService {

    private static final String DEFAULT_PASSWORD = "111111";

    private final Y9DataSourceRepository datasourceRepository;
    private final Y9TenantSystemRepository y9TenantSystemRepository;

    private final Y9DataSourceManager y9DataSourceManager;
    private final Y9SystemManager y9SystemManager;

    private final DataSourceLookup dataSourceLookup = new JndiDataSourceLookup();

    // 数据库连接是宝贵资源，dataSourceMap中的HikariDataSource在什么地方关闭？资源内存泄露？
    private final ConcurrentMap<String, HikariDataSource> dataSourceMap = Maps.newConcurrentMap();
    private final ConcurrentMap<String, Y9DataSource> y9DataSourceMap = Maps.newConcurrentMap();

    private static boolean isY9DataSourceModified(Y9DataSource cachedY9DataSource, Y9DataSource queriedY9DataSource) {
        boolean modified = false;
        if (cachedY9DataSource == null) {
            modified = true;
        } else {
            if (Objects.equals(queriedY9DataSource.getType(), DataSourceTypeEnum.HIKARI)) {
                if (!Objects.equals(cachedY9DataSource.getType(), DataSourceTypeEnum.HIKARI)) {
                    modified = true;
                }
                if (!queriedY9DataSource.getJndiName().equals(cachedY9DataSource.getJndiName())) {
                    modified = true;
                }
            } else {
                // druid
                if (!queriedY9DataSource.getUrl().equals(cachedY9DataSource.getUrl())
                    || !queriedY9DataSource.getInitialSize().equals(cachedY9DataSource.getInitialSize())
                    || !queriedY9DataSource.getMaxActive().equals(cachedY9DataSource.getMaxActive())
                    || !queriedY9DataSource.getMinIdle().equals(cachedY9DataSource.getMinIdle())
                    || !queriedY9DataSource.getUsername().equals(cachedY9DataSource.getUsername())
                    || !queriedY9DataSource.getPassword().equals(cachedY9DataSource.getPassword())
                    || !queriedY9DataSource.getType().equals(cachedY9DataSource.getType())) {
                    modified = true;
                }
            }
        }
        return modified;
    }

    @Override
    public String buildDataSourceNameWithSystemName(String shortName, String systemName) {
        return y9DataSourceManager.buildDataSourceName(shortName, systemName);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void changePassword(String id, String oldPassword, String newPassword) {
        Y9DataSource y9DataSource = y9DataSourceManager.getById(id);
        // 校验旧密码是否正确
        Y9AssertUtil.isTrue(Objects.equals(Y9Base64Util.encode(oldPassword), y9DataSource.getPassword()),
            DataSourceErrorCodeEnum.DATA_SOURCE_OLD_PASSWORD_IS_WRONG);

        y9DataSource.setPassword(Y9Base64Util.encode(newPassword));
        y9DataSourceManager.save(y9DataSource);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public DataSourceInfo createTenantDefaultDataSource(String dbName) {
        Y9DataSource y9DataSource = y9DataSourceManager.createTenantDefaultDataSourceWithId(dbName, null);
        return entityToModel(y9DataSource);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public DataSourceInfo createTenantDefaultDataSource(String dbName, String id) {
        Y9DataSource y9DataSource = y9DataSourceManager.createTenantDefaultDataSourceWithId(dbName, id);
        return entityToModel(y9DataSource);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void delete(String id) {
        y9DataSourceManager.delete(id);
    }

    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    @Override
    public void deleteAfterCheck(String id) {
        List<Y9TenantSystem> tenantlist = y9TenantSystemRepository.findByTenantDataSource(id);
        // 校验该数据源是否被使用
        Y9AssertUtil.isEmpty(tenantlist, DataSourceErrorCodeEnum.DATA_SOURCE_IS_USED);

        this.delete(id);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void dropTenantDefaultDataSource(String dataSourceId, String dbName) {
        y9DataSourceManager.dropTenantDefaultDataSource(dataSourceId, dbName);
    }

    @Override
    public Optional<DataSourceInfo> findById(String id) {
        return datasourceRepository.findById(id).map(this::entityToModel);
    }

    @Override
    public DataSourceInfo getById(String id) {
        Y9DataSource y9DataSource = y9DataSourceManager.getById(id);
        return entityToModel(y9DataSource);
    }

    @Override
    public HikariDataSource getDataSource(String id) {
        HikariDataSource dataSource = this.dataSourceMap.get(id);
        Y9DataSource queriedY9DataSource = y9DataSourceManager.getById(id);
        Y9DataSource cachedY9DataSource = y9DataSourceMap.get(id);

        if (isY9DataSourceModified(cachedY9DataSource, queriedY9DataSource)) {
            DataSourceTypeEnum type = queriedY9DataSource.getType();
            if (Objects.equals(type, DataSourceTypeEnum.JNDI)) {
                dataSource = (HikariDataSource)this.dataSourceLookup.getDataSource(queriedY9DataSource.getJndiName());
            } else {
                HikariDataSource ds = new HikariDataSource();
                if (StringUtils.isNotBlank(queriedY9DataSource.getDriver())) {
                    ds.setDriverClassName(queriedY9DataSource.getDriver());
                }
                ds.setMaximumPoolSize(queriedY9DataSource.getMaxActive());
                ds.setMinimumIdle(queriedY9DataSource.getMinIdle());
                ds.setJdbcUrl(queriedY9DataSource.getUrl());
                ds.setUsername(queriedY9DataSource.getUsername());
                ds.setPassword(Y9Base64Util.decode(queriedY9DataSource.getPassword()));
                dataSource = ds;
            }
            this.dataSourceMap.put(id, dataSource);
            this.y9DataSourceMap.put(id, queriedY9DataSource);
        }

        return dataSource;
    }

    @Override
    public Y9Page<DataSourceInfo> page(Y9PageQuery pageQuery) {
        Pageable pageable = PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize());
        Page<Y9DataSource> y9DataSourcePage = datasourceRepository.findAll(pageable);
        return Y9Page.success(pageQuery.getPage(), y9DataSourcePage.getTotalPages(),
            y9DataSourcePage.getTotalElements(), entityToModel(y9DataSourcePage.getContent()));
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void resetDefaultPassword(String id) {
        Y9DataSource y9DataSource = y9DataSourceManager.getById(id);
        // 数据源类型不能为 jndi 才能修改密码
        Y9AssertUtil.isNotTrue(Objects.equals(y9DataSource.getType(), DataSourceTypeEnum.JNDI),
            DataSourceErrorCodeEnum.JNDI_DATA_SOURCE_RESET_PASSWORD_NOT_ALLOWED);

        y9DataSource.setPassword(DEFAULT_PASSWORD);
        y9DataSourceManager.save(y9DataSource);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public DataSourceInfo save(DataSourceInfo dataSourceInfo) {
        Y9DataSource y9DataSource = PlatformModelConvertUtil.convert(dataSourceInfo, Y9DataSource.class);
        return entityToModel(y9DataSourceManager.save(y9DataSource));
    }

    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    @Override
    public DataSourceInfo saveAndPublishMessage(DataSourceInfo y9DataSource) {
        DataSourceInfo savedY9DataSource = this.save(y9DataSource);

        // 只需要发送给使用了此数据源的系统
        List<Y9TenantSystem> tenantSystemList =
            y9TenantSystemRepository.findByTenantDataSource(savedY9DataSource.getId());

        // 注册事务同步器，在事务提交后做某些操作
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    for (Y9TenantSystem y9TenantSystem : tenantSystemList) {
                        Y9MessageCommon event = new Y9MessageCommon();
                        event.setEventTarget(y9SystemManager.getByIdFromCache(y9TenantSystem.getSystemId()).getName());
                        event.setEventObject(Y9CommonEventConst.TENANT_DATASOURCE_SYNC);
                        event.setEventType(Y9CommonEventConst.TENANT_DATASOURCE_SYNC);
                        Y9PublishServiceUtil.publishMessageCommon(event);
                    }
                }
            });
        }

        return savedY9DataSource;
    }

    private List<DataSourceInfo> entityToModel(List<Y9DataSource> y9DataSourceList) {
        return PlatformModelConvertUtil.convert(y9DataSourceList, DataSourceInfo.class);
    }

    private DataSourceInfo entityToModel(Y9DataSource y9DataSource) {
        return PlatformModelConvertUtil.convert(y9DataSource, DataSourceInfo.class);
    }

}
