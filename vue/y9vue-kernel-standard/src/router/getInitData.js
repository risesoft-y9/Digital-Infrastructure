/*
 * @Author: your name
 * @Date: 2021-12-22 16:50:57
 * @LastEditTime: 2023-08-03 10:26:02
 * @LastEditors: mengjuhua
 * @Description: 登录额外信息获取
 */

export async function getLoginInfo() {
    let sessionObj = JSON.parse(sessionStorage.getItem(import.meta.env.VUE_APP_SSO_SITETOKEN_KEY));
    return await fetch(import.meta.env.VUE_APP_CONTEXT + 'api/rest/info/getLoginInfo', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
            Authorization: 'Bearer ' + sessionObj.access_token
        }
    })
        .then((res) => {
            return res.json();
        })
        .then((res) => {
            sessionStorage.setItem('getLoginInfo', 'true');
            sessionStorage.setItem('departmentMapList', JSON.stringify(res.data.departmentMapList));
            return res;
        })
        .catch((e) => {
            // sessionStorage.clear();
            // window.location = window.location.origin + window.location.pathname;
            sessionStorage.setItem('getLoginInfo', 'false');
            window.location = window.location.origin + window.location.pathname;
        });
}
