const apiAccessControlRouter = {
    path: '/apiAccessControl',
    component: () => import('@/layouts/index.vue'),
    redirect: '/apiAccessControl',
    name: 'apiAccessControl',
    meta: {
        title: '安全控制',
        roles: ['systemAdmin', 'securityAdmin'],
        icon: 'ri-book-2-line'
    },
    children: [
        {
            path: '/apiAccessControl/whiteList',
            component: () => import('@/views/apiAccessControl/whiteList.vue'),
            name: 'apiAccessControlWhiteList',
            meta: {
                title: '白名单',
                icon: 'ri-book-2-line',
                roles: ['systemAdmin', 'securityAdmin']
            }
        },
        {
            path: '/apiAccessControl/blackList',
            component: () => import('@/views/apiAccessControl/blackList.vue'),
            name: 'apiAccessControlBlackList',
            meta: {
                title: '黑名单',
                icon: 'ri-book-2-line',
                roles: ['systemAdmin', 'securityAdmin']
            }
        },
        {
            path: '/apiAccessControl/appIdSecretList',
            component: () => import('@/views/apiAccessControl/appIdSecret.vue'),
            name: 'apiAccessControlAppIdSecretList',
            meta: {
                title: '密钥',
                icon: 'ri-book-2-line',
                roles: ['systemAdmin', 'securityAdmin']
            }
        },
    ]
};

export default apiAccessControlRouter;
