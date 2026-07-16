package net.risesoft.y9public.manager.resource;

import java.util.Optional;

import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9public.entity.resource.Y9App;

/**
 * 应用 Manager
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
public interface Y9AppManager {
    /**
     * 根据id删除应用
     *
     * @param id 应用id
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    void delete(String id);

    /**
     * 根据系统id删除应用
     *
     * @param systemId 系统id
     */
    void deleteBySystemId(String systemId);

    /**
     * 根据id获取应用信息（直接读取数据库）
     *
     * @param id 应用id
     * @return {@code Optional<Y9App>}
     */
    Optional<Y9App> findById(String id);

    /**
     * 根据id获取应用信息
     *
     * @param id 应用id
     * @return {@code Optional<Y9App>}
     */
    Optional<Y9App> findByIdFromCache(String id);

    /**
     * 根据id获取应用信息（直接读取数据库）
     *
     * @param id 应用id
     * @return {@link Y9App}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9App getById(String id);

    /**
     * 根据id获取应用信息
     *
     * @param id 应用id
     * @return {@link Y9App}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9App getByIdFromCache(String id);

    /**
     * 新增应用
     *
     * @param y9App 应用信息
     * @return {@link Y9App}
     */
    Y9App insert(Y9App y9App);

    /**
     * 更新应用
     *
     * @param y9App 应用信息
     * @param originalApp 原应用信息
     * @return {@link Y9App}
     */
    Y9App update(Y9App y9App, Y9App originalApp);

    /**
     * 更新应用排序号
     *
     * @param id 应用id
     * @param index 排序号
     * @return {@link Y9App}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9App updateTabIndex(String id, int index);
}
