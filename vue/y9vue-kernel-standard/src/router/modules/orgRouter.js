/*
 * @Author: haifengy
 * @Date: 2022-03-28 09:49:09
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-07 17:59:34
 * @Description:
 */

const orgRouter = {
    path: '/org',
    component: () => import('@/layouts/index.vue'),
    redirect: '/org',
    name: 'org',
    meta: {
        title: '组织架构',
        roles: ['systemAdmin','subSystemAdmin'],
    },
    children: [
        {
            path: '/org',
            component: () => import('@/views/org/index.vue'),
            name: 'orgIndex',
            meta: {
                title: '组织架构',
                icon: 'ri-node-tree',
                roles: ['systemAdmin','subSystemAdmin'],
            },
            children: [
                // {
                //     path: '/org/edit',
                //     component: () => import('@/views/org/edit.vue'),
                //     name: 'orgEdit',
                //     hidden: true,
                //     props: true, 
                //     meta: {
                //         title: '编辑查看',
                //         icon: 'ri-organization-chart',
                //         roles: ['admin', 'deptAdmin'],
                //         transition: 'slide-left'
                //     },
                // },
            ],
        },
    ],
};

export default orgRouter;
