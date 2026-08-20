const authorizationResourceRouter = {
    path: '/authorizationResource',
    component: () => import('@/layouts/index.vue'),
    redirect: '/authorizationResource',
    name: 'authorizationResource',
    meta: {
        title: '应用资源授权',
        roles: ['securityAdmin', 'subSecurityAdmin']
    },
    children: [
        {
            path: '/authorizationResource',
            component: () => import('@/views/authorizationResource/index.vue'),
            name: 'authorizationResourceIndex',
            meta: {
                title: '应用资源授权',
                icon: 'ri-dashboard-line',
                roles: ['securityAdmin', 'subSecurityAdmin']
            }
        }
    ]
};

export default authorizationResourceRouter;
