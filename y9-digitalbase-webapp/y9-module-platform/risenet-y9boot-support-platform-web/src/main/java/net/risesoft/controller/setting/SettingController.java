package net.risesoft.controller.setting;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.controller.setting.vo.TenantSettingVO;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.permission.annotation.IsManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.setting.Y9SettingService;

/**
 * 系统设置
 * 
 * @author shidaobang
 */
@RestController
@RequestMapping(value = "/api/rest/setting", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@IsManager({ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.SECURITY_MANAGER,
    ManagerLevelEnum.OPERATION_SYSTEM_MANAGER})
@Slf4j
public class SettingController {

    private final Y9SettingService y9SettingService;

    /**
     * 获取租户设置
     * 
     * @return {@code Y9Result<TenantSettingVO>}
     */
    @GetMapping("/getTenantSetting")
    public Y9Result<TenantSettingVO> getTenantSetting() {
        return Y9Result.success(y9SettingService.getObjectFromSetting(TenantSettingVO.class));
    }

    /**
     * 保存租户设置
     * 
     * @param tenantSettingVO 租户设置信息
     * @return {@code Y9Result<Object>}
     */
    @PostMapping("/saveTenantSetting")
    public Y9Result<Object> saveTenantSetting(TenantSettingVO tenantSettingVO) {
        y9SettingService.saveObjectFiledAsSetting(tenantSettingVO);
        return Y9Result.success();
    }
}
