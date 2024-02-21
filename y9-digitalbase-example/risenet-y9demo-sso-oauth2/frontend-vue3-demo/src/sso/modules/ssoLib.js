/*
 * @Author: your name
 * @Date: 2021-05-10 18:15:43
 * @LastEditTime: 2022-01-18 18:05:29
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: /y9vue-home/src/api/lib/sso.js
 */
import axios from 'axios';
import y9_storage from "./utils/storage";


const ssoRequest = axios.create({
    baseURL: y9_storage.getObjectItem('sso', "VUE_APP_SSO"),
    withCredentials: true,
    timeout: 0
});


// 请求拦截器 
ssoRequest.interceptors.request.use(
    config => {
        // console.log('config = ', config);
        config.headers['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';
        // 自定义
        if (config.cType) {
            config.headers['userLoginName'] = config.data.userLoginName;
        }
        const access_token = y9_storage.getObjectItem(y9_storage.getObjectItem('sso', 'VUE_APP_SITETOKEN'), 'access_token');
        if (access_token) {
            // console.log("access_token = ", access_token);
            config.params ? config.params.access_token = access_token : '';
        }
        return config;
    },
    error => {
        // 处理请求错误
        console.log(error); // for debug
        return Promise.reject(error);
    }
);

// 响应拦截器
ssoRequest.interceptors.response.use(
    response => {
        // console.log("response = ", response);
        let res;
        if (response.data) {
            res = response.data;
            return res;
        } else {
            res = response;
            const {status} = res;
            if (status == 200) {
                return res;
            }
            y9_storage.type.clear();
            switch (status) {
                case 400:
                    console.log('API返回-400错误');
                    break;
                case 401:
                    console.log('API返回-401错误');
                    break;

                default:
                    console.log('API返回-未定义错误');
                    break;
            }
            return Promise.reject(res);
        }


    },
    error => {
        return Promise.reject(error);
    }
);

export default ssoRequest;