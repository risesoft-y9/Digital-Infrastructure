<!--
 * @Author:  
 * @Date: 2022-06-06 11:47:27
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-01-12 10:48:01
 * @Description: 应用资源授权 - 组织关联
-->
<template>
    <div>
        <div style="margin-bottom: 20px">
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-main"
                type="primary"
                @click="handlerOrgClick"
            >
                <i class="ri-add-line" />
                {{ $t('组织') }}
            </el-button>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-main"
                type="primary"
                @click="handlerPosClick"
            >
                <i class="ri-add-line" />
                {{ $t('岗位') }}
            </el-button>
        </div>
        <!-- 表格 -->
        <y9Table :config="tableOrgConfig">
            <template #expandRowSlot="props">
                <div class="expand-rows">
                    <p>组织节点名称: {{ props.row.orgUnitName }}</p>
                    <p>组织节点类型: {{ props.row.orgType }}</p>
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
        <!-- 组织 岗位 -->
        <y9Dialog v-model:config="personConfigDialog">
            <y9Filter :filtersValueCallBack="filtersValueCallBack" :itemList="filtersList" :showBorder="true">
                <template #treeFilter>
                    <div class="custom-select-tree-filter">
                        <el-button
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            class="global-btn-second refresh-btn"
                            @click="onRefreshTree"
                        >
                            <i class="ri-refresh-line"></i>
                            <span>{{ $t('刷新') }}</span>
                        </el-button>

                        <input autocomplete="new-password" hidden type="password" />
                        <el-input
                            v-model="currFilters.searchKey"
                            :placeholder="$t('请搜索')"
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            autocomplete
                            class="search-input"
                            name="select-tree-search"
                            type="search"
                            @input="onSearchKeyChange"
                        >
                            <template #prefix>
                                <i class="ri-search-line"></i>
                            </template>
                        </el-input>
                    </div>
                </template>
            </y9Filter>
            <!-- tree树 -->
            <selectTree
                ref="selectTreeRef"
                :checkStrictly="checkStrictly"
                :selectField="selectField"
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
    import { getRelationOrgList, removeRole, saveOrUpdateOrg } from '@/api/grantAuthorize/index';
    import { getTreeItemById, searchByName, treeInterface } from '@/api/org/index';
    import { computed, h, inject, onMounted, reactive, ref, toRefs, watch } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import { useSettingStore } from '@/store/modules/settingStore';

    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    const settingStore = useSettingStore();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');

    const props = defineProps({
        id: {
            type: String,
            default: ''
        }
    });

    // 组织 关联 列表 总数
    let orgTotal = ref(0);
    // 组织，岗位 树 当前点击 的节点 详情数据
    let currData = ref({});

    //选择tree实例
    const selectTreeRef = ref();

    // 变量 对象
    const state = reactive({
        // 区域loading
        loading: false,
        // 组织关联 表格 的配置信息
        tableOrgConfig: {
            columns: [
                // { title: '', type: 'selection', fixed: 'left' },
                { title: computed(() => t('序号')), type: 'index', width: 60, fixed: 'left' },
                { type: 'expand', width: 40, slot: 'expandRowSlot' },
                { title: computed(() => t('组织节点名称')), align: 'left', key: 'orgUnitName' },
                { title: computed(() => t('组织节点类型')), key: 'orgType', width: 150 },
                { title: computed(() => t('权限类型')), key: 'authorityStr', width: 100, slot: 'authoritySlot' },
                {
                    title: computed(() => t('操作')),
                    fixed: 'right',
                    width: 100,
                    render: (row) => {
                        return h(
                            'el-button',
                            {
                                style: {
                                    cursor: 'pointer'
                                },
                                class: 'global-btn-second',
                                onClick: async () => {
                                    ElMessageBox.confirm(`${t('是否移除')}【${row.orgName}】?`, t('提示'), {
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
            pageConfig: {
                // 分页配置，
                currentPage: 1,
                pageSize: 10,
                total: orgTotal.value // 总条数
            }
        },
        // 组织，岗位 弹框 dialog
        personConfigDialog: {
            show: false,
            title: t('授权管理 - 组织关联'),
            width: '45%',
            onOkLoading: true,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {
                    let ids = selectTreeRef.value?.y9TreeRef?.getCheckedKeys(
                        newConfig.value.selectTreeType == 'position' ? true : false
                    );

                    // 保存操作
                    const params = {
                        authority: currFilters.value.operationType,
                        resourceId: props.id
                    };
                    let result = { success: false, msg: '' };
                    result = await saveOrUpdateOrg(params, ids.toString());
                    ElNotification({
                        title: result.success ? t('成功') : t('失败'),
                        message: result.msg,
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

        currFilters: {} as any, //当前过滤值

        filtersValueCallBack: (filters) => {
            //过滤回调值
            currFilters.value = filters;
        },

        //过滤列表
        filtersList: [
            {
                type: 'slot',
                slotName: 'treeFilter',
                span: settingStore.device === 'mobile' ? 24 : 16
            },
            {
                type: 'select',
                value: 1,
                span: settingStore.device === 'mobile' ? 24 : 8,
                label: computed(() => t('操作权限')),
                key: 'operationType',
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
                    ]
                }
            }
        ],
        // 组织，岗位树 请求接口
        treeApiObj: {
            topLevel: treeInterface,
            childLevel: {
                api: getTreeItemById,
                params: {
                    treeType: 'tree_type_person',
                    disabled: false
                }
            },
            search: {
                api: searchByName,
                params: {
                    treeType: 'tree_type_org_person'
                }
            }
        },
        //
        selectField: [
            //设置需要选择的字段
            {
                fieldName: 'nodeType',
                value: ['Person', 'Organization', 'Department']
            }
        ]
    });

    let {
        loading,
        tableOrgConfig,
        personConfigDialog,
        treeApiObj,
        selectField,
        filtersList,
        currFilters,
        filtersValueCallBack
    } = toRefs(state);

    function onRefreshTree() {
        selectTreeRef.value.onRefreshTree();
    }

    let searchTimer = null;

    function onSearchKeyChange(searchVal) {
        clearTimeout(searchTimer);
        searchTimer = setTimeout(() => {
            treeApiObj.value.search.params.key = searchVal;
        }, 500);
    }

    onMounted(() => {
        initList();
    });

    watch(
        () => props.id,
        (new_, old_) => {
            if (new_ && new_ !== old_) {
                initList();
            }
        }
    );

    // 列表初始 数据
    async function initList() {
        let result = await getRelationOrgList(props.id);
        tableOrgConfig.value.tableData = result.data;
        orgTotal.value = result.data.length;
        tableOrgConfig.value.pageConfig.total = orgTotal.value;
    }

    const checkStrictly = ref(false); //父子是否关联
    // 点击 组织  按钮 出现弹框选择
    function handlerOrgClick() {
        personConfigDialog.value.title = computed(() => t('授权管理 - 组织关联'));
        personConfigDialog.value.selectTreeType = 'org';
        treeApiObj.value.childLevel.params.treeType = 'tree_type_person';
        treeApiObj.value.search.params.treeType = 'tree_type_org_person';
        selectField.value = [
            {
                fieldName: 'nodeType',
                value: ['Person', 'Organization', 'Department']
            }
        ];
        checkStrictly.value = true;
        personConfigDialog.value.show = true;
    }

    // 点击 岗位  按钮 出现弹框选择
    function handlerPosClick() {
        personConfigDialog.value.title = computed(() => t('授权管理 - 岗位关联'));
        personConfigDialog.value.selectTreeType = 'position';
        treeApiObj.value.childLevel.params.treeType = 'tree_type_position';
        treeApiObj.value.search.params.treeType = 'tree_type_org_position';
        selectField.value = [
            {
                fieldName: 'nodeType',
                value: ['Position']
            }
        ];
        checkStrictly.value = false;
        personConfigDialog.value.show = true;
    }

    // 点击 组织树 获取的数据
    function handlerTreeClick(data) {
        currData.value = data;
    }
</script>
<style lang="scss" scoped>
    .custom-select-tree-filter {
        display: flex;

        .refresh-btn {
        }

        .search-input {
            margin-left: 15px;
        }
    }

    .expand-rows {
        padding-left: 20px;
    }
</style>
