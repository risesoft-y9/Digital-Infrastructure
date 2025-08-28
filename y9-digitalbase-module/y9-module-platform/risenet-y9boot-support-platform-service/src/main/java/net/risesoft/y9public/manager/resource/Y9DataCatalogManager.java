package net.risesoft.y9public.manager.resource;

import java.util.Optional;

import net.risesoft.y9public.entity.resource.Y9DataCatalog;

/**
 * 数据目录 Manager
 *
 * @author shidaobang
 * @date 2025/08/28
 */
public interface Y9DataCatalogManager {

    Optional<Y9DataCatalog> findById(String id);

    Optional<Y9DataCatalog> findByIdFromCache(String id);

    Y9DataCatalog getById(String id);

    Y9DataCatalog getByIdFromCache(String id);

    Y9DataCatalog insert(Y9DataCatalog y9DataCatalog);

    Y9DataCatalog update(Y9DataCatalog y9DataCatalog);
}
