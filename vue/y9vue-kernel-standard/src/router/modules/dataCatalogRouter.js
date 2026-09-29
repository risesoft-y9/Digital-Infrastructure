const dataCatalogRouter = {
    path: '/dataCatalog',
    component: () => import('@/layouts/index.vue'),
    redirect: '/dataCatalog',
    name: 'dataCatalog',
    meta: {
        title: '数据目录管理',
        roles: ['systemAdmin', 'subSystemAdmin']
    },
    children: [
        {
            path: '/dataCatalog',
            component: () => import('@/views/dataCatalog/index.vue'),
            name: 'dataCatalogIndex',
            meta: {
                title: '数据目录管理',
                icon: 'ri-file-list-2-line',
                roles: ['systemAdmin', 'subSystemAdmin']
            }
        }
    ]
};

export default dataCatalogRouter;
