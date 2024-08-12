/*
 * @Author: haifengy
 * @Date: 2022-03-28 09:49:09
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-07 18:07:15
 * @Description:
 */
import y9_storage from '@/utils/storage';

const managerLevel = y9_storage.getObjectItem('ssoUserInfo', 'managerLevel');
const appRoleRouter = {
    path: '/role',
    component: () => import('@/layouts/index.vue'),
    redirect: '/role',
    name: 'role',
    meta: {
        title: managerLevel === 2 ? '应用角色关联' : '应用角色管理',
        roles: ['systemAdmin', 'subSystemAdmin', 'securityAdmin']
    },
    children: [
        {
            path: '/role',
            component: () => import('@/views/role/index.vue'),
            name: 'roleIndex',
            meta: {
                title: managerLevel === 2 ? '应用角色关联' : '应用角色管理',
                icon: 'ri-contacts-line',
                roles: ['systemAdmin', 'subSystemAdmin', 'securityAdmin']
            }
        }
    ]
};

export default appRoleRouter;
