package net.risesoft.y9public.manager.resource;

import java.util.Optional;

import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9public.entity.resource.Y9DataCatalog;

/**
 * 数据目录 Manager
 *
 * @author shidaobang
 * @date 2025/08/28
 */
public interface Y9DataCatalogManager {

    /**
     * 根据id获取数据目录信息（直接读取数据库）
     *
     * @param id 数据目录id
     * @return {@code Optional<Y9DataCatalog>}
     */
    Optional<Y9DataCatalog> findById(String id);

    /**
     * 根据id获取数据目录信息
     *
     * @param id 数据目录id
     * @return {@code Optional<Y9DataCatalog>}
     */
    Optional<Y9DataCatalog> findByIdFromCache(String id);

    /**
     * 根据id获取数据目录信息（直接读取数据库）
     *
     * @param id 数据目录id
     * @return {@link Y9DataCatalog}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9DataCatalog getById(String id);

    /**
     * 根据id获取数据目录信息
     *
     * @param id 数据目录id
     * @return {@link Y9DataCatalog}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9DataCatalog getByIdFromCache(String id);

    /**
     * 新增数据目录
     *
     * @param y9DataCatalog 数据目录信息
     * @return {@link Y9DataCatalog}
     */
    Y9DataCatalog insert(Y9DataCatalog y9DataCatalog);

    /**
     * 更新数据目录
     *
     * @param y9DataCatalog 数据目录信息
     * @param originalDataCatalog 原数据目录信息
     * @return {@link Y9DataCatalog}
     */
    Y9DataCatalog update(Y9DataCatalog y9DataCatalog, Y9DataCatalog originalDataCatalog);
}
