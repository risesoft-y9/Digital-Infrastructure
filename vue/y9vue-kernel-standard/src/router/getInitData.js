/*
 * @Author: your name
 * @Date: 2021-12-22 16:50:57
 * @LastEditTime: 2022-01-20 17:08:52
 * @LastEditors: Please set LastEditors
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: /sz-team-frontend-9.5.x/y9vue-home/src/router/getData.js
 */


export async function getLoginInfo() {
    let sessionObj = JSON.parse(sessionStorage.getItem(
        import.meta.env.VUE_APP_SSO_SITETOKEN_KEY));
    return await fetch(import.meta.env.VUE_APP_CONTEXT + 'api/rest/info/getLoginInfo', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
            'Authorization': 'Bearer ' + sessionObj.access_token
        }
    }).then(res => {
        return res.json();
    }).then((res) => {
        sessionStorage.setItem("getLoginInfo","true")
        sessionStorage.setItem('departmentMapList', JSON.stringify(res.data.departmentMapList));
        if (res.data.isShowMenu && res.data.person.tenantManager) {
            // store.commit('user/SET_ROLES', ['admin']);   // AAAAA

        } else if (!res.data.person.tenantManager) {
            // store.commit('user/SET_ROLES', ['user']);    // AAAAA
        } else {
            // store.commit('user/SET_ROLES', ['admin']);   // AAAAA
        }
        // store.commit('user/SET_INITINFO', res.data);     // AAAAA
        return res;
    }).catch(e => {
        // sessionStorage.clear();
        // window.location = window.location.origin + window.location.pathname;
        sessionStorage.setItem("getLoginInfo","false")
        window.location = window.location.origin + window.location.pathname;
    });
}