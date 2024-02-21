package net.risesoft.controller.sync;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9Organization;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.authorization.Y9AuthorizationService;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.util.Y9PlatformUtil;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 同步人员、岗位对应资源
 *
 * @author shidaobang
 * @date 2022/3/30
 */
@RestController
@RequestMapping("/sync")
@Slf4j
@RequiredArgsConstructor
public class SyncIdentityResourceController {

    private final FastDateFormat fdf = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private final Y9AuthorizationService y9AuthorizationService;
    private final Y9OrganizationService y9OrganizationService;

    /**
     * 同步所有租户人员的权限缓存
     *
     * @return
     */
    @RiseLog()
    @RequestMapping("/identityResources")
    public Y9Result<Object> identityResources() {
        double start = System.currentTimeMillis();
        LOGGER.info("更新人员与（资源、权限）关系表开始时间--------------->>{}", fdf.format(new Date()));

        List<String> tenantIdList = Y9PlatformUtil.getTenantIds();
        for (String tenantId : tenantIdList) {
            Y9LoginUserHolder.setTenantId(tenantId);
            for (Y9Organization y9Organization : y9OrganizationService.list()) {
                this.identityResources(tenantId, y9Organization.getId());
            }
        }

        double end = System.currentTimeMillis();
        double time = end - start;
        LOGGER.info("更新人员与（资源、权限）关系表完成时间--------------->>{}", fdf.format(new Date()));
        LOGGER.info("更新人员与（资源、权限）关系表所用时间--------------->>{}", time);
        return Y9Result.success();
    }

    /**
     * 同步某个租户某个组织节点下人员的权限缓存
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点id
     * @return
     */
    @RiseLog()
    @RequestMapping("/identityResources/{tenantId}/{orgUnitId}")
    public Y9Result<Object> identityResources(@PathVariable String tenantId, @PathVariable String orgUnitId) {
        double start = System.currentTimeMillis();
        LOGGER.info("更新人员与（资源、权限）关系表开始时间--------------->>{},租户id---->>{},组织id---->>>{}", fdf.format(new Date()),
            tenantId, orgUnitId);

        Y9LoginUserHolder.setTenantId(tenantId);
        y9AuthorizationService.syncToIdentityResourceAndAuthority(orgUnitId);

        double end = System.currentTimeMillis();
        double time = end - start;
        LOGGER.info("更新人员与（资源、权限）关系表完成时间--------------->>{}", fdf.format(new Date()));
        LOGGER.info("更新人员与（资源、权限）关系表所用时间--------------->>{}", time);
        return Y9Result.success();
    }

}
