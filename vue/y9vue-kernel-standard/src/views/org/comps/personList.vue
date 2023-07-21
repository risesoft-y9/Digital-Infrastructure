<template>
	<y9Card :title="`${$t('人员列表')}${currInfo.name ? ' - ' + currInfo.name : ''}`">
		<div class="margin-bottom-20" style="display: flex;justify-content: space-between;"
		 v-show="currInfo.haveEditAuth">
			<div>
				<el-button
				v-if="currTreeNodeInfo.orgType == 'Group'"
				@click="addPerson"
				:size="fontSizeObj.buttonSize" 
				:style="{ fontSize: fontSizeObj.baseFontSize }"
				type="primary"
				class="global-btn-main">
					<i class="ri-add-line"></i>
					<span>{{ $t('选择已有人员') }}</span>
				</el-button>
				<el-button
				type="primary" @click="savePersonOrder"
				:size="fontSizeObj.buttonSize" 
				:style="{ fontSize: fontSizeObj.baseFontSize }"
				class="global-btn-main">
					<i class="ri-save-line"></i>
					<span>{{ $t('保存') }}</span>
				</el-button>
			</div>
			<div>
				<el-button @click="upPerson"
				:size="fontSizeObj.buttonSize" 
				:style="{ fontSize: fontSizeObj.baseFontSize }"
				class="global-btn-second">
					<i class="ri-arrow-up-line"></i>
					<span>{{ $t('上移') }}</span>
				</el-button>
				<el-button @click="downPerson"
				:size="fontSizeObj.buttonSize" 
				:style="{ fontSize: fontSizeObj.baseFontSize }"
				class="global-btn-second">
					<i class="ri-arrow-down-line"></i>
					<span>{{ $t('下移') }}</span>
				</el-button>
			</div>
			
		</div>
		<y9Table ref="personTableRef" v-model:selectedVal="userSelectedData" :config="personListTableConfig"  @on-current-change="onCurrentChange"></y9Table>
	</y9Card>
	<y9Dialog v-model:config="dialogConfig">
		<selectTree 
		ref="selectTreeRef" 
		:treeApiObj="treeApiObj" 
		:selectField="[{fieldName: 'orgType',value: ['Person']},{fieldName: 'disabled',value: false}]"
		@onNodeExpand="onNodeExpand">
		</selectTree>
	</y9Dialog>
	<el-button style="display: none;"
    v-loading.fullscreen.lock="loading"></el-button>
</template>

