<template>
    <div class="icon-list">
        <fixedTreeModule
            ref="fixedTreeRef"
            :hiddenSearch="true"
            :showNodeDelete="false"
            :treeApiObj="treeApiObj"
            @onTreeClick="handlerTreeClick"
        >
            <template #rightContainer>
                <y9Card :title="`${currData.name ? currData.name : ''}`">
                    <template v-if="currData.name !== '应用分类导航'">
                        <y9Table
                            v-model:selectedVal="tableCurrSelectedVal"
                            :config="iconTableConfig"
                            :filterConfig="filterConfig"
                            @on-curr-page-change="onCurrPageChangeAppCategory"
                            @on-page-size-change="onPageSizeChangeAppCategory"
                            @window-height-change="windowHeightChange"
                        >
                            <template v-slot:slotBtns>
                                <el-button
                                    :size="fontSizeObj.buttonSize"
                                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                                    class="global-btn-main"
                                    type="primary"
                                    @click="handlerAdd"
                                >
                                    <i class="ri-add-line"></i>
                                    {{ $t('应用') }}
                                </el-button>
                                <el-button
                                    :size="fontSizeObj.buttonSize"
                                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                                    class="global-btn-main"
                                    type="primary"
                                    @click="handlerDelete"
                                >
                                    <i class="ri-close-line" />
                                    {{ $t('删除') }}
                                </el-button>
                                <el-button
                                    :size="fontSizeObj.buttonSize"
                                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                                    class="global-btn-main"
                                    type="primary"
                                    @click="handlerSort"
                                >
                                    <i class="ri-arrow-up-down-line"></i>
                                    {{ $t('排序') }}
                                </el-button>
                            </template>
                        </y9Table>
                    </template>
                    <template v-else>
                        <div style="height: 213px">
                            <el-alert title="请点击左侧树，选择分类再进行操作。" type="warning" />
                        </div>
                    </template>
                </y9Card>
            </template>
        </fixedTreeModule>

        <y9Dialog v-model:config="dialogConfig"></y9Dialog>

        <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
    </div>

    <y9Dialog v-model:config="dialogConfig">
        <treeSort
            v-if="dialogConfig.type == 'sort'"
            ref="sortRef"
            :apiParams="{ categoryId: currData.code }"
            :apiRequest="listOrderDataByResourceId"
            :columns="dialogConfig.columns"
        >
        </treeSort>
        <y9Table
            v-if="dialogConfig.type == 'addApp'"
            v-model:selectedVal="appSelectedVal"
            :config="appTableConfig"
            :filterConfig="appFilterConfig"
        >
            <template v-slot:slotSearch>
                <el-button class="global-btn-main" type="primary" @click="search()"> {{ $t('查询') }}</el-button>
                <el-button class="global-btn-second" @click="reset()">{{ $t('重置') }}</el-button>
            </template>
        </y9Table>
    </y9Dialog>
