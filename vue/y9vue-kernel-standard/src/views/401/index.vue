<!--
 * @Author:  
 * @Date: 2022-08-02 10:51:50
 * @LastEditors: mengjuhua
 * @LastEditTime: 2023-12-26 11:22:20
 * @Description: 无权限
-->

<template>
    <div class="login">
        <div class="form">
            <h1 class="title"> 401 Error </h1>
            <p class="msg">抱歉，该页面不存在或当前账号无权访问！！！</p>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                type="primary"
                @click="toIndex"
            >
                返回首页
            </el-button>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                type="primary"
                @click="logout"
            >
                退出重新登录
            </el-button>
        </div>
    </div>
</template>
<script lang="ts" setup>
    import { $y9_SSO } from '@/main';
    import { inject } from 'vue';
    import { useRouter } from 'vue-router';
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const router = useRouter();

    function toIndex() {
        router.push({ path: '/' });
    }

    function logout() {
        try {
            const params = {
                redirect_uri: window.location.origin + import.meta.env.VUE_APP_PUBLIC_PATH
            };
            $y9_SSO.ssoLogout(params);
        } catch (error) {
            ElMessage.error(error.msg || 'Has Error');
        }
    }
</script>
<style scoped>
    .login {
        display: flex;
        width: 100%;
        height: 100vh;
        min-height: 300px;
        overflow: auto;
        scrollbar-width: none;
        /* background-image: url("../../assets/images/bg.jpg"); */
        background-color: #f5f7f9;
        background-repeat: no-repeat;
        background-position: center center;
        background-attachment: fixed;
        background-size: cover;
        align-items: center;
    }

    .form {
        flex: none;
        width: 420px;
        padding: 60px;
        margin: 0 auto;
        border-radius: 4px;
        background-color: rgba(255, 255, 255, 0);
        text-align: center;
        color: rgba(0, 0, 0, 0.5);
    }

    .title {
        font-weight: 300;
    }

    .msg {
        padding: 8px;
    }
</style>
