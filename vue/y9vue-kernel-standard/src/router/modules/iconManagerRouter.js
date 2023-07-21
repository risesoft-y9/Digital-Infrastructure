/*
 * @Author: haifengy
 * @Date: 2022-03-28 09:49:09
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-07 18:07:15
 * @Description: 
 */

const iconManagerRouter = {
    path: '/iconManager',
    component: () => import("@/layouts/index.vue"),
    redirect: '/iconManager',
    name: 'iconManager',
    meta: {
        title: '图标库管理',
        roles: ['systemAdmin'] 
    },
    children: [
        {
            path: '/iconManager',
            component: () => import("@/views/iconManager/index.vue"),
            name: 'iconManagerIndex',
            meta: {
                title: '图标库管理',
                icon: 'ri-remixicon-line',
                roles: ['systemAdmin'] 
            }
        }
    ]
};

export default iconManagerRouter;