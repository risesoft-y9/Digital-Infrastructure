package net.risesoft.y9public.manager.resource;

import java.util.Optional;

import net.risesoft.y9public.entity.resource.Y9System;

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

    Optional<Y9System> findByName(String systemName);

    Y9System getById(String id);

    Y9System getByName(String systemName);

    Y9System save(Y9System y9System);

}
