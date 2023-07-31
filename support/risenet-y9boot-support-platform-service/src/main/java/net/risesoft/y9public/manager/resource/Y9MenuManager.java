package net.risesoft.y9public.manager.resource;

import net.risesoft.y9public.entity.resource.Y9Menu;
import org.springframework.transaction.annotation.Transactional;

/**
 * 菜单 Manager
 * 
 * @author shidaobang
 * @date 2023/07/11
 * @since 9.6.2
 */
public interface Y9MenuManager {
    Y9Menu findById(String id);

    Y9Menu save(Y9Menu y9Menu);

    Y9Menu getById(String id);

    void delete(Y9Menu y9Menu);

    @Transactional(readOnly = false)
    Y9Menu updateTabIndex(String id, int index);
}
