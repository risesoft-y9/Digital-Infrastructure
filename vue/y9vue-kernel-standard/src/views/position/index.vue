<template>
	
	<fixedTreeModule ref="fixedTreeRef" :treeApiObj="treeApiObj" @onTreeClick="onTreeClick" @onDeleteTree="onDeleteTree">

		<template #rightContainer v-if="Object.keys(currTreeNodeInfo).length > 0" >
		
			<baseInfo
			:currTreeNodeInfo="currTreeNodeInfo"
			:getTreeInstance="getTreeInstance"
			:handAssginNode="handAssginNode">
			</baseInfo>
			
			<template  v-if="currTreeNodeInfo.orgType=='Position'">
				<personList :currTreeNodeInfo="currTreeNodeInfo" :handAssginNode="handAssginNode"></personList>
				<!-- <roleRelation :currTreeNodeInfo="currTreeNodeInfo"></roleRelation> -->
			</template>
			
			<positionRelation v-else :currTreeNodeInfo="currTreeNodeInfo" :handAssginNode="handAssginNode"></positionRelation>
			
			<!-- <Log :currTreeNodeInfo="currTreeNodeInfo"></Log> -->

			
			<template v-if="currTreeNodeInfo.orgType=='Department'">
				<setLeaderList :currTreeNodeInfo="currTreeNodeInfo"></setLeaderList>
				<setManagerList :currTreeNodeInfo="currTreeNodeInfo"></setManagerList>
			</template>
			
		</template>
	
	</fixedTreeModule>
	<el-button style="display: none;"
    v-loading.fullscreen.lock="loading"></el-button>
</template>

<script lang="ts" setup>
	import { useI18n } from "vue-i18n"
	import orgForm from '../org/comps/baseInfoForm/orgForm.vue'
	import baseInfo from './comps/baseInfo.vue';
	import positionRelation from './comps/positionRelation.vue';
	import roleRelation from './comps/roleRelation.vue';
	import personList from './comps/personList.vue';
	import Log from '../org/comps/log.vue';
	import {
		treeInterface, 
		getTreeItemById,
		searchByName,
		removeOrg,
	} from '@/api/org/index';
	import {checkDeptManager} from '@/api/deptManager/index';
	import {removeDept} from '@/api/dept/index';
	import {removePosition} from '@/api/position/index';
	import setManagerList from '../org/comps/setManagerList.vue';
	import setLeaderList from '../org/comps/setLeaderList.vue';
	const { t } = useI18n();
	//数据
	const data = reactive({
		fixedTreeRef:"",//tree实例
		loading: false, // 全局loading
		treeApiObj:{//tree接口对象
			topLevel:treeInterface,
			childLevel:{
				api:getTreeItemById,
				params:{treeType:'tree_type_position',disabled:false}
			},
			search:{
				api:searchByName,
				params:{
					treeType:"tree_type_org_position"
				}
			}

		},
		currTreeNodeInfo:{},//当前tree节点的信息
		
	})
	
	const {
		fixedTreeRef,
		treeApiObj,
		currTreeNodeInfo,
		loading
	} = toRefs(data);


	//点击tree的回调
	async function onTreeClick(currTreeNode){
		const isGlobalManager = JSON.parse(sessionStorage.getItem('ssoUserInfo')).globalManager;
		let isDeptManager = true;
		if(currTreeNode.orgType === "Department"){
			const result = await checkDeptManager(currTreeNode.id);
			isDeptManager = result.data;
		}else if(currTreeNode.orgType === "Organization"){
			isDeptManager = isGlobalManager
		}
		currTreeNodeInfo.value = currTreeNode;
		currTreeNodeInfo.value.haveEditAuth = isDeptManager;//是否有编辑权限
	}
	

	
	
	function onDeleteTree(data){
		ElMessageBox.confirm(
			`${t('是否删除')}【${data.name}】?`,
			t('提示'), {
			confirmButtonText: t('确定'),
			cancelButtonText: t('取消'),
			type: 'info',
		}).then(async () => {
			loading.value = true;
			let result = {success:false,msg:''};
			if (data.orgType == 'Organization') {
				result = await removeOrg(data.id)
			} else if (data.orgType == 'Department') {
				result = await removeDept(data.id)
			} else if (data.orgType == 'Position') {
				result = await removePosition([data.id].toString())
			}
			loading.value = false;
			ElNotification({
				title: result.success ? t('成功') : t('失败'),
				message: result.msg,
				type: result.success ? 'success' : 'error',
				duration: 2000,
				offset: 80
			});
			
			if(result.success){
				
				//1.删除后，需要手动点击的节点信息，如果有父节点则默认点击父节点，没有则点击tree数据的第一个节点
				const treeData = getTreeData();//获取tree数据
				let handClickNode = null;
				if(data.parentId){
					handClickNode = findNode(treeData,data.parentId);//找到父节点的信息
				}else if(treeData.length > 0){
					handClickNode = treeData[0]
				}
				if(handClickNode){
					fixedTreeRef.value.handClickNode(handClickNode,false)//手动设置点击当前节点
				}
				
				//2.删除此节点
				getTreeInstance().remove(data);
			}
		}).catch(() => {
			ElMessage({
				type: 'info',
				message: t('已取消删除'),
				offset: 65
			});
		});
	}

	 
	//获取tree数据
	function getTreeData(){
		return fixedTreeRef.value.getTreeData();
	}
	
	//获取树的实例
	function getTreeInstance(){
		return fixedTreeRef.value.y9TreeRef
	}
	
	//请求某个节点，返回格式化好的数据
	function postNode(node){
		return new Promise((resolve,reject) => {
			fixedTreeRef.value.onTreeLazyLoad(node,data => {
				resolve(data)
			})
		})
	}
	
	//在树数据中根据id找到对应的节点并返回
	function findNode(treeData,targetId){
		return fixedTreeRef.value.findNode(treeData,targetId);
	}
	
	/**手动更新节点信息
	 * @param {Object} obj 需要合并的字段
	 * @param {String} targetId 需要更新的节点id
	 * @param {String} postChildId 请求的子节点id，如果存在该字段就请求子节点
	 */
	async function handAssginNode(obj,targetId,postChildId){
		
		if(postChildId){
			const childData = await postNode({id:postChildId})//重新请求当前节点的子节点，获取格式化后的子节点信息
			obj.children = childData
		}
		
		//1.更新当前节点的信息
		const currNode = findNode(getTreeData(),targetId);//找到树节点对应的节点信息
		Object.assign(currNode,obj)//合并节点信息
		
		//2.手动设置点击当前节点
		fixedTreeRef.value.handClickNode(currNode)//手动设置点击当前节点
	}

</script>


<style lang="scss" scoped>
</style>