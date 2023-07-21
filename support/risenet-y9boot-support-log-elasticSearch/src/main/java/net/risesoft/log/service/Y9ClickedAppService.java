package net.risesoft.log.service;

import net.risesoft.log.entity.Y9ClickedApp;

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
     * @param clickedApp
     */
    public void save(Y9ClickedApp clickedApp);
}
