/*
 * @Author: haifengy
 * @Date: 2022-03-28 09:49:09
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-09-17 16:01:20
 * @Description:
 */

const appSystemRouter = {
    path: '/system',
    component: () => import('@/layouts/index.vue'),
    redirect: '/system',
    name: 'system',
    meta: {
        title: '应用系统管理',
        roles: ['systemAdmin', 'subSystemAdmin', 'systemVendor']
    },
    children: [
        {
            path: '/system',
            component: () => import('@/views/system/index.vue'),
            name: 'systemIndex',
            meta: {
                title: '应用系统管理',
                icon: 'ri-apps-line',
                roles: ['systemAdmin', 'subSystemAdmin']
            }
        },
        {
            path: '/system',
            component: () => import('@/views/system/index4SystemVendor.vue'),
            name: 'index4SystemVendor',
            meta: {
                title: '应用系统管理',
                icon: 'ri-apps-line',
                roles: ['systemVendor']
            }
        }
    ]
};

export default appSystemRouter;
