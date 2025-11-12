<template>
    <div class="userLog">
        <div class="content">
            <y9Card :showHeader="false">
                <y9Table
                    ref="filterRef"
                    :config="auditLogsTable"
                    :filterConfig="filterLogsConfig"
                    border
                    @on-curr-page-change="handlerPageChange"
                    @on-page-size-change="handlerSizeChange"
                    @window-height-change="windowHeightChange"
                >
                    <template v-slot:slotDate>
                        <el-form>
                            <el-form-item :label="$t('时间')" :size="fontSizeObj.buttonSize">
                                <el-date-picker
                                    v-model="selectedDate"
                                    :end-placeholder="$t('结束时间')"
                                    :range-separator="$t('至')"
                                    :shortcuts="shortcuts"
                                    :start-placeholder="$t('开始时间')"
                                    format="YYYY-MM-DD"
                                    style="width: 100%; height: var(--el-input-height)"
                                    type="datetimerange"
                                    value-format="YYYY-MM-DD 00:00:00"
                                    @change="selectdDate()"
                                >
                                </el-date-picker>
                            </el-form-item>
                        </el-form>
                    </template>
                    <template v-slot:slotSearch>
                        <el-divider content-position="center">
                            <el-button
                                :size="fontSizeObj.buttonSize"
                                :style="{ fontSize: fontSizeObj.baseFontSize }"
                                class="global-btn-main"
                                type="primary"
                                @click="search()"
                                >{{ $t('查询') }}
                            </el-button>
                            <el-button
                                :size="fontSizeObj.buttonSize"
                                :style="{ fontSize: fontSizeObj.baseFontSize }"
                                class="global-btn-second"
                                @click="reset()"
                                >{{ $t('重置') }}
                            </el-button>
                        </el-divider>
                    </template>
                    <template #expandRowSlot="props">
                        <div class="expand-rows">
                            <p><b>用户 id:</b> {{ props.row.userId }}</p>
                            <p><b>客户端信息:</b> {{ props.row.userAgent }}</p>
                            <p><b>操作对象 id:</b> {{ props.row.objectId }}</p>
                            <p v-if="props.row.diff"><b>修改前后对比:</b></p>
                            <el-table v-if="props.row.diff" :data="props.row.diff">
                                <el-table-column label="字段" prop="filedName" />
                                <el-table-column label="修改前" prop="oldValue" />
                                <el-table-column label="修改后" prop="currentValue" />
                            </el-table>
                        </div>
                    </template>
                </y9Table>
            </y9Card>
        </div>
    </div>
</template>

