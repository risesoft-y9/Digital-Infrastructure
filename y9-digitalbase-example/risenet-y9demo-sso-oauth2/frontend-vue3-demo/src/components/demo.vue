<!--
 * @Author: mengjuhua
 * @Date: 2025-01-15 15:01:32
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-09-02 16:07:30
 * @Description:
-->
<template>
    <div>
        <div class="demo">
            <div class="btn" @click="logout">点击退出</div>
            <div class="userInfo">当前登陆账号：{{ userInfo }}<br/><br/>token = {{ jwt }}</div>
            <div class="btn" @click="getTreeData">点击调用后台接口:</div>
            <div class="api-data">{{ treeData }}</div>
        </div>
    </div>
</template>

<script setup>
import {$y9_SSO} from '@/main';
import {getUserInfo} from '../api/modules/test.js';

let userInfo = ref(sessionStorage.getItem('userName'));
let jwt = sessionStorage.getItem(import.meta.env.VUE_APP_SSO_SITETOKEN_KEY);
const treeData = ref({});

const logout = async () => {
    $y9_SSO.ssoLogout({});
};

async function getTreeData() {
    let result = await getUserInfo();
    if (result) {
        treeData.value = result;
    }
}
</script>

<style scoped>
.demo * {
    width: 1000px;
    margin: 0 auto;
    padding: 10px;
    word-break: break-all;
}

.demo .btn {
    cursor: pointer;
    color: red;
}

.demo .api-data {
    height: 100px;
    overflow: scroll;
}
</style>
