package net.risesoft.y9public.manager.resource;

import java.util.Optional;

import net.risesoft.y9public.entity.resource.Y9Operation;

/**
 * 按钮 manager
 * 
 * @author shidaobang
 * @date 2023/07/26
 * @since 9.6.3
 */
public interface Y9OperationManager {
    void delete(Y9Operation y9Operation);

    Optional<Y9Operation> findById(String id);

    Y9Operation getById(String id);

    Y9Operation save(Y9Operation y9Operation);

    Y9Operation updateTabIndex(String id, int index);
}
