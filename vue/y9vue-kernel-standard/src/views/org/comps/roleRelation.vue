<template>
	<y9Card :title="`${$t('角色关联')}${currInfo.name ? ' - ' + currInfo.name : ''}`">

		<div class="margin-bottom-20">
			<el-button type="primary" class="global-btn-main" 
			:size="fontSizeObj.buttonSize" 
			:style="{ fontSize: fontSizeObj.baseFontSize }"
			@click="onAddRelationRole">
				<i class="ri-add-line"></i>
				<span>{{ $t('增加关联角色') }}</span>
			</el-button>
		</div>

		<y9Table :config="roleListTableConfig"></y9Table>

	</y9Card>

	<y9Dialog v-model:config="dialogConfig">

	</y9Dialog>
</template> 

<script lang="ts" setup>
import { h, inject, ref, watch } from "vue";
import { useI18n } from "vue-i18n"
import { $deepAssignObject, } from '@/utils/object'
const { t } = useI18n();
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo');

const props = defineProps({

	currTreeNodeInfo: {//当前tree节点信息
		type: Object,
		default: () => { return {} }
	},

})

const data = reactive({

	//当前节点信息
	currInfo: props.currTreeNodeInfo,
	//表格配置
	roleListTableConfig: {
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
			{
				title: computed(() => t("操作")),
				fixed: "right",
				render: (row) => {
					return h(
						'span',
						{
							class: "flex-center",
							onClick: () => {
								ElMessageBox.confirm(
									`${t('是否解除关联角色')}【${row.name}】?`,
									t('提示'), {
									confirmButtonText: t('确定'),
									cancelButtonText: t('取消'),
									type: 'info',
								}).then(async () => {

									roleListTableConfig.value.tableData.forEach((item, index) => {
										if (item.id == row.id) {
											roleListTableConfig.value.tableData.splice(index, 1);

											ElNotification({
												title: '成功',
												message: '解除关联角色成功',
												type: 'success',
												duration: 2000,
												offset: 80
											});
										}
									})

								}).catch(() => {
									ElMessage({
										type: 'info',
										message: t('已取消解除关联角色'),
										offset: 65
									});
								});

							}
						},
						[
							h('i', {
								class: 'ri-delete-bin-line',
								style: {
									marginRight: '4px'
								}
							}),
							h('span', t('解除关联角色')),

						],
					);
				}
			}
		],
		tableData: [
			{
				id: "1",
				name: "管理员",
				path: "cn=运营岗,cn=财务管理,cn=即时数据,cn=系统角色列表",
				describe: "111",
			},
			{
				id: "2",
				name: "系统人员",
				path: "cn=技术岗,cn=财务管理,cn=即时数据,cn=系统角色列表",
				describe: "222",
			},
		],
	},
	//弹窗配置
	dialogConfig: {
		show: false,
		title: "",
		onOkLoading: true,
		onOk: (newConfig) => {

			return new Promise((resolve, reject) => {
				ElNotification({
					title: t('成功'),
					message: t('保存成功'),
					type: 'success',
					duration: 2000,
					offset: 80
				});
				resolve()
			})
		},
		visibleChange: (visible) => {
		}
	},
})

let {
	currInfo,
	roleListTableConfig,
	dialogConfig,
} = toRefs(data);

watch(
	() => props.currTreeNodeInfo,
	(newVal) => {

		currInfo.value = $deepAssignObject(currInfo.value, newVal);

	},
	{
		deep: true,
	}
)

//增加关联角色
function onAddRelationRole() {
	Object.assign(dialogConfig.value, {
		show: true,
		title: computed(() => t("增加关联角色")),
	})
}



</script>

<style lang="scss" scoped>
</style>