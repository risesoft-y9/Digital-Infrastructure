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
        params: { deptId: deptId }
    });
};

/**
 * 获取部门属性对应组织节点列表
 * @param {*} deptId
 * @param {*} category
 * @returns
 */
export const getDepartmentPropOrgUnits = async (deptId, category) => {
    return await platformRequest({
        url: '/api/rest/dept/listDepartmentPropOrgUnits',
        method: 'GET',
        cType: false,
        params: {
            deptId: deptId,
            category: category
        }
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
        params: { deptId: deptId }
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
        params: { parentId: parentId }
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
 * 设置部门属性的组织节点
 * @param {*} deptId
 * @param {*} orgBaseIds
 * @returns
 */
export const setDepartmentPropOrgUnits = async (deptId, category, orgBaseIds) => {
    const params = {
        deptId: deptId,
        category: category,
        orgBaseIds: orgBaseIds
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/dept/setDepartmentPropOrgUnits',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 移除部门属性
 * @param {*} deptId
 * @param {*} orgBaseId
 * @param {*} category
 * @returns
 */
export const removeDepartmentProp = async (deptId, orgBaseId, category) => {
    const params = {
        deptId: deptId,
        orgBaseId: orgBaseId,
        category: category
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/dept/removeDepartmentProp',
        method: 'POST',
        cType: false,
        data: data
    });
};

//保存扩展属性
export const saveDeptExtendProperties = async (deptId, properties) => {
    const params = {
        deptId: deptId,
        properties: properties
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/dept/saveExtendProperties',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 禁用/解禁部门
 * @param {*} ID
 * @returns
 */
export const changeDisabledDept = async (id) => {
    const params = {
        id: id
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/dept/changeDisabled',
        method: 'POST',
        cType: false,
        data: data
    });
};
