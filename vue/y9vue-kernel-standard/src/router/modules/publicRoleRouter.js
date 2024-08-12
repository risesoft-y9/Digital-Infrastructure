/*
 * @Author: haifengy
 * @Date: 2022-03-28 09:49:09
 * @LastEditors: error: git config user.name && git config user.email & please set dead value or install git
 * @LastEditTime: 2022-07-05 16:05:03
 * @Description:
 */
import y9_storage from '@/utils/storage';

const managerLevel = y9_storage.getObjectItem('ssoUserInfo', 'managerLevel');
const publicRoleRouter = {
    path: '/publicRole',
    component: () => import('@/layouts/index.vue'),
    redirect: '/publicRole',
    name: 'publicRole',
    meta: {
        title: managerLevel === 2 ? '公共角色关联' : '公共角色管理',
        roles: ['systemAdmin', 'securityAdmin']
    },
    children: [
        {
            path: '/publicRole',
            component: () => import('@/views/role/publicIndex.vue'),
            name: 'publicRoleIndex',
            meta: {
                title: managerLevel === 2 ? '公共角色关联' : '公共角色管理',
                icon: 'ri-contacts-line',
                roles: ['systemAdmin', 'securityAdmin']
            }
        }
    ]
};

export default publicRoleRouter;
