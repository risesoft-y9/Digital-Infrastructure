package net.risesoft.api.log;

import net.risesoft.model.ClickedApp;

/**
 * 应用点击组件
 *
 * @author mengjuhua
 * @date 2022/10/19
 * @since 9.6.0
 */
public interface ClickedAppApi {
    /**
     * 保存点击的图标的人员Id和应用名称等信息
     *
     * @param clickedApp 应用点击详情
     * @return boolean 是否保存成功
     * @since 9.6.0
     */
    boolean saveClickedAppLog(ClickedApp clickedApp);

    /**
     * 保存点击的图标的人员Id和应用名称等信息
     *
     * @param clickedAppJson 应用点击Json字符串
     * @return boolean 是否保存成功
     * @since 9.6.0
     */
    boolean saveClickedAppLogByJson(String clickedAppJson);
}