<script lang="ts" setup>
	import { useI18n } from "vue-i18n"
	import { $deepAssignObject, } from '@/utils/object'
	import {getPersonsByParentId,getPersonsByGroupID,delPerson,saveOrder,savePersons} from '@/api/person/index';
	import { treeInterface,getTreeItemById, searchByName, } from '@/api/org/index';
	import { addPersons,removePersons,orderPersons } from '@/api/group/index';
	import { inject, ref, watch } from "vue";
	const { t } = useI18n();
	// 注入 字体对象
	const fontSizeObj: any = inject('sizeObjInfo');
	const props = defineProps({
		
		currTreeNodeInfo: {//当前tree节点信息
			type: Object,
			default:() => { return {} }
		},
		
		updateTreePersonCount: Function,//更新当前节点以及其父节点的人员数量
		
		handAssginNode: Function,//手动更新节点信息
		
	})
	
	//选择tree实例
	const selectTreeRef = ref()
	
	//移动-选择树节点展开时触发
	const selectTreeExpandNode = ref()
	function onNodeExpand(node){
		selectTreeExpandNode.value = node;
	}
	
	const data = reactive({
		treeApiObj:{//tree接口对象
			topLevel:treeInterface,
			childLevel:{
				api: async () => {
					const res = await getTreeItemById({
						parentId:selectTreeExpandNode.value.id,
						treeType: 'tree_type_person', 
						disabled: false
					})
					
					const data = res.data || []
					
					const ids = personListTableConfig.value.tableData.map(item => item.id);
					
					//禁止选择已经存在的人员
					data.forEach(item => {
						if(ids.includes(item.id)){
							item.disabled = true;
							item.disabledRemark = '[已存在]';
						}
					})
					
					return data
					
				},
				params:{treeType:'tree_type_person',disabled:false}
			},
			search:{
				api:searchByName,
				params:{
					treeType:"ree_type_org"
				}
			}
		
		},
		loading: false, // 全局loading
		userSelectedData:"",//选中的人员数据
		//当前节点信息
		currInfo:props.currTreeNodeInfo,
		personListTableConfig: {//人员列表表格配置
			columns: [
				{
					type:"radio",
					width:80,
				},
				{
					title: computed(() => t("姓名")),
					key: "name",
					width: 200,
				},
				{
					title: computed(() => t("性别")),
					key: "sex",
					width: 120,
					render: (row) => {
						let text = row.sex;
						if(row.sex == 1){
							text= '男'
						}else if(row.sex == 0){
							text= '女'
						}
						
						return h('div',t(text))
					}
				},
				{
					title: computed(() => t("职务")),
					key: "duty",
					width: 300
				},
				{
					title: computed(() => t("所属部门")),
					key: "dn",
				},
				{
					title: computed(() => t("操作")),
					width: 100,
					render: (row) => {
						return h('i', {
							class: 'ri-delete-bin-line',
							onClick: () => {
								ElMessageBox.confirm(
									`${t('是否删除')}【${row.name}】?`,
									t('提示'), {
									confirmButtonText: t('确定'),
									cancelButtonText: t('取消'),
									type: 'info',
								}).then(async () => {
									loading.value = true;
									let result = {success:false,msg:''};
									if(currInfo.value.orgType === 'Group'){
										result = await removePersons(currInfo.value.id,[row.id].toString());
									}else if(currInfo.value.orgType === 'Organization' || currInfo.value.orgType === 'Department'){
										result = await delPerson([row.id].toString());
									}
									loading.value = false;
									if(result.success){
										personListTableConfig.value.tableData.forEach((item, index) => {
											if (item.id == row.id) {
												personListTableConfig.value.tableData.splice(index, 1);
											}
										})
										
										//手动更新tree的人员计数并更新子节点
										props.updateTreePersonCount(currInfo.value,-1,currInfo.value.id)
									
										
									}
									ElNotification({
										title: result.success ? t('成功') : t('失败'),
										message: result.msg,
										type: result.success ? 'success' : 'error',
										duration: 2000,
										offset: 80
									});
								}).catch(() => {
									ElMessage({
										type: 'info',
										message: t('已取消删除'),
										offset: 65
									});
								});
		
							},
						})
					}
				},
			],
			tableData: [],
			pageConfig:false,//取消分页
		
		},
		currentRow:"",
		//弹窗配置
		dialogConfig: {
			show: false,
			title: "",
			onOkLoading: true,
			onOk: (newConfig) => {
				
				return new Promise(async (resolve, reject) => {
					let result = {success:false,msg:''};
					const selectData = selectTreeRef.value?.y9TreeRef?.getCheckedNodes(true)
					let orgBaseIds = selectData.map(item => item.id);
					
					if(orgBaseIds.length == 0){
						ElNotification({title: t('失败'),message: t('请选择人员'),type: 'error',duration: 2000,offset: 80});
						reject();
						return;
					}
					
					if(currInfo.value.orgType === 'Group'){
						await addPersons(props.currTreeNodeInfo.id,orgBaseIds.toString()).then(res => result = res).catch(() => {});
					}else if(currInfo.value.orgType === 'Organization' || currInfo.value.orgType === 'Department'){
						await savePersons(props.currTreeNodeInfo.id,orgBaseIds.toString()).then(res => result = res).catch(() => {});
					}
					
					
					if(result.success){
						personListTableConfig.value.tableData = personListTableConfig.value.tableData.concat(selectData);
						
					}
					
					ElNotification({
						title: result.success ? t('成功') : t('失败'),
						message: result.msg,
						type: result.success ? 'success' : 'error',
						duration: 2000,
						offset: 80
					});
					if(result.success) {
						resolve();
					 }else {
						reject();
					 }
				})
			},
		},
		tabIndexs:[],
		personTableRef:"",
	})
	
	let {
		treeApiObj,
		currInfo,
		personListTableConfig,
		currentRow,
		userSelectedData,
		dialogConfig,
		tabIndexs,
		loading,
		personTableRef,
	} = toRefs(data);
	
	watch(
		() => props.currTreeNodeInfo,
		(newVal) => {

			currInfo.value = $deepAssignObject(currInfo.value, newVal);
			personTableRef.value?.elTableRef?.setCurrentRow();//取消选中状态。
			getPersonsList();
		},
		{
			deep:true,
		}
	)

	onMounted(()=>{
		getPersonsList();
	});

	defineExpose({
		getPersonsList
	})

	async function getPersonsList() {
		let result;
		if(currInfo.value.orgType === 'Group'){
			result = await getPersonsByGroupID(currInfo.value.id);
		}else if(currInfo.value.orgType === 'Organization' || currInfo.value.orgType === 'Department'){
			result = await getPersonsByParentId(currInfo.value.id);
		}
		
		if(result.success){
			tabIndexs.value = [];
			result.data.forEach(element => {
				if(element.sex == 1){
					element.sex = '男';
				}else if(element.sex == 0){
					element.sex = '女';
				}
				// dn 只显示最后一个
				let temp_1 = element.dn.split(',')
				let temp_2 = temp_1[temp_1.length-1].split('=')
				element.dn = temp_2[temp_2.length-1]
				tabIndexs.value.push(element.tabIndex);
			});
			personListTableConfig.value.tableData = result.data;
		}
		return personListTableConfig.value.tableData.length;
	}
	
	
	
	//添加人员
	function addPerson(){
		Object.assign(dialogConfig.value,{
			show:true,
			title: computed(() => t("添加人员")),
			width:'30%',
		})
		
	}

	function onCurrentChange(data){
		currentRow.value = data;
		if(data){
			userSelectedData.value= data.id;
		}else{
			userSelectedData.value = '';
		}
	}

	async function upPerson(){
		if(currentRow.value == '' || currentRow.value == null){
			ElNotification({title: t('失败'),message: t('请选择人员'),type: 'error',duration: 2000,offset: 80});
			return;
		}
		let tableData = personListTableConfig.value.tableData;
		tableData.forEach(function(element,index){
			if(index == 0 && element.id == currentRow.value.id){
				ElNotification({title: t('失败'),message: t('处于顶端，不能继续上移'),type: 'error',duration: 2000,offset: 80});
				return;
			}
			if(element.id == currentRow.value.id){
				let obj = tableData[index-1] ;
				tableData[index-1] = currentRow.value;
				tableData[index] = obj;
				personListTableConfig.value.tableData = tableData;
				return;
			}
		});
	}

	async function downPerson(){
		if(currentRow.value == '' || currentRow.value == null){
			ElNotification({title: t('失败'),message: t('请选择人员'),type: 'error',duration: 2000,offset: 80});
			return;
		}
		let tableData = personListTableConfig.value.tableData;
		for(let i = 0; i < tableData.length; i++){
			if((tableData.length - 1) == i && tableData[i].id == currentRow.value.id){
				ElNotification({title: t('失败'),message: t('处于末端，不能继续下移'),type: 'error',duration: 2000,offset: 80});
				return;
			}
			if(tableData[i].id == currentRow.value.id){
				let obj = tableData[i+1] ;
				tableData[i] = obj;
				tableData[i+1] = currentRow.value;
				personListTableConfig.value.tableData = tableData;
				break;
			}
		}
	}
	
	async function savePersonOrder() {
		const ids = [];
		let tableData = personListTableConfig.value.tableData;
		tableData.forEach(element => {
			ids.push(element.id);
		});
		loading.value = true;
		
		let result = {success:false,msg:''};
		if(currInfo.value.orgType === 'Group'){
			result = await orderPersons(props.currTreeNodeInfo.id,ids.toString());
		}else if(currInfo.value.orgType === 'Organization' || currInfo.value.orgType === 'Department'){
			result = await saveOrder(ids.toString(),tabIndexs.value.toString())
			props.handAssginNode({},currInfo.value.id,currInfo.value.id);//手动更新当前节点的子节点
		}
		
		loading.value = false;
		ElNotification({
			title: result.success ? t('成功') : t('失败'),
			message: result.msg,
			type: result.success ? 'success' : 'error',
			duration: 2000,
			offset: 80
		});
	}
	
	
	
</script>

<style lang="scss" scoped>

</style>