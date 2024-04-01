package net.risesoft.manager.org;

import java.util.Optional;

import net.risesoft.entity.Y9Job;

/**
 * 职位 manager
 *
 * @author shidaobang
 * @date 2023/07/26
 * @since 9.6.3
 */
public interface Y9JobManager {

    void delete(Y9Job y9Job);

    Optional<Y9Job> findById(String id);

    /**
     * 根据id，获取职位信息（直接读取数据库）
     *
     * @param id 职位id
     * @return
     */
    Optional<Y9Job> findByIdNotCache(String id);

    Y9Job getById(String id);

    Y9Job save(Y9Job y9Job);

    Y9Job updateTabIndex(String id, int tabIndex);
}
