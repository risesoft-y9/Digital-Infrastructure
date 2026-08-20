const authorizationDataCatalogRouter = {
    path: '/authorizationDataCatalog',
    component: () => import('@/layouts/index.vue'),
    redirect: '/authorizationDataCatalog',
    name: 'authorizationDataCatalog',
    meta: {
        title: '数据目录授权',
        roles: ['securityAdmin', 'subSecurityAdmin']
    },
    children: [
        {
            path: '/authorizationDataCatalog',
            component: () => import('@/views/authorizationDataCatalog/index.vue'),
            name: 'authorizationDataCatalogIndex',
            meta: {
                title: '数据目录授权',
                icon: 'ri-file-list-2-line',
                roles: ['securityAdmin', 'subSecurityAdmin']
            }
        }
    ]
};

export default authorizationDataCatalogRouter;
