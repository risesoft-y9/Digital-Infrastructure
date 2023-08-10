<!--
 * @Author: mengjuhua
 * @Date: 2023-08-02 16:04:02
 * @LastEditors: mengjuhua
 * @Description: 
-->
<template>
    <!-- 历史上传详情 -->
    <y9Table
        :config="tableConfig"
        @on-curr-page-change="onCurrPageChange"
        @on-page-size-change="onPageSizeChange"
    ></y9Table>
</template>

<script lang="ts" setup>
    import { reactive, computed, h, toRefs } from 'vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';
    const { t } = useI18n();
    const settingStore = useSettingStore();
    const data = reactive({
        tableConfig: {
            //表格配置
            columns: [
                {
                    title: computed(() => t('文件名称')),
                    key: 'name',
                },
                {
                    title: computed(() => t('文件大小')),
                    key: 'size',
                },
                {
                    title: computed(() => t('上传人')),
                    key: 'userName',
                },
                {
                    title: computed(() => t('时间')),
                    key: 'time',
                    width: settingStore.getDatetimeSpan,
                },
                {
                    title: computed(() => t('是否出错')),
                    key: 'isError',
                },
            ],
            tableData: [],
            pageConfig: {
                currentPage: 1, //当前页数，支持 v-model 双向绑定
                pageSize: 5, //每页显示条目个数，支持 v-model 双向绑定
                total: 0, //总条目数
            },
        },
    });

    let { tableConfig } = toRefs(data);

    //当前页改变时触发
    function onCurrPageChange(currPage) {
        tableConfig.value.pageConfig.currentPage = currPage;
        // 请求列表
    }
    //每页条数改变时触发
    function onPageSizeChange(pageSize) {
        tableConfig.value.pageConfig.pageSize = pageSize;
        // 请求列表
    }

    // 应用 请求 列表 初始函数
    async function getHistoryList() {
        // 请求接口
        // 赋值
        // tableConfig.value.tableData = result.rows;
        // tableConfig.value.pageConfig.total = result.total;
    }
</script>

<style></style>
