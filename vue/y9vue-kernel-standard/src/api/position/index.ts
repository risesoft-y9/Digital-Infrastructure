/*
 * @Author: your name
 * @Date: 2021-06-22 18:45:30
 * @LastEditTime: 2022-06-17 14:31:50
 * @LastEditors: zhangchongjie
 * @Description: In User Settings Edit
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-kernel-dcat-style\src\api\position\index.ts
 */
import Request from '@/api/lib/request';
import qs from 'qs';

const platformRequest = Request();

/**
 * 根据岗位id，获取岗位信息
 * @param {*} positionID 岗位id
 * @returns
 */
export const getPositionById = async (positionId) => {
    return await platformRequest({
        url: '/api/rest/position/getPositionById',
        method: 'GET',
        cType: false,
        params: { positionId: positionId }
    });
};

/**
 * 根据人员id，获取岗位列表
 * @param {*} personID
 * @returns
 */
export const getPositionsByPersonId = async (personId) => {
    return await platformRequest({
        url: '/api/rest/position/listPositionsByPersonId',
        method: 'GET',
        cType: false,
        params: { personId: personId }
    });
};

/**
 * 根据父节点id，获取岗位列表
 * @param parentId
 * @returns
 */
export const getPositionsByParentId = async (parentId) => {
    return await platformRequest({
        url: '/api/rest/position/listPositionsByParentId',
        method: 'GET',
        cType: false,
        params: { parentId: parentId }
    });
};

/**
 * 保存岗位
 * @param {*} params
 * @returns
 */
export const positionSaveOrUpdate = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/position/saveOrUpdate',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 *
 * @param positionIds 保存岗位排序
 * @returns
 */
export const saveOrder = async (positionIds, tabindexs) => {
    let params = {
        positionIds: positionIds,
        tabindexs: tabindexs
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/position/saveOrder',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 根据岗位id，批量删除人员
 * @param {*} positionID
 * @param {*} personIDs
 * @returns
 */
export const removePersons = async (positionId, personIds) => {
    let params = {
        personIds: personIds,
        positionId: positionId
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/position/removePersons',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 保存岗位的人员排序
 * @param {*} positionID
 * @param {*} personIDs
 * @returns
 */
export const orderPersons = async (positionId, personIds) => {
    let params = {
        personIds: personIds,
        positionId: positionId
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/position/orderPersons',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 批量添加岗位人员
 * @param {*} positionID
 * @param {*} personIDs
 * @returns
 */
export const addPersons = async (positionId, personIds) => {
    let params = {
        personIds: personIds,
        positionId: positionId
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/position/addPersons',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 保存岗位扩展信息
 * @param {*} positionID
 * @param {*} properties
 * @returns
 */
export const savePositionExtendProperties = async (positionId, properties) => {
    const params = {
        positionId: positionId,
        properties: properties
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/position/saveExtendProperties',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 删除岗位
 * @param {*} ids
 * @returns
 */
export const removePosition = async (ids) => {
    const params = {
        ids: ids
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/position/remove',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 移动岗位
 * @param {*} positionID
 * @param {*} parentID
 * @returns
 */
export const movePosition = async (positionId, parentId) => {
    const params = {
        positionId: positionId,
        parentId: parentId
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/position/move',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 保存人员的岗位排序
 * @param personId
 * @param positionIds
 * @returns
 */
export const orderPositions = async (personId, positionIds) => {
    let params = {
        personId: personId,
        positionIds: positionIds
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/position/orderPositions',
        method: 'POST',
        cType: false,
        data: data
    });
};
