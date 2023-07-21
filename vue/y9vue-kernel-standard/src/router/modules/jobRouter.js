/*
 * @Author: haifengy
 * @Date: 2022-03-28 09:49:09
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-07 17:59:34
 * @Description:
 */

const jobRouter = {
    path: '/jobManage',
    component: () => import("@/layouts/index.vue"),
    redirect: '/jobManage',
    name: 'jobManage',
    meta: {
        title: '职位管理',
        icon: 'ri-folder-user-line',
        roles: ['systemAdmin','subSystemAdmin']
    },
    children: [
        {
            path: '/jobManage',
            component: () => import("@/views/job/index.vue"),
            name: 'jobManage',
            meta: {
                title: '职位管理',
                icon: 'ri-folder-user-line',
                roles: ['systemAdmin','subSystemAdmin']
            }
        }
    ]
};

export default jobRouter;
