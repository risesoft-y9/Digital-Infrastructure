package net.risesoft.y9public.manager.resource;

import net.risesoft.y9public.entity.resource.Y9App;

/**
 * 应用 Manager
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
public interface Y9AppManager {
    void deleteBySystemId(String systemId);

    void delete(String id);

    Y9App getById(String id);

    Y9App findById(String id);
}
