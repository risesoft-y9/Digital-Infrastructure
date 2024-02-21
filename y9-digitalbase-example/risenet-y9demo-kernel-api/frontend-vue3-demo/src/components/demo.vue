<!--
 * @Author: qinman
 * @Date: 2022-09-30 09:03:10
 * @LastEditors: qinman
 * @LastEditTime: 2022-10-19 17:47:09
 * @Description: 
 * @FilePath: \frontend-vue3-demo\src\components\demo.vue
-->
<template>
  <div>
     <div class="demo">
          <div class="btn" @click="logout">点击退出</div>
          <div class="userInfo">当前登陆账号：{{userInfo}}<br><br>token = {{accessToken}}</div>
		  <div class="btn" @click="getUserParent">点击调用后台接口获取当前登陆用户所在部门:</div>
		  <div class="api-data">{{department}}</div>
          <div class="btn" @click="getUserMenus">点击调用后台接口获取当前登陆用户有权限的菜单:</div>
          <div class="api-data">{{menus}}</div>
      </div>
  </div>
</template>

<script setup>
	import { $y9_SSO } from '@/main';
  import { getParent,getMenus } from "../api/modules/test.js";
	let userInfo = ref(sessionStorage.getItem('userName'));
	let accessToken = sessionStorage.getItem(import.meta.env.VUE_APP_SITETOKEN);
    const department = ref({});
    const menus = ref({});

	const logout = async () => {
		let uri = import.meta.env.VUE_APP_Y9_LOGOUT_URL +  window.location.origin + window.location.pathname;
		await $y9_SSO.ssoLogout({logoutUrl: uri});
	}

	async function getUserParent(){
        let result = await getParent();
        if (result) {
            department.value = result.data;
        }
	}

    async function getUserMenus(){
        let result = await getMenus();
        if (result) {
            menus.value = result.data;
        }
	}


</script>

<style scoped>
.demo *{
    width: 1000px;
    margin: 0 auto;
    padding: 10px;
    word-break: break-all;
}
.demo .btn{
    cursor: pointer;
    color: red;
}
.demo .api-data{
    height: 100px;
    overflow: scroll;

}
</style>
