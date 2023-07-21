/*
 * @Author: your name
 * @Date: 2021-10-08 11:04:48
 * @LastEditTime: 2021-12-22 20:21:25
 * @LastEditors: Please set LastEditors
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: /sz-team-frontend-9.5.x/y9vue-home/src/api/lib/localToken.js
 */
import Cookies from 'js-cookie'; // 考虑CDN
import { siteTokenKey } from '@/settings';

/** 
 * 获取本地Token
 * @author Y9
 */
export function getToken() {
    return Cookies.get(siteTokenKey);
}

/** 
 * 设置存储Token
 * @author Y9
 */
export function setToken(token) {
    return Cookies.set(siteTokenKey, token);
}

/** 
 * 移除本地Token
 * @author Y9
 */
export function removeToken() {
    return Cookies.remove(siteTokenKey);
}