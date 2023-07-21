package net.risesoft.y9public.manager.resource;

import net.risesoft.y9public.entity.resource.Y9Menu;

/**
 * 菜单 Manager
 * 
 * @author shidaobang
 * @date 2023/07/11
 * @since 9.6.2
 */
public interface Y9MenuManager {
    Y9Menu findById(String id);
}
