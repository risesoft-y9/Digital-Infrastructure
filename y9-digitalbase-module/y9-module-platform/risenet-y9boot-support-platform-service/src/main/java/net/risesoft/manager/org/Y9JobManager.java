package net.risesoft.manager.org;

import java.util.Optional;

import net.risesoft.entity.org.Y9Job;
import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * 职位 manager
 *
 * @author shidaobang
 * @date 2023/07/26
 * @since 9.6.3
 */
public interface Y9JobManager {

    void delete(Y9Job y9Job);

    Optional<Y9Job> findByIdFromCache(String id);

    /**
     * 根据id，获取职位信息（直接读取数据库）
     *
     * @param id 职位id
     * @return {@link Y9Job} 职位对象
     */
    Optional<Y9Job> findById(String id);

    /**
     * 根据id，获取职位信息
     *
     * @param id 职位id
     * @return {@link Y9Job} 职位对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Job getByIdFromCache(String id);

    /**
     * 根据id，获取职位信息（直接读取数据库）
     *
     * @param id 职位id
     * @return {@link Y9Job} 职位对象
     */
    Y9Job getById(String id);

    Y9Job save(Y9Job y9Job);

    Y9Job updateTabIndex(String id, int tabIndex);

    Y9Job update(Y9Job job);

    Y9Job insert(Y9Job job);

    Integer getMaxTabIndex();

    boolean isNameAvailable(String name, String id);
}
