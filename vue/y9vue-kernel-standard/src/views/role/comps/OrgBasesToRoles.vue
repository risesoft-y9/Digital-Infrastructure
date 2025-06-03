<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2025-05-27 13:48:07
 * @Description: 角色关联-角色成员
-->
<template>
    <y9Table
        v-model:selectedVal="tableCurrSelectedVal"
        :config="tableConfig"
        :filterConfig="filterConfig"
        @on-curr-page-change="onTableCurrPageChange"
        @on-page-size-change="onTablePageSizeChange"
    >
        <template v-slot:filterBtnSlot>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-main"
                type="primary"
                @click="initList"
            >
                <i class="ri-search-line"></i>
                {{ $t('搜索') }}
            </el-button>
            <div class="widthBtn">
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-second"
                    @click="handlerClick('positive')"
                >
                    <i class="ri-add-line"></i>
                    {{ $t('正权限成员') }}
                </el-button>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-second"
                    @click="handlerClick('negative')"
                >
                    <i class="ri-add-line"></i>
                    {{ $t('负权限成员') }}
                </el-button>
            </div>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-second"
                @click="handlerDeleteClick('roleMember')"
            >
                <i class="ri-close-line"></i>
                {{ $t('删除') }}
            </el-button>
        </template>
        <template #expandRowSlot="props">
            <div class="expand-rows">
                <p>名称: {{ props.row.orgUnitNamePath }}</p>
                <p>成员类型: {{ props.row.orgTypeStr }}</p>
                <p>负权限成员: {{ props.row.negative ? '是' : '否' }}</p>
                <p>添加时间: {{ props.row.createTime }}</p>
            </div>
        </template>
        <template #negativeSlot="props">
            <boolWarningCell :is-true="props.row.negative"></boolWarningCell>
        </template>
    </y9Table>

    <!-- 正权限、负权限成员 -->
    <y9Dialog v-model:config="negativeConfigDialog">
        <y9Filter
            ref="negativeFilterRef"
            :filtersValueCallBack="negativeFiltersValueCallBack"
            :itemList="negativeFiltersListSource"
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
        <selectTree
            ref="selectTreeRef"
            :defaultCheckedKeys="selectTreeDefaultCheckedKeys"
            :showHeader="false"
            :treeApiObj="negativeTreeApiObj"
            checkStrictly
        ></selectTree>
    </y9Dialog>

    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { computed, inject, onMounted, ref, watch } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import { addOrgUnits, listOrgUnitIdByRoleId, removeOrgUnits, searchByUnitName } from '@/api/role/index';
    import { getTreeItemById, searchByName, treeInterface } from '@/api/org/index';
    // 基本信息
    import boolWarningCell from '@/components/BoolWarningCell/index.vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    const settingStore = useSettingStore();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');

    const props = defineProps({
        id: String // 系统id
    });

    // 全局 loading
    let loading = ref(false);
    // 角色成员  ------
    // 搜索条件
    let filterData = ref({} as any);
    // 树 ref
    const selectTreeRef = ref();
    let selectTreeDefaultCheckedKeys = ref([]);

    // 表格 过滤条件
    const filterConfig = ref({
        filtersValueCallBack: (filters) => {
            filterData.value = filters;
        },
        showBorder: true,
        itemList: [
            {
                type: 'input',
                value: '',
                key: 'orgUnitName',
                label: computed(() => t('名称')),
                span: settingStore.device === 'mobile' ? 24 : 5,
                clearable: true
            },
            {
                type: 'slot',
                slotName: 'filterBtnSlot',
                span: settingStore.device === 'mobile' ? 24 : 14
            }
        ]
    });

    // 表格 配置属性
    let tableConfig = ref({
        columns: [
            { type: 'selection', fixed: 'left', width: 60 },
            { type: 'expand', width: 40, slot: 'expandRowSlot' },
            { title: computed(() => t('序号')), type: 'index', width: 60, showOverflowTooltip: false },
            { title: computed(() => t('名称')), align: 'left', key: 'orgUnitNamePath' },
            { title: computed(() => t('成员类型')), width: 100, key: 'orgTypeStr' },
            {
                title: computed(() => t('负权限成员')),
                width: 100,
                key: 'negative',
                slot: 'negativeSlot'
            }
        ],
        tableData: [],
        pageConfig: {
            currentPage: 1, //当前页数，支持 v-model 双向绑定
            pageSize: 10, //每页显示条目个数，支持 v-model 双向绑定
            total: 10 //总条目数
        }
    });
    // 表格选中的数据
    let tableCurrSelectedVal = ref([] as any);
    // 监听 props.id

    onMounted(() => {
        initList();
    });

    watch(
        () => props.id,
        async (new_, old_) => {
            if (new_ && new_ !== old_) {
                initList();
            }
        }
    );

    // 角色成员 请求 列表接口
    async function initList() {
        // filterData
        let result = await searchByUnitName(
            tableConfig.value.pageConfig.currentPage,
            tableConfig.value.pageConfig.pageSize,
            props.id,
            filterData.value.orgUnitName
        );

        tableConfig.value.pageConfig.total = result.total;
        tableConfig.value.tableData = result.rows;
    }

    //当前页改变时触发
    function onTableCurrPageChange(currPage) {
        tableConfig.value.pageConfig.currentPage = currPage;
        initList(); //获取列表
    }

    //每页条数改变时触发
    function onTablePageSizeChange(pageSize) {
        tableConfig.value.pageConfig.pageSize = pageSize;
        initList(); //获取列表
    }

    // 正权限 负权限 弹框
    let negativeConfigDialog = ref({
        show: false,
        title: '',
        width: '38%',
        onOk: () => {
            return new Promise(async (resolve, reject) => {
                let checkedIds = selectTreeRef.value?.y9TreeRef?.getCheckedKeys();
                // 只需要新选中的 id 数组
                const ids = checkedIds.filter((item) => !selectTreeDefaultCheckedKeys.value.includes(item));

                if (ids.length === 0) {
                    ElNotification({
                        title: t('失败'),
                        message: t('请选择需要添加权限的节点'),
                        type: 'error',
                        duration: 2000,
                        offset: 80
                    });
                    reject();
                    return;
                }

                let result;

                if (negativeConfigDialog.value.title == computed(() => t('添加正权限成员')).value) {
                    await addOrgUnits(props.id, ids.join(','), false)
                        .then((res) => {
                            result = res;
                        })
                        .catch(() => {});
                } else if (negativeConfigDialog.value.title == computed(() => t('添加负权限成员')).value) {
                    await addOrgUnits(props.id, ids.join(','), true)
                        .then((res) => {
                            result = res;
                        })
                        .catch(() => {});
                }
                ElNotification({
                    title: result.success ? t('成功') : t('失败'),
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
                // 重新请求接口
                initList();
                if (result.success) {
                    resolve();
                } else {
                    reject();
                }
            });
        }
    });

    // 点击 添加正权限 添加负权限
    async function handlerClick(type) {
        if (type === 'positive') {
            let result = await listOrgUnitIdByRoleId(props.id, false);
            selectTreeDefaultCheckedKeys.value = result.data;
            negativeConfigDialog.value.title = computed(() => t('添加正权限成员'));
        } else {
            let result = await listOrgUnitIdByRoleId(props.id, true);
            selectTreeDefaultCheckedKeys.value = result.data;
            negativeConfigDialog.value.title = computed(() => t('添加负权限成员'));
        }
        negativeConfigDialog.value.show = true;
    }

    // 点击删除 按钮
    function handlerDeleteClick(type) {
        if (type === 'roleMember') {
            if (!tableCurrSelectedVal.value.length) {
                ElMessage({
                    type: 'warning',
                    message: t('请选择需要删除的人员'),
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
                ids = tableCurrSelectedVal.value.map((item) => {
                    return item.id;
                });
                let result = await removeOrgUnits(ids.join(','));
                loading.value = false;
                ElNotification({
                    title: result.success ? t('成功') : t('失败'),
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
                // 初始化数据
                initList();
                tableCurrSelectedVal.value = [];
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: t('已取消删除'),
                    offset: 65
                });
            });
    }

    let negativeFilterRef = ref();

    let negativeFiltersListSource = ref([
        {
            type: 'slot',
            slotName: 'treeFilter',
            span: settingStore.device === 'mobile' ? 24 : 16
        },
        {
            type: 'select',
            value: 'tree_type_position',
            span: settingStore.device === 'mobile' ? 24 : 8,
            label: computed(() => t('成员类型')),
            key: 'orgType',
            props: {
                clearable: false,
                options: [
                    {
                        label: computed(() => t('组织岗位')),
                        value: 'tree_type_position'
                    },
                    {
                        label: computed(() => t('组织人员')),
                        value: 'tree_type_org_person'
                    }
                ]
            }
        }
    ]);

    let orgType = ref('tree_type_position');

    function negativeFiltersValueCallBack(filters) {
        orgType.value = filters.orgType;
    }

    // 正权限  负权限 请求的tree接口
    let negativeTreeApiObj = ref({
        topLevel: treeInterface,
        childLevel: {
            //子级（二级及二级以上）tree接口
            api: getTreeItemById,
            params: {
                treeType: orgType.value,
                disabled: false
            }
        },
        search: {
            //搜索接口及参数
            api: searchByName,
            params: {
                treeType: orgType.value,
                disabled: false
            }
        }
    });

    watch(
        () => orgType.value,
        async (new_, old_) => {
            console.log(orgType.value, searchKey.value);
            searchKey.value = '';
            negativeTreeApiObj.value.childLevel.params.treeType = orgType.value;
            negativeTreeApiObj.value.search.params.treeType = orgType.value;
            selectTreeRef.value.onRefreshTree();
            // if (searchKey.value) {
            //     onSearchKeyChange(searchKey.value);
            // }
        }
    );

    const searchKey = ref();

    function onRefreshTree() {
        searchKey.value = '';
        selectTreeRef.value.onRefreshTree();
    }

    let searchTimer;

    function onSearchKeyChange(searchVal) {
        clearTimeout(searchTimer);
        searchKey.value = searchVal;
        searchTimer = setTimeout(() => {
            negativeTreeApiObj.value.search.params.key = searchVal;
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
