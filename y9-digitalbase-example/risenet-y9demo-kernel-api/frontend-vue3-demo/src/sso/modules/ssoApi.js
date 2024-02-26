/*
 * @Author: your name
 * @Date: 2021-04-09 18:53:30
 * @LastEditTime: 2022-01-18 18:04:44
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: /sz-team-frontend-9.5x/y9vue-todo/src/api/todo/index.js
 */

import Cookies from 'js-cookie';
import qs from 'qs';
import redisRequest from "./redisLib";
import ssoRequest from "./ssoLib";
import y9_storage from "./utils/storage";


export const checkSsoLoginInfoApi = async(data) => {
    const params = data.params
    return await ssoRequest({
        url: y9_storage.getObjectItem('sso', "VUE_APP_SSO_CONTEXT") + '/api/checkSsoLoginInfo',
        method: 'post',
        cType: false,
        test: true,
        // data: params,
        params: params
    });
}

export const ssoLoginApi = async(data) => {
    // const params = qs.stringify(data.params)
    const params = data.params
    return await ssoRequest({
        url: y9_storage.getObjectItem('sso', "VUE_APP_SSO_CONTEXT") + '/api/logon',
        method: 'post',
        cType: false,
        test: true,
        // data: params,
        params: params
    });
};

export const ssoGetAccessTokenApi = async(data) => {
    return await ssoRequest({
        url: y9_storage.getObjectItem('sso', "VUE_APP_SSO_CONTEXT") + '/oauth2.0/accessToken',
        method: 'get',
        cType: false,
        data,
        params: data.params
    });
}

export const ssoGetUserInfoApi = async(data) => {
        return await ssoRequest({
            url: y9_storage.getObjectItem('sso', "VUE_APP_SSO_CONTEXT") + '/oauth2.0/profile',
            method: 'get',
            cType: false,
            data,
            params: data.params
        });
    }
    // 存储redis记录
export const ssoRedisSaveApi = async(obj) => {
        return await redisRequest({
            url: 'sso/api/session/save',
            method: 'post',
            data: qs.stringify(obj)
        });
    }
    // 刷新redis记录
export const ssoRedisRefreshApi = async(obj) => {
        return await redisRequest({
            url: 'sso/api/session/refresh',
            method: 'post',
            data: qs.stringify(obj)
        });
    }
    // 获取redis记录
export const ssoRedisGetApi = async(obj) => {
        let data = qs.stringify(obj);
        return await redisRequest({
            url: 'sso/api/session/get',
            method: 'post',
            data
        });
    }
    // 删除redis记录
export const ssoRedisDeleteApi = async(obj) => {
    return await redisRequest({
        url: 'sso/api/session/delete',
        method: 'post',
        data: qs.stringify(obj)
    });
}



/** 
 * 获取本地Token
 * @author Y9
 */
export const getCookie = async(key) => {
    return Cookies.get(key);
}

/** 
 * 设置存储Token
 * @author Y9
 */
export const setCookie = async(key, info) => {
    return Cookies.set(key, info);
}

/** 
 * 移除本地Token
 * @author Y9
 */
export const removeCookie = async(key) => {
    return Cookies.remove(key);
}