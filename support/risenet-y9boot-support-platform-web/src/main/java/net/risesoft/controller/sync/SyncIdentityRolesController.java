package net.risesoft.controller.sync;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.enums.ManagerLevelEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.user.Y9User;
import net.risesoft.y9public.service.user.Y9UserService;

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

    private final Y9PersonService y9PersonService;
    private final Y9PositionService y9PositionService;
    private final Y9UserService y9UserService;

    /**
     * 同步所有租户的所有人员的人员角色对应表
     */
    @RiseLog()
    @RequestMapping("/identityRoles")
    public String syncIdentityRoles() {
        double start = System.currentTimeMillis();
        LOGGER.info("更新个人权限开始时间--------------->>{}", fdf.format(new Date()));
        List<Y9User> listUser = y9UserService.listAll();
        for (Y9User user : listUser) {
            if (ManagerLevelEnum.GENERAL_USER.getValue().equals(user.getManagerLevel())) {
                Y9LoginUserHolder.setTenantId(user.getTenantId());
                y9PersonService.updatePersonRoles(user.getPersonId());
                y9PositionService.cachePositionRoles(user.getPersonId());
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

        List<Y9User> listUser = y9UserService.listByTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        for (Y9User user : listUser) {
            if (ManagerLevelEnum.GENERAL_USER.getValue().equals(user.getManagerLevel())) {
                y9PersonService.updatePersonRoles(user.getPersonId());
                y9PositionService.cachePositionRoles(user.getPersonId());
            }
        }

        double end = System.currentTimeMillis();
        double time = end - start;
        LOGGER.info("更新个人权限所用时间--------------->>{}", time);
        LOGGER.info("更新个人权限完成时间--------------->>{}", fdf.format(new Date()));
        return "Success";
    }
}
