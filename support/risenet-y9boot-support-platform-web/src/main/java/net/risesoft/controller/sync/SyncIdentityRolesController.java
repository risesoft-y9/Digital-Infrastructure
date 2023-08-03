package net.risesoft.controller.sync;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.enums.TenantTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.service.identity.Y9PersonToRoleService;
import net.risesoft.service.identity.Y9PositionToRoleService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.tenant.Y9Tenant;
import net.risesoft.y9public.service.tenant.Y9TenantService;

/**
 * 同步人员权限信息
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@RestController
@RequestMapping("/sync")
@Slf4j
@RequiredArgsConstructor
public class SyncIdentityRolesController {
    private final FastDateFormat fdf = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private final Y9PersonToRoleService y9PersonToRoleService;
    private final Y9PositionToRoleService y9PositionToRoleService;
    private final Y9PositionService y9PositionService;
    private final Y9TenantService y9TenantService;
    private final Y9PersonService y9PersonService;

    /**
     * 同步所有租户的所有人员的人员角色对应表
     */
    @RiseLog()
    @RequestMapping("/identityRoles")
    public String syncIdentityRoles() {
        double start = System.currentTimeMillis();
        LOGGER.info("更新个人权限开始时间--------------->>{}", fdf.format(new Date()));

        List<Y9Tenant> y9TenantList = y9TenantService.listByTenantType(TenantTypeEnum.TENANT.getValue());
        for (Y9Tenant y9Tenant : y9TenantList) {
            Y9LoginUserHolder.setTenantId(y9Tenant.getId());
            List<Y9Person> y9PersonList = y9PersonService.listAll();
            for (Y9Person y9Person : y9PersonList) {
                y9PersonToRoleService.recalculate(y9Person.getId());
            }
            List<Y9Position> y9PositionList = y9PositionService.listAll();
            for (Y9Position y9Position : y9PositionList) {
                y9PositionToRoleService.recalculate(y9Position.getId());
            }
        }

        double end = System.currentTimeMillis();
        double time = end - start;
        LOGGER.info("更新个人权限完成时间--------------->>{}", fdf.format(new Date()));
        LOGGER.info("更新个人权限所用时间--------------->>{}", time);
        return "Success";
    }

    /**
     * 同步租户下所有人员的人员角色对应表
     *
     * @param tenantId 租户id
     * @return
     */
    @RiseLog()
    @RequestMapping("/identityRoles/{tenantId}")
    public String syncIdentityRolesByTenantId(@PathVariable String tenantId) {
        double start = System.currentTimeMillis();
        LOGGER.info("更新个人权限开始时间--------------->>{},租户id--->{}", fdf.format(new Date()), tenantId);

        Y9LoginUserHolder.setTenantId(tenantId);
        List<Y9Person> y9PersonList = y9PersonService.listAll();
        for (Y9Person y9Person : y9PersonList) {
            y9PersonToRoleService.recalculate(y9Person.getId());
        }
        List<Y9Position> y9PositionList = y9PositionService.listAll();
        for (Y9Position y9Position : y9PositionList) {
            y9PositionToRoleService.recalculate(y9Position.getId());
        }

        double end = System.currentTimeMillis();
        double time = end - start;
        LOGGER.info("更新个人权限所用时间--------------->>{}", time);
        LOGGER.info("更新个人权限完成时间--------------->>{}", fdf.format(new Date()));
        return "Success";
    }
}
