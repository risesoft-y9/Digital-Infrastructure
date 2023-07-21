/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-07-01 10:10:01
 * @LastEditors: mengjuhua
 * @LastEditTime: 2022-12-01 11:04:24
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-kernel-dcat-style\src\api\appIcon\index.ts
 */
import Request from '@/api/lib/request';
import qs from "qs";
const platformRequest = Request();
/**
 * 获取应用图标
 * @returns 
 */
export const getAppIconById = async (id) => {
    return await platformRequest({
        url: "/api/rest/appIcon/getAppIconById",
        method: 'GET',
        cType: false,
        params: { 'id': id },
    });
};

/**
 * 查询所有父菜单分页列表
 * @param {*} page 
 * @param {*} rows 
 * @returns 
 */
export const getAppIconPageList = async ({ page, rows }) => {
    return await platformRequest({
        url: "/api/rest/appIcon/pageAppIcons",
        method: 'GET',
        cType: false,
        params: { 'page': page, 'size': rows },
    });
};

/**
 * 按名字模糊图标
 * @param {*} name 
 * @param {*} page 
 * @param {*} rows 
 * @returns 
 */
export const searchIconPageByName = async ({ name, page, rows }) => {
    return await platformRequest({
        url: "/api/rest/appIcon/searchIconPageByName",
        method: 'GET',
        cType: false,
        params: { 'name': name, 'page': page, 'size': rows },
    });
};

/**
 * 刷新图标数据
 * @returns 
 */
export const refreshAppIconDatas = async () => {
    return await platformRequest({
        url: "/api/rest/appIcon/refreshAppIconDatas",
        method: 'GET',
        cType: false,
        params: {},
    });
};

/**
 * 上传图标
 * @param {*} iconFile 
 * @param {*} remark 
 * @returns 
 */
export const uploadIcon = async ({ iconFile, remark }) => {
    var data = new FormData();
    data.append("iconFile", iconFile);
    data.append("remark", remark);
    return await platformRequest({
        url: "/api/rest/appIcon/uploadIcon",
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 保存图标修改信息
 * @param {*} name 
 * @param {*} remark 
 * @param {*} id 
 * @returns 
 */
export const saveIcon = async ({ name, remark, id }) => {
    const params = {
        name: name,
        remark: remark,
        id: id,
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: "/api/rest/appIcon/saveIcon",
        method: 'POST',
        cType: false,
        data: data,
    });
};

/**
 *  删除图标
 * @param {*} id 
 * @returns 
 */
export const deleteIcon = async (id) => {
    const params = {
        id: id,
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: "/api/rest/appIcon/deleteIcon",
        method: 'POST',
        cType: false,
        data: data,
    });
};

/**
 * 图片文件读取
 * @returns 
 */
export const readAppIconFile = async () => {
    return await platformRequest({
        url: "/api/rest/appIcon/listAll",
        method: 'GET',
        cType: false,
        params: {},
    });
};

/**
 * 根据名称搜索图标
 * @param {*} name 
 * @returns 
 */
export const searchAppIcon = async (name) => {
    return await platformRequest({
        url: "/api/rest/appIcon/searchAppIcon",
        method: 'GET',
        cType: false,
        params: { 'name': name },
    });
};