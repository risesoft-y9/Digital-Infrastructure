package net.risesoft.controller.manager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9Manager;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.Y9ManagerService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 登录信息
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@RestController
@RequestMapping(value = "/api/rest/info", produces = "application/json")
@Slf4j
@RequiredArgsConstructor
public class InfoController {

    private final Y9ManagerService y9ManagerService;

    /**
     * 获取登录用户信息
     *
     * @return
     */
    @RiseLog(operationName = "获取登录用户信息")
    @GetMapping(value = "/getLoginInfo")
    public Y9Result<Map<String, Object>> getLoginInfo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> returnMap = new HashMap<>(16);
        returnMap.put("tenantManager", Y9LoginUserHolder.getUserInfo().isGlobalManager());
        returnMap.put("person", Y9LoginUserHolder.getUserInfo());
        returnMap.put("tenantName", Y9LoginUserHolder.getTenantName());
        returnMap.put("tipsMsg", "");
        if (Y9LoginUserHolder.getUserInfo().getManagerLevel() != 0) {
            try {
                Y9Manager y9Manager = y9ManagerService.getById(Y9LoginUserHolder.getUserInfo().getPersonId());
                y9Manager.setCheckTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                y9ManagerService.saveOrUpdate(y9Manager);

                Date modifyPwdTime = sdf.parse(y9Manager.getModifyPwdTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(modifyPwdTime);
                calendar.add(Calendar.DAY_OF_MONTH, y9Manager.getPwdCycle());
                long now = System.currentTimeMillis();
                if (calendar.getTimeInMillis() < now) {
                    returnMap.put("tipsMsg", "您已经超过" + y9Manager.getPwdCycle() + "天未修改密码，请您尽快修改密码？");
                }
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        return Y9Result.success(returnMap, "获取数据成功");
    }

}