</template>
<script lang="ts" setup>
    import { computed, inject, onMounted, reactive, ref, toRefs, watch } from 'vue';
    import {
        deleteIcon,
        getCategoryList,
        listAllAppByTenantId,
        listOrderDataByResourceId,
        pageOrderLists,
        saveAppCategoryOrder,
        searchAppList,
        updateAppCategoryOrder
    } from '@/api/appCategory';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    const settingStore = useSettingStore();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');

    //窗口变动时触发，获取表格的高度
    function windowHeightChange(tableHeight) {
        iconTableConfig.value.height = tableHeight - 35 - 35 - 28;
        iconTableConfig.value.maxHeight = tableHeight - 35 - 35 - 28; //35 35 是y9-card-content样式中上padding、下padding的值
    }

    //当前tree节点的信息
    let currData = ref({
        id: '',
        name: ''
    });

    let query = ref({ appName: '' });

    let sortRef = ref();
    const data = reactive({
        fixedTreeRef: '', //tree实例
        loading: false,
        treeApiObj: {
            //tree接口对象
            topLevel: async () => {
                //顶级（一级）tree接口
                const res = await getCategoryList();
                let data = res.data;
                data.forEach((item) => {
                    item.isLeaf = true;
                    item.title_icon = 'ri-store-2-line';
                });
                return data;
            },
            childLevel: {}
        },
        //弹窗配置
        dialogConfig: {
            show: false,
            title: '',
            onOkLoading: true,
            type: '',
            showFooter: true,
            columns: [] as any[],
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {
                    if (newConfig.value.type == 'addApp') {
                        const ids = [] as any;
                        appSelectedVal.value.forEach((element) => {
                            ids.push(element.id);
                        });
                        let result = await saveAppCategoryOrder(ids.toString(), currData.value.code);
                        if (result.success) {
                            refreshData();
                        }
                        ElNotification({
                            title: result.success ? '成功' : '失败',
                            message: result.msg,
                            type: result.success ? 'success' : 'error',
                            duration: 2000,
                            offset: 80
                        });
                        resolve();
                    } else if (newConfig.value.type == 'sort') {
                        let tableData = sortRef.value.tableConfig.tableData;
                        const ids = [] as any;
                        tableData.forEach((element) => {
                            ids.push(element.id);
                        });
                        let result = await updateAppCategoryOrder(ids.toString());
                        if (result.success) {
                            refreshData();
                        }
                        ElNotification({
                            title: result.success ? '成功' : '失败',
                            message: result.msg,
                            type: result.success ? 'success' : 'error',
                            duration: 2000,
                            offset: 80
                        });
                        resolve();
                    }
                });
            },
            visibleChange: (visible) => {
                // console.log('visible',visible)
            }
        },
        y9ListRef: '', //气泡框的列表实例
        addOrgFormRef: '',
        personListRef: '',
        sortRef: '',
        // 用户组 选择框选择的 数据
        tableCurrSelectedVal: [] as any[],
        // 用户组 表格的 配置条件
        iconTableConfig: {
            openAutoComputedHeight: true,
            columns: [
                {
                    type: 'selection',
                    width: 100,
                    align: 'center'
                },
                {
                    title: computed(() => t('排列序号')),
                    width: 135,
                    key: 'tabIndex'
                },
                {
                    title: computed(() => t('应用名称')),
                    key: 'appName'
                },
                {
                    title: computed(() => t('路径')),
                    key: 'appUrl'
                }
            ],
            tableData: [],
            pageConfig: {
                currentPage: 1, //当前页数，支持 v-model 双向绑定
                pageSize: 20, //每页显示条目个数，支持 v-model 双向绑定
                total: 0
            }
        },
        filterConfig: {
            itemList: [
                {
                    type: 'slot',
                    slotName: 'slotBtns',
                    span: 13
                }
            ]
        },
        appSelectedVal: [] as any[],
        appTableConfig: {
            columns: [
                {
                    type: 'selection',
                    width: 100,
                    align: 'center'
                },
                {
                    title: computed(() => t('应用名称')),
                    key: 'name',
                    width: 250
                },
                {
                    title: computed(() => t('路径')),
                    key: 'url',
                    align: 'left'
                }
            ],
            tableData: [],
            pageConfig: false
        },
        appFilterConfig: {
            filtersValueCallBack: (filter) => {
                query.value = filter;
            },
            itemList: [
                {
                    type: 'input',
                    value: '',
                    key: 'appName',
                    label: computed(() => t('应用名称')),
                    labelWith: '82px',
                    props: {
                        placeholder: computed(() => t('应用名称'))
                    },
                    span: settingStore.device === 'mobile' ? 24 : 6
                },
                {
                    type: 'slot',
                    slotName: 'slotSearch',
                    span: 6
                }
            ],
            showBorder: true
        }
    });

    const {
        fixedTreeRef,
        treeApiObj,
        dialogConfig,
        loading,
        tableCurrSelectedVal,
        iconTableConfig,
        filterConfig,
        appSelectedVal,
        appTableConfig,
        appFilterConfig
    } = toRefs(data);

    // 初始化列表 请求
    onMounted(() => {});

    watch(
        () => currData.value.id,
        async (new_, old_) => {
            console.log(currData.value.id);
            if (new_ && new_ !== old_) {
                myAppCategoryListInfo();
            }
        }
    );

    //点击tree的回调
    function handlerTreeClick(currTreeNode) {
        currData.value = currTreeNode;
    }

    // 列表初始化
    async function myAppCategoryListInfo() {
        let result = await pageOrderLists({
            categoryId: currData.value.code,
            page: iconTableConfig.value.pageConfig.currentPage,
            size: iconTableConfig.value.pageConfig.pageSize
        });
        // 赋值
        iconTableConfig.value.tableData = result.rows;
        iconTableConfig.value.pageConfig.total = result.total;
    }

    // 表格 分页 函数
    function onCurrPageChangeAppCategory(currPage) {
        iconTableConfig.value.pageConfig.currentPage = currPage;
        myAppCategoryListInfo();
    }

    function onPageSizeChangeAppCategory(pageSize) {
        iconTableConfig.value.pageConfig.pageSize = pageSize;
        iconTableConfig.value.pageConfig.currentPage = 1;
        myAppCategoryListInfo();
    }

    function refreshData() {
        myAppCategoryListInfo();
    }

    function handlerAdd() {
        listApp();
        dialogConfig.value.title = '选择应用';
        dialogConfig.value.show = true;
        dialogConfig.value.type = 'addApp';
    }

    async function listApp() {
        let result = await listAllAppByTenantId();
        // 赋值
        appTableConfig.value.tableData = result.data;
    }

    const search = async () => {
        appTableConfig.value.tableData = [];
        let result = await searchAppList(query.value.appName);
        appTableConfig.value.tableData = result.data;
    };

    const reset = async () => {
        appTableConfig.value.tableData = [];
        listApp();
    };

    function handlerSort() {
        dialogConfig.value.title = '应用排序';
        dialogConfig.value.show = true;
        dialogConfig.value.type = 'sort';
        dialogConfig.value.showFooter = true;
        dialogConfig.value.columns = [
            {
                type: 'index',
                title: '序号',
                width: 100
            },
            {
                title: '名称',
                key: 'appName'
            },
            {
                title: '路径',
                key: 'appUrl'
            }
        ];
    }

    async function handlerDelete() {
        if (!tableCurrSelectedVal.value.length) {
            // 没有选择用户组
            ElMessage({
                message: '请先勾选一个应用',
                type: 'warning',
                offset: 60
            });
            return;
        }
        ElMessageBox.confirm(`${t('是否删除所选数据')}?`, t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info'
        })
            .then(async () => {
                loading.value = true;
                const ids = [] as any;
                tableCurrSelectedVal.value.forEach((element) => {
                    ids.push(element.id);
                });
                deleteIcon(ids.toString()).then((res) => {
                    loading.value = false;
                    if (res.success) {
                        ElMessage({ type: 'success', message: res.msg, offset: 65 });
                        refreshData();
                    } else {
                        ElMessage({ type: 'error', message: res.msg, offset: 65 });
                    }
                });
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: t('已取消删除'),
                    offset: 65
                });
            });
    }
