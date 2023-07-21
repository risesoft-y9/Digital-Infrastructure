import Request from '@/api/lib/request';
import qs from 'qs';

const platformRequest = Request();
/**
 * 根据部门id，获取部门信息
 * @param {*} deptID
 * @returns
 */
export const getDepartmentById = async (deptId) => {
    return await platformRequest({
        url: '/api/rest/dept/getDepartmentById',
        method: 'GET',
        cType: false,
        params: { 'deptId': deptId }
    });
};

/**
 * 获取部门领导列表
 * @param {*} deptID
 * @returns
 */
export const getDeptLeaders = async (deptId) => {
    return await platformRequest({
        url: '/api/rest/dept/listDeptLeaders',
        method: 'GET',
        cType: false,
        params: { 'deptId': deptId }
    });
};

/**
 * 获取部门的主管领导列表
 * @param {*} deptID
 * @returns
 */
export const getManagers = async (deptId) => {
    return await platformRequest({
        url: '/api/rest/dept/listDeptManagers',
        method: 'GET',
        cType: false,
        params: { 'deptId': deptId }
    });
};

/**
 * 保存部门信息
 * @param {*} params
 * @returns
 */
export const deptSaveOrUpdate = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/dept/saveOrUpdate',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 删除部门
 * @param {*} deptID
 * @returns
 */
export const removeDept = async (deptId) => {
    return await platformRequest({
        url: '/api/rest/dept/remove',
        method: 'POST',
        cType: false,
        params: { 'deptId': deptId }
    });
};

/**
 * 获取部门排序列表
 * @param {*} parentID
 * @returns
 */
export const getOrderDepts = async (parentId) => {
    return await platformRequest({
        url: '/api/rest/dept/listOrderDepts',
        method: 'GET',
        cType: false,
        params: { 'parentId': parentId }
    });
};

/**
 * 保存综合排序
 * @param {*} parentID
 * @param {*} deptIDs
 * @returns
 */
export const saveOrder = async (parentId, deptIds) => {
    const params = {
        parentId: parentId,
        deptIds: deptIds
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/dept/saveOrder',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 移动部门
 * @param {*} deptID
 * @param {*} parentID
 * @returns
 */
export const moveDept = async (deptId, parentId) => {
    const params = {
        deptId: deptId,
        parentId: parentId
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/dept/move',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 设置部门主管领导
 * @param {*} deptID
 * @param {*} personIDs
 * @returns
 */
export const setDeptManagers = async (deptId, orgBaseIds) => {
    const params = {
        deptId: deptId,
        orgBaseIds: orgBaseIds
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/dept/setDeptManagers',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 移除部门主管领导
 * @param {*} deptID
 * @param {*} personID
 * @returns
 */
export const removeManager = async (deptId, orgBaseId) => {
    const params = {
        deptId: deptId,
        orgBaseId: orgBaseId
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/dept/removeManager',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 设置部门领导
 * @param {*} deptID
 * @param {*} personIDs
 * @returns
 */
export const setDeptLeaders = async (deptId, orgBaseIds) => {
    const params = {
        deptId: deptId,
        orgBaseIds: orgBaseIds
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/dept/setDeptLeaders',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 移除部门领导
 * @param {*} deptID
 * @param {*} personID
 * @returns
 */
export const removeLeader = async (deptId, orgBaseId) => {
    const params = {
        deptId: deptId,
        orgBaseId: orgBaseId
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/dept/removeLeader',
        method: 'POST',
        cType: false,
        data: data
    });
};
//保存扩展属性
export const saveDeptExtendProperties = async (deptId,properties) => {
    const params = {
        deptId: deptId,
        properties:properties
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/dept/saveExtendProperties',
        method: 'POST',
        cType: false,
        data: data
    });
};
