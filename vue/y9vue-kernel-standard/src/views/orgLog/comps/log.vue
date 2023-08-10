<template>
    <y9Card :title="`${$t('操作日志信息')}${currInfo.name ? ' - ' + currInfo.name : ''}`">
        <y9Table :config="logTableConfig"></y9Table>
    </y9Card>
</template>

<script lang="ts" setup>
    import { $deepAssignObject } from '@/utils/object';
    import { getShadowTitles, getShadowRows } from '@/api/org/index';
    import { onMounted, reactive, ref, toRefs, watch } from 'vue';
    const props = defineProps({
        currTreeNodeInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            },
        },
    });

    const data = reactive({
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        //表格配置
        logTableConfig: {
            columns: [{}],
            tableData: [],
            emptytext: '暂无修改信息',
            pageConfig: false, //取消分页
        },
    });

    let { currInfo, logTableConfig } = toRefs(data);

    watch(
        () => props.currTreeNodeInfo,
        (newVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal);
            getLogList();
        },
        {
            deep: true,
        }
    );
    async function getLogList() {
        let entityClass = '';
        if (currInfo.value.orgType != undefined) {
            switch (currInfo.value.orgType) {
                case 'Organization':
                    entityClass = 'net.risesoft.entity.Y9Organization';
                    break;
                case 'Department':
                    entityClass = 'net.risesoft.entity.Y9Department';
                    break;
                case 'Group':
                    entityClass = 'net.risesoft.entity.Y9Group';
                    break;
                case 'Position':
                    entityClass = 'net.risesoft.entity.Y9Position';
                    break;
                case 'Person':
                    entityClass = 'net.risesoft.entity.Y9Person';
                    break;
                case 'Person':
                    entityClass = 'net.risesoft.y9public.entity.role.Y9Role';
                    break;
            }

            getShadowTitles(currInfo.value.id, entityClass)
                .then((res) => {
                    let column = res.data;
                    let data = [
                        {
                            title: '操作信息',
                            key: 'commitAuthor',
                            minWidth: 400,
                        },
                    ];
                    for (let key in column) {
                        data.push({
                            title: column[key],
                            key: column[key],
                            minWidth: 200,
                        });
                    }
                    logTableConfig.value.columns = data;
                    getShadowRows(currInfo.value.id, entityClass)
                        .then((res) => {
                            logTableConfig.value.tableData = res.data;
                        })
                        .catch(() => {
                            logTableConfig.value.tableData = [];
                        });
                })
                .catch(() => {
                    logTableConfig.value.columns = [];
                });
        }
    }
    onMounted(() => {
        // getLogList();
    });
</script>

<style lang="scss" scoped></style>
