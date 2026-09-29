<!--
 * @Author: fuyu
 * @Date: 2022-06-06 11:47:27
 * @LastEditors: mengjuhua
 * @LastEditTime: 2025-12-24 09:22:22
 * @Description: 应用资源授权
-->
<template>
    <div>
        <fixedTreeModule
            ref="fixedTreeRef"
            :showNodeDelete="false"
            :treeApiObj="treeApiObj"
            @onTreeClick="handlerTreeClick"
        >
            <template v-if="currData.id" v-slot:rightContainer>
                <!-- 右边卡片 -->
                <div v-if="currData.nodeType === 'SYSTEM'">
                    <y9Card :title="`${$t('基本信息')} - ${currData.name ? currData.name : ''}`">
                        <template v-slot>
                            <BasicInfo :id="currData.id" :type="currData.nodeType" />
                        </template>
                    </y9Card>
                </div>
                <div v-else>
                    <y9Card :title="`${$t('基本信息')} - ${currData.name ? currData.name : ''}`">
                        <template v-slot>
                            <BasicInfo :id="currData.id" :type="currData.nodeType" />
                        </template>
                    </y9Card>
                    <!-- 角色关联 -->
                    <y9Card :title="`${$t('角色关联授权')} - ${currData.name ? currData.name : ''}`">
                        <template v-slot>
                            <RelationRole :id="currData.id" :appId="currData.appId" />
                        </template>
                    </y9Card>
                    <!-- 角色关联授权继承 -->
                    <y9Card
                        v-if="currData.nodeType !== 'APP' && currData.inherit"
                        :title="`${$t('角色关联授权继承')} - ${currData.name ? currData.name : ''}`"
                    >
                        <template v-slot>
                            <InheritRole :id="currData.id" />
                        </template>
                    </y9Card>
                    <!-- 组织关联 -->
                    <y9Card :title="`${$t('组织关联授权')} - ${currData.name ? currData.name : ''}`">
                        <RelationOrg :id="currData.id" />
                    </y9Card>
                    <!-- 组织关联授权继承 -->
                    <y9Card
                        v-if="currData.nodeType !== 'APP' && currData.inherit"
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
    import { reactive, ref, toRefs } from 'vue';
    import { useI18n } from 'vue-i18n';
    import { resourceTree, treeSearch } from '@/api/resource/index';
    // 基本信息
    import BasicInfo from '@/views/authorization/comps/BasicInfo.vue';
    // 角色 关联
    import RelationRole from '@/views/authorization/comps/RelationRole.vue';
    // 组织 关联
    import RelationOrg from '@/views/authorization/comps/RelationOrg.vue';
    import InheritRole from '@/views/authorization/comps/InheritRole.vue';
    import InheritOrg from '@/views/authorization/comps/InheritOrg.vue';

    const { t } = useI18n();

    // 单独变量
    // 点击树节点 对应数据的载体
    let currData = ref({} as any);
    // 树 ref
    let fixedTreeRef = ref();
    // 变量 对象
    let state = reactive({
        // loading
        loading: false,
        // 控制 基本信息 编辑按钮 与 保存，取消按钮的显示与隐藏
        editBtnFlag: true,
        // 保存 按钮的loading 控制
        saveBtnLoading: false,
        // 点击保存按钮 的 flag
        saveBtnClick: false,

        // 树的一级 子级的请求接口函数
        treeApiObj: {
            topLevel: resourceTree, //一级接口
            childLevel: {
                //子级（二级及二级以上）tree接口
                api: resourceTree,
                params: {}
            },
            search: {
                //搜索接口及参数
                api: treeSearch,
                params: {}
            }
        }
    });

    let { editBtnFlag, saveBtnLoading, saveBtnClick, treeApiObj, loading } = toRefs(state);

    // 树节点的 基本信息 获取
    function handlerTreeClick(data) {
        // 将拿到的节点信息 储存起来
        currData.value = data;
    }
</script>
<style lang="scss" scoped>
    // .btn-class {
    //     // display: flex;
    //     // justify-content: space-between;
    // }
</style>
