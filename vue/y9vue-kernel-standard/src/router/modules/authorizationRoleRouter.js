const authorizationRoleRouter = {
    path: '/authorizationRole',
    component: () => import('@/layouts/index.vue'),
    redirect: '/authorizationRole',
    name: 'authorizationRole',
    meta: {
        title: '应用角色关联',
        roles: ['securityAdmin', 'subSecurityAdmin']
    },
    children: [
        {
            path: '/authorizationRole',
            component: () => import('@/views/authorizationRole/index.vue'),
            name: 'authorizationRoleIndex',
            meta: {
                title: '应用角色关联',
                icon: 'ri-contacts-line',
                roles: ['securityAdmin', 'subSecurityAdmin']
            }
        }
    ]
};

export default authorizationRoleRouter;
