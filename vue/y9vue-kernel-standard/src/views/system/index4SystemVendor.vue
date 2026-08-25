<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2025-12-24 10:42:56
 * @Description: 应用系统管理
-->
<template>
    <fixedTreeModule
        ref="fixedTreeRef"
        :hiddenSearch="true"
        :treeApiObj="treeApiObj"
        :showNodeDelete="false"
        nodeLabel="cnName"
        @onTreeClick="handlerTreeClick"
    >
        <template v-slot:rightContainer>
            <div v-if="currData.id">
                <BasicInfo :currTreeNodeInfo="currData" />

                <y9Card
                    v-show="currData.manageable"
                    :title="`${$t('应用管理')} - ${currData.cnName ? currData.cnName : ''}`"
                >
                    <template v-slot>
                        <AppList :id="currData.id" />
                    </template>
                </y9Card>

                <audit-log v-show="currData.manageable" :currTreeNodeInfo="currData"></audit-log>
            </div>
        </template>
    </fixedTreeModule>
    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { inject, ref } from 'vue';
    import { useI18n } from 'vue-i18n';

    import BasicInfo from '@/views/system/comps/BasicInfo.vue';
    import AppList from './comps/AppList.vue';
    import auditLog from '@/views/y9log/entityAuditLog/index.vue';
    import { systemList } from '@/api/system/index';

    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    // loading
    let loading = ref(false);

    // 点击树节点 对应数据的载体
    let currData = ref({} as any);

    // 点击树  拿到对应数据
    function handlerTreeClick(currTreeNode) {
        currData.value = currTreeNode;
    }

    // 左边树 ref
    const fixedTreeRef = ref();
    // 树的一级 子级, 搜索的请求接口函数
    const treeApiObj = ref({
        topLevel: async () => {
            let data = [];
            const res = await systemList();
            data = res.data;
            data.forEach((item) => {
                item.isLeaf = true;
            });
            return data;
        }
        // childLevel:{//子级（二级及二级以上）tree接口
        //     api:applicationList,
        //     params:{
        //         systemId: currData.value.id
        //     }
        // },
        // search: {
        //     //搜索接口及参数
        //     api: systemList,
        //     params: {},
        // },
    });
</script>
<style lang="scss" scoped>
    :deep(.custom-right) {
        display: flex;
        align-items: center;
    }

    .basic-btns {
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        margin-bottom: 20px;
    }
</style>
