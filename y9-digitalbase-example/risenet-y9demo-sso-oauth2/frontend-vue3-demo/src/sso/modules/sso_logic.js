"use strict";

/**
 * sso插件
 *
 * 面向对象的实现方式
 * 规范代码
 *
 */
import JSEncrypt from "jsencrypt";
import ssoRequest from "./sso_lib";
import Y9Utils from "./sso_helper";
import {encode64} from "./utils/base64";
import y9_storage from "./utils/storage";
import {jwtDecode} from "jwt-decode";

function removeParamFromUrl(url, paramName) {
    // 创建一个URL对象
    const urlObj = new URL(url, window.location.href);
    // 创建一个空对象来存储不需要删除的参数
    const params = {};
    // 遍历URL的searchParams
    for (const [key, value] of urlObj.searchParams) {
        // 如果键不等于要删除的参数名，则添加到新对象中
        if (key !== paramName) {
            params[key] = value;
        }
    }
    // 将新参数对象转换回查询字符串
    urlObj.search = new URLSearchParams(params).toString();
    // 返回新的URL
    return urlObj.href;
}

/**
 * 全局弹窗组件
 * @param {string} content - 弹窗内容
 * @param {boolean} showOverlay - 是否显示全屏遮罩，默认为true
 * 示例调用
 * createPopup('<h1>这是一个弹窗</h1><p>这是弹窗的内容。</p>', true); // 带全屏遮罩
 * createPopup('<h1>没有遮罩的弹窗</h1>', false); // 不带全屏遮罩
 */
function createPopup(content, showOverlay = true) {
    // 创建遮罩层（全屏背景）
    const overlay = document.createElement("div");
    overlay.style.position = "fixed";
    overlay.style.top = "0";
    overlay.style.left = "0";
    overlay.style.width = "100%";
    overlay.style.height = "100%";
    overlay.style.backgroundColor = "rgba(0, 0, 0, 0.5)";
    overlay.style.zIndex = "9998";
    overlay.style.display = showOverlay ? "block" : "none"; // 控制是否显示遮罩

    // 创建弹窗容器
    const popup = document.createElement("div");
    popup.style.position = "fixed";
    popup.style.top = "50%";
    popup.style.left = "50%";
    popup.style.transform = "translate(-50%, -50%)";
    popup.style.backgroundColor = "#fff";
    popup.style.padding = "20px";
    popup.style.borderRadius = "8px";
    popup.style.boxShadow = "0 4px 6px rgba(0, 0, 0, 0.1)";
    popup.style.zIndex = "9999";
    popup.style.minWidth = "300px";
    popup.style.textAlign = "center";

    // 设置弹窗内容
    popup.innerHTML = content;

    // 创建关闭按钮
    const closeButton = document.createElement("button");
    closeButton.textContent = "关闭";
    closeButton.style.marginTop = "15px";
    closeButton.style.padding = "8px 16px";
    closeButton.style.backgroundColor = "#007bff";
    closeButton.style.color = "#fff";
    closeButton.style.border = "none";
    closeButton.style.borderRadius = "4px";
    closeButton.style.cursor = "pointer";

    // 关闭弹窗的逻辑
    closeButton.onclick = () => {
        document.body.removeChild(overlay);
        document.body.removeChild(popup);
        window.location.reload();
    };

    // 将关闭按钮添加到弹窗中
    popup.appendChild(closeButton);

    // 将遮罩和弹窗添加到页面中
    document.body.appendChild(overlay);
    document.body.appendChild(popup);
}

/**
 * 不管有没有登录，只校验一次license
 */
async function checkAppLicense(licenseApi) {
    // 获取缓存的日期
    let lastCheckDate = localStorage.getItem("lastCheckDate");
    const date = new Date();
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, "0");
    const day = date.getDate().toString().padStart(2, "0");
    const yyyymmdd = year.toString() + month + day;
    // 如果存在且等于今天，则不再校验
    if (lastCheckDate && lastCheckDate == yyyymmdd) {
        return true;
    }
    // 否则，更新缓存，调接口
    localStorage.setItem("lastCheckDate", yyyymmdd);
    let str = window.location.href + "";
    str = str.split("?")[0];
    let strToArray = str.split("/");
    let contextUrl = `${strToArray[0]}/${strToArray[1]}/${strToArray[2]}/${strToArray[3]}`;
    let checkLicenseApi = null;
    let licenseExpiredUrl = `${contextUrl}/LicenseExpired`;

    if (licenseApi) {
        checkLicenseApi = licenseApi;
    } else {
        checkLicenseApi = `${contextUrl}/services/rest/license/getInfo`;
    }
    let res = null;
    try {
        res = await fetch(checkLicenseApi, {
            method: "POST",
            headers: {
                "Content-Type":
                    "application/x-www-form-urlencoded;charset=UTF-8",
            },
        })
            .then((res) => {
                return res.json();
            })
            .then((res) => {
                return res;
            })
            .catch((e) => {
                console.log(e);
            });
    } catch (error) {
        console.log(error);
    }
    // 0为已过期，-1为永久有效，>0为实际有效天数
    // res.code = 500;
    if (res && res.code == 200) {
        // 修改返回的数据，用于测试
        res.data.days = 1;
        console.log(res);
        if (res.data.days > 0 && res.data.days < 30) {
            // 弹窗提示还有几天过期
            createPopup(
                "<h1>license 过期提示</h1><p>还有" +
                res.data.days +
                "天过期</p>",
                true,
            );
            await sleep(3000); // 暂停 3 秒
        }
        if (res.data.days == 0) {
            // 移除缓存信息，重新校验
            localStorage.removeItem("lastCheckDate");
            console.log(licenseExpiredUrl);
            // 跳转到过期页面，提示已过期
            window.location = licenseExpiredUrl;
        }
    }
    if (res && res.code >= 500) {
        // 移除缓存信息，重新校验
        localStorage.removeItem("lastCheckDate");

        createPopup("<h1>license 无效</h1>", true);
        // await sleep(3000); // 暂停 3 秒
        window.location = `${licenseExpiredUrl}?info=License无效`;
    }
}

