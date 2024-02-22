/*
 * @Author: your name
 * @Date: 2021-05-30 15:03:25
 * @LastEditTime: 2022-01-19 11:39:58
 * @LastEditors: Please set LastEditors
 * @Description: 0.4.91 基础上修改（local 改为 session，配合中间缓存redis）
 * @FilePath: /y9vue-home/src/plugin/sso.js
 */
import {
  ssoGetAccessToken,
  getUserInfo,
  parseQueryString,
  ssoLogin,
  ssoLogout,
} from "./modules/helpers";
import y9_storage from "./modules/utils/storage";
import {
  ssoRedisSaveApi,
  ssoRedisRefreshApi,
  ssoRedisGetApi,
  ssoRedisDeleteApi,
} from "./modules/ssoApi";
import md5 from "md5";

const sso = {};
// 外部传入插件的参数
let appFeatures = false,
  appLoginPageUrl;

// 更新 token
async function refreshToken(refresh_token) {
  // console.log('refreshToken -- ',to, from);
  const currentTimestamp = Date.parse(new Date()).toString().slice(0, 10);
  const key = "refresh_token";
  const grantType = key;
  const getNewToken = await ssoGetAccessToken(refresh_token, 1, grantType, key);
  if (getNewToken && getNewToken.expires_in) {
    // expire_in是秒数，API显示的数据是 28800秒 = 8小时
    getNewToken.expires_in = getNewToken.expires_in - 1800; // 有效期时间，客户端比服务器少300秒
    getNewToken.count_expires_in =
      parseInt(currentTimestamp) + parseInt(getNewToken.expires_in);
  }

  // session存储登陆信息
  const isCashe = y9_storage.setObjectItem(
    y9_storage.getObjectItem("sso", "VUE_APP_SITETOKEN"),
    getNewToken
  );
  if (!isCashe) {
    console.log("没有成功缓存");
    return false;
  } else {
    return true;
  }
}
// 	guid
function uuid(type = false) {
  let guid = "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(
    /[xy]/g,
    function (c) {
      var r = (Math.random() * 16) | 0,
        v = c == "x" ? r : (r & 0x3) | 0x8;
      return v.toString(16);
    }
  );
  if (!type) {
    y9_storage.setStringItem(
      y9_storage.getObjectItem("sso", "VUE_APP_SESSIONSTORAGE_GUID"),
      guid
    );
    return guid;
  }
  if (type === "session") {
    y9_storage.setStringItem(
      y9_storage.getObjectItem("sso", "VUE_APP_SESSIONSTORAGE_GUID"),
      guid
    );
    return guid;
  }
}

// 初始化应用的用户数据
async function initUserData(access_token) {
  let guid = y9_storage.getStringItem(
    y9_storage.getObjectItem("sso", "VUE_APP_SESSIONSTORAGE_GUID")
  );
  var get_user_info = await getUserInfo(access_token);
  const NAME = md5(get_user_info.name);
  y9_storage.setStringItem(
    y9_storage.getObjectItem("sso", "VUE_APP_REDISKEY"),
    NAME
  );
  y9_storage.setStringItem("userName", get_user_info.name);
  if (!guid) {
    // 增加guid
    await ssoRedisSaveApi({
      key: NAME,
      value: uuid(),
    });
  } else {
    // 只更新过期时间
    await ssoRedisRefreshApi({
      key: NAME,
    });
  }
  return get_user_info;
}

