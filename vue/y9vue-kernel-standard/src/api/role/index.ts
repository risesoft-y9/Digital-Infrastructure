import Request from '@/api/lib/request';
import qs from 'qs';

const roleRequest = Request();

// 角色树
export const roleTree = async (params) => {
    return await roleRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/role/tree',
        url: '/api/rest/role/tree',
        method: 'get',
        cType: false,
        params
    });
};

// 角色二级树
export const roleTreeList = async (params) => {
    return await roleRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/role/tree',
        url: '/api/rest/role/listByParentId2',
        method: 'get',
        cType: false,
        params
    });
};

// 根据角色名称，查询角色节点 搜索角色树
export const treeSelect = async (value) => {
    const params = { name: value.key };
    return await roleRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/role/treeSearch',
        url: '/api/rest/role/treeSearch2',
        method: 'get',
        cType: false,
        params
    });
};

// 根据角色id 获取信息
export const getRoleInfo = async (id) => {
    return await roleRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/role/getRoleById'
        url: '/api/rest/role/getRoleById',
        method: 'get',
        cType: false,
        params: { id }
    });
};

// 新建或 更新 角色 节点信息
export const saveOrUpdate = async (params) => {
    const data = qs.stringify(params);
    return await roleRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/role/saveOrUpdate',
        url: '/api/rest/role/saveOrUpdate',
        method: 'post',
        cType: false,
        data
    });
};

// 保存资源的排序
export const saveOrder = async (ids) => {
    const data = qs.stringify({ ids });
    return await roleRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/role/saveOrder',
        url: '/api/rest/role/saveOrder',
        method: 'post',
        cType: false,
        data
    });
};

// 角色的移动
export const saveMoveRole = async (params) => {
    const data = qs.stringify(params);
    return await roleRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/role/saveMove',
        url: '/api/rest/role/saveMove',
        method: 'post',
        cType: false,
        data
    });
};

// 根据角色 删除角色
export const deleteRoleById = async (id) => {
    return await roleRequest({
        url: '/api/rest/role/deleteById',
        method: 'POST',
        cType: false,
        params: { id: id }
    });
};

// 根据 名称和 所属部门 查找角色成员
export const searchByUnitNameAndUnitDN = async (roleId, unitName, unitDN) => {
    return await roleRequest({
        url: '/api/rest/orgBasesToRoles/searchByUnitNameAndUnitDN',
        method: 'GET',
        cType: false,
        params: { roleId: roleId, unitName: unitName, unitDn: unitDN }
    });
};

// 添加组织机构节点 对此角色的映射
export const addOrgUnits = async (roleNodeID, orgUnitIDs, negative) => {
    const params = {
        roleId: roleNodeID,
        orgUnitIds: orgUnitIDs,
        negative: negative
    };
    const data = qs.stringify(params);
    return await roleRequest({
        url: '/api/rest/orgBasesToRoles/addOrgUnits',
        method: 'POST',
        cType: false,
        data: data
    });
};

// 对此角色 中移除组织机构节点
export const removeOrgUnits = async (ids) => {
    const params = {
        ids: ids
    };
    const data = qs.stringify(params);
    return await roleRequest({
        url: '/api/rest/orgBasesToRoles/remove',
        method: 'POST',
        cType: false,
        data
    });
};

// 根据角色ID获取权限列表
export const getRelateResourceList = async (roleId, page, size) => {
    const data = qs.stringify({
        roleId,
        page,
        size
    });
    return await roleRequest({
        url: '/api/rest/authorization/listRelateResource',
        method: 'POST',
        cType: false,
        data
    });
};

// 移除角色授权许可记录
export const removeAuthPermissionRecord = async (params) => {
    const data = qs.stringify(params);
    return await roleRequest({
        url: '/api/rest/authorization/remove',
        method: 'POST',
        cType: false,
        data
    });
};

// 保存关联资源权限许可对象
export const saveOrUpdateRelateResource = async (params) => {
    const data = qs.stringify(params);
    return await roleRequest({
        url: '/api/rest/authorization/saveOrUpdate',
        method: 'POST',
        cType: false,
        data
    });
};

// 获取公共角色树
export const getPublicRoleTree = async () => {
    return await roleRequest({
        // url: `http://127.0.0.1:4523/mock/891645/platform/api/rest/publicRole/treeRoot`,
        url: `/api/rest/publicRole/treeRoot2`,
        method: 'get',
        cType: false
    });
};

// 根据角色名称，查询公共角色节点
export const publicTreeSearch = async (value) => {
    const params = { name: value.key };
    return await roleRequest({
        url: '/api/rest/publicRole/treeSearch2',
        method: 'GET',
        cType: false,
        params
    });
};
