<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2025-05-30 14:23:00
 * @Description: 角色关联-资源授权
-->
<template>
    <y9Table
        v-model:selectedVal="resourceTableCurrSelectedVal"
        :config="resourceTableConfig"
        :filterConfig="resourcefilterConfig"
        @on-curr-page-change="onResourceTableCurrPageChange"
        @on-page-size-change="onResourceTablePageSizeChange"
    >
        <template v-slot:filterBtnSlot>
            <div class="widthBtn">
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-second"
                    @click="onSort('resource')"
                >
                    <i class="ri-add-line"></i>
                    {{ $t('资源授权') }}
                </el-button>
            </div>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-second"
                @click="handlerDeleteClick('resource')"
            >
                <i class="ri-close-line"></i>
                {{ $t('删除') }}
            </el-button>
        </template>
        <template #expandRowSlot="props">
            <div class="expand-rows">
                <p>资源名称: {{ props.row.resourceNamePath }}</p>
                <p>资源类型: {{ props.row.resourceTypeStr }}</p>
                <p>所属系统: {{ props.row.systemCnName }}</p>
                <p>权限: {{ props.row.authorityStr }}</p>
                <p>授权者: {{ props.row.authorizer }}</p>
                <p>授权时间: {{ props.row.authorizeTime }}</p>
            </div>
        </template>
        <template #authoritySlot="props">
            <boolWarningCell
                :false-text="props.row.authorityStr"
                :is-true="props.row.authorityStr === '隐藏'"
                :true-text="props.row.authorityStr"
            ></boolWarningCell>
        </template>
    </y9Table>

    <!-- 资源授权 -->
    <y9Dialog v-model:config="addAuthorizationConfigDialog">
        <y9Filter
            ref="resourceFilterRef"
            :filtersValueCallBack="sourceFiltersValueCallBack"
            :itemList="filtersListSource"
            showBorder
        >
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
                        v-model="searchKey"
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
            v-if="type == 'private' && appId != null"
            ref="resourceSelectTree"
            :defaultCheckedKeys="resourceTreeDefaultCheckedKeys"
            :selectField="selectResourceField"
            :showHeader="false"
            :treeApiObj="resourceTreeApiObj"
            checkStrictly
        ></selectTree>

        <selectTree
            v-else
            ref="resourcePublicSelectTree"
            :defaultCheckedKeys="resourceTreeDefaultCheckedKeys"
            :selectField="selectResourceField"
            :showHeader="false"
            :treeApiObj="resourceTreePublicApiObj"
            checkStrictly
        ></selectTree>
    </y9Dialog>
    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { computed, inject, onMounted, ref, watch } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import y9_storage from '@/utils/storage';
    import {
        getRelateResourceList,
        listResourceIdByRoleId,
        removeAuthPermissionRecord,
        saveOrUpdateRelateResource
    } from '@/api/role/index';
    import { appTreeRoot, resourceTree, resourceTreeRoot, systemTreeRoot, treeSearch } from '@/api/resource/index';
    import boolWarningCell from '@/components/BoolWarningCell/index.vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    const settingStore = useSettingStore();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const managerLevel = y9_storage.getObjectItem('ssoUserInfo', 'managerLevel');

    const props = defineProps({
        id: String, // 系统id
        parentId: String,
        appId: String,
        type: String //角色/公共角色（public）。角色管理（private）
    });

    async function getResourceTreeDefaultCheckedKeys() {
        let result = await listResourceIdByRoleId(props.id, authorityRef.value);
        resourceTreeDefaultCheckedKeys.value = result.data;
    }

    //排序按钮点击时触发
    const onSort = (type) => {
        if (type === 'resource') {
            getResourceTreeDefaultCheckedKeys();
            Object.assign(addAuthorizationConfigDialog.value, {
                show: true,
                title: computed(() => t('添加资源授权'))
            });
        }
    };

    // 全局 loading
    let loading = ref(false);

    onMounted(() => {
        getRelateResourceListFn();
    });

    // 监听 currData.type
    watch(
        () => props.id,
        (newVal) => {
            if (managerLevel === 2) {
                getRelateResourceListFn();
            }
        }
    );

    //资源列表选中的数据
    let resourceTableCurrSelectedVal = ref([] as any);
    //资源列表表格配置
    const resourceTableConfig = ref({
        columns: [
            { type: 'selection', fixed: 'left', width: 60 },
            { type: 'expand', width: 40, slot: 'expandRowSlot' },
            { title: computed(() => t('序号')), type: 'index', width: 60, showOverflowTooltip: false },
            { title: computed(() => t('资源名称')), align: 'left', key: 'resourceNamePath' },
            { title: computed(() => t('资源类型')), width: 120, key: 'resourceTypeStr' },
            { title: computed(() => t('所属系统')), key: 'systemCnName' },
            {
                title: computed(() => t('权限')),
                width: 80,
                key: 'authority',
                slot: 'authoritySlot'
            }
        ],
        tableData: [],
        pageConfig: {
            currentPage: 1, //当前页数，支持 v-model 双向绑定
            pageSize: 10, //每页显示条目个数，支持 v-model 双向绑定
            total: 10 //总条目数
        }
    });

    //资源列表搜索条件
    const resourceFilterData = ref({} as any);
    // 资源列表表格 过滤条件
    const resourcefilterConfig = ref({
        filtersValueCallBack: (filters) => {
            resourceFilterData.value = filters;
        },
        showBorder: true,
        itemList: [
            {
                type: 'slot',
                slotName: 'filterBtnSlot',
                span: settingStore.device === 'mobile' ? 24 : 6
            }
        ]
    });

    async function getRelateResourceListFn() {
        const result = await getRelateResourceList(
            props.id,
            resourceTableConfig.value.pageConfig.currentPage,
            resourceTableConfig.value.pageConfig.pageSize
        );
        resourceTableConfig.value.tableData = result.rows;
        resourceTableConfig.value.pageConfig.total = result.total;
    }

    //当前页改变时触发
    function onResourceTableCurrPageChange(currPage) {
        resourceTableConfig.value.pageConfig.currentPage = currPage;
        getRelateResourceListFn(); //获取列表
    }

    //每页条数改变时触发
    function onResourceTablePageSizeChange(pageSize) {
        resourceTableConfig.value.pageConfig.pageSize = pageSize;
        getRelateResourceListFn(); //获取列表
    }

    // 点击删除 按钮
    function handlerDeleteClick(type) {
        if (type === 'resource') {
            if (!resourceTableCurrSelectedVal.value.length) {
                ElMessage({
                    type: 'warning',
                    message: t('请选择需要删除的资源'),
                    offset: 65
                });
                return;
            }
        }

        ElMessageBox.confirm(`${t('是否删除选中的数据')}?`, t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info'
        })
            .then(async () => {
                loading.value = true;
                let ids = [];
                if (type === 'resource') {
                    ids = resourceTableCurrSelectedVal.value.map((item) => {
                        return item.id;
                    });
                    let result = await removeAuthPermissionRecord({ ids: ids.join(',') });
                    loading.value = false;
                    ElNotification({
                        title: result.success ? t('成功') : t('失败'),
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    // 初始化数据
                    getRelateResourceListFn();
                    resourceTableCurrSelectedVal.value = [];
                }
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: t('已取消删除'),
                    offset: 65
                });
            });
    }

    // 资源授权 ref
    const resourceFilterRef = ref(null);
    // 资源授权的 操作权限
    let authorityRef = ref(1);

    watch(authorityRef, (current, prev) => {
        getResourceTreeDefaultCheckedKeys();
    });

    const resourceSelectTree = ref();
    const resourcePublicSelectTree = ref();
    let resourceTreeDefaultCheckedKeys = ref([]);

    // 资源授权  弹框
    let addAuthorizationConfigDialog = ref({
        show: false,
        title: computed(() => t('添加资源授权')),
        width: '45%',
        onOk: () => {
            return new Promise(async (resolve, reject) => {
                let checkedIds = [];
                if (props.type == 'private' && props.appId != null) {
                    checkedIds = resourceSelectTree.value?.y9TreeRef?.getCheckedKeys();
                } else {
                    checkedIds = resourcePublicSelectTree.value?.y9TreeRef?.getCheckedKeys();
                }
                console.log(props.type == 'private' && props.appId != null, checkedIds);

                // 只需要新选中的 id 数组
                const ids = checkedIds.filter((item) => !resourceTreeDefaultCheckedKeys.value.includes(item));

                if (ids.length == 0) {
                    ElNotification({
                        title: t('失败'),
                        message: t('请选择需要授权的资源'),
                        type: 'error',
                        duration: 2000,
                        offset: 80
                    });
                    reject();
                    return;
                }

                // 保存操作
                const params = {
                    authority: authorityRef.value,
                    principalId: props.id,
                    principalType: 0,
                    resourceIds: ids.toString()
                };

                await saveOrUpdateRelateResource(params)
                    .then((result) => {
                        ElNotification({
                            title: result.success ? t('成功') : t('失败'),
                            message: result.msg,
                            type: result.success ? 'success' : 'error',
                            duration: 2000,
                            offset: 80
                        });
                        if (result.success) {
                            // 重新请求接口
                            getRelateResourceListFn();
                            resolve();
                        }
                    })
                    .catch(() => {
                        reject();
                    });
            });
        }
    });
    // 资源授权 弹框 选择树过滤
    let filtersListSource = ref([
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
            key: 'authority',
            props: {
                clearable: false,
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
    ]);

    function sourceFiltersValueCallBack(filters) {
        authorityRef.value = filters.authority;
    }

    // 选择树的选择 框
    let selectResourceField = [
        {
            fieldName: 'nodeType',
            value: ['APP', 'MENU', 'OPERATION']
        }
    ];
    // 资源授权 请求的tree接口
    let resourceTreeApiObj = ref({
        topLevel: async () => {
            console.log(props.type, props.appId, props.parentId);
            let result = { success: false, msg: '', data: {} as any };
            result = await appTreeRoot(props.appId);
            return result.data;
        },
        childLevel: {
            //子级（二级及二级以上）tree接口
            api: resourceTreeRoot,
            params: {}
        },
        search: {
            //搜索接口及参数
            api: treeSearch,
            params: {}
        }
    });

    //系统角色+公共角色 资源授权
    let resourceTreePublicApiObj = ref({
        topLevel: async () => {
            let result = { success: false, msg: '', data: {} as any };
            if (props.type == 'public') {
                result = await resourceTree({});
            } else {
                result = await systemTreeRoot(props.parentId);
            }

            return result.data;
        },
        childLevel: {
            //子级（二级及二级以上）tree接口
            api: resourceTree
        },
        search: {
            //搜索接口及参数
            api: treeSearch,
            params: {}
        }
    });

    const searchKey = ref();

    function onRefreshTree() {
        if (props.type == 'private' && props.appId) {
            resourceSelectTree.value.onRefreshTree();
        } else {
            resourcePublicSelectTree.value.onRefreshTree();
        }
    }

    let searchTimer = null;

    function onSearchKeyChange(searchVal) {
        clearTimeout(searchTimer);
        searchTimer = setTimeout(() => {
            resourceTreeApiObj.value.search.params.key = searchVal;
            if (props.type == 'private') {
                if (props.appId) {
                    resourceTreeApiObj.value.search.params.appId = props.appId;
                } else {
                    resourceTreePublicApiObj.value.search.params.systemId = props.parentId;
                    resourceTreePublicApiObj.value.search.params.key = searchVal;
                }
            } else {
                resourceTreePublicApiObj.value.search.params.key = searchVal;
            }
        }, 500);
    }
</script>
<style lang="scss" scoped>
    .basic-btns {
        display: flex;
        justify-content: space-between;
        flex-wrap: wrap;
        margin-bottom: 20px;

        .btn-top {
            margin-bottom: 10px;
        }
    }

    .widthBtn {
        display: flex;
        margin: 0 10px;

        :deep(.el-button) {
            min-width: 90px;
        }
    }

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
