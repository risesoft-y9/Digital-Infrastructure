/**
 * sso 插件初始化
 */
import Y9SSO from "./modules/sso_logic";
import y9_storage from "./modules/utils/storage";

export default function install(Vue, options) {
    y9_storage.setObjectItem("sso", options.env.sso);
    Y9SSO.initParams(Vue, options);
    Y9SSO.ssoTimerRun();
    Vue.$y9_SSO = {
        checkLogin: Y9SSO.checkLogin,
        checkToken: Y9SSO.checkToken,
        ssoLogout: Y9SSO.ssoLogout,
        ssoLogin: Y9SSO.ssoLogin,
        getLoginNameAndTenants: Y9SSO.getLoginNameAndTenants,
        clearCurrentSessionStorage: Y9SSO.clearCurrentSessionStorage,
    };
}
