<!--
 * 
-->
<template>
    <div>
        <!-- 表格 -->
        <y9Table :config="tableRoleConfig" :filterConfig="filterConfig">
            <template v-slot:filterBtnSlot>
                <el-button :size="fontSizeObj.buttonSize"  :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-main" @click="initList" type="primary">
                    <i class="ri-search-line"></i>
                    {{ $t('搜索') }}
                </el-button>
                <el-button  :size="fontSizeObj.buttonSize"  :style="{ fontSize: fontSizeObj.baseFontSize }"
                @click="handlerRoleAdd('app')" class="global-btn-second">
                    <i class="ri-add-line" />
                    {{ $t('应用角色') }}
                </el-button>
                <el-button :size="fontSizeObj.buttonSize"  :style="{ fontSize: fontSizeObj.baseFontSize }"
                @click="handlerRoleAdd('public')" class="global-btn-second">
                    <i class="ri-add-line" />
                    {{ $t('公共角色') }}
                </el-button>
            </template>
        </y9Table>
        <!-- 授权 -->
        <y9Dialog v-model:config="positiveAuthorityDialog">
            <y9Filter :itemList="filtersList" :filtersValueCallBack="filtersValueCallBack" :showBorder="true"></y9Filter>
            <!-- tree树 -->
            <selectTree ref="selectTreeRef" :selectField="[{ fieldName: 'type', value: 'role' }]" @onTreeClick="handlerTreeClick" :treeApiObj="treeApiObj" :showHeader="false"></selectTree>
        </y9Dialog>
        <!-- 制造loading效果 -->
        <el-button style="display: none;" v-loading.fullscreen.lock="loading"></el-button>
    </div>
</template>

<script lang="ts" setup>
import { h, inject, ref, watch } from "vue";
import {
    getRelationRoleList, // 列表
    removeRole, // 移除
    saveOrUpdateRole, // 关联角色 信息 保存
} from "@/api/grantAuthorize/index";
import { getResourceTree, roleTreeList, getPublicRoleTree } from '@/api/role/index';
import { useI18n } from "vue-i18n";
import { useSettingStore } from "@/store/modules/settingStore";
const settingStore = useSettingStore();
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo');
const { t } = useI18n();
const props = defineProps({
    id: {
        type: String,
        default: ''
    },
    appId: {
        type: String,
        default: ''
    }
})

// 角色关联 表格总数
let roleTotal = ref(0);
// 当前tree点击 的数据
let currTreeData = ref({});
// 角色树类型：应用角色树(appRole)、公共角色树(publicRole)
let roleTreeType = ref('appRole');

