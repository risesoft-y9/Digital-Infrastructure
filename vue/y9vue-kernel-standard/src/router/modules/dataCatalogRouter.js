import y9_storage from '@/utils/storage';

const managerLevel = y9_storage.getObjectItem('ssoUserInfo', 'managerLevel');
const dataCatalogRouter = {
    path: '/dataCatalog',
    component: () => import('@/layouts/index.vue'),
    redirect: '/dataCatalog',
    name: 'dataCatalog',
    meta: {
        title: managerLevel === 2 ? '数据目录授权' : '数据目录管理',
        roles: ['systemAdmin', 'subSystemAdmin', 'securityAdmin']
    },
    children: [
        {
            path: '/dataCatalog',
            component: () => import('@/views/dataCatalog/index.vue'),
            name: 'dataCatalogIndex',
            meta: {
                title: managerLevel === 2 ? '数据目录授权' : '数据目录管理',
                icon: 'ri-file-list-2-line',
                roles: ['systemAdmin', 'subSystemAdmin', 'securityAdmin']
            }
        }
    ]
};

export default dataCatalogRouter;
