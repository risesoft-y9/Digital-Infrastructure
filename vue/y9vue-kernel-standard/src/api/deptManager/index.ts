/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2022-06-27 10:48:32
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-06-27 10:48:32
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-kernel-dcat-style\src\api\deptManager\index.ts
 */
import Request from '@/api/lib/request';
import qs from 'qs';

const platformRequest = Request();

/**
 * 设置部门管理员
 * @param params
 * @returns
 */
export const saveOrUpdate = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/deptManager/saveOrUpdate',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 根据父节点id，获取人员列表
 * @param {*} parentID
 * @returns
 */
export const getManagersByParentId = async (parentId) => {
    return await platformRequest({
        url: '/api/rest/deptManager/listManagersByParentId',
        method: 'GET',
        cType: false,
        params: { parentId: parentId }
    });
};

/**
 * 批量删除人员
 * @param {ids:[]} IDs
 */
export const delManager = async (ids) => {
    const params = {
        ids: ids
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/deptManager/remove',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 根据用户id，获取用户信息
 * @param {*} managerId 人员id
 * @returns
 */
export const getManagerById = async (managerId) => {
    return await platformRequest({
        url: '/api/rest/deptManager/getManagerById',
        method: 'GET',
        cType: false,
        params: { managerId: managerId }
    });
};

/**
 * 判断登录名是否可用
 * @param {*} personID
 * @param {*} loginName
 * @returns
 */
export const loginNameCheck = async (personId, loginName) => {
    return await platformRequest({
        url: '/api/rest/deptManager/checkLoginName',
        method: 'GET',
        cType: false,
        params: { personId: personId, loginName: loginName }
    });
};

/**
 * 重置密码
 * @param {*} personID
 * @returns
 */
export const resetPassword = async (personId) => {
    const params = {
        personId: personId
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/deptManager/resetPassword',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 验证是否为该部门的管理员
 */
export const checkDeptManager = async (deptId) => {
    const params = {
        deptId: deptId
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/deptManager/checkDeptManager',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 禁用/解禁人员
 * @param {*} ID
 * @returns
 */
export const changeDisabled = async (id) => {
    const params = {
        id: id
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/deptManager/changeDisabled',
        method: 'POST',
        cType: false,
        data: data
    });
};
