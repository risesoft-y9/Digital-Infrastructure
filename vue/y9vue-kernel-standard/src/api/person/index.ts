/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2022-04-28 15:09:08
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-06-16 09:50:20
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-kernel-dcat-style\src\api\person\index.ts
 */
import Request from '@/api/lib/request';
import qs from 'qs';

const platformRequest = Request();

/**
 * 根据用户id，获取用户信息
 * @param {*} personID 人员id
 * @returns
 */
export const getPersonById = async (personId) => {
    return await platformRequest({
        url: '/api/rest/person/getPersonById',
        method: 'GET',
        cType: false,
        params: { 'personId': personId }
    });
};

/**
 * 获取人员扩展信息
 * @param {*} personID
 * @returns
 */
export const getPersonExtById = async (personId) => {
    return await platformRequest({
        url: '/api/rest/person/getPersonExtById',
        method: 'GET',
        cType: false,
        params: { 'personId': personId }
    });
};

/**
 * 获取解密处理之后身份证的扩展信息
 * @param {*} personID
 * @returns
 */
export const getPersonExtByIdWithEncry = async (personId) => {
    return await platformRequest({
        url: '/api/rest/person/getPersonExtByIdWithEncry',
        method: 'GET',
        cType: false,
        params: { 'personId': personId }
    });
};

/**
 * 判断登录名称是否可用
 * @param {*} personID
 * @param {*} loginName
 * @returns
 */
export const loginNameCheck = async (personId, loginName) => {
    return await platformRequest({
        url: '/api/rest/person/checkLoginName',
        method: 'GET',
        cType: false,
        params: { 'personId': personId, 'loginName': loginName }
    });
};

/**
 *  判断邮箱是否可用
 * @param {*} personID
 * @param {*} email
 * @returns
 */
export const EmailCheck = async (personId, email) => {
    return await platformRequest({
        url: '/api/rest/person/checkEmail',
        method: 'GET',
        cType: false,
        params: { 'personId': personId, 'email': email }
    });
};

/**
 * 判断同一个租户CA认证码是否重复（false 重复，true 不重复）
 * @param {*} personID
 * @param {*} CAID
 * @returns
 */
export const CAIDCheck = async (personId, CAID) => {
    return await platformRequest({
        url: '/api/rest/person/checkCaid',
        method: 'GET',
        cType: false,
        params: { 'personId': personId, 'CAID': CAID }
    });
};

/**
 * 根据父节点id，获取人员列表
 * @param {*} parentID
 * @returns
 */
export const getPersonsByParentId = async (parentId) => {
    return await platformRequest({
        url: '/api/rest/person/listPersonsByParentId',
        method: 'GET',
        cType: false,
        params: { 'parentId': parentId }
    });
};

/**
 * 根据岗位id，获取人员列表
 * @param {*} positionID
 * @returns
 */
export const getPersonsByPositionId = async (positionId) => {
    return await platformRequest({
        url: '/api/rest/person/listPersonsByPositionId',
        method: 'GET',
        cType: false,
        params: { 'positionId': positionId }
    });
};

/**
 * 根据用户组id，获取人员列表
 * @param {*} groupID
 * @returns
 */
export const getPersonsByGroupID = async (groupId) => {
    return await platformRequest({
        url: '/api/rest/person/listPersonsByGroupId',
        method: 'GET',
        cType: false,
        params: { 'groupId': groupId }
    });
};

/**
 * 保存人员信息
 * @param {*} params
 * @returns
 */
export const personSaveOrUpdate = async (params) => {
    const data = qs.stringify(params, { indices: false });
    return await platformRequest({
        url: '/api/rest/person/saveOrUpdate',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 批量删除人员
 * @param {ids:[]} IDs
 */
export const delPerson = async (ids) => {
    const params = {
        ids: ids
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/person/remove',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 保存人员排序
 * @param {*} personIDs
 * @param {*} tabindexs
 * @returns
 */
export const saveOrder = async (personIds, tabindexs) => {
    const params = {
        personIds: personIds,
        tabindexs: tabindexs
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/person/saveOrder',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 移动人员
 * @param {*} personID
 * @param {*} parentID
 * @returns
 */
export const movePerson = async (personId, parentId) => {
    const params = {
        personId: personId,
        parentId: parentId
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/person/move',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 添加人员
 * @param {*} parentID
 * @param {*} personIDs
 * @returns
 */
export const savePersons = async (parentId, personIds) => {
    const params = {
        parentId: parentId,
        personIds: personIds
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/person/savePersons',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 保存扩展信息
 * @param {*} personID
 * @param {*} properties
 * @returns
 */
export const savePersonExtendProperties = async (personId, properties) => {
    const params = {
        personId: personId,
        properties: properties
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/person/saveExtendProperties',
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
        url: '/api/rest/person/changeDisabled',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 解锁账号
 * @param {*} personId
 * @returns
 */
export const accountUnlock = async (personId) => {
    const params = {
        personId: personId
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/person/accountUnlock',
        method: 'POST',
        cType: false,
        data: data
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
        url: '/api/rest/person/resetPassword',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 添加人员的用户组
 * @param {*} personID
 * @param {*} groupIDs
 * @returns
 */
export const addGroups = async (personId, groupIds) => {
    const params = {
        personId: personId,
        groupIds: groupIds
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/person/addGroups',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 移除人员的用户组
 * @param {*} personID
 * @param {*} groupIDs
 * @returns
 */
export const removeGroups = async (personId, groupIds) => {
    const params = {
        personId: personId,
        groupIds: groupIds
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/person/removeGroups',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 添加人员的岗位
 * @param {*} personID
 * @param {*} positionIDs
 * @returns
 */
export const addPositions = async (personId, positionIds) => {
    const params = {
        personId: personId,
        positionIds: positionIds
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/person/addPositions',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 移除人员的岗位
 * @param {*} personID
 * @param {*} positionIDs
 * @returns
 */
export const removePositions = async (personId, positionIds) => {
    const params = {
        personId: personId,
        positionIds: positionIds
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/person/removePositions',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 上传图片
 * @param {*} iconFile
 * @param {*} personID
 * @returns
 */
export const savePersonPhoto = async (iconFile, personId) => {
    const data = new FormData();
    data.append('iconFile', iconFile);
    data.append('personId', personId);
    return await platformRequest({
        url: '/api/rest/person/savePersonPhoto',
        method: 'POST',
        cType: false,
        data: data
    });
};
