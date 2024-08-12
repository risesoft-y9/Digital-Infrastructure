/*
 * @Author: hongzhew
 * @Date: 2022-03-31 20:14:43
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-07 17:46:04
 * @Description:
 */
const positionRouter = {
    path: '/position',
    component: () => import('@/layouts/index.vue'),
    redirect: '/position',
    name: 'position',
    meta: {
        title: '组织岗位',
        roles: ['systemAdmin', 'subSystemAdmin']
    },
    children: [
        {
            path: '/position',
            component: () => import('@/views/position/index.vue'),
            name: 'positionIndex',
            meta: {
                title: '组织岗位',
                icon: 'ri-shield-user-line',
                roles: ['systemAdmin', 'subSystemAdmin']
            },
            children: [
                // {
                //     path: '/position/edit',
                //     component: () => import('@/views/position/edit.vue'),
                //     name: 'positionEdit',
                //     hidden: true,
                //     props: true,
                //     meta: {
                //         title: '编辑查看',
                //         icon: 'ri-organization-chart',
                //         roles: ['admin', 'deptAdmin'],
                //         transition: 'slide-left'
                //     },
                // },
            ]
        }
    ]
};

export default positionRouter;
