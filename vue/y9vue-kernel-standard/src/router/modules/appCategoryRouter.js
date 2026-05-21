const appCategoryRouter = {
    path: '/appCategory',
    component: () => import('@/layouts/index.vue'),
    redirect: '/appCategoryManage',
    name: 'appCategory',
    meta: {
        title: '应用分类',
        roles: ['securityAdmin', 'systemAdmin'],
        icon: 'ri-settings-line'
    },
    children: [
        {
            path: '/appCategoryManage',
            component: () => import('@/views/appCategory/appCategory.vue'),
            name: 'appCategoryManage',
            meta: {
                title: '应用分类管理',
                roles: ['systemAdmin'],
                icon: 'ri-settings-2-line'
            }
        },
        {
            path: '/listByOrgUnit',
            component: () => import('@/views/appCategory/listByOrgUnit.vue'),
            name: 'listByOrgUnit',
            meta: {
                title: '岗位应用权限',
                roles: ['securityAdmin', 'systemAdmin'],
                icon: 'ri-list-settings-line'
            }
        },
        {
            path: '/listByApp',
            component: () => import('@/views/appCategory/listByApp.vue'),
            name: 'listByApp',
            meta: {
                title: '应用权限详情',
                roles: ['securityAdmin', 'systemAdmin'],
                icon: 'ri-list-settings-line'
            }
        }
    ]
};

export default appCategoryRouter;
