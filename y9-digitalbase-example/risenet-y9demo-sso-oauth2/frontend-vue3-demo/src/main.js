import {createApp} from 'vue'
import App from './App.vue'
import sso from "@/sso"
import router from "@/router/index"
import {setupStore} from '@/store';

// 传入sso所需的环境变量
const env = {
    sso: {
        VUE_APP_SSO_DOMAINURL: import.meta.env.VUE_APP_SSO_DOMAINURL, // sso接口
        VUE_APP_SSO_CONTEXT: import.meta.env.VUE_APP_SSO_CONTEXT, // sso接口上下文
        VUE_APP_SSO_AUTHORIZE_URL: import.meta.env.VUE_APP_SSO_AUTHORIZE_URL, //sso授权码接口
        VUE_APP_SSO_LOGOUT_URL: import.meta.env.VUE_APP_SSO_LOGOUT_URL, //退出URL
        VUE_APP_SSO_CLIENT_ID: import.meta.env.VUE_APP_SSO_CLIENT_ID, //sso接口的固定字段
        VUE_APP_SSO_SECRET: import.meta.env.VUE_APP_SSO_SECRET, //sso接口的固定字段
        VUE_APP_SSO_GRANT_TYPE: import.meta.env.VUE_APP_SSO_GRANT_TYPE, //sso接口的固定字段
        VUE_APP_SSO_SITETOKEN_KEY: import.meta.env.VUE_APP_SSO_SITETOKEN_KEY //sso-token_key
    },
    logInfo: {
        showLog: true
    }
};

const app = createApp(App)
app.use(sso, {env})
setupStore(app);
app.use(router)

app.mount('#app')
export const $y9_SSO = app.$y9_SSO;
