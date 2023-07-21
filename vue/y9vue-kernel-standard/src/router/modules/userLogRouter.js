/*
 * @Author: haifengy
 * @Date: 2022-03-28 09:49:09
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-07 18:07:15
 * @Description: 
 */

const userLogRouter = {
    path: '/userLog',
    component: () => import("@/layouts/index.vue"),
    redirect: '/userLog/loginLogs',
    name: 'userLog',
    meta: {
        title: '用户日志',
        icon: 'ri-file-user-line',
        roles: ['securityAdmin','subSecurityAdmin']
    },
    children: [
        {
            path: '/userLog/loginLogs',
            component: () => import("@/views/userLog/loginLogs.vue"),
            name: 'userLogIndex-loginLogs',
            meta: {
                title: '登录日志',
                icon: 'ri-file-lock-line',
                roles: ['securityAdmin','subSecurityAdmin']
            }
        },
        {
            path: '/userLog/operationLogs',
            component: () => import("@/views/userLog/operationLogs.vue"),
            name: 'userLogIndex-operationLogs',
            meta: {
                title: '操作日志',
                icon: 'ri-file-lock-line',
                roles: ['securityAdmin','subSecurityAdmin']
            }
        }
    ]
};

export default userLogRouter;