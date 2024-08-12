/*
 * @Author: your name
 * @Date: 2021-07-08 17:38:25
 * @LastEditTime: 2022-12-12 11:41:35
 * @LastEditors: mengjuhua
 * @Description: 字典表类型
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-kernel-dcat-style\src\api\dictionary\index.ts
 */
import Request from '@/api/lib/request';
import qs from 'qs';

const platformRequest = Request();

/**
 * 获取字典类型列表
 * @param params
 * @returns
 */
export const getOptionClassList = async () => {
    return await platformRequest({
        url: '/api/rest/optionClass/listOptionClass',
        method: 'GET',
        cType: false,
        params: {}
    });
};

/**
 * 保存字典类型
 * @param params
 * @returns
 */
export const saveOptionClass = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/optionClass/saveOptionClass',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 删除字典类型
 * @param type
 * @returns
 */
export const removeOptionClass = async (type) => {
    const params = {
        type: type
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/optionClass/remove',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 *  根据字典类型，获取字典属性值列表
 * @param {*} type
 * @returns
 */
export const listByType = async (type) => {
    return await platformRequest({
        url: '/api/rest/optionValue/listByType',
        method: 'GET',
        cType: false,
        params: { type: type }
    });
};

/**
 * 保存新增字典值数据
 * @param {*} params
 * @returns
 */
export const saveOptionValue = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/optionValue/saveOptionValue',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 删除字典值数据
 * @param {*} ids
 * @returns
 */
export const removeByIds = async (ids) => {
    const params = {
        ids: ids
    };
    const data = qs.stringify(params);

    return await platformRequest({
        url: '/api/rest/optionValue/removeByIds',
        method: 'POST',
        cType: false,
        data: data
    });
};
/**
 * 获取组织机构类型列表
 * @returns
 */
export const getOrgTypeList = async () => {
    return await platformRequest({
        url: '/api/rest/optionValue/listOrgType',
        method: 'GET',
        cType: false,
        params: {}
    });
};

/**
 * 获取职务名称列表
 * @returns
 */
export const getDutyList = async () => {
    return await platformRequest({
        url: '/api/rest/optionValue/listDuty',
        method: 'GET',
        cType: false,
        params: {}
    });
};

/**
 *  获取职务类型列表
 * @returns
 */
export const getDutyTypeList = async () => {
    return await platformRequest({
        url: '/api/rest/optionValue/listDutyType',
        method: 'GET',
        cType: false,
        params: {}
    });
};

/**
 * 获取职备级别列表
 * @returns
 */
export const getDutyLevelList = async () => {
    return await platformRequest({
        url: '/api/rest/optionValue/listDutyLevel',
        method: 'GET',
        cType: false,
        params: {}
    });
};

/**
 * 获取证件类型列表
 * @returns
 */
export const getPrincipalIDTypeList = async () => {
    return await platformRequest({
        url: '/api/rest/optionValue/listPrincipalIdType',
        method: 'GET',
        cType: false,
        params: {}
    });
};

/**
 * 获取人员编制列表
 * @returns
 */
export const getOfficialTypeList = async () => {
    return await platformRequest({
        url: '/api/rest/optionValue/listOfficialType',
        method: 'GET',
        cType: false,
        params: {}
    });
};

/**
 * 获取职位列表
 */
export const getJobList = async () => {
    return await platformRequest({
        url: '/api/rest/job/listAll',
        method: 'GET',
        cType: false,
        params: {}
    });
};

/**
 * 保存新增职位数据
 * @param {*} params
 * @returns
 */
export const saveJobValue = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/job/saveOrUpdate',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 删除职位数据
 * @param {*} ids
 * @returns
 */
export const deleteByIds = async (ids) => {
    const params = {
        ids: ids
    };
    return await platformRequest({
        url: '/api/rest/job/deleteByIds',
        method: 'GET',
        cType: false,
        params: params
    });
};

//按主键名称搜索
export const getJobByMajorName = async (params) => {
    return await platformRequest({
        url: '/api/rest/job/searchByName',
        method: 'GET',
        cType: false,
        params: params
    });
};

//按id查找职位
export const jobInfoGet = async (id) => {
    return await platformRequest({
        url: `/api/rest/job/getJobById/${id}`,
        method: 'get',
        cType: false
    });
};

/**
 * 保存职位排序
 * @param {*} personIDs
 * @param {*} tabindexs
 * @returns
 */
export const saveOrder = async (jobIds, tabindexs) => {
    const params = {
        jobIds: jobIds,
        tabindexs: tabindexs
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/job/saveOrder',
        method: 'POST',
        cType: false,
        data: data
    });
};
