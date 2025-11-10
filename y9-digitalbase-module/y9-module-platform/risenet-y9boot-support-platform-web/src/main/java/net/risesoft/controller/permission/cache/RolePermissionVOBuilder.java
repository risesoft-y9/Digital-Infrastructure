package net.risesoft.controller.permission.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import net.risesoft.model.platform.Role;
import net.risesoft.model.platform.permission.cache.IdentityToRoleBase;
import net.risesoft.vo.permission.RolePermissionVO;
import net.risesoft.y9public.service.resource.Y9AppService;
import net.risesoft.y9public.service.resource.Y9SystemService;
import net.risesoft.y9public.service.role.Y9RoleService;

/**
 * 构建 角色权限Vo 集合
 * 
 * @author shidaobang
 * @date 2023/07/20
 * @since 9.6.2
 */
@Component
@RequiredArgsConstructor
public class RolePermissionVOBuilder {

    private final Y9AppService y9AppService;
    private final Y9RoleService y9RoleService;
    private final Y9SystemService y9SystemService;

    private RolePermissionVO.App buildApp(String appId, List<IdentityToRoleBase> identityToRoleBaseList) {
        RolePermissionVO.App app = new RolePermissionVO.App();

        if (StringUtils.isBlank(appId)) {
            app.setAppName(null);
        } else {
            app.setAppName(y9AppService.getById(appId).getName());
        }
        app.setPermissionDetailList(buildPermissionDetailList(identityToRoleBaseList));
        return app;
    }

    private List<RolePermissionVO.App> buildAppList(List<IdentityToRoleBase> allIdentityToRoleBaseList) {
        List<RolePermissionVO.App> appList = new ArrayList<>();

        // 应用下的角色
        Map<String,
            List<IdentityToRoleBase>> appIdAndIdentityToRoleBaseListMap = allIdentityToRoleBaseList.stream()
                .filter(identityToRoleBase -> StringUtils.isNotBlank(identityToRoleBase.getAppId()))
                .collect(Collectors.groupingBy(IdentityToRoleBase::getAppId));
        for (Map.Entry<String, List<IdentityToRoleBase>> entry : appIdAndIdentityToRoleBaseListMap.entrySet()) {
            appList.add(buildApp(entry.getKey(), entry.getValue()));
        }

        // 系统下的角色，所有应用公用
        List<IdentityToRoleBase> systemIdentityToRoleBaseList = allIdentityToRoleBaseList.stream()
            .filter(y9IdentityToRoleBase -> StringUtils.isBlank(y9IdentityToRoleBase.getAppId()))
            .collect(Collectors.toList());
        if (!systemIdentityToRoleBaseList.isEmpty()) {
            appList.add(buildApp(null, systemIdentityToRoleBaseList));
        }

        return appList;
    }

    private List<RolePermissionVO.PermissionDetail>
        buildPermissionDetailList(List<IdentityToRoleBase> identityToRoleBaseList) {
        List<RolePermissionVO.PermissionDetail> permissionDetailList = new ArrayList<>();
        for (IdentityToRoleBase identityToRoleBase : identityToRoleBaseList) {
            RolePermissionVO.PermissionDetail permissionDetail = new RolePermissionVO.PermissionDetail();
            Optional<Role> roleOptional = y9RoleService.findById(identityToRoleBase.getRoleId());
            if (roleOptional.isPresent()) {
                Role role = roleOptional.get();
                permissionDetail.setRoleName(role.getName());
                permissionDetail.setRoleDescription(role.getDescription());
                permissionDetailList.add(permissionDetail);
            }
        }
        return permissionDetailList;
    }

    private RolePermissionVO buildRolePermissionVO(String systemId, List<IdentityToRoleBase> identityToRoleBaseList) {
        RolePermissionVO rolePermissionVO = new RolePermissionVO();
        if (StringUtils.isBlank(systemId)) {
            rolePermissionVO.setSystemCnName(null);
            rolePermissionVO.setAppList(List.of(buildApp(null, identityToRoleBaseList)));
        } else {
            rolePermissionVO.setSystemCnName(y9SystemService.getById(systemId).getCnName());
            rolePermissionVO.setAppList(buildAppList(identityToRoleBaseList));
        }
        return rolePermissionVO;
    }

    public List<RolePermissionVO> buildRolePermissionVOList(List<IdentityToRoleBase> identityToRoleBaseList) {
        List<RolePermissionVO> rolePermissionVOList = new ArrayList<>();

        // 系统应用角色
        Map<String,
            List<IdentityToRoleBase>> systemIdY9IdentityToRoleBaseListMap = identityToRoleBaseList.stream()
                .filter(identityToRoleBase -> StringUtils.isNotBlank(identityToRoleBase.getSystemId()))
                .collect(Collectors.groupingBy(IdentityToRoleBase::getSystemId));
        for (Map.Entry<String, List<IdentityToRoleBase>> entry : systemIdY9IdentityToRoleBaseListMap.entrySet()) {
            rolePermissionVOList.add(buildRolePermissionVO(entry.getKey(), entry.getValue()));
        }

        // 公共角色
        List<IdentityToRoleBase> identityToPublicRoleList = identityToRoleBaseList.stream()
            .filter(identityToRoleBase -> StringUtils.isBlank(identityToRoleBase.getSystemId()))
            .collect(Collectors.toList());
        if (!identityToPublicRoleList.isEmpty()) {
            rolePermissionVOList.add(buildRolePermissionVO(null, identityToPublicRoleList));
        }

        return rolePermissionVOList;
    }
}
