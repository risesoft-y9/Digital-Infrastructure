const auditLogRouter = {
    path: '/auditLog',
    component: () => import('@/layouts/index.vue'),
    redirect: '/auditLog/index',
    name: 'auditLog',
    meta: {
        title: '审计日志',
        icon: 'ri-file-lock-line',
        roles: ['auditAdmin', 'subAuditAdmin']
    },
    children: [
        {
            path: '/auditLog/index',
            component: () => import('@/views/y9log/auditLog/index.vue'),
            name: 'auditLogIndex',
            meta: {
                title: '审计日志',
                icon: 'ri-file-lock-line',
                roles: ['auditAdmin', 'subAuditAdmin']
            }
        }
    ]
};

export default auditLogRouter;
