import { encode64 } from "./utils/base64";
import { checkSsoLoginInfoApi, ssoLoginApi, ssoGetAccessTokenApi, ssoGetUserInfoApi, ssoRedisDeleteApi } from "./ssoApi";
import y9_storage from "./utils/storage";


// const CLIENT_ID = y9_storage.getObjectItem('sso', "VUE_APP_SSO_CLIENT_ID"),
//     SECRET = y9_storage.getObjectItem('sso', "VUE_APP_SSO_SECRET"),
//     GRANT_TYPE = y9_storage.getObjectItem('sso', "VUE_APP_GRANT_TYPE"),
//     sso_authorize_url = y9_storage.getObjectItem('sso', "VUE_APP_SSO_AUTHORIZE_URL");


// Parse a query string into an object
export const parseQueryString = (string) => {
    if (string == "") { return false; }
    var segments = string.split("&").map(s => s.split("="));
    var queryString = {};
    segments.forEach(s => queryString[s[0]] = s[1]);
    return queryString;
}

// 登陆
export const ssoLogin = async(username, password, tenantName, sso_callback_url) => {
    // console.log(username, password, sso_callback_url);
    const config_logon = {
        loginType: 'loginName', // 固定
        noLoginScreen: true,
        tenantLoginName: tenantName ? tenantName : 'risesoft',
        username: encode64(username),
        password: encode64(password),
        service: sso_callback_url
    };
    // checkSsoLoginInfo - API
    const check = await checkSsoLoginInfoApi({ params: config_logon }).catch((e) => {
        console.log('checkSsoLoginInfo-API- catch到error = ', e);
    });
    if (check.success) {
        // sso登陆
        return await ssoLoginApi({ params: config_logon }).then(res => {
            // console.log('res = ', res);
            // 登陆失败的错误信息
            if (res.success) {
                // 登陆成功 获取授权码
                const url = y9_storage.getObjectItem('sso', "VUE_APP_SSO_AUTHORIZE_URL") +
                    "?response_type=code" // 固定
                    +
                    "&client_id=" + y9_storage.getObjectItem('sso', "VUE_APP_SSO_CLIENT_ID") // 固定
                    +
                    "&client_secret=" + y9_storage.getObjectItem('sso', "VUE_APP_SSO_SECRET") // 固定
                    +
                    "&redirect_uri=" + sso_callback_url;

                window.location = url;
                return true;

            } else {
                console.log(res.msg);
                return false;
            }
        }).catch((e) => {
            console.log('登陆验证catch到error = ', e);
        });
    } else {
        window.alert(check.msg);
        return false;
    }

}

export const ssoGetAccessToken = async(value, Code = 0, grantType = y9_storage.getObjectItem('sso', "VUE_APP_GRANT_TYPE"), key = 'redirect_uri') => {
    if (!Code) {
        return false;
    }
    // 获取accessToken
    const config_token = {
        grant_type: grantType, // 固定
        client_id: y9_storage.getObjectItem('sso', "VUE_APP_SSO_CLIENT_ID"), // 固定
        client_secret: y9_storage.getObjectItem('sso', "VUE_APP_SSO_SECRET"), // 固定
        [key]: value,
        code: Code
    };
    if (Code == 1) {
        delete config_token.code
    }
    // console.log('config_token -> ',config_token)
    const getAccessToken = await ssoGetAccessTokenApi({ params: config_token }).then(res => {
        return res;
    }).catch((e) => {
        // y9_storage.type().clear();
        if (window.location.href.includes('?code=')) {
            console.log("ssoGetAccessTokenApi -> catch ");
            window.location.href = window.location.origin + window.location.pathname;
        } else {
            console.log(e, window.location);
        }

    });
    // store.commit('user/SET_TOKEN', getAccessToken.access_token);
    return getAccessToken;
}


export const getUserInfo = async(token) => {
    const config_getUserInfo = {
        access_token: token
    };
    const get_user_info = await ssoGetUserInfoApi({ params: config_getUserInfo }).then(res => {
        y9_storage.type().setItem('y9UserInfo', JSON.stringify(res));
        return res;
    }).catch((e) => {
        console.log(e)
    });
    return get_user_info;
}

// 传参的obj应该定义一个默认值，否则会打印一个不影响程序运行的错误，
// 但是如果修复的话，发现单点登录无法登陆进去，因为cookie中的TGC设置了sameSite = none（不携带cookie），TGC立马就过期了，被拒绝了
export const ssoLogout = async(obj) => {
    let logoutUrl;
    if (obj.logoutUrl) {
        logoutUrl = obj.logoutUrl;
    }
    // 执行删除前操作-自定义的
    if (obj.__y9delete__) {
        await obj.__y9delete__();
    }
    // 清除redis缓存
    await ssoRedisDeleteApi({
        key: y9_storage.getStringItem(y9_storage.getObjectItem('sso', "VUE_APP_REDISKEY"))
    }).then(async() => {
        sessionStorage.clear();
        localStorage.clear();
        // window.location.href = logoutUrl;
        // 执行sso的退出
        fetch(logoutUrl, {
            credentials: 'omit'
        }).then(() => {
            y9_storage.type().clear();
            window.location.href = logoutUrl;
        })
    })

}