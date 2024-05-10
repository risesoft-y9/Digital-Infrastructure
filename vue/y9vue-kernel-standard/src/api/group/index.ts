/*
 * @Author: your name
 * @Date: 2021-06-22 18:43:49
 * @LastEditTime: 2022-06-17 17:24:42
 * @LastEditors: zhangchongjie
 * @Description: In User Settings Edit
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-kernel-dcat-style\src\api\group\index.ts
 */
import Request from '@/api/lib/request';
import qs from "qs";
const platformRequest = Request();
/**
 * 根据用户组id，获取用户组信息
 * @param {*} groupID 用户组id
 * @returns 
 */
export const getGroupById = async (groupId) => {
    return await platformRequest({
        url: "/api/rest/group/getGroupById",
        method: 'GET',
        cType: false,
        params: { "groupId": groupId },
    });
};

/**
 * 根据人员id，获取用户组列表
 * @param {*} personID 
 * @returns 
 */
export const getGroupsByPersonId = async (personId) => {
    return await platformRequest({
        url: "/api/rest/group/listGroupsByPersonId",
        method: 'GET',
        cType: false,
        params: { "personId": personId },
    });
};

/**
 * 根据用户组id，批量删除用户组成员
 * @param {*} groupID 
 * @param {*} personIDs 
 * @returns 
 */
export const removePersons = async (groupId, personIds) => {
    return await platformRequest({
        url: "/api/rest/group/removePersons",
        method: 'POST',
        cType: false,
        params: {
			"groupId":groupId,
			"personIds":personIds,
		}
    });
};

/**
 * 保存用户组的人员排序
 * @param {*} groupID 
 * @param {*} personIDs 
 * @returns 
 */
export const orderPersons = async (groupId, personIds) => {
    let params = {
        personIds: personIds,
        groupId: groupId
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: "/api/rest/group/orderPersons",
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 批量添加用户组成员
 * @param {*} groupID 用户组id
 * @param {*} personIDs 
 * @returns 
 */
export const addPersons = async (groupId, personIds) => {
    let params = {
        personIds: personIds,
        groupId: groupId
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: "/api/rest/group/addPersons",
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 保存用户组信息
 * @param {*} params 
 * @returns 
 */
export const groupSaveOrUpdate = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: "/api/rest/group/saveOrUpdate",
        method: 'POST',
        cType: false,
        data: data,
    });
};

/**
 * 保存用户组扩展信息
 * @param {*} groupID 
 * @param {*} properties 
 * @returns 
 */
export const saveGroupExtendProperties = async (groupId, properties) => {
    const params = {
        groupId: groupId,
        properties: properties
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: "/api/rest/group/saveExtendProperties",
        method: 'POST',
        cType: false,
        data: data,
    });
};

/**
 * 删除岗位
 * @param {*} groupID 
 * @returns 
 */
export const removeGroup = async (groupId) => {
    return await platformRequest({
        url: "/api/rest/group/remove",
        method: 'POST',
        cType: false,
        params: { "groupId": groupId },
    });
};

/**
 * 移动用户组
 * @param {*} groupID 
 * @param {*} parentID 
 * @returns 
 */
export const moveGroup = async (groupId, parentId) => {
    const params = {
        groupId: groupId,
        parentId: parentId
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: "/api/rest/group/move",
        method: 'POST',
        cType: false,
        data: data
    });
};

export const orderGroups = async (personId,groupIds) => {
    const params = {
        personId: personId,
        groupIds:groupIds
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: "/api/rest/group/orderGroups",
        method: 'POST',
        cType: false,
        data: data,
    });
};

/**
 * 禁用/解禁用户组
 * @param {*} ID
 * @returns
 */
export const changeDisabledGroup = async (id) => {
    const params = {
        id: id
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/group/changeDisabled',
        method: 'POST',
        cType: false,
        data: data
    });
};