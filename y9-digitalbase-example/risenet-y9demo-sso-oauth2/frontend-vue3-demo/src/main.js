import {createApp} from 'vue'
import App from './App.vue'
import sso from "@/sso"
import router from "@/router/index"

const env = {
    sso: {
        VUE_APP_SSO: import.meta.env.VUE_APP_SSO, // sso接口
        VUE_APP_SSO_CONTEXT: import.meta.env.VUE_APP_SSO_CONTEXT, // sso接口上下文
        VUE_APP_SSO_AUTHORIZE_URL: import.meta.env.VUE_APP_SSO_AUTHORIZE_URL, //sso授权码接口
        VUE_APP_Y9_LOGOUT_URL: import.meta.env.VUE_APP_Y9_LOGOUT_URL, //退出URL
        VUE_APP_SSO_CLIENT_ID: import.meta.env.VUE_APP_SSO_CLIENT_ID, //sso接口的固定字段
        VUE_APP_SSO_SECRET: import.meta.env.VUE_APP_SSO_SECRET, //sso接口的固定字段
        VUE_APP_GRANT_TYPE: import.meta.env.VUE_APP_GRANT_TYPE, //sso接口的固定字段
        VUE_APP_REDISKEY: import.meta.env.VUE_APP_REDISKEY, //sso-redisKey
        VUE_APP_SESSIONSTORAGE_GUID: import.meta.env.VUE_APP_SESSIONSTORAGE_GUID, //sso-sessionStorage_guid
        VUE_APP_SITETOKEN: import.meta.env.VUE_APP_SITETOKEN, //sso-token_key
        VUE_APP_SERVER_REDIS: import.meta.env.VUE_APP_SERVER_REDIS //sso-redisServerUrl
    },
    // appFeatures: import.meta.env.VUE_APP_APPFEATURES === '1' ? true : false,
    // appLoginPageUrl: window.location.origin + '/' + import.meta.env.VUE_APP_NAME + '/',
}

const app = createApp(App)
app.use(sso, {env})
app.use(router)
app.mount('#app')
export const $y9_SSO = app.$y9_SSO;