// 生成随机字符串
function generateRandomString() {
  var array = new Uint32Array(28);
  window.crypto.getRandomValues(array);
  return Array.from(array, (dec) => ("0" + dec.toString(16)).substr(-2)).join(
    ""
  );
}
// 登陆页面
function navToLogin(ssoAuthorizeUrl, sso_callback_url) {
  const url =
    ssoAuthorizeUrl +
    "?response_type=code" + // 固定
    "&client_id=" +
    y9_storage.getObjectItem("sso", "VUE_APP_SSO_CLIENT_ID") + // 固定
    "&client_secret=" +
    y9_storage.getObjectItem("sso", "VUE_APP_SSO_SECRET") + // 固定
    "&state=" +
    generateRandomString() +
    "&scope=all" +
    "&redirect_uri=" +
    sso_callback_url;
  // 多标签场景下，其中一个标签退出，另一个标签刷新y9_storage.getObjectItem()返回false，获取不到数据
  if (!ssoAuthorizeUrl) {
    let arr = url.split("=");
    window.location = arr[arr.length - 1];
  } else {
    window.location = url;
  }
}
// 登陆跳转
async function checkLogin(callbackPageUrl = "") {
  // 获取应用的首页URL
  let sso_callback_url = window.location.origin + window.location.pathname,
    q = parseQueryString(window.location.search.substring(1));
  // 客户自己的单点登陆页面
  if (!q && appLoginPageUrl) {
    if (callbackPageUrl) {
      navToLogin(
        y9_storage.getObjectItem("sso", "VUE_APP_SSO_AUTHORIZE_URL"),
        callbackPageUrl
      );
    } else {
      window.location = appLoginPageUrl;
    }
  }
  // 单点登录服务器上的登陆页面
  if (!q && !appLoginPageUrl) {
    // next({ name: 'login', query: { sso_callback_url } })
    navToLogin(
      y9_storage.getObjectItem("sso", "VUE_APP_SSO_AUTHORIZE_URL"),
      sso_callback_url
    );
  }

  // sso的uri回调中包含code
  if (q && q.code) {
    const getAccessToken = await ssoGetAccessToken(sso_callback_url, q.code);
    let currentTimestamp = Date.parse(new Date()).toString().slice(0, 10);
    // expire_in是秒数，API显示的数据是 28800秒 = 8小时
    getAccessToken.expires_in = getAccessToken.expires_in - 1800; // 有效期时间，客户端比服务器少300秒
    getAccessToken.count_expires_in =
      parseInt(currentTimestamp) + parseInt(getAccessToken.expires_in);
    // session存储登陆信息
    let isCashe = y9_storage.setObjectItem(
      y9_storage.getObjectItem("sso", "VUE_APP_SITETOKEN"),
      getAccessToken
    );
    if (isCashe) {
      await initUserData(getAccessToken.access_token);
    }
    // 删除回调url中的code
    window.location = window.location.origin + window.location.pathname;
  }

  // 切换账号
  if (q && q.ticket) {
    // 获取当前url
    // window.location = window.location.origin + window.location.pathname;
    sso_callback_url = window.location.href;
    navToLogin(
      y9_storage.getObjectItem("sso", "VUE_APP_SSO_AUTHORIZE_URL"),
      sso_callback_url
    );
  }

  // 工作流 新开页签会因为没有缓存而无法进入这个函数里的条件，导致单点登录失败
  if (q && q.itemId) {
    // 获取当前url
    sso_callback_url = window.location.href;
    navToLogin(
      y9_storage.getObjectItem("sso", "VUE_APP_SSO_AUTHORIZE_URL"),
      sso_callback_url
    );
  }
}

// 检查初始化数据
async function checkInitData() {
  let userName = y9_storage.getStringItem("userName"),
    access_token = y9_storage.getObjectItem(
      y9_storage.getObjectItem("sso", "VUE_APP_SITETOKEN"),
      "access_token"
    );
  if (!userName) {
    await initUserData(access_token, true).catch((e) => console.log(e.message));
  }
}

// 获取当前会话登陆信息
async function getSessionCache() {
  // 当前时间戳
  let currentTimestamp = Date.parse(new Date()).toString().slice(0, 10),
    localCache = y9_storage.getObjectItem(
      y9_storage.getObjectItem("sso", "VUE_APP_SITETOKEN")
    ),
    count_expires_in;
  // 查询会话页面是否有session缓存
  if (localCache && localCache.refresh_token) {
    count_expires_in = y9_storage.getObjectItem(
      y9_storage.getObjectItem("sso", "VUE_APP_SITETOKEN"),
      "count_expires_in"
    );
    let isValidTime = currentTimestamp - count_expires_in;
    // 是否需要更新token
    if (isValidTime > 0 && isValidTime < 1800) {
      let refresh_token = y9_storage.getObjectItem(
        y9_storage.getObjectItem("sso", "VUE_APP_SITETOKEN"),
        "refresh_token"
      );
      await refreshToken(refresh_token).catch((e) => console.log(e.message));
      return true;
    } else {
      return true;
    }
  } else {
    return false;
  }
}

// 检查token
async function checkToken() {
  let isValid = await getSessionCache();
  if (isValid) {
    // 判断iframe工程里有没有这个字段
    await checkInitData();
    return true;
  } else {
    // 无效
    return false;
  }
}

sso.install = function (Vue, options) {
  if (!options || !options.env || !options.env.sso) {
    console.error("没有传入sso配置信息");
  }
  y9_storage.setObjectItem("sso", options.env.sso);
  if (
    options &&
    options.env &&
    options.env.appFeatures &&
    options.env.appLoginPageUrl
  ) {
    appFeatures = options.env.appFeatures;
    appLoginPageUrl = options.env.appLoginPageUrl;
  }

  const $y9_SSO = {
    ssoRedisGetApi,
    ssoRedisDeleteApi,
    checkLogin,
    checkToken,
    ssoLogout,
    ssoLogin,
  };
  Vue.$y9_SSO = $y9_SSO;
};

export default sso;
