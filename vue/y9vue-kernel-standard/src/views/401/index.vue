<!--
 * @Author:  
 * @Date: 2022-08-02 10:51:50
 * @LastEditors: mengjuhua
 * @LastEditTime: 2023-08-03 10:27:59
 * @Description: 无权限
-->

<template>
    <div class="login">
        <div class="form">
            <h1 class="title"> 401 Error </h1>
            <p class="msg">抱歉，该登录账号非管理员用户账号，没有权限！！！</p>
            <el-button
                type="primary"
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
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
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');

    function logout() {
        try {
            const params = {
                to: { path: window.location.pathname },
                logoutUrl: import.meta.env.VUE_APP_SSO_LOGOUT_URL + import.meta.env.VUE_APP_NAME + '/',
                __y9delete__: () => {
                    // 删除前执行的函数
                    console.log('删除前执行的函数');
                },
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
