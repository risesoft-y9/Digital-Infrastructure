package net.risesoft.manager.org;

import java.util.Optional;

import net.risesoft.entity.org.Y9Organization;
import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * 组织 manager
 *
 * @author shidaobang
 * @date 2023/07/26
 * @since 9.6.3
 */
public interface Y9OrganizationManager {

    void delete(Y9Organization y9Organization);

    Optional<Y9Organization> findByIdFromCache(String id);

    /**
     * 根据id，获取组织机构信息（直接读取数据库）
     *
     * @param id 组织机构id
     * @return {@code Optional<Y9Organization>}
     */
    Optional<Y9Organization> findById(String id);

    /**
     * 根据id，获取组织机构信息
     *
     * @param id 组织机构id
     * @return {@link Y9Organization}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Organization getByIdFromCache(String id);

    /**
     * 根据id，获取组织机构信息（直接读取数据库）
     *
     * @param id 组织机构id
     * @return {@link Y9Organization}
     */
    Y9Organization getById(String id);

    Y9Organization insert(Y9Organization organization);

    Y9Organization update(Y9Organization organization);

    /**
     * 更新组织机构排序
     *
     * @param id 组织机构id
     * @param tabIndex 排序
     * @return {@link Y9Organization}
     */
    Y9Organization updateTabIndex(String id, int tabIndex);
}
