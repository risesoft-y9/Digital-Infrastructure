<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2023-12-26 11:25:01
 * @Description: 组织操作日志
-->
<template>
    <fixedTreeModule :treeApiObj="treeApiObj" nodeLabel="newName" @onTreeClick="onTreeClick">
        <template #rightContainer>
            <template v-if="Object.keys(currTreeNodeInfo).length > 0">
                <Log :currTreeNodeInfo="currTreeNodeInfo"></Log>
            </template>
        </template>
    </fixedTreeModule>

    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import Log from './comps/log.vue';
    import { getTreeItemById, searchByName, treeInterface } from '@/api/org/index';
    import { checkDeptManager } from '@/api/deptManager/index';
    import { reactive, toRefs } from 'vue';

    const { t } = useI18n();

    //数据
    const data = reactive({
        loading: false,
        treeApiObj: {
            //tree接口对象
            topLevel: treeInterface,
            childLevel: {
                api: getTreeItemById,
                params: { treeType: 'tree_type_org', disabled: true }
            },
            search: {
                api: searchByName,
                params: {
                    treeType: 'tree_type_org'
                }
            }
        },
        currTreeNodeInfo: { haveEditAuth: true }, //当前tree节点的信息
        logRef: ''
    });

    const { treeApiObj, currTreeNodeInfo, logRef, loading } = toRefs(data);

    //点击tree的回调
    async function onTreeClick(currTreeNode) {
        let isDeptManager = true;
        if (currTreeNode.nodeType === 'Department') {
            const result = await checkDeptManager(currTreeNode.id);

            isDeptManager = result.data;
        }

        currTreeNodeInfo.value = currTreeNode;
        currTreeNodeInfo.value.haveEditAuth = isDeptManager; //是否有编辑权限
    }
</script>

<style lang="scss" scoped></style>
