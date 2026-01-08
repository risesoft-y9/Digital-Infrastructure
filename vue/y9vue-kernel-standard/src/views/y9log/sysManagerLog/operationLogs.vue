<!--
 * @Author: your name
 * @Date: 2022-05-05 09:43:05
 * @LastEditTime: 2024-01-05 10:39:25
 * @LastEditors: mengjuhua
 * @Description: 系统管理员日志 - 操作日志  
-->
<template>
    <div class="userLog">
        <div class="content">
            <y9Card :showHeader="false">
                <y9Table
                    ref="filterRef"
                    :config="operationLogsTable"
                    :filterConfig="filterOperaConfig"
                    border
                    @on-curr-page-change="handlerPageChange"
                    @on-page-size-change="handlerSizeChange"
                    @window-height-change="windowHeightChange"
                >
                    <template v-slot:slotDate>
                        <el-form>
                            <el-form-item :label="$t('操作时间')" :size="fontSizeObj.buttonSize">
                                <el-date-picker
                                    v-model="selectedDate"
                                    :end-placeholder="$t('结束时间')"
                                    :range-separator="$t('至')"
                                    :shortcuts="shortcuts"
                                    :start-placeholder="$t('开始时间')"
                                    format="YYYY-MM-DD"
                                    style="width: 100%; height: var(--el-input-height)"
                                    type="daterange"
                                    value-format="YYYY-MM-DD"
                                    @change="selectdDate()"
                                ></el-date-picker>
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
                </y9Table>
            </y9Card>
        </div>
    </div>
</template>

