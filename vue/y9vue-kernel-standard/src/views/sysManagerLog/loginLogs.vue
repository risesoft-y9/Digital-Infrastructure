<!--
 * @Author: your name
 * @Date: 2022-05-05 09:43:05
 * @LastEditTime: 2023-08-03 11:30:05
 * @LastEditors: mengjuhua
 * @Description: 系统管理员日志 - 登录日志 
-->
<template>
    <div class="userLog">
        <div class="content">
            <y9Card :showHeader="false">
                <y9Table
                    ref="filterRef"
                    :config="loginLogsTable"
                    @on-curr-page-change="handlerPageChange"
                    @on-page-size-change="handlerSizeChange"
                    border
                    :filterConfig="filterLogsConfig"
                    @window-height-change="windowHeightChange"
                >
                    <template v-slot:slotDate>
                        <el-form>
                            <el-form-item :label="$t('登录时间')" :size="fontSizeObj.buttonSize">
                                <el-date-picker
                                    v-model="selectedDate"
                                    type="datetimerange"
                                    :range-separator="$t('至')"
                                    :shortcuts="shortcuts"
                                    :start-placeholder="$t('开始时间')"
                                    :end-placeholder="$t('结束时间')"
                                    format="YYYY-MM-DD"
                                    value-format="YYYY-MM-DD"
                                    @change="selectdDate()"
                                    style="width: 100%; height: var(--el-input-height)"
                                ></el-date-picker>
                            </el-form-item>
                        </el-form>
                    </template>
                    <template v-slot:slotSearch>
                        <el-divider content-position="center">
                            <el-button
                                class="global-btn-main"
                                type="primary"
                                :size="fontSizeObj.buttonSize"
                                :style="{ fontSize: fontSizeObj.baseFontSize }"
                                @click="search()"
                                >{{ $t('查询') }}</el-button
                            >
                            <el-button
                                class="global-btn-second"
                                :style="{ fontSize: fontSizeObj.baseFontSize }"
                                :size="fontSizeObj.buttonSize"
                                @click="reset()"
                                >{{ $t('重置') }}</el-button
                            >
                        </el-divider>
                    </template>
                </y9Table>
            </y9Card>
        </div>
    </div>
</template>

