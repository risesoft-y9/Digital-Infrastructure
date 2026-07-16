package net.risesoft.y9public.manager.resource;

import java.util.Optional;

import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9public.entity.resource.Y9Menu;

/**
 * 菜单 Manager
 * 
 * @author shidaobang
 * @date 2023/07/11
 * @since 9.6.2
 */
public interface Y9MenuManager {
    /**
     * 删除菜单
     *
     * @param y9Menu 菜单信息
     */
    void delete(Y9Menu y9Menu);

    /**
     * 根据id获取菜单信息（直接读取数据库）
     *
     * @param id 菜单id
     * @return {@code Optional<Y9Menu>}
     */
    Optional<Y9Menu> findById(String id);

    /**
     * 根据id获取菜单信息
     *
     * @param id 菜单id
     * @return {@code Optional<Y9Menu>}
     */
    Optional<Y9Menu> findByIdFromCache(String id);

    /**
     * 根据id获取菜单信息（直接读取数据库）
     *
     * @param id 菜单id
     * @return {@link Y9Menu}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Menu getById(String id);

    /**
     * 根据id获取菜单信息
     *
     * @param id 菜单id
     * @return {@link Y9Menu}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Menu getByIdFromCache(String id);

    /**
     * 新增菜单
     *
     * @param y9Menu 菜单信息
     * @return {@link Y9Menu}
     */
    Y9Menu insert(Y9Menu y9Menu);

    /**
     * 更新菜单
     *
     * @param y9Menu 菜单信息
     * @param originalMenu 原菜单信息
     * @return {@link Y9Menu}
     */
    Y9Menu update(Y9Menu y9Menu, Y9Menu originalMenu);

    /**
     * 更新菜单排序号
     *
     * @param id 菜单id
     * @param index 排序号
     * @return {@link Y9Menu}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Menu updateTabIndex(String id, int index);
}