<script lang="ts" setup>
    import { computed, inject, ref } from 'vue';
    import { searchLogInfoList4SystemManagers } from '@/api/log';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';
    import moment from 'moment';

    const { t } = useI18n();
    const settingStore = useSettingStore();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const query: any = ref({});

    const filterRef = ref();

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

    const filterOperaConfig = ref({
        filtersValueCallBack: (filter) => {
            query.value = filter;
        },
        itemList: [
            {
                type: 'input',
                value: '',
                key: 'userName',
                label: computed(() => t('用户名称')),
                labelWidth: '82px',
                span: settingStore.device === 'mobile' ? 24 : 6
            },
            {
                type: 'input',
                value: '',
                key: 'userHostIp',
                labelWidth: '82px',
                label: computed(() => t('客户端IP')),
                span: settingStore.device === 'mobile' ? 24 : 6
            },
            {
                type: 'input',
                value: '',
                key: 'systemName',
                label: computed(() => t('系统名称')),
                labelWidth: '82px',
                span: settingStore.device === 'mobile' ? 24 : 6
            },
            {
                type: 'input',
                value: '',
                key: 'operateName',
                label: computed(() => t('操作名称')),
                labelWidth: '82px',
                span: settingStore.device === 'mobile' ? 24 : 6
            },
            {
                type: 'select',
                value: '',
                key: 'operateType',
                label: computed(() => t('操作类型')),
                labelWidth: '82px',
                props: {
                    options: [
                        //选项列表
                        {
                            label: computed(() => t('全部')),
                            value: ''
                        },
                        {
                            label: computed(() => t('使用')),
                            value: '使用'
                        },
                        {
                            label: computed(() => t('登录')),
                            value: '登录'
                        },
                        {
                            label: computed(() => t('退出')),
                            value: '退出'
                        },
                        {
                            label: computed(() => t('查看')),
                            value: '查看'
                        },
                        {
                            label: computed(() => t('增加')),
                            value: '增加'
                        },
                        {
                            label: computed(() => t('修改')),
                            value: '修改'
                        },
                        {
                            label: computed(() => t('删除')),
                            value: '删除'
                        },
                        {
                            label: computed(() => t('发送')),
                            value: '发送'
                        },
                        {
                            label: computed(() => t('活动')),
                            value: '活动'
                        },
                        {
                            label: computed(() => t('检查')),
                            value: '检查'
                        }
                    ]
                },
                span: settingStore.device === 'mobile' ? 24 : 6
            },
            {
                type: 'select',
                value: '',
                key: 'success',
                label: computed(() => t('操作状态')),
                labelWidth: '82px',
                props: {
                    options: [
                        //选项列表
                        {
                            label: computed(() => t('全部')),
                            value: ''
                        },
                        {
                            label: computed(() => t('成功')),
                            value: '成功'
                        },
                        {
                            label: computed(() => t('出错')),
                            value: '出错'
                        }
                    ]
                },
                span: settingStore.device === 'mobile' ? 24 : 6
            },
            {
                type: 'select',
                value: '',
                key: 'logLevel',
                label: computed(() => t('日志级别')),
                labelWidth: '82px',
                props: {
                    options: [
                        //选项列表
                        {
                            label: computed(() => t('全部')),
                            value: ''
                        },
                        {
                            label: computed(() => t('错误')),
                            value: 'ERROR'
                        },
                        {
                            label: computed(() => t('警告')),
                            value: 'WARN'
                        },
                        {
                            label: computed(() => t('调试')),
                            value: 'DEBUG'
                        },
                        {
                            label: computed(() => t('跟踪')),
                            value: 'TRACE'
                        },
                        {
                            label: computed(() => t('记录日志')),
                            value: 'RSLOG'
                        },
                        {
                            label: computed(() => t('管理日志')),
                            value: 'MANAGERLOG'
                        },
                        {
                            label: computed(() => t('通知')),
                            value: 'INFO'
                        }
                    ]
                },
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

    const logTimeFormat = (row?, column?, cellValue?) => {
        var time = Date.parse(row.logTime);
        if (time != null) {
            var logTime = new Date();
            logTime.setTime(time);
            var year = logTime.getFullYear();
            var month = logTime.getMonth() + 1 < 10 ? '0' + (logTime.getMonth() + 1) : logTime.getMonth() + 1;
            var date = logTime.getDate() < 10 ? '0' + logTime.getDate() : logTime.getDate();
            var hour = logTime.getHours() < 10 ? '0' + logTime.getHours() : logTime.getHours();
            var minute = logTime.getMinutes() < 10 ? '0' + logTime.getMinutes() : logTime.getMinutes();
            var second = logTime.getSeconds() < 10 ? '0' + logTime.getSeconds() : logTime.getSeconds();
            // return year + '-' + month + '-' + date + ' ' + hour + ':' + minute + ':' + second;
            return moment(row.logTime).format('YYYY-MM-DD HH:mm:ss');
        } else {
            return cellValue;
        }
    };

    const elapsedTimeFormat = (row?, column?, cellValue?) => {
        var msec = cellValue / 1000 / 1000;
        if (msec < 1) {
            let s = msec / 1000;
            return Math.round(s * Math.pow(10, 6)) / Math.pow(10, 6) + '秒';
        } else {
            let s = msec / 1000;
            return Math.round(s * Math.pow(10, 3)) / Math.pow(10, 3) + '秒';
        }
    };

    //窗口变动时触发，获取表格的高度
    function windowHeightChange(tableHeight) {
        operationLogsTable.value.maxHeight = tableHeight - 35 - 35; //35 35 是y9-card-content样式中上padding、下padding的值
    }

    // 表格 配置
    let operationLogsTable = ref({
        columns: [
            { title: computed(() => t('序号')), showOverflowTooltip: false, type: 'index', width: 60 },
            { title: computed(() => t('用户名称')), key: 'userName', width: 130 },
            { title: computed(() => t('客户端IP')), key: 'userHostIp', width: 150 },
            { title: computed(() => t('系统名称')), key: 'systemName', width: 120 },
            { title: computed(() => t('操作方法')), key: 'methodName' },
            { title: computed(() => t('操作名称')), key: 'operateName' },
            { title: computed(() => t('操作类型')), key: 'operateType', width: 100 },
            { title: computed(() => t('操作状态')), key: 'success', width: 100 },
            {
                title: computed(() => t('日志级别')),
                key: 'logLevel',
                width: 120,
                render: (row) => {
                    switch (row.logLevel) {
                        case 'ERROR':
                            return '错误';
                        case 'WARN':
                            return '警告';
                        case 'DEBUG':
                            return '调试';
                        case 'TRACE':
                            return '跟踪';
                        case 'RSLOG':
                            return '记录日志';
                        case 'MANAGERLOG':
                            return '管理日志';
                        case 'INFO':
                            return '通知';
                    }
                }
            },
            { title: computed(() => t('操作用时')), key: 'elapsedTime', formatter: elapsedTimeFormat, width: 120 },
            {
                title: computed(() => t('操作时间')),
                key: 'logTime',
                formatter: logTimeFormat,
                width: settingStore.getDatetimeSpan
            },
            { title: computed(() => t('错误信息')), key: 'throwable' }
        ],
        tableData: [],
        pageConfig: {
            currentPage: 1, //当前页数，支持 v-model 双向绑定
            pageSize: 20, //每页显示条目个数，支持 v-model 双向绑定
            total: 0 //总条目数
        },
        loading: false
    });

    // 初始化列表
    async function getDataList() {
        operationLogsTable.value.loading = true;
        let res = await searchLogInfoList4SystemManagers(
            query.value,
            operationLogsTable.value.pageConfig.currentPage,
            operationLogsTable.value.pageConfig.pageSize
        );
        operationLogsTable.value.tableData = res.rows || [];
        operationLogsTable.value.pageConfig.total = res.total || 0;
        operationLogsTable.value.pageConfig.currentPage = res.currPage || 1;
        operationLogsTable.value.loading = false;
    }

    getDataList();

    const refreshTable = () => {
        operationLogsTable.value.tableData = [];
        operationLogsTable.value.pageConfig.total = 0;
        operationLogsTable.value.pageConfig.currentPage = 1;
        operationLogsTable.value.pageConfig.pageSize = 20;
        getDataList();
    };

    const search = () => {
        operationLogsTable.value.tableData = [];
        operationLogsTable.value.pageConfig.currentPage = 1;
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
        operationLogsTable.value.pageConfig.currentPage = currPage;
        getDataList();
    }

    //每页条数改变时触发
    function handlerSizeChange(pageSize) {
        operationLogsTable.value.pageConfig.pageSize = pageSize;
        getDataList();
    }
</script>
<style lang="scss" scoped>
    .userLog .content {
        width: auto;
        margin: 0 auto;

        .pagination-style {
            display: flex;
            justify-content: v-bind("settingStore.device === 'mobile'?'flex-start':'flex-end'");
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

    :deep(.el-divider__text) {
        display: flex;
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
</style>
