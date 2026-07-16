package net.risesoft.y9public.manager.resource;

import java.util.Optional;

import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9public.entity.resource.Y9Operation;

/**
 * 按钮 manager
 * 
 * @author shidaobang
 * @date 2023/07/26
 * @since 9.6.3
 */
public interface Y9OperationManager {
    /**
     * 删除按钮
     *
     * @param y9Operation 按钮信息
     */
    void delete(Y9Operation y9Operation);

    /**
     * 根据id获取按钮信息（直接读取数据库）
     *
     * @param id 按钮id
     * @return {@code Optional<Y9Operation>}
     */
    Optional<Y9Operation> findById(String id);

    /**
     * 根据id获取按钮信息
     *
     * @param id 按钮id
     * @return {@code Optional<Y9Operation>}
     */
    Optional<Y9Operation> findByIdFromCache(String id);

    /**
     * 根据id获取按钮信息（直接读取数据库）
     *
     * @param id 按钮id
     * @return {@link Y9Operation}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Operation getById(String id);

    /**
     * 根据id获取按钮信息
     *
     * @param id 按钮id
     * @return {@link Y9Operation}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Operation getByIdFromCache(String id);

    /**
     * 新增按钮
     *
     * @param y9Operation 按钮信息
     * @return {@link Y9Operation}
     */
    Y9Operation insert(Y9Operation y9Operation);

    /**
     * 更新按钮
     *
     * @param y9Operation 按钮信息
     * @param originalOperation 原按钮信息
     * @return {@link Y9Operation}
     */
    Y9Operation update(Y9Operation y9Operation, Y9Operation originalOperation);

    /**
     * 更新按钮排序号
     *
     * @param id 按钮id
     * @param index 排序号
     * @return {@link Y9Operation}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Operation updateTabIndex(String id, int index);
}
