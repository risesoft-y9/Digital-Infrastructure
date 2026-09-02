/*
 * @Author: your name
 * @Date: 2021-04-15 10:16:53
 * @LastEditTime: 2023-07-10 10:41:42
 * @LastEditors: mengjuhua
 * @Description: In User Settings Edit
 */

import y9_storage from '@/utils/storage';
import axios from 'axios'; // 考虑CDN
import {$y9_SSO} from '@/main';


// 创建一个axios实例
function y9Request(baseUrl = '') {
    let requestList = new Set();

    const service = axios.create({
        baseURL: import.meta.env.VUE_APP_CONTEXT,
        withCredentials: true,
        timeout: 0
    });
    // 请求拦截器(发送请求的时候，携带一些信息)
    service.interceptors.request.use(
        (config) => {

            config.headers['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';
            // 自定义
            const access_token = y9_storage.getObjectItem(import.meta.env.VUE_APP_SSO_SITETOKEN_KEY, 'access_token');
            if (access_token) {
                // console.log("access_token = ",access_token);
                config.headers['Authorization'] = 'Bearer ' + access_token;
            }

            return config;
        },
        (error) => {
            // 处理请求错误
            console.log(error); // for debug
            return Promise.reject(error);
        }
    );

    // 响应拦截器(接收到数据的时候，进行数据过滤、对状态码判断，进行对应的操作)
    service.interceptors.response.use(
        (response) => {
            // 相同请求不得在600毫秒内重复发送，反之继续执行
            setTimeout(() => {
                requestList.delete(response.config.url);
            }, 600);
            if (response.data) {
                return response.data;
            } else {
                return response;
            }
        },
        (error) => {
            // 异常情况
            if (axios.isCancel(error)) {
                // log
                // 请求取消
                console.warn(error);
                // console.table([error.message.split('---')[0]], 'cancel')
            } else if (error.response) {
                // 请求成功发出且服务器也响应了状态码，但状态代码超出了 2xx 的范围
                requestList.delete(error.config.url);
                let data = error.response.data;
                if (error.response.status === 401) {
                    $y9_SSO.clearCurrentSessionStorage();
                    $y9_SSO.checkLogin();
                } else if (error.response.status === 400) {
                    // 参数、业务上的错误统一返回 http 状态 400，返回原始 body 到请求处自行处理
                    return data;
                }
            }

            return Promise.reject(error);
        }
    );

    return service;
}

export default y9Request;
