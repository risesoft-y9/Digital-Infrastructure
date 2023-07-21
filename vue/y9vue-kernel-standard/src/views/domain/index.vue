<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-06-28 10:03:00
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-06-28 11:48:59
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-kernel-dcat-style\src\views\domain\index.vue
-->

<template>
  <fixedTreeModule :treeApiObj="treeApiObj" :showNodeDelete="false" @onTreeClick="onTreeClick">
  
  	<template #rightContainer v-if="Object.keys(currTreeNodeInfo).length > 0" >
	
  		<addDomain v-if="currTreeNodeInfo.orgType == 'Department'" :currTreeNodeInfo="currTreeNodeInfo"></addDomain>
		<reminder v-else :currTreeNodeInfo="currTreeNodeInfo" ></reminder>
  		
  	</template>
  
  </fixedTreeModule>
</template>

<script lang="ts" setup>
	import addDomain from './comps/addDomain.vue'
	import reminder from './comps/reminder.vue'
	import {
		treeInterface, 
		getTreeItemById,
		searchByName,
	} from '@/api/org/index';
	
	//数据
	const data = reactive({
		
		treeApiObj:{//tree接口对象
			topLevel:treeInterface,
			childLevel:{
				api:getTreeItemById,
				params:{treeType:'tree_type_dept',disabled:false}
			},
			search:{
				api:searchByName,
				params:{
					treeType:"tree_type_dept"
				}
			}
	
		},
		currTreeNodeInfo:{},//当前tree节点的信息

		
	})
	
	const {
		treeApiObj,
		treeSetting,
		currTreeNodeInfo,
	} = toRefs(data);
	
	//点击tree的回调
	function onTreeClick(currTreeNode){
		
		currTreeNodeInfo.value = currTreeNode;
		
	}
	
</script>

<style scoped lang="scss">
</style>