<template>
	<y9Card :title="`${$t('查看修改日志')}${currInfo.name ? ' - ' + currInfo.name : ''}`">

	
		<y9Table :config="logTableConfig"></y9Table>
		
	</y9Card>
</template> 

<script lang="ts" setup>
	import { $deepAssignObject,$dataType } from '@/utils/object'
	import { getShadowTitles, getShadowRows } from '@/api/org/index';
	import { useI18n } from "vue-i18n"
	const { t } = useI18n();
	const props = defineProps({
		
		currTreeNodeInfo: {//当前tree节点信息
			type: Object,
			default:() => { return {} }
		},
		
	})
	
	const data = reactive({
	
		//当前节点信息
		currInfo:props.currTreeNodeInfo,
		//表格配置
		logTableConfig: {
			columns: [
				{
					title: computed(() => t("操作信息")),
					key: "commitAuthor",
					width:300, 
					fixed:"left",
					
				},
				{
					title: "deptTypeName",
					key: "deptTypeName",
					minWidth:200, 
				},
				{
					title: "leader",
					key: "leader",
					minWidth:200, 
				},
				{
					title: "zipCode",
					key: "zipCode",
					minWidth:200, 
				},
				{
					title: "deptOffice",
					key: "deptOffice",
					minWidth:200, 
				},
				{
					title: "gradeCodeName",
					key: "gradeCodeName",
					minWidth:200, 
				},
				{
					title: "description",
					key: "description",
					minWidth:200, 
				},
				{
					title: "dn",
					key: "dn",
					minWidth:200, 
				},
				{
					title: "tabIndex",
					key: "tabIndex",
					minWidth:200, 
				},
				{
					title: "parentID",
					key: "parentID",
					minWidth:200, 
				},
				{
					title: "orgType",
					key: "orgType",
					minWidth:200, 
				},
				{
					title: "enName",
					key: "enName",
					minWidth:200, 
				},
				{
					title: "tenantID",
					key: "tenantID",
					minWidth:200, 
				},
				{
					title: "disabled",
					key: "disabled",
					minWidth:200, 
				},
				{
					title: "bureau",
					key: "bureau",
					minWidth:200, 
				},
				{
					title: "id",
					key: "id",
					minWidth:200, 
				},
				{
					title: "aliasName",
					key: "aliasName",
					minWidth:200, 
				},
				{
					title: "manager",
					key: "manager",
					minWidth:200, 
				},
				{
					title: "guidPath",
					key: "guidPath",
					minWidth:200, 
				},
				{
					title: "deptType",
					key: "deptType",
					minWidth:200, 
				},
				{
					title: "establishDate",
					key: "establishDate",
					minWidth:200, 
				},
				{
					title: "version",
					key: "version",
					minWidth:200, 
				},
				{
					title: "customID",
					key: "customID",
					minWidth:200, 
				},
				{
					title: "deptFax",
					key: "deptFax",
					minWidth:200, 
				},
				{
					title: "shortDN",
					key: "shortDN",
					minWidth:200, 
				},
				{
					title: "divisionCode",
					key: "divisionCode",
					minWidth:200, 
				},
				{
					title: "deleted",
					key: "deleted",
					minWidth:200, 
				},
				{
					title: "createTime",
					key: "createTime",
					minWidth:200, 
				},
				{
					title: "deptAddress",
					key: "deptAddress",
					minWidth:200, 
				},
				{
					title: "name",
					key: "name",
					minWidth:200, 
				},
				{
					title: "gradeCode",
					key: "gradeCode",
					minWidth:200, 
				},
				{
					title: "properties",
					key: "properties",
					minWidth:200, 
				},
				{
					title: "deptGivenName",
					key: "deptGivenName",
					minWidth:200, 
				},
				{
					title: "deptPhone",
					key: "deptPhone",
					minWidth:200, 
				},
				
			],
			tableData: [
				// {
				// 	commitAuthor: "修改时间：2022-02-21 11:47:42  修改人员：有生系统管理员(IP:119.123.243.61)",
				// 	createTime: "2018-11-10T04:47:28.667",
				// 	customID: "",
				// 	deleted: false,
				// 	description: "",
				// 	disabled: false,
				// 	dn: "cn=总经理（CEO）,ou=总经理办公室,o=数字底座",
				// 	duty: "旗长",
				// 	dutyLevel: 0,
				// 	dutyLevelName: "副科级",
				// 	dutyType: "领导",
				// 	guidPath: "4868e899c5034a87a49b5cd9d345de96,9882d4b30031406280a7740de359c686,7188d7d0f3f84204ac30ae1f9fcaae14",
				// 	id: "7188d7d0f3f84204ac30ae1f9fcaae14",
				// 	name: "总经理（CEO）",
				// 	orderedPath: "00001,00000,00017",
				// 	orgType: "Position",
				// 	parentID: "9882d4b30031406280a7740de359c686",
				// 	properties: "{}",
				// 	shortDN: "cn=总经理（CEO）",
				// 	tabIndex: 17,
				// }
			],
			pageConfig:false,//取消分页
		},
	})
	
	let {
		currInfo,
		logTableConfig,
	} = toRefs(data);
	
	watch(
		() => props.currTreeNodeInfo,
		(newVal) => {
			
			currInfo.value = $deepAssignObject(currInfo.value, newVal);
			
		},
		{
			deep:true,
		}
	)
	
	async function getLogList(){
	  let entityClass = '';
	  if(currInfo.value.orgType != undefined){
	    switch (currInfo.value.orgType) {
	      case "Organization":
	        entityClass = "net.risesoft.entity.Y9Organization";
	        break;
	      case "Department":
	        entityClass = "net.risesoft.entity.Y9Department";
	        break;
	      case "Group":
	        entityClass = "net.risesoft.entity.Y9Group";
	        break;
	      case "Position":
	        entityClass = "net.risesoft.entity.Y9Position";
	        break;
	      case "Person":
	        entityClass = "net.risesoft.entity.Y9Person";
	        break;
	      case "Person":
	        entityClass = "net.risesoft.y9public.entity.role.Y9Role";
	        break;
	    }
	
		const result = await getShadowTitles(currInfo.value.id,entityClass)
		

		
	  //   getShadowTitles(props.id,entityClass.value).then((res) => {
	  //       columnList.value = res;
	  //       getShadowRows(props.id,entityClass.value).then((res) => {
	  //         tableData.value = res.rows;
	  //       });
	  //   });
	  }
	}
	onMounted(()=>{
	  // getLogList();
	});
	
	
	
</script>

<style lang="scss" scoped>
</style>