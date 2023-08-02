package net.risesoft.api.log.impl;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.log.ClickedAppApi;
import net.risesoft.log.entity.Y9ClickedApp;
import net.risesoft.log.service.Y9ClickedAppService;
import net.risesoft.model.ClickedApp;
import net.risesoft.y9.json.Y9JsonUtil;

import javax.validation.constraints.NotBlank;

/**
 * 应用点击详情
 *
 * @author mengjuhua
 * @date 2022/10/19
 */
@RestController
@RequestMapping(value = "/services/rest/clickedApp")
@Slf4j
@RequiredArgsConstructor
public class ClickedAppApiController implements ClickedAppApi {
    
    private final Y9ClickedAppService y9ClickedAppService;

    /**
     * 保存点击的图标的人员Id和应用名称等信息
     *
     * @param clickedApp 应用点击详情
     * @return boolean 是否保存成功
     */
    @Override
    @PostMapping("/saveClickedAppLog")
    public boolean saveClickedAppLog(ClickedApp clickedApp) {
        boolean result = true;
        try {
            String clickedAppJson = Y9JsonUtil.writeValueAsString(clickedApp);
            Y9ClickedApp clickedApp2 = Y9JsonUtil.readValue(clickedAppJson, Y9ClickedApp.class);
            y9ClickedAppService.save(clickedApp2);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            result = false;
        }
        return result;
    }

    /**
     * 保存点击的图标的人员Id和应用名称等信息
     *
     * @param clickedAppJson 应用点击Json字符串
     * @return boolean 是否保存成功
     */
    @Override
    @PostMapping("/saveClickedAppLogByJson")
    public boolean saveClickedAppLogByJson(@RequestParam("clickedAppJson") @NotBlank String clickedAppJson) {
        boolean result = true;
        try {
            Y9ClickedApp clickedApp = Y9JsonUtil.readValue(clickedAppJson, Y9ClickedApp.class);
            y9ClickedAppService.save(clickedApp);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            result = false;
        }
        return result;
    }

}
