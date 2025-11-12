<template>
    <y9Card :title="`${$t('修改日志')}${currInfo.name ? ' - ' + currInfo.name : ''}`">
        <y9Table
            :config="logTableConfig"
            @on-curr-page-change="handlerPageChange"
            @on-page-size-change="handlerSizeChange"
        >
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
</template>

<script lang="ts" setup>
    import { $deepAssignObject } from '@/utils/object';
    import { computed, onMounted, reactive, toRefs, watch } from 'vue';
    import { useI18n } from 'vue-i18n';
    import { pageAuditLog } from '@/api/auditLog';

    const { t } = useI18n();

    const props = defineProps({
        currTreeNodeInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            }
        }
    });

    const data = reactive({
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        //表格配置
        logTableConfig: {
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
            emptytext: '暂无修改信息',
            pageConfig: {
                currentPage: 1, //当前页数，支持 v-model 双向绑定
                pageSize: 10, //每页显示条目个数，支持 v-model 双向绑定
                total: 0 //总条目数
            }
        }
    });

    let { currInfo, logTableConfig } = toRefs(data);

    watch(
        () => props.currTreeNodeInfo,
        (newVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal);
            getLogList();
        },
        {
            deep: true
        }
    );

    async function getLogList() {
        let queryParams = { objectId: currInfo.value.id};
        let res = await pageAuditLog(
            queryParams,
            logTableConfig.value.pageConfig.currentPage,
            logTableConfig.value.pageConfig.pageSize
        );
        logTableConfig.value.tableData = res.rows || [];
        logTableConfig.value.pageConfig.total = res.total || 0;
        logTableConfig.value.pageConfig.currentPage = res.currPage || 1;

        fillObjectDiff();
    }

    function fillObjectDiff() {
        let tableData = logTableConfig.value.tableData;
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

    //当前页改变时触发
    function handlerPageChange(currPage) {
        logTableConfig.value.pageConfig.currentPage = currPage;
        getLogList();
    }

    //每页条数改变时触发
    function handlerSizeChange(pageSize) {
        logTableConfig.value.pageConfig.pageSize = pageSize;
        getLogList();
    }

    onMounted(() => {
        // getLogList();
    });
</script>

<style lang="scss" scoped></style>
