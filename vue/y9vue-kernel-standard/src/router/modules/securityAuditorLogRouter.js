/*
 * @Author: haifengy
 * @Date: 2022-03-28 09:49:09
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-07 18:07:15
 * @Description:
 */

const securityAuditorLogRouter = {
    path: '/securityAuditorLog',
    component: () => import('@/layouts/index.vue'),
    redirect: '/securityAuditorLog/loginLogs',
    name: 'securityAuditorLog',
    meta: {
        title: '安全审计员日志',
        icon: 'ri-file-shield-line',
        roles: ['securityAdmin', 'subSecurityAdmin']
    },
    children: [
        {
            path: '/securityAuditorLog/loginLogs',
            component: () => import('@/views/securityAuditorLog/loginLogs.vue'),
            name: 'securityAuditorLogIndex-loginLogs',
            meta: {
                title: '登录日志',
                icon: 'ri-file-lock-line',
                roles: ['securityAdmin', 'subSecurityAdmin']
            }
        },
        {
            path: '/securityAuditorLog/operationLogs',
            component: () => import('@/views/securityAuditorLog/operationLogs.vue'),
            name: 'securityAuditorLogIndex-operationLogs',
            meta: {
                title: '操作日志',
                icon: 'ri-file-lock-line',
                roles: ['securityAdmin', 'subSecurityAdmin']
            }
        }
    ]
};

export default securityAuditorLogRouter;
