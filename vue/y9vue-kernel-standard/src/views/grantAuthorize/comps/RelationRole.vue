<!--
 * @Author:  
 * @Date: 2022-06-06 11:47:27
 * @LastEditors: mengjuhua
 * @LastEditTime: 2025-12-24 09:22:06
 * @Description: 应用资源授权 - 角色关联
-->
<template>
    <div>
        <!-- 表格 -->
        <y9Table :config="tableRoleConfig" :filterConfig="filterConfig">
            <template v-slot:filterBtnSlot>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-main"
                    type="primary"
                    @click="search"
                >
                    <i class="ri-search-line"></i>
                    {{ $t('搜索') }}
                </el-button>
                <el-button
                    v-if="appId"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-second"
                    @click="handlerRoleAdd('app')"
                >
                    <i class="ri-add-line" />
                    {{ $t('应用角色') }}
                </el-button>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-second"
                    @click="handlerRoleAdd('public')"
                >
                    <i class="ri-add-line" />
                    {{ $t('公共角色') }}
                </el-button>
            </template>
            <template #expandRowSlot="props">
                <div class="expand-rows">
                    <p>角色名称: {{ props.row.roleNamePath }}</p>
                    <p>权限类型: {{ props.row.authorityStr }}</p>
                    <p>授权者: {{ props.row.authorizer }}</p>
                    <p>授权时间: {{ props.row.authorizeTime }}</p>
                </div>
            </template>
            <template #authoritySlot="props">
                <boolWarningCell
                    :is-true="props.row.authorityStr === '隐藏'"
                    :true-text="props.row.authorityStr"
                    :false-text="props.row.authorityStr"
                ></boolWarningCell>
            </template>
        </y9Table>
        <!-- 授权 -->
        <y9Dialog v-model:config="positiveAuthorityDialog">
            <y9Filter
                :filtersValueCallBack="filtersValueCallBack"
                :itemList="filtersList"
                :showBorder="true"
            ></y9Filter>
            <!-- tree树 -->
            <selectTree
                ref="selectTreeRef"
                :defaultCheckedKeys="selectTreeDefaultCheckedKeys"
                :selectField="[{ fieldName: 'nodeType', value: 'role' }]"
                :showHeader="false"
                :treeApiObj="treeApiObj"
                @onTreeClick="handlerTreeClick"
            ></selectTree>
        </y9Dialog>
        <!-- 制造loading效果 -->
        <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
    </div>
</template>

