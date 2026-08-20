/*
 * @Author: haifengy
 * @Date: 2022-03-28 09:49:09
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-07 18:07:15
 * @Description:
 */

const appRoleRouter = {
    path: '/role',
    component: () => import('@/layouts/index.vue'),
    redirect: '/role',
    name: 'role',
    meta: {
        title: '应用角色管理',
        roles: ['systemAdmin', 'subSystemAdmin']
    },
    children: [
        {
            path: '/role',
            component: () => import('@/views/role/index.vue'),
            name: 'roleIndex',
            meta: {
                title: '应用角色管理',
                icon: 'ri-contacts-line',
                roles: ['systemAdmin', 'subSystemAdmin']
            }
        }
    ]
};

export default appRoleRouter;
