package net.risesoft.y9public.manager.resource;

import java.util.List;
import java.util.Optional;

import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9public.entity.Y9System;

/**
 * 系统 Manager
 * 
 * @author shidaobang
 * @date 2023/08/22
 * @since 9.6.3
 */
public interface Y9SystemManager {

    /**
     * 根据id删除系统
     *
     * @param id 系统id
     */
    void delete(String id);

    /**
     * 根据id获取系统信息（直接读取数据库）
     *
     * @param id 系统id
     * @return {@code Optional<Y9System>}
     */
    Optional<Y9System> findById(String id);

    /**
     * 根据id获取系统信息
     *
     * @param id 系统id
     * @return {@code Optional<Y9System>}
     */
    Optional<Y9System> findByIdFromCache(String id);

    /**
     * 根据系统名称获取系统信息
     *
     * @param systemName 系统名称
     * @return {@code Optional<Y9System>}
     */
    Optional<Y9System> findByName(String systemName);

    /**
     * 根据id获取系统信息（直接读取数据库）
     *
     * @param id 系统id
     * @return {@link Y9System}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9System getById(String id);

    /**
     * 根据id获取系统信息
     *
     * @param id 系统id
     * @return {@link Y9System}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9System getByIdFromCache(String id);

    /**
     * 根据系统名称获取系统信息
     *
     * @param systemName 系统名称
     * @return {@link Y9System}
     * @throws Y9NotFoundException systemName 对应的记录不存在的情况
     */
    Y9System getByName(String systemName);

    /**
     * 新增系统
     *
     * @param y9System 系统信息
     * @return {@link Y9System}
     */
    Y9System insert(Y9System y9System);

    /**
     * 更新系统
     *
     * @param y9System 系统信息
     * @return {@link Y9System}
     */
    Y9System update(Y9System y9System);

    /**
     * 获取系统id列表
     *
     * @param autoInit 是否自动租用系统
     * @return {@code List<System>}
     */
    List<String> listByAutoInit(Boolean autoInit);
}
