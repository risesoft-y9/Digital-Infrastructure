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
    Y9Organization save(Y9Organization y9Organization);

    Optional<Y9Organization> findById(String id);

    Y9Organization getById(String id);

    void delete(Y9Organization y9Organization);
}
