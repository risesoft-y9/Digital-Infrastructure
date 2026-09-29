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

    /**
     * 删除职位
     *
     * @param y9Job 职位信息
     */
    void delete(Y9Job y9Job);

    /**
     * 根据id获取职位信息
     *
     * @param id 职位id
     * @return {@code Optional<Y9Job>}
     */
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
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Job getById(String id);

    /**
     * 保存职位
     *
     * @param y9Job 职位信息
     * @return {@link Y9Job}
     */
    Y9Job save(Y9Job y9Job);

    /**
     * 更新职位排序号
     *
     * @param id 职位id
     * @param tabIndex 排序号
     * @return {@link Y9Job}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Job updateTabIndex(String id, int tabIndex);

    /**
     * 更新职位
     *
     * @param job 职位信息
     * @return {@link Y9Job}
     * @throws Y9NotFoundException job.id 对应的记录不存在的情况
     */
    Y9Job update(Y9Job job);

    /**
     * 新增职位
     *
     * @param job 职位信息
     * @return {@link Y9Job}
     */
    Y9Job insert(Y9Job job);

    /**
     * 获取最大的排序号
     *
     * @return 最大排序号
     */
    Integer getMaxTabIndex();
}
