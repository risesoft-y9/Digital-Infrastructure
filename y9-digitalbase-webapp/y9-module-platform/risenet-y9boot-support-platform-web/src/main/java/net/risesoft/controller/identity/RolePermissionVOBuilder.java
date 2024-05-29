package net.risesoft.controller.identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.controller.identity.vo.RolePermissionVO;
import net.risesoft.entity.identity.Y9IdentityToRoleBase;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.service.resource.Y9AppService;
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

    private RolePermissionVO.App buildApp(String appId, List<Y9IdentityToRoleBase> y9IdentityToRoleBaseList) {
        RolePermissionVO.App app = new RolePermissionVO.App();

        Optional<Y9App> y9AppOptional = y9AppService.findById(appId);
        if (y9AppOptional.isPresent()) {
            app.setAppName(y9AppOptional.get().getName());
        } else if (InitDataConsts.TOP_PUBLIC_ROLE_ID.equals(appId)) {
            app.setAppName("公共角色");
        }
        app.setPermissionDetailList(buildPermissionDetailList(y9IdentityToRoleBaseList));
        return app;
    }

    private List<RolePermissionVO.App> buildAppList(List<Y9IdentityToRoleBase> y9IdentityToRoleBaseList) {
        List<RolePermissionVO.App> appList = new ArrayList<>();
        Map<String, List<Y9IdentityToRoleBase>> appIdY9IdentityToRoleBaseListMap =
            y9IdentityToRoleBaseList.stream().collect(Collectors.groupingBy(Y9IdentityToRoleBase::getAppId));
        for (Map.Entry<String, List<Y9IdentityToRoleBase>> entry : appIdY9IdentityToRoleBaseListMap.entrySet()) {
            appList.add(buildApp(entry.getKey(), entry.getValue()));
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

    private RolePermissionVO buildRolePermissionVO(List<Y9IdentityToRoleBase> y9IdentityToRoleBaseList) {
        RolePermissionVO rolePermissionVO = new RolePermissionVO();
        rolePermissionVO.setSystemCnName(y9IdentityToRoleBaseList.get(0).getSystemCnName());
        rolePermissionVO.setAppList(buildAppList(y9IdentityToRoleBaseList));
        return rolePermissionVO;
    }

    public List<RolePermissionVO> buildRolePermissionVOList(List<Y9IdentityToRoleBase> y9IdentityToRoleBaseList) {
        List<RolePermissionVO> rolePermissionVOList = new ArrayList<>();
        Map<String, List<Y9IdentityToRoleBase>> systemNameY9IdentityToRoleBaseListMap =
            y9IdentityToRoleBaseList.stream().collect(Collectors.groupingBy(Y9IdentityToRoleBase::getSystemName));
        for (Map.Entry<String, List<Y9IdentityToRoleBase>> entry : systemNameY9IdentityToRoleBaseListMap.entrySet()) {
            rolePermissionVOList.add(buildRolePermissionVO(entry.getValue()));
        }
        return rolePermissionVOList;
    }
}
