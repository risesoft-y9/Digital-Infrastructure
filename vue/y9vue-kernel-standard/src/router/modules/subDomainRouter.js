/*
 * @Author: haifengy
 * @Date: 2022-03-28 09:49:09
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-07 18:07:15
 * @Description: 
 */

const subDomainRouter = {
    path: '/domain',
    component: () => import("@/layouts/index.vue"),
    redirect: '/domain',
    name: 'domain',
    meta: {
        title: '子域三员管理',
        roles: ['securityAdmin','systemAdmin'] 
    },
    children: [
        {
            path: '/domain',
            component: () => import("@/views/domain/index.vue"),
            name: 'domainIndex',
            meta: {
                title: '子域三员管理',
                icon: 'ri-picture-in-picture-line',
                roles: ['securityAdmin','systemAdmin'] 
            }
        }
    ]
};

export default subDomainRouter;