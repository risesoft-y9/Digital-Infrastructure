/*
 * @Author: shidaobang
 * @Date: 2024-04-07 09:27:33
 * @LastEditors: mengjuhua
 * @Description:
 */
const settingRouter = {
    path: '/setting',
    component: () => import('@/layouts/index.vue'),
    redirect: '/setting',
    name: 'setting',
    meta: {
        title: '系统设置',
        icon: 'ri-settings-line',
        roles: ['systemAdmin']
    },
    children: [
        {
            path: '/setting',
            component: () => import('@/views/setting/index.vue'),
            name: 'settingIndex',
            meta: {
                title: '系统设置',
                icon: 'ri-settings-line',
                roles: ['systemAdmin']
            }
        },
        {
            path: '/y9TenantInfo',
            component: () => import('@/views/tenant/index.vue'),
            name: 'y9TenantInfo',
            meta: {
                title: '个性化设置',
                icon: 'ri-contacts-line',
                roles: ['systemAdmin']
            }
        }
    ]
};

export default settingRouter;
