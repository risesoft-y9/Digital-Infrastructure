package net.risesoft.y9public.manager.resource;

import java.util.List;
import java.util.Optional;

import net.risesoft.y9public.entity.Y9System;

/**
 * 系统 Manager
 * 
 * @author shidaobang
 * @date 2023/08/22
 * @since 9.6.3
 */
public interface Y9SystemManager {

    void delete(String id);

    Optional<Y9System> findById(String id);

    Optional<Y9System> findByIdFromCache(String id);

    Optional<Y9System> findByName(String systemName);

    Y9System getById(String id);

    Y9System getByIdFromCache(String id);

    Y9System getByName(String systemName);

    Y9System insert(Y9System y9System);

    Y9System update(Y9System y9System);

    /**
     * 获取系统id列表
     *
     * @param autoInit 是否自动租用系统
     * @return {@code List<System>}
     */
    List<String> listByAutoInit(Boolean autoInit);
}
