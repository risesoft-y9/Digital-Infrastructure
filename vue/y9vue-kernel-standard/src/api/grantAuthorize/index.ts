import Request from '@/api/lib/request';
import qs from 'qs';

const grantRequest = Request();

// 根据资源 id 获取关联的角色授权列表
export const getRelationRoleList = async (id, roleName, authority) => {
    return await grantRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/authorization/listRelateRole',
        url: '/api/rest/authorization/listRelateRole',
        method: 'get',
        cType: false,
        params: { resourceId: id, roleName: roleName, authority: authority }
    });
};

// 根据资源 id 获取继承的的角色授权列表
export const getInheritRoleList = async (id) => {
    return await grantRequest({
        url: '/api/rest/authorization/listInheritRole',
        method: 'get',
        cType: false,
        params: { resourceId: id }
    });
};

// 根据资源 id 获取继承的的组织节点授权列表
export const getInheritOrgList = async (id) => {
    return await grantRequest({
        url: '/api/rest/authorization/listInheritOrg',
        method: 'get',
        cType: false,
        params: { resourceId: id }
    });
};

// 移除 角色 组织 关联
export const removeRole = async (ids) => {
    let data = qs.stringify({ ids });
    return await grantRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/authorization/remove',
        url: '/api/rest/authorization/remove',
        method: 'post',
        cType: false,
        data
    });
};

// 根据 资源 id 获取 关联的组织列表
export const getRelationOrgList = async (id) => {
    return await grantRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/authorization/listRelateOrgList',
        url: '/api/rest/authorization/listRelateOrgList',
        method: 'get',
        cType: false,
        params: { resourceId: id }
    });
};

//  保存资源 关联 角色信息
export const saveOrUpdateRole = async (params, roleIds) => {
    const formData = new FormData();
    formData.append('roleIds', roleIds);
    // 请求加入新参数
    for (var prop in params) {
        if (Object.prototype.hasOwnProperty.call(params, prop)) {
            formData.append(prop, params[prop]);
        }
    }
    return await grantRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/authorization/saveOrUpdateRole',
        url: '/api/rest/authorization/saveOrUpdateRole',
        method: 'POST',
        cType: false,
        data: formData
    });
};

// 保存 资源关联 组织信息
export const saveOrUpdateOrg = async (params, orgIds) => {
    const formData = new FormData();
    formData.append('orgIds', orgIds);
    // 请求加入新参数
    for (var prop in params) {
        if (Object.prototype.hasOwnProperty.call(params, prop)) {
            formData.append(prop, params[prop]);
        }
    }
    return await grantRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/authorization/saveOrUpdateOrg',
        url: '/api/rest/authorization/saveOrUpdateOrg',
        method: 'POST',
        cType: false,
        data: formData
    });
};
