/*
 * @Author: haifengy
 * @Date: 2022-03-28 09:49:09
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-07 18:07:15
 * @Description:
 */

const appResourceRouter = {
    path: '/resource',
    component: () => import('@/layouts/index.vue'),
    redirect: '/resource',
    name: 'resource',
    meta: {
        title: '应用资源管理',
        roles: ['systemAdmin', 'subSystemAdmin']
    },
    children: [
        {
            path: '/resource',
            component: () => import('@/views/resource/index.vue'),
            name: 'resourceIndex',
            meta: {
                title: '应用资源管理',
                icon: 'ri-dashboard-line',
                roles: ['systemAdmin', 'subSystemAdmin']
            }
        }
    ]
};

export default appResourceRouter;
