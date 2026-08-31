const systemVendorRouter = {
    path: '/systemVendor',
    component: () => import('@/layouts/index.vue'),
    redirect: '/systemVendor',
    name: 'systemVendor',
    meta: {
        title: '系统开发商',
        roles: ['systemAdmin']
    },
    children: [
        {
            path: '/systemVendor',
            component: () => import('@/views/systemVendor/index.vue'),
            name: 'systemVendorIndex',
            meta: {
                title: '系统开发商',
                icon: 'ri-admin-line',
                roles: ['systemAdmin']
            }
        }
    ]
};

export default systemVendorRouter;
