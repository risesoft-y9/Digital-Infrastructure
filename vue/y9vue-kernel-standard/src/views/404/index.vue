<!--
 * @Author: error: git config user.name && git config user.email & please set dead value or install git
 * @Date: 2022-06-06 11:47:27
 * @LastEditors: mengjuhua
 * @LastEditTime: 2023-12-26 11:22:25
 * @FilePath: \workspace-y9boot-v9.6.x-vue\y9vue-kernel-standard\src\views\404\index.vue
 * @Description: 404
-->
<template>
    <div class="login">
        <div class="form">
            <h1 class="title"> 404 Error </h1>
            <p>抱歉，你访问的页面不存在</p>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                type="primary"
                @click="$router.push('/')"
            >
                返回首页
            </el-button>
        </div>
    </div>
</template>
<script lang="ts" setup>
    import { inject } from 'vue';
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
</script>
<style lang="scss" scoped>
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

        .form {
            flex: none;
            width: 320px;
            padding: 60px;
            margin: 0 auto;
            border-radius: 4px;
            background-color: rgba($color: #000000, $alpha: 0.1);
            text-align: center;
            color: rgba($color: #000000, $alpha: 0.5);

            .title {
                font-weight: 300;
            }
        }
    }
</style>
