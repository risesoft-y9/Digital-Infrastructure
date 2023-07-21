/*
 * @Author: haifengy
 * @Date: 2022-03-28 09:49:09
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-07 18:07:15
 * @Description: 
 */

const personalRouter = {
    path: '/personal',
    component: () => import("@/layouts/index.vue"),
    redirect: '/personInfo',
    name: 'personal',
    hidden: true,
    meta: {
        title: '个人中心',
        roles: ['systemAdmin','securityAdmin','auditAdmin','subSystemAdmin','subSecurityAdmin','subAuditAdmin'] 
    },
    children: [
        {
            path: '/personInfo',
            component: () => import("@/views/personal/index.vue"),
            name: 'personInfo',
            hidden: true,
            meta: {
                title: '个人中心',
                icon: 'ri-contacts-line',
                roles: ['systemAdmin','securityAdmin','auditAdmin','subSystemAdmin','subSecurityAdmin','subAuditAdmin'] 
            }
        }
    ]
};

export default personalRouter;