package net.risesoft.controller.identity.vo;

import lombok.Data;

import java.util.List;

/**
 * 主体的拥有的某个系统所有角色权限详情
 * 
 * @author shidaobang
 * @date 2023/07/20
 * @since 9.6.2
 */
@Data
public class RolePermissionVO {

    /** 系统中文名称 */
    private String systemCnName;
    
    /** 资源列表 */
    private List<App> appList;

    @Data
    public static class App {
        /** 应用名 */
        private String name;

        /** 权限详细列表 */
        private List<PermissionDetail> permissionDetailList;
    }
    
    @Data
    public static class PermissionDetail {
        /** 角色名 */
        private String roleName;
        
        /** 角色描述 */
        private String roleDescription;
    }
}
