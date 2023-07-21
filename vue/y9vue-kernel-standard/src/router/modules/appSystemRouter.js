/*
 * @Author: haifengy
 * @Date: 2022-03-28 09:49:09
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-07 18:07:15
 * @Description: 
 */

const appSystemRouter = {
    path: '/system',
    component: () => import("@/layouts/index.vue"),
    redirect: '/system',
    name: 'system',
    meta: {
        title: '应用系统管理',
        roles: ['systemAdmin','subSystemAdmin'] 
    },
    children: [
        {
            path: '/system',
            component: () => import("@/views/system/index.vue"),
            name: 'systemIndex',
            meta: {
                title: '应用系统管理',
                icon: 'ri-apps-line',
                roles: ['systemAdmin','subSystemAdmin'] 
            }
        }
    ]
};

export default appSystemRouter;