<template>
    <div class="icon-list">
        <fixedTreeModule
            ref="iconfixedTreeRef"
            :hiddenSearch="true"
            :showNodeDelete="false"
            :treeApiObj="iconTreeApi"
            @onTreeClick="handlerIconTreeClick"
        >
            <template #rightContainer>
                <y9Card :title="`${currData.name ? currData.name : ''}`">
                    <y9Table
                        :config="personTableConfig"
                        :filterConfig="filterConfig"
                        @on-curr-page-change="onCurrPageChangeAppCategory"
                        @on-page-size-change="onPageSizeChangeAppCategory"
                        @window-height-change="windowHeightChange"
                    >
                        <template v-slot:slotSearch>
                            <el-button class="global-btn-main" type="primary" @click="search()">
                                {{ $t('查询') }}
                            </el-button>
                            <el-button class="global-btn-second" @click="reset()">{{ $t('重置') }}</el-button>
                        </template>
                    </y9Table>
                </y9Card>
            </template>
        </fixedTreeModule>

        <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
    </div>
</template>
<script lang="ts" setup>
    import { computed, onMounted, reactive, ref, toRefs, watch } from 'vue';
    import { listAllAppByTenantId, pageByAppIcon } from '@/api/appCategory';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    const settingStore = useSettingStore();

    //当前tree节点的信息
    let currData = ref({
        id: '',
        name: ''
    });

    //窗口变动时触发，获取表格的高度
    function windowHeightChange(tableHeight) {
        personTableConfig.value.height = tableHeight - 35 - 35 - 28;
        personTableConfig.value.maxHeight = tableHeight - 35 - 35 - 28; //35 35 是y9-card-content样式中上padding、下padding的值
    }

    const data = reactive({
        // 选中的tab
        query: {
            personName: '',
            deptName: ''
        },
        iconfixedTreeRef: '', //tree实例
        loading: false,
        iconTreeApi: {
            //tree接口对象
            topLevel: async () => {
                //顶级（一级）tree接口
                const res = await listAllAppByTenantId();
                let data = res.data;
                data.forEach((item) => {
                    item.isLeaf = true;
                    item.title_icon = 'ri-apps-line';
                });
                return data;
            },
            childLevel: {}
        },
        personTableConfig: {
            openAutoComputedHeight: true,
            columns: [
                {
                    type: 'index',
                    width: 100,
                    align: 'center'
                },
                {
                    title: computed(() => t('名称')),
                    key: 'name',
                    width: 350
                },
                {
                    title: computed(() => t('所属部门')),
                    key: 'dn',
                    align: 'left'
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
            filtersValueCallBack: (filter) => {
                query.value = filter;
            },
            itemList: [
                {
                    type: 'input',
                    value: '',
                    key: 'personName',
                    label: computed(() => t('人员名称')),
                    labelWith: '82px',
                    props: {
                        placeholder: computed(() => t('人员名称'))
                    },
                    span: settingStore.device === 'mobile' ? 24 : 6
                },
                {
                    type: 'input',
                    value: '',
                    key: 'deptName',
                    label: computed(() => t('部门名称')),
                    labelWith: '82px',
                    props: {
                        placeholder: computed(() => t('部门名称'))
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

    const { iconfixedTreeRef, iconTreeApi, loading, personTableConfig, filterConfig, query } = toRefs(data);

    // 初始化列表 请求
    onMounted(() => {});

    watch(
        () => currData.value.id,
        async (new_, old_) => {
            if (new_ && new_ !== old_) {
                getPersonListInfo();
            }
        }
    );

    //点击tree的回调
    function handlerIconTreeClick(currTreeNode) {
        currData.value = currTreeNode;
    }

    // 用户组 列表初始化
    async function getPersonListInfo() {
        let result = await pageByAppIcon({
            appId: currData.value.id,
            personName: query.value.personName,
            deptName: query.value.deptName,
            page: personTableConfig.value.pageConfig.currentPage,
            size: personTableConfig.value.pageConfig.pageSize
        });
        // 赋值
        personTableConfig.value.tableData = result.rows;
        personTableConfig.value.pageConfig.total = result.total;
    }

    // 我的用户组 表格 分页 函数
    function onCurrPageChangeAppCategory(currPage) {
        personTableConfig.value.pageConfig.currentPage = currPage;
        getPersonListInfo();
    }

    function onPageSizeChangeAppCategory(pageSize) {
        personTableConfig.value.pageConfig.pageSize = pageSize;
        personTableConfig.value.pageConfig.currentPage = 1;
        getPersonListInfo();
    }

    function search() {
        getPersonListInfo();
    }

    function reset() {
        query.value = {
            personName: '',
            deptName: ''
        };
        getPersonListInfo();
    }
</script>
<style lang="scss" scoped>
    .systemName {
        margin-left: 24px;
        margin-bottom: 24px;
        text-align: center;
        float: left;
    }

    .imges {
        margin-left: auto;
        margin-right: auto;
        margin-top: 4px;
        margin-bottom: 4px;
        width: 42px;
        height: 42px;
    }
</style>
