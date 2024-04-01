package net.risesoft.manager.org;

import java.util.Optional;

import net.risesoft.entity.Y9Organization;

/**
 * 组织 manager
 *
 * @author shidaobang
 * @date 2023/07/26
 * @since 9.6.3
 */
public interface Y9OrganizationManager {

    void delete(Y9Organization y9Organization);

    Optional<Y9Organization> findById(String id);

    /**
     * 根据id，获取组织机构信息（直接读取数据库）
     *
     * @param id 组织机构id
     * @return
     */
    Optional<Y9Organization> findByIdNotCache(String id);

    Y9Organization getById(String id);

    Y9Organization save(Y9Organization y9Organization);

    /**
     * 保存或者更新组织机构扩展信息
     *
     * @param id 组织机构id
     * @param properties 扩展属性
     * @return {@link Y9Organization}
     */
    Y9Organization saveProperties(String id, String properties);

    /**
     * 更新组织机构排序
     *
     * @param id 组织机构id
     * @param tabIndex 排序
     * @return
     */
    Y9Organization updateTabIndex(String id, int tabIndex);
}
