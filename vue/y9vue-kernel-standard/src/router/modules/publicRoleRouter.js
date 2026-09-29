/*
 * @Author: haifengy
 * @Date: 2022-03-28 09:49:09
 * @LastEditors: error: git config user.name && git config user.email & please set dead value or install git
 * @LastEditTime: 2022-07-05 16:05:03
 * @Description:
 */

const publicRoleRouter = {
    path: '/publicRole',
    component: () => import('@/layouts/index.vue'),
    redirect: '/publicRole',
    name: 'publicRole',
    meta: {
        title: '公共角色管理',
        roles: ['systemAdmin']
    },
    children: [
        {
            path: '/publicRole',
            component: () => import('@/views/role/publicIndex.vue'),
            name: 'publicRoleIndex',
            meta: {
                title: '公共角色管理',
                icon: 'ri-contacts-line',
                roles: ['systemAdmin']
            }
        }
    ]
};

export default publicRoleRouter;
