
<template>  

 <y9Table :config="tableConfig" :filterConfig="filterConfig" @on-curr-page-change="onCurrPageChange" @on-page-size-change="onPageSizeChange">
		 <template #addIcon>
			<el-button
			class="global-btn-third"
			:size="fontSizeObj.buttonSize" 
			:style="{ fontSize: fontSizeObj.baseFontSize }"
			@click="onEditIcon('add','新增图标')">
			  	<i class="ri-add-line"></i>
				<span>{{ $t('图标') }}</span>
			</el-button>
		 </template>
  </y9Table>

  <y9Dialog v-model:config="dialogConfig">
	  <y9Form ref="y9FormRef" :config="formConfig"></y9Form>
  </y9Dialog>

  <el-button style="display: none;"
    v-loading.fullscreen.lock="loading"></el-button>
  
</template>

<script lang='ts' setup>
	import { useI18n } from "vue-i18n"
	import { $keyNameAssign, $deeploneObject, $objEqual, $tableHandleRender } from '@/utils/object.ts'
	import { getAppIconPageList,searchIconPageByName,deleteIcon,uploadIcon,saveIcon } from '@/api/appIcon/index'
	import { useSettingStore } from "@/store/modules/settingStore";
	import { inject, reactive, ref, watch } from "vue";
	const settingStore = useSettingStore();
	// 注入 字体对象
	const fontSizeObj: any = inject('sizeObjInfo');
	const { t } = useI18n();
	const data = reactive({
		currFilters:{},//当前选择的过滤数据
		loading: false, // 全局loading
		tableConfig: {//表格配置
			border:false,
			headerBackground:true,
			columns: [
				{
					type:"index",
					title: computed(() => t("序号")),
					width:122,
					fixed:'left',
				},
				{
					title: computed(() => t("图标")),
					showOverflowTooltip:false,
					imgConfig:{
						type:'base64',
						popWidth:settingStore.device === 'mobile'?150:200,
					},
					key:"iconData",
				},
				{
					title: computed(() => t("图标名称")),
					key:"name",
				},
				{
					title: computed(() => t("图标类型")),
					key:"type",
				},
				{
					title: computed(() => t("图标路径")),
					key:"path",
				},
				{
					title: computed(() => t("添加时间")),
					key:"createTime",
					width: settingStore.getDatetimeSpan
				},
				{
					title: computed(() => t("更新时间")),
					key:"updateTime",
					width: settingStore.getDatetimeSpan
				},
				{
					title: computed(() => t("备注")),
					key:"remark",
				},
				{
					title: computed(() => t("操作")),
					with:200,
					fixed:"right",
					render: (row,params) => {
						
						return $tableHandleRender([
							{
								title: t("编辑"),
								remixicon:'ri-edit-line',
								onClick: () => {
									onEditIcon('edit','编辑图标',row)
									
								},
							},
							{
								title:t("删除"),
								remixicon:"ri-delete-bin-line",
								onClick: () => {
									ElMessageBox.confirm(
										`${t('是否删除')}【${row.name}】 ?`,
										t('提示'), {
										confirmButtonText: t('确定'),
										cancelButtonText: t('取消'),
										type: 'info',
										// loading: true,
									}).then(async () => {
										
											// let loading = ElLoading.service({
											// 				lock: true,
											// 				background: 'rgba(0, 0, 0, 0.5)',
											// 			});
											loading.value = true;
											const result = await deleteIcon(row.id);
											
											if(result.success){
												getIconList();//获取icon列表
											}
											
											ElNotification({
												title: result.success ? t('删除成功') : t('删除失败'),
												message: result.msg,
												type: result.success ? 'success' : 'error',
												duration: 2000,
												offset: 80
											});
										
											// loading.close()
											loading.value = false;
										
										
									}).catch(() => {
										ElMessage({
											type: 'info',
											message: t('已取消删除'),
											offset: 65
										});
									});
									
								},
							}
						])
						
					
					}
				}
				
			],
			tableData:[],
		
			pageConfig:{
				currentPage: 1,//当前页数，支持 v-model 双向绑定
				pageSize: 10,//每页显示条目个数，支持 v-model 双向绑定
			},
			
		},
		filterConfig:{//过滤配置
			showBorder: true,
			itemList:[
				{
					type:"slot",
					span:settingStore.device === 'mobile'?8:18,
					slotName:"addIcon",
				},
				{
					type:"input",
					key:"name",
					span:settingStore.device === 'mobile'?16:6,
				},
				
			],
			filtersValueCallBack:(filters) => {//过滤值回调
				
				currFilters.value = filters
				
				
			},
			
			
		},
		dialogConfig:{//弹窗配置
			show: false,
			width:"50%",
			title: "",
			resetText: computed(() => t("重置")),
			onOk: (newConfig) => {
				
				return new Promise((resolve, reject) => {
				
					getY9FormRef().validate(async valid => {
						
						if(valid){
							let res = {};					
							if(dialogConfig.value.type == 'edit'){
								await saveIcon(y9FormRef.value.model).then(result => {
									res = result;
								}).catch(() => {})
							}else {
								await uploadIcon(y9FormRef.value.model).then(result => {
									res = result;
								}).catch(() => {})
							}
							ElNotification({
								title:res.success?t('成功'):t('失败'),
								message: res.msg,
								type: res.success?'success':'error',
								duration: 2000,
								offset: 80
							});
							if(res.success){
								getIconList();//获取icon列表
								resolve();
							}else {
								reject();
							}
						
						}else {
							ElMessage({
								type: 'error',
								message: t('请上传图标'),
								offset: 65
							});
							reject()
						}
					})
					
				})
			},
			onReset: (newConfig) => {
		
				getY9FormRef().resetFields()//重置表单
				
				if(newConfig.value.type == 'add' && y9FormRef.value.model['iconFile'].length == 0){
					y9FormRef.value.elUploadRef[0].clearFiles()//清空图片
				}
				
				
			},
			visibleChange:(visible) => {
				if(!visible){//关闭弹窗时，初始化表单
					
					for(let key in formConfig.value.model){
						
						if(key == 'iconFile'){
							formConfig.value.model[key] = []
						}else {
							formConfig.value.model[key] = ""
						}
						
					}
					
				}
				
			}
		},
		formConfig:{//表单配置
			model:{
				id:"",
				iconFile:[],
				name:"",
				remark:"",
			},
			rules:{//	表单验证规则。类型：FormRules
				iconFile:[
					{ required: true, message: computed(() => t("请选择图标")), trigger: 'change' }
				],
				name:[
					{ required: true, message: computed(() => t("请输入图标名称")), trigger: 'blur' }
				],
			}, 
			itemList:[
				{
					type:"upload",
					label: computed(() => t("选择图标")),
					prop:"iconFile",
					props:{
						defaultCustomClass:settingStore.device === 'mobile'?'custom-picture':'custom-picture-card',
						listType:'picture-card',
						limit:1,
						drag:true,
						accept:"image/png",
						fileList:[],
					}
				},
				
				{
					type:"textarea",
					label: computed(() => t("备注")),
					prop:"remark",
				},
			],
			descriptionsFormConfig: {
				labelWidth: '200px',
				labelAlign: 'center'
			},
		},
		y9FormRef:"",
	})
	
	let {
		loading,
		currFilters,
		tableConfig,
		filterConfig,
		dialogConfig,
		formConfig,
		y9FormRef,
	} = toRefs(data);
	
	//监听过滤条件改变时，获取icon列表
	watch(
		() => currFilters.value,
		(newVal) => {
		
			if(newVal.name){
				getIconList(true);//获取icon列表
			}else {
				getIconList();//获取icon列表
			}
			
		},
		{
			deep:true,
			immediate:true,
		}
		
	)
	
	
	function getY9FormRef(){
		return y9FormRef.value.elFormRef
	}
	//获取icon列表
	async function getIconList(isSearch = false){
		
		tableConfig.value.loading = true;
		
		let sendData = {
			page:tableConfig.value.pageConfig.currentPage,
			rows:tableConfig.value.pageConfig.pageSize,
			name:currFilters.value.name,
		}
		
		let result = {}
		
		if(isSearch){
			result = await searchIconPageByName(sendData);
		}else {
			result = await getAppIconPageList(sendData);
		}
		 
		if(result.success){
			
			tableConfig.value.tableData = result.rows;
			
			tableConfig.value.pageConfig.total = result.total;
			
		}

		tableConfig.value.loading = false;
		
	}
	
	
	
	//当前页改变时触发
	function onCurrPageChange(currPage){
		tableConfig.value.pageConfig.currentPage = currPage;
		getIconList();//获取icon列表
	}
	//每页条数改变时触发
	function onPageSizeChange(pageSize){
		tableConfig.value.pageConfig.pageSize = pageSize;
		getIconList();//获取icon列表
	}
	
	//点击新增、编辑icon，出现弹窗
	function onEditIcon(type,title,row?){
		
		for(let i in formConfig.value.itemList){
			
			let item = formConfig.value.itemList[i];

			if(type == 'edit' && item.prop == 'iconFile'){
				
				if(formConfig.value.itemList[1].prop !== 'name'){
					formConfig.value.itemList.splice(i+1,0,{
						type:"input",
						label: computed(() => t("图标名称")),
						prop:"name",
					})
				}
			
				break;
			
			}else if(type == 'add' && item.prop == 'name'){
				
				formConfig.value.itemList.splice(i,1);
				
				break;
			}
			
		}
		
		formConfig.value.itemList.forEach(item => {
			
			if(item.prop == 'iconFile'){
				
				if(row !== undefined){//编辑状态时，添加默认上传文件
				
					item.props.fileList = [{
						
						name:row.name,
						
						url:'data:image/png;base64,'+row.iconData,
						
					}];
					
					item.props.removeIcon = false;
					
					item.props.replaceIcon = false;
					
				}else {//添加状态时，清空默认上传文件
				
					item.props.fileList = [];
					
					item.props.removeIcon = true;
					
					item.props.replaceIcon = true;
					
				}
				
			}
			
		});
		
		if(row !== undefined && type == 'edit'){//编辑状态，给表单赋值
	
			$keyNameAssign(formConfig.value.model,row,{
				
				replace:{
					
					iconFile:"iconData",
					
				},
				
			}) 
			
		}
		
		//显示弹窗
		Object.assign(dialogConfig.value,{
			show:true,
			title:t(`${title}`),
			type:type
		})
		
		
	}
	
	
</script>
<style scoped lang="scss">
	:deep(.custom-picture-card){
		.el-upload-list__item{
			width: 360px;
			height: 260px;
			display: flex;
			align-items: center;
			justify-content: center;
		}
	}

</style>