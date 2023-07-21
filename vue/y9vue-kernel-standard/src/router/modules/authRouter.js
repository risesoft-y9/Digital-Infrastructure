
const authRouter = [{
    path: '/auth',
    redirect: '/sysManagerLog',
    name: 'authOrg',
    hidden: true,
    meta: {
        title: '安全审计员',
        icon: 'el-icon-s-custom',
        roles: ['auditAdmin','subAuditAdmin'] // 数组，只有一个元素，且在这个文件中是唯一的权限名称
    }
},
{
    path: '/auth',
    redirect: '/grantAuthorize',
    name: 'authOrg',
    hidden: true,
    meta: {
        title: '安全保密员',
        icon: 'el-icon-s-custom',
        roles: ['securityAdmin','subSecurityAdmin'] // 数组，只有一个元素，且在这个文件中是唯一的权限名称
    }
},
{
    path: '/auth',
    redirect: '/home',
    name: 'authOrg',
    hidden: true,
    meta: {
        title: '系统管理员',
        icon: 'el-icon-s-custom',
        roles: ['systemAdmin','subSystemAdmin'],
        notShowAdmin: true
    }
}
];


export default authRouter;