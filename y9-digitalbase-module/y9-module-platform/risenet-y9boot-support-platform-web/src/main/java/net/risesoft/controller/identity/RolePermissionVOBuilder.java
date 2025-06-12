package net.risesoft.controller.identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import net.risesoft.controller.identity.vo.RolePermissionVO;
import net.risesoft.entity.identity.Y9IdentityToRoleBase;
import net.risesoft.y9public.entity.role.Y9Role;
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

    private RolePermissionVO.App buildApp(String appId, List<Y9IdentityToRoleBase> y9IdentityToRoleBaseList) {
        RolePermissionVO.App app = new RolePermissionVO.App();

        if (StringUtils.isBlank(appId)) {
            app.setAppName(null);
        } else {
            app.setAppName(y9AppService.getById(appId).getName());
        }
        app.setPermissionDetailList(buildPermissionDetailList(y9IdentityToRoleBaseList));
        return app;
    }

    private List<RolePermissionVO.App> buildAppList(List<Y9IdentityToRoleBase> y9IdentityToRoleBaseList) {
        List<RolePermissionVO.App> appList = new ArrayList<>();

        // 应用下的角色
        Map<String,
            List<Y9IdentityToRoleBase>> appIdY9IdentityToRoleBaseListMap = y9IdentityToRoleBaseList.stream()
                .filter(y9IdentityToRoleBase -> StringUtils.isNotBlank(y9IdentityToRoleBase.getAppId()))
                .collect(Collectors.groupingBy(Y9IdentityToRoleBase::getAppId));
        for (Map.Entry<String, List<Y9IdentityToRoleBase>> entry : appIdY9IdentityToRoleBaseListMap.entrySet()) {
            appList.add(buildApp(entry.getKey(), entry.getValue()));
        }

        // 系统下的角色，所有应用公用
        List<Y9IdentityToRoleBase> y9IdentityToRoleBases = y9IdentityToRoleBaseList.stream()
            .filter(y9IdentityToRoleBase -> StringUtils.isBlank(y9IdentityToRoleBase.getAppId()))
            .collect(Collectors.toList());
        if (!y9IdentityToRoleBases.isEmpty()) {
            appList.add(buildApp(null, y9IdentityToRoleBases));
        }

        return appList;
    }

    private List<RolePermissionVO.PermissionDetail>
        buildPermissionDetailList(List<Y9IdentityToRoleBase> y9IdentityToRoleBaseList) {
        List<RolePermissionVO.PermissionDetail> permissionDetailList = new ArrayList<>();
        for (Y9IdentityToRoleBase y9IdentityToRoleBase : y9IdentityToRoleBaseList) {
            RolePermissionVO.PermissionDetail permissionDetail = new RolePermissionVO.PermissionDetail();
            Optional<Y9Role> y9RoleOptional = y9RoleService.findById(y9IdentityToRoleBase.getRoleId());
            if (y9RoleOptional.isPresent()) {
                Y9Role y9Role = y9RoleOptional.get();
                permissionDetail.setRoleName(y9Role.getName());
                permissionDetail.setRoleDescription(y9Role.getDescription());
                permissionDetailList.add(permissionDetail);
            }
        }
        return permissionDetailList;
    }

    private RolePermissionVO buildRolePermissionVO(String systemId,
        List<Y9IdentityToRoleBase> y9IdentityToRoleBaseList) {
        RolePermissionVO rolePermissionVO = new RolePermissionVO();
        if (StringUtils.isBlank(systemId)) {
            rolePermissionVO.setSystemCnName(null);
            rolePermissionVO.setAppList(List.of(buildApp(null, y9IdentityToRoleBaseList)));
        } else {
            rolePermissionVO.setSystemCnName(y9SystemService.getById(systemId).getCnName());
            rolePermissionVO.setAppList(buildAppList(y9IdentityToRoleBaseList));
        }
        return rolePermissionVO;
    }

    public List<RolePermissionVO> buildRolePermissionVOList(List<Y9IdentityToRoleBase> y9IdentityToRoleBaseList) {
        List<RolePermissionVO> rolePermissionVOList = new ArrayList<>();

        // 系统应用角色
        Map<String,
            List<Y9IdentityToRoleBase>> systemIdY9IdentityToRoleBaseListMap = y9IdentityToRoleBaseList.stream()
                .filter(y9IdentityToRoleBase -> StringUtils.isNotBlank(y9IdentityToRoleBase.getSystemId()))
                .collect(Collectors.groupingBy(Y9IdentityToRoleBase::getSystemId));
        for (Map.Entry<String, List<Y9IdentityToRoleBase>> entry : systemIdY9IdentityToRoleBaseListMap.entrySet()) {
            rolePermissionVOList.add(buildRolePermissionVO(entry.getKey(), entry.getValue()));
        }

        // 公共角色
        List<Y9IdentityToRoleBase> identityToPublicRoleList = y9IdentityToRoleBaseList.stream()
            .filter(y9IdentityToRoleBase -> StringUtils.isBlank(y9IdentityToRoleBase.getSystemId()))
            .collect(Collectors.toList());
        if (!identityToPublicRoleList.isEmpty()) {
            rolePermissionVOList.add(buildRolePermissionVO(null, identityToPublicRoleList));
        }

        return rolePermissionVOList;
    }
}
