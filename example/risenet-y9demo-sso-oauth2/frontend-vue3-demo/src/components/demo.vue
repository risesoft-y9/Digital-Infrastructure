<template>
  <div>
     <div class="demo">
          <div class="btn" @click="logout">点击退出</div>
          <div class="userInfo">当前登陆账号：{{userInfo}}<br><br>token = {{jwt}}</div>
		  <div class="btn" @click="getTreeData">点击调用后台接口:</div>
		  <div class="api-data">{{treeData}}</div>
      </div>
  </div>
</template>

<script setup>
	import { $y9_SSO } from '@/main';
    import { getUserInfo } from "../api/modules/test.js";
	let userInfo = ref(sessionStorage.getItem('userName'));
	let jwt = sessionStorage.getItem(import.meta.env.VUE_APP_SITETOKEN);
    const treeData = ref({});

	const logout = async () => {
		let uri = import.meta.env.VUE_APP_Y9_LOGOUT_URL +  window.location.origin + window.location.pathname;
		await $y9_SSO.ssoLogout({logoutUrl: uri});
	}

	async function getTreeData(){
        let result = await getUserInfo();
        if (result) {
            treeData.value = result;
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
