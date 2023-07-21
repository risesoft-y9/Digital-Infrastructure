/*
 * @Author: haifengy
 * @Date: 2022-03-28 09:49:09
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-07 18:07:15
 * @Description: 
 */

const grantAuthorizeRouter = {
    path: '/grantAuthorize',
    component: () => import("@/layouts/index.vue"),
    redirect: '/grantAuthorize',
    name: 'grantAuthorize',
    meta: {
        title: '授权管理',
        roles: ['securityAdmin','subSecurityAdmin'] 
    },
    children: [
        {
            path: '/grantAuthorize',
            component: () => import("@/views/grantAuthorize/index.vue"),
            name: 'grantAuthorizeIndex',
            meta: {
                title: '授权管理',
                icon: 'ri-admin-line',
                roles: ['securityAdmin','subSecurityAdmin'] 
            }
        }
    ]
};

export default grantAuthorizeRouter;