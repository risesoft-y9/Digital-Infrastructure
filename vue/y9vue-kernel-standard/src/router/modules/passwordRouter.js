const passwordRouter = {
    path: '/password',
    component: () => import('@/views/password/index.vue'),
    name: 'password',
    hidden: true,
    meta: {
        title: '修改密码',
        roles: ['systemAdmin', 'securityAdmin', 'auditAdmin', 'subSystemAdmin', 'subSecurityAdmin', 'subAuditAdmin']
    }
};

export default passwordRouter;
