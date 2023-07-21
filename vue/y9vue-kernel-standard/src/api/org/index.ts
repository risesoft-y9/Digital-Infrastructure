/*
 * @Author: your name
 * @Date: 2021-04-09 18:53:30
 * @LastEditTime: 2022-12-01 11:05:09
 * @LastEditors: mengjuhua
 * @Description: In User Settings Edit
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-kernel-dcat-style\src\api\org\index.ts
 */
import Request from "@/api/lib/request";
import qs from 'qs';

// const baseURL = process.env.VUE_APP_HOST_Y9HOME
const platformRequest = Request();

// 树组件 - 一级接口数据
export const treeInterface = async (params) => {
  return await platformRequest({
    //url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/org/list',
    url: '/api/rest/org/list',
    method: 'get',
    cType: false,
    params: {}
  });
};
// 树组件 - 二（三）级接口数据
export const getTreeItemById = async (params) => {

  return await platformRequest({
    // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/org/getTree',
    url: "/api/rest/org/getTree",
    method: 'get',
    cType: false,
    params: {
      id: params.parentId,
      treeType: params.treeType,
      disabled: params.disabled
    }
  });
};


// 树组件 - 搜索接口
export const searchByName = async (params) => {

  return await platformRequest({
    url: "/api/rest/org/treeSearch",
    // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/org/treeSearch',      // apifox - mock
    method: 'GET',
    cType: false,
    params: { 'name': params.key, 'treeType': params.treeType },
  });
};

/**
* 保存组织机构信息
* @param {*} params 
* @returns 
*/
export const orgSaveOrUpdate = async (params) => {
  const data = qs.stringify(params);
  return await platformRequest({
    url: "/api/rest/org/saveOrUpdate",
    method: 'POST',
    cType: false,
    data: data,
  });
};

/**
* 删除机构
* @param {*} orgId 
* @returns 
*/
export const removeOrg = async (orgId) => {
  return await platformRequest({
    url: "/api/rest/org/remove",
    method: 'POST',
    cType: false,
    params: { "orgId": orgId },
  });
};



/**
* 组织机构排序
* @param {*} orgId 
* @returns 
*/
export const orgSaveOrder = async (orgIds) => {
  return await platformRequest({
    url: "/api/rest/org/saveOrder",
    method: 'POST',
    cType: false,
    params: { "orgIds": orgIds },
  });
};



/**
* 数据同步
* @param {Object} params 
* 	syncId {string} 同步节点id
* 	orgType {string} 组织类型
* 	targetSysName {string} 同步方式
* 	needRecursion {Number} 是否递归
* @returns 
*/
export const sync = async (params) => {
  const data = qs.stringify(params);
  return await platformRequest({
    url: "/api/rest/org/sync",
    method: 'POST',
    cType: false,
    data: data,
  });
};

/**
* 获取人员数
* @param {*} ID 
* @param {*} orgType 
* @returns 
*/
export const getAllPersonsCount = async (id, orgType) => {
  return await platformRequest({
    url: "/api/rest/org/getAllPersonsCount",
    method: 'GET',
    cType: false,
    params: { 'id': id, 'orgType': orgType },
  });
};


//保存扩展属性
export const saveOrgExtendProperties = async (orgId, properties) => {
  const params = {
    orgId: orgId,
    properties: properties
  };
  const data = qs.stringify(params);
  return await platformRequest({
    url: "/api/rest/org/saveExtendProperties",
    method: 'POST',
    cType: false,
    data: data,
  });
};


//日志表头
export const getShadowTitles = async (id, entity) => {
  return await platformRequest({
    url: '/history/getShadowTitles',
    method: 'GET',
    cType: false,
    params: { 'id': id, 'entity': entity }
  });
};
//日志数据
export const getShadowRows = async (id, entity) => {
  return await platformRequest({
    url: '/history/getShadowRows',
    method: 'GET',
    cType: false,
    params: { 'id': id, 'entity': entity }
  });
};



