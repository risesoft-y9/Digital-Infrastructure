const authorizationPublicRoleRouter = {
    path: '/authorizationPublicRole',
    component: () => import('@/layouts/index.vue'),
    redirect: '/authorizationPublicRole',
    name: 'authorizationPublicRole',
    meta: {
        title: '公共角色关联',
        roles: ['securityAdmin', 'subSecurityAdmin']
    },
    children: [
        {
            path: '/authorizationPublicRole',
            component: () => import('@/views/authorizationPublicRole/index.vue'),
            name: 'authorizationPublicRoleIndex',
            meta: {
                title: '公共角色关联',
                icon: 'ri-contacts-line',
                roles: ['securityAdmin', 'subSecurityAdmin']
            }
        }
    ]
};

export default authorizationPublicRoleRouter;
