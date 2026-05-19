package net.risesoft.controller.sync;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.platform.System;
import net.risesoft.model.platform.org.Organization;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.AppCategory;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.service.permission.cache.Y9PersonalAppService;
import net.risesoft.service.setting.Y9AppCategoryService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.app.y9platform.Y9PlatformProperties;
import net.risesoft.y9public.service.resource.Y9SystemService;
import net.risesoft.y9public.service.tenant.Y9TenantSystemService;
import net.risesoft.y9public.service.user.Y9UserService;

/**
 * 同步个人应用
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@RestController
@RequestMapping("/sync")
@RequiredArgsConstructor
@Slf4j
@Validated
public class SyncPersonalAppController {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm");

    private final Y9PlatformProperties y9PlatformProperties;

    private final Y9PersonalAppService y9PersonalAppService;

    private final Y9TenantSystemService y9TenantSystemService;

    private final Y9SystemService y9SystemService;

    private final Y9AppCategoryService y9AppCategoryService;

    private final Y9UserService y9UserService;

    private final Y9OrganizationService y9OrganizationService;

    private final CompositeOrgBaseService compositeOrgBaseService;

    /**
     * 同步人员的个人应用
     */
    @RiseLog(operationName = "同步人员的个人应用", operationType = OperationTypeEnum.MODIFY)
    @RequestMapping("/personalAppForPerson")
    public Y9Result<Object> personalAppForPerson() {
        double start = Instant.now().toEpochMilli();
        LOGGER.info("开始人员图标更新操作时间:{}", DATE_FORMAT.format(start));

        Optional<System> y9SystemOptional = y9SystemService.findByName(y9PlatformProperties.getSystemName());
        if (y9SystemOptional.isPresent()) {
            List<String> tenantIds = y9TenantSystemService.listTenantIdBySystemId(y9SystemOptional.get().getId());
            for (String tenantId : tenantIds) {
                List<UserInfo> listUser = y9UserService.listByTenantId(tenantId);

                Y9LoginUserHolder.setTenantId(tenantId);
                LOGGER.info("当前租户ID为---------》{}", tenantId);
                List<AppCategory> categoryList = y9AppCategoryService.buildDefaultAppCategoryList();
                for (UserInfo user : listUser) {
                    String personId = user.getPersonId();
                    try {
                        if (null != user.getLoginName()) {
                            y9PersonalAppService.buildPersonalAppForPerson(personId, categoryList);
                        }
                    } catch (Exception e) {
                        LOGGER.error("tenantId---》{},失败人员---》{}, personID----》{}", user.getTenantId(),
                            user.getLoginName(), personId);
                        LOGGER.error("图标更新失败，错误信息为----》", e);
                    }
                }
            }
        }

        double end = Instant.now().toEpochMilli();
        LOGGER.info("更新人员图标所用时间---------------》{}", end - start);
        LOGGER.info("完成人员图标更新操作时间---------》{}", DATE_FORMAT.format(end));
        return Y9Result.successMsg("Update Success");
    }

    /**
     * 同步岗位的个人应用
     */
    @RiseLog(operationName = "同步岗位的个人应用", operationType = OperationTypeEnum.MODIFY)
    @RequestMapping("/personalAppForPosition")
    public Y9Result<Object> personalAppForPosition() {
        double start = Instant.now().toEpochMilli();
        LOGGER.info("开始人员图标更新操作时间:{}", DATE_FORMAT.format(start));

        Optional<System> y9SystemOptional = y9SystemService.findByName(y9PlatformProperties.getSystemName());
        if (y9SystemOptional.isPresent()) {
            List<String> tenantIds = y9TenantSystemService.listTenantIdBySystemId(y9SystemOptional.get().getId());
            for (String tenantId : tenantIds) {
                Y9LoginUserHolder.setTenantId(tenantId);
                LOGGER.info("当前租户ID为---------》{}", tenantId);
                List<AppCategory> appCategoryList = y9AppCategoryService.buildDefaultAppCategoryList();
                List<Organization> organizations = y9OrganizationService.list(false, false);
                for (Organization org : organizations) {
                    List<Position> positions = compositeOrgBaseService.listAllDescendantPositions(org.getId());
                    if (null != positions && !positions.isEmpty()) {
                        for (Position position : positions) {
                            try {
                                y9PersonalAppService.buildPersonalAppForPosition(position.getId(), appCategoryList);
                            } catch (Exception e) {
                                LOGGER.error("tenantId---》{},失败人员---》{}, personID----》{}", tenantId, position.getName(),
                                    position.getId());
                                LOGGER.error("图标更新失败，错误信息为----》", e);
                            }
                        }
                    }
                }
            }
        }

        double end = Instant.now().toEpochMilli();
        LOGGER.info("更新岗位新图标所用时间---------------》{}", end - start);
        LOGGER.info("完成岗位图标更新操作时间---------》{}", DATE_FORMAT.format(end));
        return Y9Result.successMsg("Update Success");
    }

    /**
     * 同步租户人员的个人应用
     *
     * @param tenantId 租户id
     * @return
     */
    @RiseLog(operationName = "同步租户人员的个人应用", operationType = OperationTypeEnum.MODIFY)
    @RequestMapping("/personalAppForPersonByTenantId")
    public Y9Result<Object> personalAppForPersonByTenantId(@NotBlank String tenantId) {
        double start = Instant.now().toEpochMilli();
        LOGGER.info("开始人员图标更新操作时间:{}", DATE_FORMAT.format(start));

        List<UserInfo> listUser = y9UserService.listByTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        List<AppCategory> appOrderList = y9AppCategoryService.buildDefaultAppCategoryList();
        for (UserInfo user : listUser) {
            String personId = user.getPersonId();
            try {
                if (null != user.getLoginName()) {
                    y9PersonalAppService.buildPersonalAppForPerson(personId, appOrderList);
                }
            } catch (Exception e) {
                LOGGER.error("tenantId--->{},失败人员--->{}, personID---->{}", user.getTenantId(), user.getLoginName(),
                    personId);
                LOGGER.error("图标更新失败，错误信息为----》》", e);
            }
        }

        double end = Instant.now().toEpochMilli();
        LOGGER.info("更新租户人员图标所用时间---------------》{}", end - start);
        LOGGER.info("完成租户人员图标更新操作时间---------》{}", DATE_FORMAT.format(end));
        return Y9Result.successMsg("Update Success");
    }

    /**
     * 同步租户岗位的个人应用
     *
     * @param tenantId 租户id
     * @return
     */
    @RiseLog(operationName = "同步租户岗位的个人应用", operationType = OperationTypeEnum.MODIFY)
    @RequestMapping("/personalAppForPositionByTenantId")
    public Y9Result<Object> personalAppForPositionByTenantId(@NotBlank String tenantId) {
        double start = Instant.now().toEpochMilli();
        LOGGER.info("开始人员图标更新操作时间:{}", DATE_FORMAT.format(start));

        Y9LoginUserHolder.setTenantId(tenantId);
        List<AppCategory> appOrderList = y9AppCategoryService.buildDefaultAppCategoryList();
        List<Organization> organizations = y9OrganizationService.list(false, false);
        for (Organization org : organizations) {
            List<Position> positions = compositeOrgBaseService.listAllDescendantPositions(org.getId());
            if (null != positions && !positions.isEmpty()) {
                for (Position position : positions) {
                    try {
                        y9PersonalAppService.buildPersonalAppForPosition(position.getId(), appOrderList);
                    } catch (Exception e) {
                        LOGGER.error("tenantId--->{},失败人员--->{}, personID---->{}", tenantId, position.getName(),
                            position.getId());
                        LOGGER.error("图标更新失败，错误信息为----》》", e);
                    }
                }
            }
        }

        double end = Instant.now().toEpochMilli();
        LOGGER.info("更新租户岗位新图标所用时间--------------->>{}", end - start);
        LOGGER.info("完成租户岗位图标更新操作时间---------》{}", DATE_FORMAT.format(end));
        return Y9Result.successMsg("Update Success");
    }

}
