package net.risesoft.api.v0.log;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.ClickedApp;

/**
 * 应用点击组件
 *
 * @author mengjuhua
 * @date 2022/10/19
 * @since 9.6.0
 */
@Validated
@Deprecated
public interface ClickedAppApi {
    /**
     * 保存点击的图标的人员Id和应用名称等信息
     *
     * @param clickedApp 应用点击详情
     * @return boolean 是否保存成功
     * @since 9.6.0
     */
    @PostMapping("/saveClickedAppLog")
    boolean saveClickedAppLog(ClickedApp clickedApp);

    /**
     * 保存点击的图标的人员Id和应用名称等信息
     *
     * @param clickedAppJson 应用点击Json字符串
     * @return boolean 是否保存成功
     * @since 9.6.0
     */
    @PostMapping("/saveClickedAppLogByJson")
    boolean saveClickedAppLogByJson(@RequestParam("clickedAppJson") @NotBlank String clickedAppJson);
}
