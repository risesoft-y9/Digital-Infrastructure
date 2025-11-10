package net.risesoft.controller.role;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.platform.Role;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.vo.role.RoleTreeNodeVO;
import net.risesoft.y9public.service.role.Y9RoleService;

/**
 * 系统公共角色
 *
 * @author mengjuhua
 * @since 9.6.0
 *
 */
@RestController
@RequestMapping(value = "/api/rest/publicRole", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@IsAnyManager({ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.SECURITY_MANAGER,
    ManagerLevelEnum.OPERATION_SYSTEM_MANAGER})
public class PublicRoleController {

    private final Y9RoleService y9RoleService;

    /**
     * 获取系统公共角色顶节点
     *
     * @return {@code Y9Result<List<}{@link RoleTreeNodeVO}{@code >>}
     */
    @RiseLog(operationName = "获取系统公共角色顶节点")
    @RequestMapping(value = "/treeRoot2")
    public Y9Result<List<RoleTreeNodeVO>> treeRoot2() {
        Role role = y9RoleService.getById(InitDataConsts.TOP_PUBLIC_ROLE_ID);
        List<Role> y9RoleList = List.of(role);
        return Y9Result.success(RoleTreeNodeVO.convertRoleList(y9RoleList), "展开应用角色树成功");
    }

    /**
     * 根据角色名称，查询公共角色节点
     *
     * @param name 角色名称
     * @return {@code Y9Result<List<}{@link RoleTreeNodeVO}{@code >>}
     */
    @RiseLog(operationName = "根据角色名称，查询公共角色节点")
    @RequestMapping(value = "/treeSearch2")
    public Y9Result<List<RoleTreeNodeVO>> treeSearch2(@RequestParam String name) {
        List<Role> roleList = y9RoleService.treeSearch(name, InitDataConsts.TOP_PUBLIC_ROLE_ID);
        return Y9Result.success(RoleTreeNodeVO.convertRoleList(roleList), "根据角色名称查询角色节点成功");
    }
}
