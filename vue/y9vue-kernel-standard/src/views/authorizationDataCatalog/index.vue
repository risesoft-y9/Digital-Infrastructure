<!--
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-06-16 10:16:08
 * @LastEditors: mengjuhua
 * @LastEditTime: 2025-12-24 09:28:09
 * @Descripttion: 数据目录管理/数据目录授权
-->
<template>
    <div>
        <fixedTreeModule
            ref="fixedTreeRef"
            :treeApiObj="treeApiObj"
            hiddenSearch="true"
            nodeLabel="newName"
            @onTreeClick="handlerTreeClick"
        >
            <template v-slot:treeHeaderRight>
                <el-select v-model="currentTreeType" class="expand-select" @change="treeTypeChange">
                    <el-option v-for="item in treeTypeList" :key="item.code" :label="item.name" :value="item.code" />
                </el-select>
            </template>
            <template v-slot:rightContainer>
                <!-- 右边卡片 -->
                <div v-if="currData.id">
                    <y9Card :title="`${$t('基本信息')} - ${currData.name ? currData.name : ''}`">
                        <template v-slot>
                            <BasicInfo :id="currData.id" :type="currData.nodeType" />
                        </template>
                    </y9Card>

                    <!-- 角色关联 -->
                    <y9Card
                        v-if="currData.nodeType === 'DATA_CATALOG'"
                        :title="`${$t('角色关联授权')} - ${currData.name ? currData.name : ''}`"
                    >
                        <template v-slot>
                            <RelationRole :id="currData.id" :appId="currData.appId" />
                        </template>
                    </y9Card>

                    <!-- 角色关联授权继承 -->
                    <y9Card
                        v-if="currData.nodeType === 'DATA_CATALOG' && currData.parentId"
                        :title="`${$t('角色关联授权继承')} - ${currData.name ? currData.name : ''}`"
                    >
                        <template v-slot>
                            <InheritRole :id="currData.id" />
                        </template>
                    </y9Card>

                    <!-- 组织关联 -->
                    <y9Card
                        v-if="currData.nodeType === 'DATA_CATALOG'"
                        :title="`${$t('组织关联授权')} - ${currData.name ? currData.name : ''}`"
                    >
                        <RelationOrg :id="currData.id" />
                    </y9Card>

                    <!-- 组织关联授权继承 -->
                    <y9Card
                        v-if="currData.nodeType === 'DATA_CATALOG' && currData.parentId"
                        :title="`${$t('组织关联授权继承')} - ${currData.name ? currData.name : ''}`"
                    >
                        <InheritOrg :id="currData.id" />
                    </y9Card>
                </div>
            </template>
        </fixedTreeModule>

        <!-- 制造loading效果 -->
        <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
    </div>
</template>

<script lang="ts" setup>
    import { inject, onMounted, ref } from 'vue';
    import { dataCatalogTree, dataCatalogTreeSearch, getTreeTypeList } from '@/api/dataCatalog';
    import { useI18n } from 'vue-i18n';

    // 基本信息
    import BasicInfo from '@/views/authorization/comps/BasicInfo.vue';
    import RelationRole from '@/views/authorization/comps/RelationRole.vue';
    import RelationOrg from '@/views/authorization/comps/RelationOrg.vue';
    import InheritRole from '@/views/authorization/comps/InheritRole.vue';
    import InheritOrg from '@/views/authorization/comps/InheritOrg.vue';
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');

    const { t } = useI18n();

    // loading
    let loading = ref(false);

    // 点击树节点 对应数据的载体
    let currData: any = ref({ id: null });

    let currentTreeType = ref(null);

    let treeTypeList = ref([] as any);

    // 节点的 基本信息 获取
    function handlerTreeClick(data) {
        // 将拿到的节点信息 储存起来
        currData.value = data;
    }

    // 树 ref
    const fixedTreeRef = ref();
    // 树的一级 子级的请求接口函数
    const treeApiObj = ref({
        topLevel: async () => {
            await initTreeTypeList();
            return dataCatalogTree({ treeType: currentTreeType.value });
        }, //顶级（一级）tree接口,
        childLevel: {
            //子级（二级及二级以上）tree接口
            api: dataCatalogTree,
            params: {
                treeType: currentTreeType.value
            }
        },
        search: {
            //搜索接口及参数
            api: dataCatalogTreeSearch,
            params: {}
        }
    });

    async function initTreeTypeList() {
        if (!currentTreeType.value) {
            let result = await getTreeTypeList();
            treeTypeList.value = result.data;
            currentTreeType.value = result.data[0].code;
        }
    }

    onMounted(() => {
        // initTreeTypeList();
    });

    function treeTypeChange(value) {
        currentTreeType.value = value;
        currData.value = { id: null };
        fixedTreeRef.value.onRefreshTree();
    }
</script>
<style lang="scss" scoped>
    :deep(.custom-right) {
        display: flex;
        flex-wrap: wrap;
        max-width: 70%;
        min-width: 40%;

        .expand-select {
            width: 150px;
            margin-right: 20px;

            .el-select__wrapper {
                font-size: v-bind('fontSizeObj.baseFontSize');
            }
        }
    }
</style>
