package net.risesoft.y9public.manager.resource;

import java.util.Optional;

import net.risesoft.y9public.entity.resource.Y9Menu;

/**
 * 菜单 Manager
 * 
 * @author shidaobang
 * @date 2023/07/11
 * @since 9.6.2
 */
public interface Y9MenuManager {
    void delete(Y9Menu y9Menu);

    Optional<Y9Menu> findById(String id);

    Y9Menu getById(String id);

    Y9Menu insert(Y9Menu y9Menu);

    Y9Menu update(Y9Menu y9Menu);

    Y9Menu updateTabIndex(String id, int index);
}
