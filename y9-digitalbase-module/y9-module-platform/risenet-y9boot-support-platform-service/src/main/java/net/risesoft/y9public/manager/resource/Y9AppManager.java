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

    Optional<Y9App> findById(String id);

    Optional<Y9App> findByIdFromCache(String id);

    Y9App getById(String id);

    Y9App getByIdFromCache(String id);

    Y9App insert(Y9App y9App);

    Y9App update(Y9App y9App);

    Y9App updateTabIndex(String id, int index);
}