//选择tree实例
const selectTreeRef = ref()
// 变量 对象
const state = reactive({
    // 区域 loading
    loading: false,
    // 列表 选择的 行数据
    selectIds: [],
    // 角色关联 表格 行内表单的 变量
    roleFormLine: {
        roleNodeName: '',
        operationType: 1,
    },
    // 角色关联 表格的 配置信息
    tableRoleConfig: {
        columns: [
            // { title: '', type: 'selection', fixed: 'left' },
            { title: computed(() => t("序号")), type: 'index', width: '100px', fixed: 'left' },
            { title: computed(() => t("角色名称")), key: 'roleName' },
            { title: computed(() => t("操作")), key: 'authorityStr', width: 100 },
            { title: computed(() => t("授权者")), key: 'authorizer' },
            { title: computed(() => t("授权时间")), key: 'authorizeTime', width: settingStore.getDatetimeSpan },
            {
                title: computed(() => t("操作")), fixed: 'right', width: '130px',
                render: (row) => {
                    return h('span', {
                        style: {
                            cursor: 'pointer',
                        },
                        class: 'global-btn-second',
                        onClick: async () => {
                            ElMessageBox.confirm(`${t('是否移除')}【${row.roleName}】?`, t('提示'), {
                                confirmButtonText: t('确定'),
                                cancelButtonText: t('取消'),
                                type: 'info',
                            }).then(async () => {
                                // 请求 移除 接口函数---
                                loading.value = true;
                                let result = await removeRole(row.id);
                                ElNotification({
                                    title: result.success ? t('成功') : t('失败'),
                                    message: result.success ? t('移除成功') : t('移除失败'),
                                    type: result.success ? 'success' : 'error',
                                    duration: 2000,
                                    offset: 80,
                                });
                                loading.value = false;
                                // 重新请求 列表数据
                                initList();
                            }).catch(() => {
                                ElMessage({
                                    type: 'info',
                                    message: t('已取消移除'),
                                    offset: 65,
                                });
                            });
                        }
                    }, t('移除'))
                }
            }
        ],
        tableData: [],
        pageConfig: {
            // 分页配置，
            currentPage: 1,
            pageSize: 10,
            total: roleTotal.value, // 总条数
        },
    },
    // 表格过滤
    filterConfig: {
        filtersValueCallBack: (filters) => {
            roleFormLine.value = filters;
        },
        showBorder: true,
        itemList: [
            {
                type: 'input',
                value: '',
                key: 'roleNodeName',
                label: computed(() => t("角色名称")),
                span: settingStore.device === 'mobile' ? 24 : 6,
                clearable: true,
            },
            {
                type: 'select',
                value: 1,
                key: 'operationType',
                label: computed(() => t("操作权限")),
                span: settingStore.device === 'mobile' ? 24 : 6,
                props: {
                    options: [
                        { label: computed(() => t("隐藏")).value, value: 0 },
                        { label: computed(() => t("浏览")), value: 1 },
                        { label: computed(() => t("维护")), value: 2 },
                        { label: computed(() => t("管理")), value: 3 }
                    ]
                }
            },
            {
                type: 'slot',
                slotName: 'filterBtnSlot',
                span: 6,
            }
        ]
    },
    // 权限 授权 弹框 搜索条件
    formline: {
        operationType: 1
    },
    //选择树过滤
    filtersList: [
        {
            type: "select",
            value: 1,
            span: settingStore.device === 'mobile' ? 24 : 12,
            label: computed(() => t("操作权限")),
            key: "operationType",
            props: {
                options: [
                    {
                        label: computed(() => t("隐藏")),
                        value: 0
                    },
                    {
                        label: computed(() => t("浏览")),
                        value: 1
                    },
                    {
                        label: computed(() => t("维护")),
                        value: 2
                    },
                    {
                        label: computed(() => t("管理")),
                        value: 3
                    },
                ],
                placeholder: t('请选择操作权限')
            }
        },

    ],
    filtersValueCallBack: (filters) => {//过滤回调值
        formline.value = filters;
    },
    //  dialog 表单
    positiveAuthorityDialog: {
        show: false,
        title: computed(() => t("关联角色")),
        width: '35%',
        onOk: (newConfig) => {
            return new Promise(async (resolve, reject) => {
                let ids = selectTreeRef.value?.y9TreeRef?.getCheckedKeys(true);
                // 保存操作
                const params = {
                    authority: formline.value.operationType,
                    resourceId: props.id,
                }
                await saveOrUpdateRole(params, ids.toString()).then(result => {
                    ElNotification({
                        title: result.success ? t('成功') : t('失败'),
                        message: result.success ? t('添加权限成功') : t('添加权限失败'),
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80,
                    });
                    if(result.success) {
                        // 重新请求 列表数据
                        initList();
                    }
                    resolve();
                    
                }).catch(() => {
                    reject();
                })
            })
        }
    },
    treeApiObj: {
        topLevel: async () => {
			let data = [];
			if (roleTreeType.value === 'appRole') {
				const res = await getResourceTree(props.appId);
				data = res.data
			}else{
				const res = await getPublicRoleTree()
				data = res.data
			}
			return data;
        },
        childLevel: {
            api: roleTreeList,
            params: {},
        },
    },
});

let { loading, filtersList, filtersValueCallBack, roleFormLine, tableRoleConfig, selectIds, treeApiObj,
    positiveAuthorityDialog, filterConfig, formline } = toRefs(state);

onMounted(() => {
    initList();
})

watch(() => props.id, (new_, old_) => {
    if (new_ && new_ !== old_) {
        initList();
    }
})

function handlerRoleAdd(type){
    if(type === 'app') {
        roleTreeType.value = 'appRole'
    }else if(type === 'public'){
        roleTreeType.value = 'publicRole'
    }
    positiveAuthorityDialog.value.title = computed(() => t("关联角色"))
    positiveAuthorityDialog.value.show = true; 
}


// 请求 列表接口
async function initList() {
    let result = await getRelationRoleList(props.id, roleFormLine.value.roleNodeName,
        roleFormLine.value.operationType);
    tableRoleConfig.value.tableData = result.data;
    roleTotal.value = result.data.length;
    tableRoleConfig.value.pageConfig.total = roleTotal.value;
}

// 点击 tree 获取当前数据
function handlerTreeClick(data) {
    currTreeData.value = data;
}


</script>
<style scoped lang="scss">

</style>