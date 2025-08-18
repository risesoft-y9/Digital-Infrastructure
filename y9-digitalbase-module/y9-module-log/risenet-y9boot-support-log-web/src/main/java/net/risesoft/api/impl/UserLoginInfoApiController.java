package net.risesoft.api.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jakarta.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.log.UserLoginInfoApi;
import net.risesoft.model.log.LoginInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.Y9logIpDeptMapping;
import net.risesoft.y9public.entity.Y9logUserHostIpInfo;
import net.risesoft.y9public.entity.Y9logUserLoginInfo;
import net.risesoft.y9public.service.Y9logIpDeptMappingService;
import net.risesoft.y9public.service.Y9logUserHostIpInfoService;
import net.risesoft.y9public.service.Y9logUserLoginInfoService;

import cn.hutool.core.thread.ThreadFactoryBuilder;

/**
 * 个人登录日志组件
 *
 * @author mengjuhua
 * @date 2022/10/19
 * @since 9.6.0
 */
@RestController
@RequestMapping(value = "/services/rest/v1/userLoginInfo", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
public class UserLoginInfoApiController implements UserLoginInfoApi {

    private final Y9logIpDeptMappingService ipDeptMappingService;
    private final Y9logUserHostIpInfoService userHostIpInfoService;
    private final Y9logUserLoginInfoService userLoginInfoService;

    /**
     * 根据租户id和人员id，获取最新登录信息
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return {@code LoginInfo} 通用请求返回对象 - data 是登录信息
     * @since 9.6.0
     */
    @Override
    public LoginInfo getTopByTenantIdAndUserId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId) {
        Y9logUserLoginInfo login = userLoginInfoService.getTopByTenantIdAndUserId(tenantId, personId);
        return Y9ModelConvertUtil.convert(login, LoginInfo.class);
    }

    /**
     * 获取登录日志列表
     *
     * @param userHostIp 用户IP
     * @param personId 用户id
     * @param tenantId 租户id
     * @param success 是否成功
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageQuery 分页信息
     * @return {@code Y9Page<LoginInfo>} 通用分页请求返回对象 - data 是登录日志集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/pageSearch")
    public Y9Page<LoginInfo> pageSearch(@RequestParam(value = "userHostIp", required = false) String userHostIp,
        @RequestParam("personId") String personId, @RequestParam("tenantId") String tenantId,
        @RequestParam(value = "success", required = false) String success,
        @RequestParam(value = "startTime", required = false) String startTime,
        @RequestParam(value = "endTime", required = false) String endTime, @Validated Y9PageQuery pageQuery) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9Page<Y9logUserLoginInfo> loginList =
            userLoginInfoService.page(tenantId, userHostIp, userHostIp, success, startTime, endTime, pageQuery);
        List<Y9logUserLoginInfo> list = loginList.getRows();
        List<LoginInfo> infoList = Y9ModelConvertUtil.convert(list, LoginInfo.class);
        return Y9Page.success(loginList.getCurrPage(), loginList.getTotalPages(), loginList.getTotal(), infoList);
    }

    /**
     * 保存登录信息
     *
     * @param info 用户登录信息
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveLoginInfo")
    public Y9Result<Object> saveLoginInfo(@RequestBody LoginInfo info) {
        String userHostIp = info.getUserHostIp();
        if (userLoginInfoService != null) {
            Y9logUserLoginInfo userLoginInfo = new Y9logUserLoginInfo();
            userLoginInfo.setId(info.getId());
            userLoginInfo.setLoginTime(info.getLoginTime());
            userLoginInfo.setLoginType(info.getLoginType());
            userLoginInfo.setUserId(info.getUserId());
            userLoginInfo.setUserName(info.getUserName());
            userLoginInfo.setUserHostIp(info.getUserHostIp());
            userLoginInfo.setUserHostMac(info.getUserHostMac());
            userLoginInfo.setUserHostName(info.getUserHostName());
            userLoginInfo.setUserHostDiskId(info.getUserHostDiskId());
            userLoginInfo.setTenantId(info.getTenantId());
            userLoginInfo.setTenantName(info.getTenantName());
            userLoginInfo.setServerIp(info.getServerIp());
            userLoginInfo.setSuccess(info.getSuccess());
            userLoginInfo.setLogMessage(info.getLogMessage());
            userLoginInfo.setBrowserName(info.getBrowserName());
            userLoginInfo.setBrowserVersion(info.getBrowserVersion());
            userLoginInfo.setScreenResolution(info.getScreenResolution());
            userLoginInfo.setOsName(info.getOsName());
            userLoginInfoService.save(userLoginInfo);
        }

        /**
         * 保存为录入的ip,部门为"此IP未指定部门"
         */
        String clientIpSection = userHostIp.substring(0, userHostIp.indexOf("."));
        if (userHostIpInfoService != null) {
            List<Y9logUserHostIpInfo> list = userHostIpInfoService.listByUserHostIp(userHostIp);
            if (list.size() <= 0) {
                Y9logUserHostIpInfo userHostIpInfo = new Y9logUserHostIpInfo();
                userHostIpInfo.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                userHostIpInfo.setClientIpSection(clientIpSection);
                userHostIpInfo.setUserHostIp(userHostIp);
                userHostIpInfoService.save(userHostIpInfo);
            }
        }

        if (ipDeptMappingService != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<Y9logIpDeptMapping> list = ipDeptMappingService.listByClientIpSection(clientIpSection);
            if (list.size() <= 0) {
                Y9logIpDeptMapping ipDeptMapping = new Y9logIpDeptMapping();
                ipDeptMapping.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                ipDeptMapping.setClientIpSection(clientIpSection);
                ipDeptMapping.setDeptName("此IP未指定部门");
                ipDeptMapping.setOperator(info.getUserName());
                ipDeptMapping.setSaveTime(sdf.format(new Date()));
                ipDeptMapping.setStatus(1);
                ipDeptMapping.setTabIndex(666);
                ipDeptMapping.setUpdateTime(sdf.format(new Date()));
                ipDeptMappingService.save(ipDeptMapping);
            } else {
                for (Y9logIpDeptMapping ipDeptMapping : list) {
                    if (ipDeptMapping.getStatus() == null || ipDeptMapping.getStatus() != 1) {
                        ipDeptMapping.setStatus(1);
                        ipDeptMappingService.save(ipDeptMapping);
                    }
                }
            }
        }
        return Y9Result.success();
    }

    /**
     * 异步保存登录信息
     *
     * @param info 用户登录信息
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveLoginInfoAsync")
    public Y9Result<Object> saveLoginInfoAsync(@RequestBody LoginInfo info) {
        ThreadFactory myThread = new ThreadFactoryBuilder().setNamePrefix("y9-saveLoginInfoAsync").build();
        ThreadPoolExecutor threadPool =
            new ThreadPoolExecutor(2, 2, 100, TimeUnit.SECONDS, new LinkedBlockingDeque<>(5), myThread);
        threadPool.execute(() -> saveLoginInfo(info));
        threadPool.shutdown();
        return Y9Result.success();
    }
}
