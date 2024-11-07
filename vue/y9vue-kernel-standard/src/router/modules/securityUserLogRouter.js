/*
 * @Author: haifengy
 * @Date: 2022-03-28 09:49:09
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-07 18:07:15
 * @Description:
 */

const securityUserLogRouter = {
    path: '/securityUser',
    component: () => import('@/layouts/index.vue'),
    redirect: '/securityUser/loginLogs',
    name: 'securityUser',
    meta: {
        title: '安全保密员日志',
        icon: 'ri-file-lock-line',
        roles: ['auditAdmin', 'subAuditAdmin']
    },
    children: [
        {
            path: '/securityUser/loginLogs',
            component: () => import('@/views/y9log/securityUserLog/loginLogs.vue'),
            name: 'securityUserIndex-loginLogs',
            meta: {
                title: '登录日志',
                icon: 'ri-file-lock-line',
                roles: ['auditAdmin', 'subAuditAdmin']
            }
        },
        {
            path: '/securityUser/operationLogs',
            component: () => import('@/views/y9log/securityUserLog/operationLogs.vue'),
            name: 'securityUserIndex-operationLogs',
            meta: {
                title: '操作日志',
                icon: 'ri-file-lock-line',
                roles: ['auditAdmin', 'subAuditAdmin']
            }
        }
    ]
};

export default securityUserLogRouter;