<script lang="ts" setup>
    import { computed, inject, ref } from 'vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';
    import { pageAuditLog } from '@/api/auditLog';

    const { t } = useI18n();
    const settingStore = useSettingStore();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const query: any = ref({});

    let filterRef = ref();
    const selectedDate = ref('');
    const shortcuts = [
        {
            text: t('最近一周'),
            value: () => {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                return [start, end];
            }
        },
        {
            text: t('最近一个月'),
            value: () => {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                return [start, end];
            }
        },
        {
            text: t('最近三个月'),
            value: () => {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                return [start, end];
            }
        }
    ];
    const selectdDate = () => {
        query.value.startTime = selectedDate.value[0];
        query.value.endTime = selectedDate.value[1];
    };

    const filterLogsConfig = ref({
        filtersValueCallBack: (filter) => {
            console.log('filter:', filter);
            query.value = filter;
        },
        itemList: [
            {
                type: 'input',
                value: '',
                key: 'userName',
                label: computed(() => t('用户名')),
                labelWidth: '82px',
                span: settingStore.device === 'mobile' ? 24 : 6
            },
            {
                type: 'input',
                value: '',
                key: 'userHostIp',
                labelWidth: '82px',
                label: computed(() => t('用户IP')),
                span: settingStore.device === 'mobile' ? 24 : 6
            },
            {
                type: 'input',
                value: '',
                key: 'action',
                label: computed(() => t('操作类型')),
                labelWidth: '82px',
                span: settingStore.device === 'mobile' ? 24 : 6
            },
            {
                type: 'input',
                value: '',
                key: 'description',
                label: computed(() => t('操作描述')),
                labelWidth: '82px',
                span: settingStore.device === 'mobile' ? 24 : 6
            },
            {
                type: 'slot',
                slotName: 'slotDate',
                span: settingStore.device === 'mobile' ? 24 : 6
            },
            {
                type: 'slot',
                slotName: 'slotSearch',
                span: 24
            }
        ],
        showBorder: true
        // borderRadio: '4px'
    });

    //窗口变动时触发，获取表格的高度
    function windowHeightChange(tableHeight) {
        auditLogsTable.value.maxHeight = tableHeight - 35 - 35; //35 35 是y9-card-content样式中上padding、下padding的值
    }

    // 表格 配置
    let auditLogsTable = ref({
        columns: [
            { type: 'expand', width: 40, slot: 'expandRowSlot' },
            { title: computed(() => t('序号')), showOverflowTooltip: false, type: 'index', width: 80 },
            { title: computed(() => t('用户名')), key: 'userName', width: 130 },
            { title: computed(() => t('用户IP')), key: 'userIp', width: 150 },
            { title: computed(() => t('操作时间')), key: 'createTime', width: 180 },
            { title: computed(() => t('操作类型')), key: 'action', align: 'left', width: 250 },
            { title: computed(() => t('操作描述')), key: 'description', align: 'left' }
        ],
        tableData: [],
        pageConfig: {
            currentPage: 1, //当前页数，支持 v-model 双向绑定
            pageSize: 20, //每页显示条目个数，支持 v-model 双向绑定
            total: 0 //总条目数
        },
        loading: false
    });

    async function getDataList() {
        auditLogsTable.value.loading = true;
        let res = await pageAuditLog(
            query.value,
            auditLogsTable.value.pageConfig.currentPage,
            auditLogsTable.value.pageConfig.pageSize
        );
        auditLogsTable.value.tableData = res.rows || [];
        auditLogsTable.value.pageConfig.total = res.total || 0;
        auditLogsTable.value.pageConfig.currentPage = res.currPage || 1;
        auditLogsTable.value.loading = false;

        fillObjectDiff();
    }

    function fillObjectDiff() {
        let tableData = auditLogsTable.value.tableData;
        for (let row of tableData) {
            let oldObject = JSON.parse(row.oldObjectJson);
            let currentObject = JSON.parse(row.currentObjectJson);

            if (oldObject && currentObject) {
                const diff = [];
                for (const key in currentObject) {
                    if (oldObject[key] !== currentObject[key]) {
                        diff.push({
                            filedName: key,
                            oldValue: oldObject[key],
                            currentValue: currentObject[key]
                        });
                    }
                }
                row.diff = diff;
            }
        }
    }

    getDataList();

    const refreshTable = () => {
        auditLogsTable.value.tableData = [];
        auditLogsTable.value.pageConfig.total = 0;
        auditLogsTable.value.pageConfig.currentPage = 1;
        auditLogsTable.value.pageConfig.pageSize = 20;
        getDataList();
    };

    const search = () => {
        auditLogsTable.value.tableData = [];
        auditLogsTable.value.pageConfig.currentPage = 1;
        getDataList();
    };

    const reset = () => {
        query.value = {};
        filterRef.value.elTableFilterRef.onReset();
        selectedDate.value = '';
        refreshTable();
    };

    //当前页改变时触发
    function handlerPageChange(currPage) {
        auditLogsTable.value.pageConfig.currentPage = currPage;
        getDataList();
    }

    //每页条数改变时触发
    function handlerSizeChange(pageSize) {
        auditLogsTable.value.pageConfig.pageSize = pageSize;
        getDataList();
    }
</script>
<style lang="scss" scoped>
    :deep(.el-form-item__label) {
        text-align: justify !important;
    }

    :deep(.el-divider__text) {
        display: flex;
    }

    .userLog .content {
        width: auto;
        margin: 0 auto;

        .pagination-style {
            display: flex;
            justify-content: v-bind("settingStore.device === 'mobile'? 'flex-start':'flex-end'");
            margin-top: 20px;
            overflow: auto;
            scrollbar-width: none;
        }

        :deep(.y9-card-content) {
            padding: 35px 20px;

            .el-divider--horizontal {
                margin: 20px 0 30px 0;
            }
        }
    }

    :deep(.el-form-item__label) {
        width: 90px;
        display: flex;
        justify-content: flex-start;
        font-size: v-bind('fontSizeObj.baseFontSize');
        align-items: center;
        line-height: 18px;
    }

    :deep(.el-date-range-picker__time-header) {
        display: none;
    }

    :deep(.el-form-item__content) {
        align-items: stretch;
    }

    :deep(.el-form-item) {
        margin: 0;
    }

    :deep(.el-date-editor) {
        i,
        input {
            font-size: v-bind('fontSizeObj.baseFontSize');
        }
    }

    .expand-rows {
        padding-left: 20px;
    }
</style>
