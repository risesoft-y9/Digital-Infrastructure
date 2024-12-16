/*
 * @Author: haifengy
 * @Date: 2022-03-28 09:49:09
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-07 18:07:15
 * @Description:
 */

const grantAuthorizeRouter = {
    path: '/permission',
    component: () => import('@/layouts/index.vue'),
    redirect: '/permission',
    name: 'permission',
    meta: {
        title: '权限树',
        icon: 'ri-admin-line',
        roles: ['systemAdmin', 'subSystemAdmin', 'securityAdmin', 'subSecurityAdmin']
    },
    children: [
        {
            path: '/resourcePermission',
            component: () => import('@/views/permission/resourcePermission.vue'),
            name: 'resourcePermissionIndex',
            meta: {
                title: '资源权限树',
                icon: 'ri-admin-line',
                roles: ['systemAdmin', 'subSystemAdmin', 'securityAdmin', 'subSecurityAdmin']
            }
        },
        {
            path: '/rolePermission',
            component: () => import('@/views/permission/rolePermission.vue'),
            name: 'rolePermissionIndex',
            meta: {
                title: '角色权限树',
                icon: 'ri-admin-line',
                roles: ['systemAdmin', 'subSystemAdmin', 'securityAdmin', 'subSecurityAdmin']
            }
        }
    ]
};

export default grantAuthorizeRouter;
