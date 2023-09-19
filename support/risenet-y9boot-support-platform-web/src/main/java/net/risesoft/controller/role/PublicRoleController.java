package net.risesoft.controller.role;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.DefaultIdConsts;
import net.risesoft.controller.role.vo.RoleVO;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.service.role.Y9RoleService;

/**
 * 系统公共角色
 *
 * @author mengjuhua
 * @since 9.6.0
 *
 */
@RestController
@RequestMapping(value = "/api/rest/publicRole", produces = "application/json")
@RequiredArgsConstructor
public class PublicRoleController {

    private final Y9RoleService y9RoleService;

    /**
     * 获取系统公共角色顶节点
     *
     * @return
     */
    @RiseLog(operationName = "获取系统公共角色顶节点")
    @RequestMapping(value = "/treeRoot")
    public Y9Result<List<RoleVO>> publicRoleRoot() {
        Y9Role y9Role = y9RoleService.getById(DefaultIdConsts.TOP_PUBLIC_ROLE_ID);
        List<RoleVO> y9RoleList = new ArrayList<>();
        RoleVO role = Y9ModelConvertUtil.convert(y9Role, RoleVO.class);
        role.setHasChild(Boolean.TRUE);
        y9RoleList.add(role);
        return Y9Result.success(y9RoleList, "展开应用角色树成功");
    }
}
