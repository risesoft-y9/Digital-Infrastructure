<!--
 * @Descripttion: 子域三员管理
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-06-28 10:03:00
 * @LastEditors: mengjuhua
 * @LastEditTime: 2023-08-03 10:38:21
-->

<template>
    <fixedTreeModule :treeApiObj="treeApiObj" :showNodeDelete="false" @onTreeClick="onTreeClick">
        <template #rightContainer v-if="Object.keys(currTreeNodeInfo).length > 0">
            <addDomain v-if="currTreeNodeInfo.orgType == 'Department'" :currTreeNodeInfo="currTreeNodeInfo"></addDomain>
            <reminder v-else :currTreeNodeInfo="currTreeNodeInfo"></reminder>
        </template>
    </fixedTreeModule>
</template>

<script lang="ts" setup>
    import addDomain from './comps/addDomain.vue';
    import reminder from './comps/reminder.vue';
    import { treeInterface, getTreeItemById, searchByName } from '@/api/org/index';
    import { reactive, toRefs } from 'vue';
    //数据
    const data = reactive({
        treeApiObj: {
            //tree接口对象
            topLevel: treeInterface,
            childLevel: {
                api: getTreeItemById,
                params: { treeType: 'tree_type_dept', disabled: false },
            },
            search: {
                api: searchByName,
                params: {
                    treeType: 'tree_type_dept',
                },
            },
        },
        currTreeNodeInfo: {} as any, //当前tree节点的信息
    });

    const { treeApiObj, currTreeNodeInfo } = toRefs(data);

    //点击tree的回调
    function onTreeClick(currTreeNode) {
        currTreeNodeInfo.value = currTreeNode;
    }
</script>

<style scoped lang="scss"></style>
