<template>
    <y9Card :title="`${$t('修改日志')}${currInfo.name ? ' - ' + currInfo.name : ''}`">
        <y9Table :config="logTableConfig"></y9Table>
    </y9Card>
</template>

<script lang="ts" setup>
    import { $deepAssignObject } from '@/utils/object';
    import { getShadowRows, getShadowTitles } from '@/api/org';
    import { onMounted, reactive, toRefs, watch } from 'vue';

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
            columns: [{}],
            tableData: [],
            emptytext: '暂无修改信息',
            pageConfig: false //取消分页
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
        let entityClass = '';
        if (currInfo.value.nodeType != undefined) {
            switch (currInfo.value.nodeType) {
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
                case 'role':
                    entityClass = 'net.risesoft.y9public.entity.role.Y9Role';
                    break;
                case 'APP':
                    entityClass = 'net.risesoft.y9public.entity.resource.Y9App';
                    break;
                case 'MENU':
                    entityClass = 'net.risesoft.y9public.entity.resource.Y9Menu';
                    break;
                case 'OPERATION':
                    entityClass = 'net.risesoft.y9public.entity.resource.Y9Operation';
                    break;
                case 'SYSTEM':
                    entityClass = 'net.risesoft.y9public.entity.resource.Y9System';
                    break;
            }

            getShadowTitles(currInfo.value.id, entityClass)
                .then((res) => {
                    let column = res.data;
                    let data = [
                        {
                            title: '操作信息',
                            key: 'commitAuthor',
                            minWidth: 400
                        }
                    ];
                    for (let key in column) {
                        data.push({
                            title: column[key],
                            key: column[key],
                            minWidth: 200
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