</script>
<style lang="scss" scoped>
    .icon-list .right-container {
        width: 100% !important;
    }

    .personal {
        margin-top: 60px;
        // tab图标 与 文字的间隔
        .custom-tabs-label {
            i {
                margin-right: 2px;
                vertical-align: middle;
            }
        }

        :deep(.el-tabs) {
            .el-tabs__content {
                overflow: auto;
                height: calc(100vh - 303px);
            }

            .el-tabs__nav-wrap::after {
                height: 1px;
            }
        }

        :deep(.el-form) {
            margin: 20px 80px;

            .el-form-item {
                margin: 9px 0;
            }

            .el-form-item__label {
                padding: 0 20px 0 0;
            }

            .el-divider--horizontal {
                margin: 24px -14%;
            }

            .el-descriptions__label.el-descriptions__cell.is-bordered-label {
                text-align: center;
                font-weight: 800;
                width: 28%;
            }
        }

        .icon-list {
            display: block;
        }
    }

    :deep(.fixed-herder-horizontal) {
        margin-top: 130px;
        height: calc(100vh - 60px - 60px - 95px - 90px);

        .y9-card {
            height: calc(100vh - 60px - 60px - 95px - 90px);
        }
    }

    :deep(.el-card) {
        border-radius: 5px;
    }

    // el-card 阴影效果
    :deep(.el-card.is-always-shadow) {
        box-shadow: 2px 2px 2px 1px rgb(0 0 0 / 6%);
    }
</style>
