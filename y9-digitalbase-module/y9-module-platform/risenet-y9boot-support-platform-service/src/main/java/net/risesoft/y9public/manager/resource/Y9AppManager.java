package net.risesoft.y9public.manager.resource;

import java.util.Optional;

import net.risesoft.y9public.entity.resource.Y9App;

/**
 * 应用 Manager
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
public interface Y9AppManager {
    void delete(String id);

    void deleteBySystemId(String systemId);

    /**
     * 删除相关租户数据 <br>
     * 切换不同的数据源 需开启新事务
     *
     * @param appId 应用id
     */
    void deleteTenantRelatedByAppId(String appId);

    Optional<Y9App> findById(String id);

    Y9App getById(String id);

    Y9App save(Y9App y9App);

    Y9App updateTabIndex(String id, int index);
}