export default (function () {
    const {log, error, warn} = console;
    /**
     * 定义插件类
     */
    var Y9SSO = function () {
    };
    /**
     * 单例模式
     */
    Y9SSO.instant = null;

    /**
     * 初始化类的参数
     */
    Y9SSO.prototype.initParams = function (Vue, options) {
        if (!options || !options.env || !options.env.sso) {
            error("没有传入sso配置信息");
            return false;
        }
        this.ssoInfo = options.env.sso;
        // 插件必要的参数,SSO服务地址
        if (!this.ssoInfo.hasOwnProperty("VUE_APP_SSO_DOMAINURL")) {
            error(
                "入口函数（比如main.js）没有获取到env.sso['VUE_APP_SSO_DOMAINURL']参数",
            );
            window.alert(
                "重构后，原传入参数【VUE_APP_SSO】更名为【VUE_APP_SSO_DOMAINURL】,请检查！",
            );
            return false;
        }
        this.vue_app_sso_domainUrl = this.ssoInfo.VUE_APP_SSO_DOMAINURL;

        // 插件必要的参数,SSO服务上下文
        if (!this.ssoInfo.hasOwnProperty("VUE_APP_SSO_CONTEXT")) {
            error(
                "入口函数（比如main.js）没有获取到env.sso['VUE_APP_SSO_CONTEXT']参数",
            );
            return false;
        }
        this.vue_app_sso_context = this.ssoInfo.VUE_APP_SSO_CONTEXT;

        // 插件必要的参数,SSO服务授权地址
        if (!this.ssoInfo.hasOwnProperty("VUE_APP_SSO_AUTHORIZE_URL")) {
            error(
                "入口函数（比如main.js）没有获取到env.sso['VUE_APP_SSO_AUTHORIZE_URL']参数",
            );
            return false;
        }
        this.vue_app_sso_authorize_url = this.ssoInfo.VUE_APP_SSO_AUTHORIZE_URL;
        if (this.vue_app_sso_authorize_url.indexOf("oidc") > -1) {
            this.oidcVersion = true;
        } else {
            this.oidcVersion = false;
        }

        // 插件必要的参数,SSO服务注销地址
        if (!this.ssoInfo.hasOwnProperty("VUE_APP_SSO_LOGOUT_URL")) {
            error(
                "入口函数（比如main.js）没有获取到env.sso['VUE_APP_SSO_LOGOUT_URL']参数",
            );
            window.alert(
                "重构后，原传入参数【VUE_APP_Y9_LOGOUT_URL】更名为【VUE_APP_SSO_LOGOUT_URL】,请检查！",
            );
            return false;
        }
        this.vue_app_sso_logout_url = this.ssoInfo.VUE_APP_SSO_LOGOUT_URL;

        // 插件必要的参数,SSO服务客户端ID
        if (!this.ssoInfo.hasOwnProperty("VUE_APP_SSO_CLIENT_ID")) {
            error(
                "入口函数（比如main.js）没有获取到env.sso['VUE_APP_SSO_CLIENT_ID']参数",
            );
            return false;
        }
        this.vue_app_sso_client_id = this.ssoInfo.VUE_APP_SSO_CLIENT_ID;

        // 插件必要的参数,SSO服务客户端密钥
        if (!this.ssoInfo.hasOwnProperty("VUE_APP_SSO_SECRET")) {
            error(
                "入口函数（比如main.js）没有获取到env.sso['VUE_APP_SSO_SECRET']参数",
            );
            return false;
        }
        this.vue_app_sso_secret = this.ssoInfo.VUE_APP_SSO_SECRET;

        // 插件必要的参数,SSO服务授权类型
        if (!this.ssoInfo.hasOwnProperty("VUE_APP_SSO_GRANT_TYPE")) {
            error(
                "入口函数（比如main.js）没有获取到env.sso['VUE_APP_SSO_GRANT_TYPE']参数",
            );
            window.alert(
                "重构后，原传入参数【VUE_APP_GRANT_TYPE】更名为【VUE_APP_SSO_GRANT_TYPE】,请检查！",
            );
            return false;
        }
        this.vue_app_sso_grant_type = this.ssoInfo.VUE_APP_SSO_GRANT_TYPE;

        // 插件必要的参数,SSO服务站点令牌密钥键
        if (!this.ssoInfo.hasOwnProperty("VUE_APP_SSO_SITETOKEN_KEY")) {
            error(
                "入口函数（比如main.js）没有获取到env.sso['VUE_APP_SSO_SITETOKEN_KEY']参数",
            );
            window.alert(
                "重构后，原传入参数【VUE_APP_SITETOKEN】更名为【VUE_APP_SSO_SITETOKEN_KEY】,请检查！",
            );
            return false;
        }
        this.vue_app_sso_siteTokenKey = this.ssoInfo.VUE_APP_SSO_SITETOKEN_KEY;

        // 插件可选参数
        if (this.ssoInfo.hasOwnProperty("VUE_APP_APPFEATURES")) {
            this.appFeatures = this.ssoInfo.VUE_APP_APPFEATURES;
            if (!this.ssoInfo.hasOwnProperty("VUE_APP_LOGIN_PAGE_URL")) {
                error(
                    "特定的登录页面检测， 必须传入sso.env.VUE_APP_LOGIN_PAGE_URL参数",
                );
                return false;
            }
            this.appLoginPageUrl = this.ssoInfo.VUE_APP_LOGIN_PAGE_URL;
        }
        // 插件可选参数，指定传入 license 参数
        // this.hostLicense = null;
        // if (this.ssoInfo.hasOwnProperty("VUE_APP_HOST_LICENSE")) {
        // 	this.hostLicense = this.ssoInfo.VUE_APP_HOST_LICENSE;
        // }
        // 如果传了license 参数，但是值仍是空，按照对应工程的上下文接口拼接
        // if (
        // 	!this.hostLicense &&
        // 	this.ssoInfo.hasOwnProperty("VUE_APP_CONTEXT")
        // ) {
        // 	this.hostLicense = `${this.ssoInfo.VUE_APP_CONTEXT}services/rest/license/getInfo`;
        // }

        // 插件可选参数 - 打印log
        if (options.env.hasOwnProperty("logInfo")) {
            if (options.env.logInfo.hasOwnProperty("showLog")) {
                this.showLog = true;
            } else {
                this.showLog = false;
            }

            // 定时器间隔执行时间，有传入值则使用它，无则默认
            if (options.env.logInfo.hasOwnProperty("sso_refresh_Timer_t")) {
                this.sso_refresh_Timer_t =
                    options.env.logInfo.sso_refresh_Timer_t;
            } else {
                this.sso_refresh_Timer_t = 1800000;
            }
        } else {
            this.showLog = false;
            this.sso_refresh_Timer_t = 1800000;
        }

        // 定时器
        this.sso_Timer = null;

        if (this.showLog) {
            log(`Y9SSO对象：` + this);
            log(`VUE_APP_SSO_DOMAINURL=${this.vue_app_sso_domainUrl}`);
            log(`VUE_APP_SSO_CONTEXT=${this.vue_app_sso_context}`);
            log(`VUE_APP_SSO_AUTHORIZE_URL=${this.vue_app_sso_authorize_url}`);
            log(`VUE_APP_SSO_LOGOUT_URL=${this.vue_app_sso_logout_url}`);
            log(`VUE_APP_SSO_CLIENT_ID=${this.vue_app_sso_client_id}`);
            log(`VUE_APP_SSO_SECRET=${this.vue_app_sso_secret}`);
            log(`VUE_APP_SSO_GRANT_TYPE=${this.vue_app_sso_grant_type}`);
            log(`VUE_APP_SSO_SITETOKEN_KEY=${this.vue_app_sso_siteTokenKey}`);
        }
    };

    /**
     * 定时器
     */
    Y9SSO.prototype.ssoTimerRun = async function () {
        if (Y9SSO.instant.sso_Timer) {
            clearInterval(Y9SSO.instant.sso_Timer);
        } else {
            Y9SSO.instant.sso_Timer = setInterval(async () => {
                await Y9SSO.instant.ssoTimer_refreshToken();
            }, Y9SSO.instant.sso_refresh_Timer_t);
        }
    };

    /**
     * 登录跳转逻辑【重要】
     */
    Y9SSO.prototype.checkLogin = async function (callbackPageUrl = "") {
        let q = Y9Utils.parseQueryString(window.location.search.substring(1));
        // 先存储所有参数，也许有带其它参数可供项目使用到【从安全角度看，可能需要删除和单点登录相关的code和serviceTicketId和state】
        const last_q = y9_storage.getObjectItem("query") || {};
        const assgin_q = q ? Object.assign(last_q, q) : last_q;
        y9_storage.setObjectItem("query", assgin_q);

        // 获取应用的首页URL
        let sso_callback_url =
            window.location.origin + window.location.pathname;
        // 客户自己的单点登陆页面【项目最好别走这个if，多项目集成时可能会有大问题】
        if (!q && Y9SSO.instant.appLoginPageUrl) {
            if (callbackPageUrl) {
                Y9SSO.instant.showLog
                    ? log(
                        `使用了独立的单点登陆页面【项目最好别走这个if，多项目集成时容易出问题】`,
                    )
                    : "";
                Y9SSO.instant.navToLogin(callbackPageUrl);
            } else {
                Y9SSO.instant.showLog ? log(`使用了独立的单点登陆页面`) : "";
                window.location = Y9SSO.instant.appLoginPageUrl;
            }
        }
        // 单点登录服务器上的登陆页面
        if (!q && !Y9SSO.instant.appLoginPageUrl) {
            Y9SSO.instant.showLog ? log(`单点登录服务器上的登陆页面`) : "";
            Y9SSO.instant.navToLogin(sso_callback_url);
        }

        // sso的uri回调中包含code
        if (q && q.code) {
            /**
             * 2025-02-27 兼容原先的auth2.0旧版本
             */
            let _clientIdKey_ = "client_id";
            let _clientSecretKey_ = "client_secret";
            const config_token = {
                grant_type: Y9SSO.instant.vue_app_sso_grant_type, // 固定
                [_clientIdKey_]: Y9SSO.instant.vue_app_sso_client_id, // 固定
                [_clientSecretKey_]: Y9SSO.instant.vue_app_sso_secret, // 固定
                code: q.code,
                redirect_uri: sso_callback_url,
            };
            Y9SSO.instant.showLog
                ? log(`登录跳转链接中包含code时获取access_token`)
                : "";
            const getAccessToken = await Y9SSO.instant
                .ssoGetAccessTokenApi({params: config_token})
                .then((res) => {
                    return res;
                })
                .catch((e) => {
                    error(
                        `登录跳转链接中包含code时，获取access_token的过程中catch到错误:` +
                        e,
                    );
                });
            Y9SSO.instant.showLog
                ? log(
                    `获取access_token成功，在sessionStorage中查看${Y9SSO.instant.vue_app_sso_siteTokenKey}`,
                )
                : "";
            let currentTimestamp = Date.parse(new Date())
                .toString()
                .slice(0, 10);

            Y9SSO.instant.showLog
                ? log(
                    `检查ssoGetAccessTokenApi返回的数据类型${typeof getAccessToken},数据是${JSON.stringify(
                        getAccessToken,
                    )}`,
                )
                : "";
            /**
             * 2024-4-8
             * 福田-数字底座-生产环境打包结果的测试
             * 谷歌浏览器上，有时候这里得到的是一个字符串导致出错，而不是异步接口应该返回的数据对象
             */
            if (typeof getAccessToken != "object") {
                window.location = window.location.href;
            }
            // expire_in是秒数，API显示的数据是 28800秒 = 8小时
            getAccessToken.expires_in = getAccessToken.expires_in - 1800; // 有效期时间，客户端比服务器少300秒
            getAccessToken.count_expires_in =
                parseInt(currentTimestamp) +
                parseInt(getAccessToken.expires_in);
            // 单独缓存 id_token，因为它只有第一次登录时有，后续刷新页面不会再次出现，主要用于获取用户信息、单点登出时使用
            if (getAccessToken.id_token) {
                y9_storage.setStringItem("id_token", getAccessToken.id_token);
            } else {
                warn(
                    "id_token不存在，请检查测试，1、是否使用 oidc版本的单点服务？    2、前端问题？    3、后端问题？",
                );
            }
            // session存储登陆信息
            let isCashe = y9_storage.setObjectItem(
                Y9SSO.instant.vue_app_sso_siteTokenKey,
                getAccessToken,
            );
            if (isCashe) {
                await Y9SSO.instant.ssoGetUserInfoApi();
            }
            // 删除回调url中的code
            Y9SSO.instant.showLog ? log(`删除回调url中的code`) : "";
            // window.location = window.location.origin + window.location.pathname;
            let baseUrl = window.location.origin + window.location.pathname;
            history.replaceState({}, "", baseUrl);
        }

        // 切换账号
        if (q && q.ticket) {
            // 获取当前url
            sso_callback_url = window.location.href;
            Y9SSO.instant.showLog ? log(`切换账号`) : "";
            Y9SSO.instant.navToLogin(sso_callback_url);
        }

        // 工作流 新开页签会因为没有缓存而无法进入这个函数里的条件，导致单点登录失败
        if (q && q.itemId) {
            // 获取当前url
            sso_callback_url = window.location.href;
            Y9SSO.instant.showLog ? log(`q.itemId`) : "";
            Y9SSO.instant.navToLogin(sso_callback_url);
        }

        // 处理任意其它未知参数的情况
        if (q) {
            Y9SSO.instant.showLog
                ? log(`处理任意其它未知参数的情况，q=${JSON.stringify(q)}`)
                : "";
            window.location = window.location.origin + window.location.pathname; // 即当前url地址，包括带参数的情况
            // Y9SSO.instant.navToLogin(sso_callback_url);
        }
    };

    /**
     * 单点登录 - 授权成功后，将要跳转的url
     */
    Y9SSO.prototype.navToLogin = function (sso_callback_url) {
        /**
         * 2025-02-27 兼容原先的auth2.0旧版本
         */
        let _scope_ = `scope=openid y9`;
        let _response_type_ = `response_type=code`;
        let _state_ = `state=${Y9Utils.generateRandomString()}`;
        let _clientId_ = `client_id=${Y9SSO.instant.vue_app_sso_client_id}`;
        let _clientSecret_ = `client_secret=${Y9SSO.instant.vue_app_sso_secret}`;
        let _redirect_uri_ = `redirect_uri=${sso_callback_url}`;
        if (!Y9SSO.instant.oidcVersion) {
            // value不一样
            _scope_ = `scope=all`;
        }
        const url = `${Y9SSO.instant.vue_app_sso_authorize_url}?${_clientId_}&${_clientSecret_}&${_response_type_}&${_state_}&${_redirect_uri_}&${_scope_}`;

        Y9SSO.instant.showLog
            ? log(
                `单点登录 - 传入插件的授权码url=【${Y9SSO.instant.vue_app_sso_authorize_url}】`,
            )
            : "";
        Y9SSO.instant.showLog
            ? log(`单点登录 - 拼接授权码的完整url=【${url}】`)
            : "";
        Y9SSO.instant.showLog
            ? log(
                `单点登录 - 授权成功后，将要跳转的url=【${sso_callback_url}】`,
            )
            : "";
        // 多标签场景下，其中一个标签退出，另一个标签刷新y9_storage.getObjectItem()返回false，获取不到数据
        if (!Y9SSO.instant.vue_app_sso_authorize_url) {
            Y9SSO.instant.showLog
                ? log(`【重构后测试此if问题是否还存在】`)
                : "";
            let arr = url.split("=");
            window.location = arr[arr.length - 1];
        } else {
            window.location = url;
        }
    };

    /**
     * 获取用户输入的用户名和密码开始单点登录
     */
    Y9SSO.prototype.ssoLogin = async function (
        username,
        password,
        tenantShortName,
        redirect_uri,
    ) {
        const pwdEcodeType = await Y9SSO.instant.getRandom();
        const __JSEncrypt__ = new JSEncrypt();
        __JSEncrypt__.setPublicKey(pwdEcodeType);
        let str = __JSEncrypt__.encrypt(encode64(password));
        const params = {
            // loginType: 'loginName', // 固定
            tenantShortName: tenantShortName ? tenantShortName : "risesoft",
            username: encode64(username),
            password: str,
            pwdEcodeType,
        };
        // checkSsoLoginInfo - API
        const check = await Y9SSO.instant
            .checkSsoLoginInfoApi({params})
            .catch((e) => {
                error(
                    "获取用户输入的用户名和密码开始单点登录的过程中，【checkSsoLoginInfoApi执行catch到错误】 e=" +
                    e,
                );
            });
        if (check.success) {
            console.log(check);
            Y9SSO.instant.showLog ? log(`登陆前的认证成功 ${check}`) : "";
            params.loginName = "loginName";
            params.noLoginScreen = true;
            params.service = redirect_uri;
            // sso登陆
            return await Y9SSO.instant
                .ssoLoginApi(params)
                .then(async (res) => {
                    if (res.success) {
                        // 登陆成功 获取授权码
                        /**
                         * 2025-02-27 兼容原先的auth2.0旧版本
                         */
                        let _scope_ = `scope=openid y9`;
                        let _response_type_ = `response_type=code`;
                        let _state_ = `state=${Y9Utils.generateRandomString()}`;
                        let _clientId_ = `client_id=${Y9SSO.instant.vue_app_sso_client_id}`;
                        let _clientSecret_ = `client_secret=${Y9SSO.instant.vue_app_sso_secret}`;
                        let _redirect_uri_ = `redirect_uri=${sso_callback_url}`;
                        if (!Y9SSO.instant.oidcVersion) {
                            // value不一样
                            _scope_ = `scope=all`;
                        }
                        const url = `${Y9SSO.instant.vue_app_sso_authorize_url}?${_response_type_}&${_clientId_}&${_clientSecret_}&${_redirect_uri_}&${_scope_}`;
                        Y9SSO.instant.showLog ? log(`登陆成功`) : "";
                        Y9SSO.instant.showLog
                            ? log(`拼接获取授权码的完整URL=${url}`)
                            : "";
                        window.location = url;
                    } else {
                        Y9SSO.instant.showLog
                            ? error(
                                `登陆失败，请检查用户名和密码，resultInfo=` +
                                check,
                            )
                            : "";
                        return false;
                    }
                })
                .catch((e) => {
                    error(
                        "获取用户输入的用户名和密码开始单点登录的过程中，【ssoLoginApi执行catch到错误】 e=" +
                        e,
                    );
                });
        } else {
            window.alert(check.msg);
            return false;
        }
    };

    /**
     * 获取当前会话登陆信息
     */
    Y9SSO.prototype.getSessionCache = async function () {
        // 当前时间戳
        let currentTimestamp = Date.parse(new Date()).toString().slice(0, 10),
            localCache = y9_storage.getObjectItem(
                Y9SSO.instant.vue_app_sso_siteTokenKey,
            ),
            count_expires_in;
        // 查询会话页面是否有session缓存
        if (localCache && localCache.refresh_token) {
            count_expires_in = y9_storage.getObjectItem(
                Y9SSO.instant.vue_app_sso_siteTokenKey,
                "count_expires_in",
            );
            let isValidTime = currentTimestamp - count_expires_in;
            // 是否需要更新token
            if (isValidTime > 0 && isValidTime < 1800) {
                await Y9SSO.instant.refreshToken().catch((e) => {
                    error(
                        `获取当前会话登陆信息 - 执行refreshToken函数catch错误：` +
                        e,
                    );
                });
                return true;
            } else {
                return true;
            }
        } else {
            return false;
        }
    };

    /**
     * 检查token
     * 2025-4-3，先检查 license
     */
    Y9SSO.prototype.checkToken = async function () {
        /**
         * 检查license
         */
            // await checkAppLicense(Y9SSO.instant.hostLicense);

        let isValid = await Y9SSO.instant.getSessionCache();
        if (isValid) {
            let userName = y9_storage.getStringItem("userName");
            if (!userName) {
                await Y9SSO.instant.ssoGetUserInfoApi();
            }
            return true;
        } else {
            // 无效
            return false;
        }
    };

    /**
     * 强制更新token、redis缓存时间
     * 每30分钟定时器更新一次
     * */
    Y9SSO.prototype.ssoTimer_refreshToken = async function () {
        Y9SSO.instant.showLog
            ? log(`========================================================`)
            : "";
        Y9SSO.instant.showLog
            ? log(`【${new Date()}】sso - 自动执行了定时器`)
            : "";
        Y9SSO.instant.showLog ? log(`更新前`) : "";
        // Y9SSO.instant.showLog?log(`guid = `, sessionStorage.getItem("guid")):'';
        Y9SSO.instant.showLog
            ? log(
                `${Y9SSO.instant.vue_app_sso_siteTokenKey} = `,
                y9_storage.getObjectItem(
                    Y9SSO.instant.vue_app_sso_siteTokenKey,
                ),
            )
            : "";
        Y9SSO.instant.showLog
            ? log(`--------------------------------------------------------`)
            : "";

        // 当前时间戳
        let currentTimestamp = Date.parse(new Date()).toString().slice(0, 10),
            localCache = y9_storage.getObjectItem(
                Y9SSO.instant.vue_app_sso_siteTokenKey,
            ),
            count_expires_in;

        // 查询会话页面是否有session缓存
        if (localCache && localCache.refresh_token) {
            count_expires_in = y9_storage.getObjectItem(
                Y9SSO.instant.vue_app_sso_siteTokenKey,
                "count_expires_in",
            );

            let refresh_token = y9_storage.getObjectItem(
                Y9SSO.instant.vue_app_sso_siteTokenKey,
                "refresh_token",
            );
            // 更新token
            await Y9SSO.instant.refreshToken().catch((e) => log(e.message));
            // 重新获取用户信息
            // await Y9SSO.instant.initUserData();
        }
        Y9SSO.instant.showLog ? log(`更新后`) : "";
        // showLog?log(`guid = `, sessionStorage.getItem("guid")):'';
        Y9SSO.instant.showLog
            ? log(
                `${Y9SSO.instant.vue_app_sso_siteTokenKey} = `,
                y9_storage.getObjectItem(
                    Y9SSO.instant.vue_app_sso_siteTokenKey,
                ),
            )
            : "";
        Y9SSO.instant.showLog
            ? log(`========================================================`)
            : "";
    };

    /**
     * 更新token
     */
    Y9SSO.prototype.refreshToken = async function () {
        const currentTimestamp = Date.parse(new Date()).toString().slice(0, 10);
        let _clientIdKey_ = "client_id";
        let _clientSecretKey_ = "client_secret";
        const config_token = {
            grant_type: "refresh_token", // 固定
            [_clientIdKey_]: Y9SSO.instant.vue_app_sso_client_id, // 固定
            [_clientSecretKey_]: Y9SSO.instant.vue_app_sso_secret, // 固定
            refresh_token: y9_storage.getObjectItem(
                Y9SSO.instant.vue_app_sso_siteTokenKey,
                "refresh_token",
            ),
            redirect_uri: window.location.href,
        };

        const getNewToken = await Y9SSO.instant
            .ssoGetAccessTokenApi({params: config_token})
            .then((res) => {
                return res;
            })
            .catch((e) => {
                error(`更新token的过程中catch到错误:` + e);
            });
        if (getNewToken && getNewToken.expires_in) {
            // expire_in是秒数，API显示的数据是 28800秒 = 8小时
            getNewToken.expires_in = getNewToken.expires_in - 1800; // 有效期时间，客户端比服务器少300秒
            getNewToken.count_expires_in =
                parseInt(currentTimestamp) + parseInt(getNewToken.expires_in);
        }

        // session存储登陆信息
        const isCashe = y9_storage.setObjectItem(
            Y9SSO.instant.vue_app_sso_siteTokenKey,
            getNewToken,
        );
        if (!isCashe) {
            Y9SSO.instant.showLog ? log("没有成功缓存") : "";
            return false;
        } else {
            return true;
        }
    };

    /**
     * 检索用户和租户信息
     */
    Y9SSO.prototype.getLoginNameAndTenants = async function (params) {
        Y9SSO.instant.showLog
            ? log(
                `单点登录 - 【sso-检索用户和租户信息API】得到的参数：`,
                params,
            )
            : "";
        return await ssoRequest({
            url: `${Y9SSO.instant.vue_app_sso_context}/api/loginNameAndTenants`,
            method: "post",
            cType: false,
            test: true,
            params,
        });
    };

    /**
     * 获取 RSA 加密公钥
     */
    Y9SSO.prototype.getRandom = async function () {
        let random = await ssoRequest({
            url: `${Y9SSO.instant.vue_app_sso_context}/api/getRandom`,
            method: "get",
            cType: false,
            test: true,
            params: {_: Date.now()},
        });
        if (random.success) {
            return random.data;
        } else {
            error("获取公钥失败," + random.msg);
        }
    };

    /**
     * 单点登录 - 检查API
     */
    Y9SSO.prototype.checkSsoLoginInfoApi = async function (data) {
        Y9SSO.instant.showLog
            ? log(
                `单点登录 - 【sso-1-登录前的认证API】得到的参数：`,
                data.params,
            )
            : "";
        return await ssoRequest({
            url: `${Y9SSO.instant.vue_app_sso_context}/api/checkSsoLoginInfo`,
            method: "post",
            cType: false,
            test: true,
            params: data.params,
        });
    };

    /**
     * 单点登录 - 登录API
     */
    Y9SSO.prototype.ssoLoginApi = async function (params) {
        Y9SSO.instant.showLog
            ? log(`单点登录 - 【sso-2-登录API】得到的参数：`, params)
            : "";
        return await ssoRequest({
            url: `${Y9SSO.instant.vue_app_sso_context}/api/logon`,
            method: "post",
            cType: false,
            test: true,
            params,
        });

        // let data = new URLSearchParams();
        // data.append('username', params.username);
        // data.append('password', params.password);
        // data.append('loginName', params.loginName);
        // data.append('tenantShortName', params.tenantShortName);
        // data.append('service', params.service);
        // data.append('pwdEcodeType', params.pwdEcodeType);
        // data.append('noLoginScreen', params.noLoginScreen);
        // return await fetch(`${Y9SSO.instant.vue_app_sso_domainUrl + Y9SSO.instant.vue_app_sso_context}/api/logon`, {
        //     method: 'POST',
        //     body: data,
        //     headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        // })
        //     .then((res) => res.json())
        //     .catch((error) => {
        //         console.log(error);
        //     });
    };

    /**
     * 单点登录 - 获取和更新token API
     */
    // Y9SSO.prototype.ssoGetAccessTokenApi = async function (data) {
    //     Y9SSO.instant.showLog ? log(`单点登录 - 【sso-3-获取和更新token API】得到的参数：`, data.params) : '';
    //     return await ssoRequest({
    //         url: `${Y9SSO.instant.vue_app_sso_context}/oidc/accessToken`,
    //         method: 'get',
    //         cType: false,
    //         params: data.params
    //     });
    // };
    /**
     * 1、/oidc/accessToken 需要 POST 请求，且参数以 form-data 方式提交
     * 2、redirect_uri 这个参数中不能有 code state response 这几个参数
     *  这两个调整内容都是*****由于新版本的 sso 增加的限制*****，
     * 这个修改应该都能兼容旧版本的 sso 服务端
     */
    Y9SSO.prototype.ssoGetAccessTokenApi = async function (data) {
        Y9SSO.instant.showLog
            ? log(
                `单点登录 - 【sso-3-获取和更新token API】得到的参数：`,
                data.params,
            )
            : "";
        let delete_code_url = removeParamFromUrl(
            data.params.redirect_uri,
            "code",
        );
        let delete_state_url = removeParamFromUrl(delete_code_url, "state");
        let delete_response_url = removeParamFromUrl(
            delete_state_url,
            "response",
        );
        let redirect_uri = delete_response_url;
        Y9SSO.instant.showLog
            ? log(
                `redirect_uri 这个参数中不能有 code state response 这几个参数的处理结果`,
                redirect_uri,
            )
            : "";
        let formData = new URLSearchParams();
        formData.append("grant_type", data.params.grant_type);
        formData.append("client_id", data.params.client_id);
        formData.append("client_secret", data.params.client_secret);
        if (data.params.grant_type === "refresh_token") {
            formData.append("refresh_token", data.params.refresh_token);
        } else {
            formData.append("code", data.params.code);
        }
        formData.append("redirect_uri", redirect_uri);
        let strText = "oidc";
        if (!Y9SSO.instant.oidcVersion) {
            strText = "oauth2.0";
        }
        return await ssoRequest({
            url: `${Y9SSO.instant.vue_app_sso_context}/${strText}/accessToken`,
            method: "post",
            cType: false,
            params: formData,
        });
    };

    /**
     * 单点登录 - 获取用户的信息 API
     */
    Y9SSO.prototype.ssoGetUserInfoApi = async function () {
        let access_token = y9_storage.getObjectItem(
            Y9SSO.instant.vue_app_sso_siteTokenKey,
            "access_token",
        );
        let userInfoKey = "ssoUserInfo";
        let get_user_info = null;
        if (!access_token) {
            error(
                `单点登录 - 【sso-4-获取用户的信息 API】没有获取到access_token参数`,
            );
            return false;
        }
        Y9SSO.instant.showLog
            ? log(
                `单点登录 - 【sso-4-获取用户的信息 API】access_token = `,
                access_token,
            )
            : "";

        function isJWT(str) {
            // JWT 应由三部分组成，用两个点分隔
            const parts = str.split(".");
            if (parts.length !== 3) return false;
            // Base64Url 正则表达式（允许大小写字母、数字、下划线和连字符）
            const base64UrlPattern = /^[A-Za-z0-9_-]+$/;
            // 检查所有部分是否非空且符合 Base64Url 格式
            return parts.every(
                (part) => part.length > 0 && base64UrlPattern.test(part),
            );
        }

        // 如果 access_token 是 jwt格式的，直接解析出来，无论是 oidc 还是 oauth2.0
        if (isJWT(access_token)) {
            get_user_info = jwtDecode(access_token);
            Y9SSO.instant.showLog ? log(`JWT解析：`, get_user_info) : "";
        }
        // 否则，用这个方法获取用户信息
        else {
            let strText = "oidc";
            if (!Y9SSO.instant.oidcVersion) {
                strText = "oauth2.0";
            }
            get_user_info = await ssoRequest({
                url: `${Y9SSO.instant.vue_app_sso_context}/${strText}/profile`,
                method: "get",
                cType: false,
                params: {access_token: access_token},
            });
            Y9SSO.instant.showLog ? log(`API获取：`, get_user_info) : "";
        }

        y9_storage.setObjectItem(userInfoKey, get_user_info);
        y9_storage.setStringItem("userName", get_user_info.name);
        return get_user_info;
    };

    /**
     * 单点登录 - 登出 API
     */
    // 传参的obj应该定义一个默认值，否则会打印一个不影响程序运行的错误，
    // 但是如果修复的话，发现单点登录无法登陆进去，因为cookie中的TGC设置了sameSite = none（不携带cookie），TGC立马就过期了，被拒绝了
    Y9SSO.prototype.ssoLogout = async function (obj) {
        let _service_ = `post_logout_redirect_uri=${window.location.href}`;
        let _id_token_ = `id_token_hint=${y9_storage.getStringItem(
            "id_token",
        )}`;
        let logoutUrl = `${Y9SSO.instant.vue_app_sso_domainUrl}sso/oidc/logout?${_service_}&${_id_token_}`;
        // 如果是,执行传入的自定义的登出URL
        if (obj.logoutUrl) {
            logoutUrl = obj.logoutUrl;
            // 是oidc版本的，检查是否带有id_token，如果没有，则警告一下
            if (
                Y9SSO.instant.oidcVersion &&
                logoutUrl.indexOf("id_token_hint") < 0
            ) {
                warn(
                    `执行了【传入的】登出URL，但logoutUrl未包含 id_token_hint 参数`,
                );
            }
            if (
                Y9SSO.instant.oidcVersion &&
                logoutUrl.indexOf("post_logout_redirect_uri") < 0
            ) {
                warn(
                    `执行了【传入的】登出URL，但logoutUrl未包含 post_logout_redirect_uri 参数，登出后无法跳转到登录页面`,
                );
            }
            // oidc版本的
            if (Y9SSO.instant.oidcVersion) {
                Y9SSO.instant.showLog
                    ? log(`执行了【传入的】登出URL=${logoutUrl} oidc版本`)
                    : "";
            } else {
                Y9SSO.instant.showLog
                    ? log(`执行了【传入的】登出URL=${logoutUrl} oauth2.0版本`)
                    : "";
            }
        } else if (obj.redirect_uri) {
            _service_ = `post_logout_redirect_uri=${obj.redirect_uri}`;
            logoutUrl = `${Y9SSO.instant.vue_app_sso_domainUrl}sso/oidc/logout?${_service_}&${_id_token_}`;
            // oidc版本的
            if (!Y9SSO.instant.oidcVersion) {
                _service_ = `service=${obj.redirect_uri}`;
                logoutUrl = `${Y9SSO.instant.vue_app_sso_domainUrl}sso/logout?${_service_}`;
            }
        }
        // 否则，执行插件这里的默认的登出URL
        else {
            // oidc版本的
            if (Y9SSO.instant.oidcVersion) {
                Y9SSO.instant.showLog
                    ? log(`执行了【默认的】登出URL=${logoutUrl} oidc版本`)
                    : "";
            } else {
                _service_ = `service=${window.location.href}/`;
                logoutUrl = `${Y9SSO.instant.vue_app_sso_domainUrl}sso/logout?${_service_}`;
                Y9SSO.instant.showLog
                    ? log(`执行了【默认的】登出URL=${logoutUrl} oauth2.0版本`)
                    : "";
            }
        }
        // 执行删除前操作-自定义的
        if (obj.__y9delete__) {
            await obj.__y9delete__(); // 执行自定义的删除函数（事件）
            Y9SSO.instant.showLog
                ? log(`执行了自定义的删除函数（事件）obj.__y9delete__`)
                : "";
        }

        let temp = sessionStorage.getItem("sso");
        sessionStorage.clear();
        Y9SSO.instant.showLog
            ? log(`登出时执行了【缓存清除语句】==> sessionStorage.clear()`)
            : "";
        sessionStorage.setItem("sso", temp);
        Y9SSO.instant.showLog
            ? log(`如果没有关闭会话页面，这里恢复了要传入sso插件的参数`)
            : "";
        // 执行sso的退出
        window.location = window.encodeURI(logoutUrl);
    };

    /**
     * 单点登录 - 不登出，只清除缓存并重载当前页面
     */
    Y9SSO.prototype.clearCurrentSessionStorage = async function () {
        sessionStorage.clear();
        window.location.reload();
    };

    // 单例模式
    if (Y9SSO.instant) {
        return Y9SSO.instant;
    } else {
        Y9SSO.instant = new Y9SSO();
        return Y9SSO.instant;
    }
})();
