package net.risesoft.controller.identity;

import lombok.RequiredArgsConstructor;
import net.risesoft.controller.identity.vo.RolePermissionVO;
import net.risesoft.entity.identity.Y9IdentityToRoleBase;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.service.resource.Y9AppService;
import net.risesoft.y9public.service.role.Y9RoleService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    
    public List<RolePermissionVO> buildRolePermissionVOList(List<Y9IdentityToRoleBase> y9IdentityToRoleBaseList) {
        List<RolePermissionVO> rolePermissionVOList = new ArrayList<>();
        Map<String, List<Y9IdentityToRoleBase>> systemNameY9IdentityToRoleBaseListMap
                = y9IdentityToRoleBaseList.stream().collect(Collectors.groupingBy(Y9IdentityToRoleBase::getSystemName));
        for (Map.Entry<String, List<Y9IdentityToRoleBase>> entry : systemNameY9IdentityToRoleBaseListMap.entrySet()) {
            rolePermissionVOList.add(buildRolePermissionVO(entry.getValue()));
        }
        return rolePermissionVOList;
    }

    private RolePermissionVO buildRolePermissionVO(List<Y9IdentityToRoleBase> y9IdentityToRoleBaseList) {
        RolePermissionVO rolePermissionVO = new RolePermissionVO();
        rolePermissionVO.setSystemCnName(y9IdentityToRoleBaseList.get(0).getSystemCnName());
        rolePermissionVO.setAppList(buildAppList(y9IdentityToRoleBaseList));
        return rolePermissionVO;
    }

    private List<RolePermissionVO.App> buildAppList(List<Y9IdentityToRoleBase> y9IdentityToRoleBaseList) {
        List<RolePermissionVO.App> appList = new ArrayList<>();
        Map<String, List<Y9IdentityToRoleBase>> appIdY9IdentityToRoleBaseListMap
                = y9IdentityToRoleBaseList.stream().collect(Collectors.groupingBy(Y9IdentityToRoleBase::getAppId));
        for (Map.Entry<String, List<Y9IdentityToRoleBase>> entry : appIdY9IdentityToRoleBaseListMap.entrySet()) {
            appList.add(buildApp(entry.getKey(), entry.getValue()));
        }
        return appList;
    }

    private RolePermissionVO.App buildApp(String appId, List<Y9IdentityToRoleBase> y9IdentityToRoleBaseList) {
        RolePermissionVO.App app = new RolePermissionVO.App();
        Y9App y9App = y9AppService.findById(appId);
        if (y9App != null) {
            app.setName(app.getName());
            app.setPermissionDetailList(buildPermissionDetailList(y9IdentityToRoleBaseList));
        }
        return app;
    }

    private List<RolePermissionVO.PermissionDetail> buildPermissionDetailList(List<Y9IdentityToRoleBase> y9IdentityToRoleBaseList) {
        List<RolePermissionVO.PermissionDetail> permissionDetailList = new ArrayList<>();
        for (Y9IdentityToRoleBase y9IdentityToRoleBase : y9IdentityToRoleBaseList) {
            RolePermissionVO.PermissionDetail permissionDetail = new RolePermissionVO.PermissionDetail();
            Y9Role y9Role = y9RoleService.findById(y9IdentityToRoleBase.getRoleId());
            if (y9Role != null) {
                permissionDetail.setRoleName(y9Role.getName());
                permissionDetail.setRoleDescription(y9Role.getDescription());
                permissionDetailList.add(permissionDetail);
            }
        }
        return permissionDetailList;
    }
}
