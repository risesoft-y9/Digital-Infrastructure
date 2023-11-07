package net.risesoft.api.impl;

import jakarta.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.log.ClickedAppApi;
import net.risesoft.log.entity.Y9ClickedApp;
import net.risesoft.log.service.Y9ClickedAppService;
import net.risesoft.model.ClickedApp;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * 应用点击详情
 *
 * @author mengjuhua
 * @date 2022/10/19
 */
@RestController
@RequestMapping(value = "/services/rest/v1/clickedApp")
@Slf4j
@RequiredArgsConstructor
public class ClickedAppApiController implements ClickedAppApi {

    private final Y9ClickedAppService y9ClickedAppService;

    /**
     * 保存点击的图标的人员Id和应用名称等信息
     *
     * @param clickedApp 应用点击详情
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveClickedAppLog")
    public Y9Result<Object> saveClickedAppLog(@RequestBody ClickedApp clickedApp) {
        String clickedAppJson = Y9JsonUtil.writeValueAsString(clickedApp);
        Y9ClickedApp clickedApp2 = Y9JsonUtil.readValue(clickedAppJson, Y9ClickedApp.class);
        y9ClickedAppService.save(clickedApp2);
        return Y9Result.success();
    }

}
