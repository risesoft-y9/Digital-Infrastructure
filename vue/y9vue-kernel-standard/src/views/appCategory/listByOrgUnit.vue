<template>
    <div class="icon-list">
        <fixedTreeModule
            ref="positionfixedTreeRef"
            :hiddenSearch="true"
            :showNodeDelete="false"
            :treeApiObj="positionTreeApi"
            @onTreeClick="handlerPositionTreeClick"
        >
            <template #rightContainer>
                <y9Card :title="`${currData.name ? currData.name : ''}`">
                    <template v-if="currData.nodeType === 'Position'">
                        <y9Table
                            :config="iconTableConfig"
                            :filterConfig="filterConfig"
                            @window-height-change="windowHeightChange"
                        >
                            <template v-slot:slotsync>
                                <el-button class="global-btn-main" type="primary" @click="syncPositionIcon()">
                                    <i class="ri-refresh-line"></i>
                                    {{ $t('图标') }}
                                </el-button>
                            </template>

                            <template #title="{ row, column, index }">
                                <img :src="row.iconData" :title="row.name" style="width: 25px" />
                            </template>
                        </y9Table>
                    </template>
                    <template v-else>
                        <div style="height: 213px">
                            <el-button
                                class="global-btn-main"
                                style="margin-bottom: 10px"
                                type="primary"
                                @click="syncDeptIcon()"
                            >
                                <i class="ri-refresh-line"></i>
                                {{ $t('图标') }}
                            </el-button>
                            <el-alert title="请点击左侧树，选择岗位再进行操作。" type="warning" />
                        </div>
                    </template>
                </y9Card>
            </template>
        </fixedTreeModule>

        <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
    </div>
</template>
<script lang="ts" setup>
    import { computed, onMounted, reactive, ref, toRefs, watch } from 'vue';
    import { listAppByPersonId2, syncDeptIcons, syncPositionIcons } from '@/api/appCategory';
    import { getTreeItemById, searchByName, treeInterface } from '@/api/org';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    const settingStore = useSettingStore();

    //当前tree节点的信息
    let currData = ref({
        id: '',
        name: '',
        nodeType: ''
    });

    //窗口变动时触发，获取表格的高度
    function windowHeightChange(tableHeight) {
        iconTableConfig.value.height = tableHeight - 35 - 35 - 28;
        iconTableConfig.value.maxHeight = tableHeight - 35 - 35 - 28; //35 35 是y9-card-content样式中上padding、下padding的值
    }

    const data = reactive({
        positionfixedTreeRef: '', //tree实例
        loading: false,
        positionTreeApi: {
            //tree接口对象
            topLevel: treeInterface,
            childLevel: {
                api: getTreeItemById,
                params: { treeType: 'tree_type_position' }
            },
            search: {
                api: searchByName,
                params: {
                    key: '',
                    treeType: 'tree_type_position'
                }
            }
        },
        iconTableConfig: {
            openAutoComputedHeight: true,
            columns: [
                {
                    type: 'index',
                    title: '#',
                    width: 100,
                    align: 'center'
                },
                {
                    title: computed(() => t('图标')),
                    key: 'iconData',
                    width: 100,
                    slot: 'title'
                },
                {
                    title: computed(() => t('应用名称')),
                    key: 'name',
                    width: 250
                },
                {
                    title: computed(() => t('应用地址')),
                    key: 'url',
                    align: 'left'
                }
            ],
            tableData: [],
            pageConfig: false
        },
        query: { appName: '' },
        filterConfig: {
            filtersValueCallBack: (filter) => {
                query.value = filter;
            },
            itemList: [
                {
                    type: 'slot',
                    slotName: 'slotsync',
                    span: settingStore.device === 'mobile' ? 24 : 12
                },
                {
                    type: 'input',
                    value: '',
                    key: 'appName',
                    label: '',
                    labelWith: '82px',
                    props: {
                        placeholder: computed(() => t('应用名称'))
                    },
                    span: settingStore.device === 'mobile' ? 24 : 12
                }
            ],
            showBorder: true
        },
        iconList: [] as any
    });

    const { positionfixedTreeRef, positionTreeApi, loading, iconList, query, iconTableConfig, filterConfig } =
        toRefs(data);

    // 初始化列表 请求
    onMounted(() => {});

    watch(
        () => currData.value.id,
        async (new_, old_) => {
            if (new_ && new_ !== old_) {
                if (currData.value.nodeType === 'Position') {
                    getAppCategoryListInfo2();
                }
            }
        }
    );

    watch(
        () => query.value,
        (newVal) => {
            searchApp();
        },
        {
            deep: true,
            immediate: true
        }
    );

    //点击tree的回调
    function handlerPositionTreeClick(currTreeNode) {
        currData.value = currTreeNode;
    }

    async function getAppCategoryListInfo2() {
        let result = await listAppByPersonId2(currData.value.id);
        // 赋值
        iconTableConfig.value.tableData = result.data;
        iconList.value = result.data;
    }

    async function searchApp() {
        let items = [] as any;
        if (query.value.appName != null || query.value.appName != undefined) {
            iconList.value.forEach((element) => {
                if (element.name.indexOf(query.value.appName) !== -1) {
                    items.push(element);
                }
            });
            iconTableConfig.value.tableData = items;
        } else {
            iconTableConfig.value.tableData = iconList.value;
        }
    }

    async function syncDeptIcon() {
        loading.value = true;
        let result = await syncDeptIcons(currData.value.id);
        loading.value = false;
        ElNotification({
            title: result.success ? t('成功') : t('失败'),
            message: result.msg,
            type: result.success ? 'success' : 'error',
            duration: 2000,
            offset: 80
        });
    }

    async function syncPositionIcon() {
        loading.value = true;
        let result = await syncPositionIcons(currData.value.id);
        loading.value = false;
        if (result.success) {
            getAppCategoryListInfo2();
        }
        ElNotification({
            title: result.success ? t('成功') : t('失败'),
            message: result.msg,
            type: result.success ? 'success' : 'error',
            duration: 2000,
            offset: 80
        });
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
