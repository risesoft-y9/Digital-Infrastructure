/*
 * @Author: haifengy
 * @Date: 2022-03-28 09:49:09
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-07 18:07:15
 * @Description: 
 */

const sysManagerLogRouter = {
    path: '/sysManagerLog',
    component: () => import("@/layouts/index.vue"),
    redirect: '/sysManagerLog/loginLogs',
    name: 'sysManagerLog',
    meta: {
        title: '系统管理员日志',
        icon: 'ri-file-settings-line',
        roles: ['auditAdmin','subAuditAdmin']
    },
    children: [
        {
            path: '/sysManagerLog/loginLogs',
            component: () => import('@/views/sysManagerLog/loginLogs.vue'),
            name: 'sysManagerLogIndex-loginLogs',
            meta: {
                title: '登录日志',
                icon: 'ri-file-lock-line',
                roles: ['auditAdmin','subAuditAdmin']
            }
        }, {
            path: '/sysManagerLog/operationLogs',
            component: () => import('@/views/sysManagerLog/operationLogs.vue'),
            name: 'sysManagerLogIndex-operationLogs',
            meta: {
                title: '操作日志',
                icon: 'ri-file-lock-line',
                roles: ['auditAdmin','subAuditAdmin']
            }
        },
        // {
        //     path: '/securityUser/orgLog',
        //     component: () => import("@/views/orgLog/index.vue"),
        //     name: 'securityUserIndex-orgLog',
        //     meta: {
        //         title: '管理操作日志',
        //         icon: 'ri-file-lock-line',
        //         roles: ['auditAdmin','subAuditAdmin']
        //     }
        // }
    ]
};

export default sysManagerLogRouter;