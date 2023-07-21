<template>
	<y9Card :title="`${$t('查看所有角色')}${currInfo.name ? ' - ' + currInfo.name : ''}`">
		<y9Table :config="roleListTableConfig"></y9Table>
		
	</y9Card>
</template>

<script lang="ts" setup>
	import { $deepAssignObject, } from '@/utils/object'
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
		roleListTableConfig: {//查看所有角色表格配置
			columns: [
				{
					title: computed(() => t("名称")),
					key: "name",
				},
				{
					title: computed(() => t("路径")),
					key: "path",
				},
				{
					title: computed(() => t("描述")),
					key: "describe",
				},
			],
			tableData: [
				{
					id: "1",
					name: "出纳",
					path: "cn=出纳,cn=财务管理,cn=即时数据,cn=系统角色列表",
					describe: "",
				},
				{
					id: "2",
					name: "普通角色",
					path: "cn=普通角色,cn=财务管理,cn=即时数据,cn=系统角色列表",
					describe: "",
				},
			],
		},
	})
	
	let {
		currInfo,
		roleListTableConfig,
	} = toRefs(data);
	
	watch(
		() => props.currTreeNodeInfo,
		(newVal) => {
			
			currInfo.value = $deepAssignObject(currInfo.value, newVal);
			
		},
		{
			deep:true,
			immediate:true
		}
	)
	
</script>

<style lang="scss" scoped>
</style>