<script lang="ts" setup>
    import { inject, ref, computed } from 'vue';
    import { searchLoginInfoList4SystemManagers } from '@/api/userLoginInfo/index';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';
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
            },
        },
        {
            text: t('最近一个月'),
            value: () => {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                return [start, end];
            },
        },
        {
            text: t('最近三个月'),
            value: () => {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                return [start, end];
            },
        },
    ];
    const selectdDate = () => {
        query.value.startTime = selectedDate.value[0];
        query.value.endTime = selectedDate.value[1];
    };
    const filterLogsConfig = ref({
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
                span: settingStore.device === 'mobile' ? 24 : 6,
            },
            {
                type: 'input',
                value: '',
                key: 'userHostIp',
                labelWidth: '82px',
                label: computed(() => t('客户端IP')),
                span: settingStore.device === 'mobile' ? 24 : 6,
            },
            {
                type: 'input',
                value: '',
                key: 'oSName',
                label: computed(() => t('操作系统')),
                labelWidth: '82px',
                span: settingStore.device === 'mobile' ? 24 : 6,
            },
            {
                type: 'input',
                value: '',
                key: 'screenResolution',
                label: computed(() => t('分辨率')),
                labelWidth: '82px',
                span: settingStore.device === 'mobile' ? 24 : 6,
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
                            value: '',
                        },
                        {
                            label: computed(() => t('成功')),
                            value: 'true',
                        },
                        {
                            label: computed(() => t('出错')),
                            value: 'false',
                        },
                    ],
                },
                span: settingStore.device === 'mobile' ? 24 : 6,
            },
            {
                type: 'input',
                value: '',
                key: 'browserName',
                label: computed(() => t('浏览器名称')),
                labelWidth: '82px',
                span: settingStore.device === 'mobile' ? 24 : 6,
            },
            {
                type: 'input',
                value: '',
                key: 'browserVersion',
                label: computed(() => t('浏览器版本')),
                labelWidth: '82px',
                span: settingStore.device === 'mobile' ? 24 : 6,
            },
            {
                type: 'slot',
                slotName: 'slotDate',
                span: settingStore.device === 'mobile' ? 24 : 6,
            },
            {
                type: 'slot',
                slotName: 'slotSearch',
                span: 24,
            },
        ],
        showBorder: true,
        // borderRadio: '4px'
    });

    const logTimeFormat = (row?, column?, cellValue?) => {
        var time = Date.parse(row.loginTime);
        if (time != null) {
            var logTime = new Date();
            logTime.setTime(time);
            var year = logTime.getFullYear();
            var month = logTime.getMonth() + 1 < 10 ? '0' + (logTime.getMonth() + 1) : logTime.getMonth() + 1;
            var date = logTime.getDate() < 10 ? '0' + logTime.getDate() : logTime.getDate();
            var hour = logTime.getHours() < 10 ? '0' + logTime.getHours() : logTime.getHours();
            var minute = logTime.getMinutes() < 10 ? '0' + logTime.getMinutes() : logTime.getMinutes();
            var second = logTime.getSeconds() < 10 ? '0' + logTime.getSeconds() : logTime.getSeconds();
            return year + '-' + month + '-' + date + ' ' + hour + ':' + minute + ':' + second;
        } else {
            return cellValue;
        }
    };

    //窗口变动时触发，获取表格的高度
    function windowHeightChange(tableHeight) {
        loginLogsTable.value.maxHeight = tableHeight - 35 - 35; //35 35 是y9-card-content样式中上padding、下padding的值
    }

    // 表格 配置
    let loginLogsTable = ref({
        columns: [
            { title: computed(() => t('序号')), showOverflowTooltip: false, type: 'index', width: 80 },
            { title: computed(() => t('用户名称')), key: 'userName', width: 130 },
            { title: computed(() => t('登录名称')), key: 'userLoginName', width: 150 },
            { title: computed(() => t('客户端IP')), key: 'userHostIp', width: 150 },
            { title: computed(() => t('服务器IP')), key: 'serverIp', width: 150 },
            { title: computed(() => t('操作系统')), key: 'osName', width: 130 },
            { title: computed(() => t('分辨率')), key: 'screenResolution', width: 120 },
            {
                title: computed(() => t('操作状态')),
                key: 'success',
                width: 90,
                render: (row) => {
                    return row.success == 'true' ? '成功' : '失败';
                },
            },
            { title: computed(() => t('浏览器名称')), key: 'browserName', width: 120 },
            { title: computed(() => t('浏览器版本')), key: 'browserVersion', width: 150 },
            {
                title: computed(() => t('登录时间')),
                key: 'loginTime',
                formatter: logTimeFormat,
                width: settingStore.getDatetimeSpan,
            },
            { title: computed(() => t('错误信息')), key: 'throwable' },
        ],
        tableData: [],
        pageConfig: {
            currentPage: 1, //当前页数，支持 v-model 双向绑定
            pageSize: 20, //每页显示条目个数，支持 v-model 双向绑定
            total: 0, //总条目数
        },
        loading: false,
    });

    const search = () => {
        loginLogsTable.value.tableData = [];
        loginLogsTable.value.pageConfig.currentPage = 1;
        getDataList();
    };

    const refreshTable = () => {
        loginLogsTable.value.tableData = [];
        loginLogsTable.value.pageConfig.total = 0;
        loginLogsTable.value.pageConfig.currentPage = 1;
        loginLogsTable.value.pageConfig.pageSize = 20;
        getDataList();
    };

    const reset = () => {
        query.value = {};
        filterRef.value.elTableFilterRef.onReset();
        selectedDate.value = '';
        refreshTable();
    };

    // 初始化 列表
    async function getDataList() {
        loginLogsTable.value.loading = true;
        let res = await searchLoginInfoList4SystemManagers(
            query.value,
            loginLogsTable.value.pageConfig.currentPage,
            loginLogsTable.value.pageConfig.pageSize
        );
        loginLogsTable.value.tableData = res.rows || [];
        loginLogsTable.value.pageConfig.total = res.total || 0;
        loginLogsTable.value.pageConfig.currentPage = res.currPage || 1;
        loginLogsTable.value.loading = false;
    }
    getDataList();

    //当前页改变时触发
    function handlerPageChange(currPage) {
        loginLogsTable.value.pageConfig.currentPage = currPage;
        getDataList();
    }
    //每页条数改变时触发
    function handlerSizeChange(pageSize) {
        loginLogsTable.value.pageConfig.pageSize = pageSize;
        getDataList();
    }
</script>
<style lang="scss" scoped>
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