<script lang="ts" setup>
    import { computed, h, inject, onMounted, reactive, ref, toRefs, watch } from 'vue';
    import { getRelationRoleList, removeRole, saveOrUpdateRole } from '@/api/grantAuthorize/index';
    import { appRoleTree, getPublicRoleTree, listPrincipalIdByResourceId, roleTreeList } from '@/api/role/index';
    import { useI18n } from 'vue-i18n';
    import { useSettingStore } from '@/store/modules/settingStore';

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
    });

    // 角色关联 表格总数
    let roleTotal = ref(0);
    // 当前tree点击 的数据
    let currTreeData = ref({});
    // 角色树类型：应用角色树(appRole)、公共角色树(publicRole)
    let roleTreeType = ref('appRole');

    //选择tree实例
    const selectTreeRef = ref();
    let selectTreeDefaultCheckedKeys = ref([]);

    // 变量 对象
    const state = reactive({
        // 区域 loading
        loading: false,
        // 列表 选择的 行数据
        selectIds: [],
        // 角色关联 表格 行内表单的 变量
        roleFormLine: {
            roleNodeName: '',
            authority: 1
        },
        // 角色关联 表格的 配置信息
        tableRoleConfig: {
            columns: [
                // { title: '', type: 'selection', fixed: 'left' },
                { title: computed(() => t('序号')), type: 'index', width: 60, fixed: 'left' },
                { type: 'expand', width: 40, slot: 'expandRowSlot' },
                { title: computed(() => t('角色名称')), align: 'left', key: 'roleNamePath' },
                { title: computed(() => t('权限类型')), key: 'authorityStr', width: 100, slot: 'authoritySlot' },
                {
                    title: computed(() => t('操作')),
                    fixed: 'right',
                    width: '130px',
                    render: (row) => {
                        return h(
                            'span',
                            {
                                style: {
                                    cursor: 'pointer'
                                },
                                class: 'global-btn-second',
                                onClick: async () => {
                                    ElMessageBox.confirm(`${t('是否移除')}【${row.roleName}】?`, t('提示'), {
                                        confirmButtonText: t('确定'),
                                        cancelButtonText: t('取消'),
                                        type: 'info'
                                    })
                                        .then(async () => {
                                            // 请求 移除 接口函数---
                                            loading.value = true;
                                            let result = await removeRole(row.id);
                                            ElNotification({
                                                title: result.success ? t('成功') : t('失败'),
                                                message: result.success ? t('移除成功') : t('移除失败'),
                                                type: result.success ? 'success' : 'error',
                                                duration: 2000,
                                                offset: 80
                                            });
                                            loading.value = false;
                                            // 重新请求 列表数据
                                            initList();
                                        })
                                        .catch(() => {
                                            ElMessage({
                                                type: 'info',
                                                message: t('已取消移除'),
                                                offset: 65
                                            });
                                        });
                                }
                            },
                            t('移除')
                        );
                    }
                }
            ],
            tableData: [],
            pageConfig: false
        },
        // 表格过滤
        filterConfig: {
            filtersValueCallBack: (filters) => {
                roleFormLine.value = filters;
            },
            showBorder: true,
            itemList: [
                {
                    type: 'select',
                    value: 1,
                    key: 'authority',
                    label: computed(() => t('操作权限')),
                    labelWidth: '60px',
                    span: settingStore.device === 'mobile' ? 24 : 5,
                    props: {
                        options: [
                            { label: computed(() => t('隐藏')).value, value: 0 },
                            { label: computed(() => t('浏览')), value: 1 },
                            { label: computed(() => t('维护')), value: 2 },
                            { label: computed(() => t('管理')), value: 3 }
                        ]
                    }
                },
                {
                    type: 'input',
                    value: '',
                    key: 'roleNodeName',
                    label: computed(() => t('角色名称')),
                    span: settingStore.device === 'mobile' ? 24 : 6,
                    clearable: true
                },

                {
                    type: 'slot',
                    slotName: 'filterBtnSlot',
                    span: 6
                }
            ]
        },
        // 权限 授权 弹框 搜索条件
        formline: {
            authority: 1
        },
        //选择树过滤
        filtersList: [
            {
                type: 'select',
                value: 1,
                span: settingStore.device === 'mobile' ? 24 : 12,
                label: computed(() => t('操作权限')),
                key: 'authority',
                props: {
                    options: [
                        {
                            label: computed(() => t('隐藏')),
                            value: 0
                        },
                        {
                            label: computed(() => t('浏览')),
                            value: 1
                        },
                        {
                            label: computed(() => t('维护')),
                            value: 2
                        },
                        {
                            label: computed(() => t('管理')),
                            value: 3
                        }
                    ],
                    placeholder: t('请选择操作权限')
                }
            }
        ],
        filtersValueCallBack: (filters) => {
            //过滤回调值
            formline.value = filters;
        },
        //  dialog 表单
        positiveAuthorityDialog: {
            show: false,
            title: computed(() => t('关联角色')),
            width: '35%',
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {
                    let checkedIds = selectTreeRef.value?.y9TreeRef?.getCheckedKeys(true);
                    const ids = checkedIds.filter((item) => !selectTreeDefaultCheckedKeys.value.includes(item));

                    // 保存操作
                    const params = {
                        authority: formline.value.authority,
                        resourceId: props.id
                    };
                    let result = { success: false, msg: '' };
                    result = await saveOrUpdateRole(params, ids.toString());
                    ElNotification({
                        title: result.success ? t('成功') : t('失败'),
                        message: result.success ? t('添加权限成功') : result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    if (result.success) {
                        // 重新请求 列表数据
                        initList();
                    }
                    resolve();
                });
            }
        },
        treeApiObj: {
            topLevel: async () => {
                let data: any = [];
                if (roleTreeType.value === 'appRole') {
                    const res = await appRoleTree(props.appId);
                    return res.data;
                } else {
                    const res = await getPublicRoleTree();
                    data = res.data;
                }
                return data;
            },
            childLevel: {
                api: roleTreeList,
                params: {}
            }
        }
    });

    let {
        loading,
        filtersList,
        filtersValueCallBack,
        roleFormLine,
        tableRoleConfig,
        treeApiObj,
        positiveAuthorityDialog,
        filterConfig,
        formline
    } = toRefs(state);

    onMounted(() => {
        initList();
    });

    watch(
        () => props.id,
        (new_, old_) => {
            if (new_ && new_ !== old_) {
                roleFormLine.value = {
                    roleNodeName: '',
                    authority: 1
                };
                initList();
            }
        }
    );

    watch(
        () => formline.value.authority,
        (new_, old_) => {
            getSelectTreeDefaultCheckedKeys();
            selectTreeRef.value && selectTreeRef.value.onRefreshTree();
        }
    );

    async function getSelectTreeDefaultCheckedKeys() {
        let result = await listPrincipalIdByResourceId(props.id, formline.value.authority);
        selectTreeDefaultCheckedKeys.value = result.data;
    }

    async function handlerRoleAdd(type) {
        await getSelectTreeDefaultCheckedKeys();

        if (type === 'app') {
            roleTreeType.value = 'appRole';
        } else if (type === 'public') {
            roleTreeType.value = 'publicRole';
        }
        positiveAuthorityDialog.value.title = computed(() => t('关联角色'));
        positiveAuthorityDialog.value.show = true;
    }

    // 请求 列表接口
    async function initList() {
        let result = await getRelationRoleList(props.id, roleFormLine.value.roleNodeName, roleFormLine.value.authority);
        tableRoleConfig.value.tableData = result.data;
    }

    async function search() {
        if (roleFormLine.value.roleNodeName == '' || roleFormLine.value.roleNodeName == undefined) {
            ElNotification({
                title: t('提示'),
                message: t('请输入角色名称'),
                type: 'info',
                duration: 2000,
                offset: 80
            });
            return;
        }
        tableRoleConfig.value.tableData = [];
        initList();
    }

    // 点击 tree 获取当前数据
    function handlerTreeClick(data) {
        currTreeData.value = data;
    }
</script>
<style lang="scss" scoped>
    .expand-rows {
        padding-left: 20px;
    }
</style>
