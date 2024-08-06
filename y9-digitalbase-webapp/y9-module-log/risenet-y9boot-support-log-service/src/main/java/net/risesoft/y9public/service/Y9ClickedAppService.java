package net.risesoft.y9public.service;

import net.risesoft.y9public.entity.Y9ClickedApp;

/**
 * 应用点击日志管理
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9ClickedAppService {

    /**
     * 保存应用点击日志
     *
     * @param clickedApp 应用点击日志信息
     */
    void save(Y9ClickedApp clickedApp);
}
