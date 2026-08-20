<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2025-12-24 10:40:52
 * @Description: 应用角色关联 + 应用角色管理
-->
<template>
    <fixedTreeModule ref="fixedTreeRef" :hiddenSearch="false" :treeApiObj="treeApiObj" @onTreeClick="handlerTreeClick">
        <template v-slot:rightContainer>
            <!-- 右边卡片 -->
            <div v-if="currData.id">
                <y9Card :title="`${$t('基本信息')} - ${currData.name ? currData.name : ''}`">
                    <template v-slot>
                        <BasicInfo
                            v-if="
                                currData.nodeType === 'folder' ||
                                currData.nodeType === 'role' ||
                                currData.nodeType === 'APP'
                            "
                            :id="currData.id"
                            :editFlag="editBtnFlag"
                            :saveClickFlag="saveBtnClick"
                            :type="currData.nodeType"
                            @getInfoData="handlerEditSave"
                        />
                        <SystemBasicInfo v-if="currData.nodeType === 'SYSTEM'" :id="currData.id" :editFlag="true" />
                    </template>
                </y9Card>

                <y9Card
                    v-if="currData.nodeType === 'role'"
                    :title="`${$t('角色成员')} - ${currData.name ? currData.name : ''}`"
                >
                    <OrgBasesToRoles :id="currData.id"></OrgBasesToRoles>
                </y9Card>

                <y9Card
                    v-if="currData.nodeType === 'role'"
                    :title="`${$t('授权列表')} - ${currData.name ? currData.name : ''}`"
                >
                    <Authorization
                        :id="currData.id"
                        :appId="currData.appId"
                        :parentId="currData.parentId"
                        type="private"
                    >
                    </Authorization>
                </y9Card>
            </div>
        </template>
    </fixedTreeModule>
    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { inject, ref } from 'vue';
    import { roleTree, saveOrUpdate, treeSelect } from '@/api/role/index';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';

    // 基本信息
    import BasicInfo from '@/views/role/comps/BasicInfo.vue';
    import SystemBasicInfo from '@/views/authorization/comps/SystemBasicInfo.vue';
    import OrgBasesToRoles from '@/views/authorization/comps/OrgBasesToRoles.vue';
    import Authorization from '@/views/authorization/comps/Authorization.vue';

    const { t } = useI18n();
    const settingStore = useSettingStore();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');

    // 全局 loading
    let loading = ref(false);

    // 点击树节点 对应数据的载体
    let currData = ref({} as any);

    // 树 ref
    const fixedTreeRef = ref();
    // 树的一级 子级的请求接口函数
    const treeApiObj = ref({
        topLevel: roleTree,
        childLevel: {
            //子级（二级及二级以上）tree接口
            api: roleTree
        },
        search: {
            //搜索接口及参数
            api: treeSelect,
            params: {}
        }
    });

    // 树节点的 基本信息 获取
    function handlerTreeClick(data) {
        // 将拿到的节点信息 储存起来
        currData.value = data;
    }

    // 控制 基本信息 编辑按钮 与 保存，取消按钮的显示与隐藏
    let editBtnFlag = ref(true);
    // 保存 按钮的loading 控制
    let saveBtnLoading = ref(false);
    // 点击保存按钮 的 flag
    let saveBtnClick = ref(false);

    // 基本信息 编辑后保存
    async function handlerEditSave(data) {
        saveBtnLoading.value = true;
        // 更新基本信息 接口操作 --
        // data 为基本信息 数据
        const result = await saveOrUpdate(data);

        /**
         * 对树进行操作：手动更新节点信息
         */
        //1.更新当前节点的信息
        const treeData = fixedTreeRef.value.getTreeData(); //获取tree数据
        const currNode = fixedTreeRef.value.findNode(treeData, currData.value.id); //找到树节点对应的节点信息
        Object.assign(currNode, data); //合并节点信息
        //2.手动设置点击当前节点
        fixedTreeRef.value?.handClickNode(currNode); //手动设置点击当前节点

        ElNotification({
            title: result.success ? t('成功') : t('失败'),
            message: result.msg,
            type: result.success ? 'success' : 'error',
            duration: 2000,
            offset: 80
        });
        // loading为false 编辑 按钮出现 保存按钮未点击状态
        saveBtnLoading.value = false;
        editBtnFlag.value = true;
        saveBtnClick.value = false;
    }
</script>
<style lang="scss" scoped>
    .basic-btns {
        display: flex;
        justify-content: space-between;
        flex-wrap: wrap;
        margin-bottom: 20px;

        .btn-top {
            margin-bottom: 10px;
        }
    }

    .widthBtn {
        display: flex;
        margin: 0 10px;

        :deep(.el-button) {
            min-width: 90px;
        }
    }

    .custom-select-tree-filter {
        display: flex;

        .refresh-btn {
        }

        .search-input {
            margin-left: 15px;
        }
    }

    .expand-rows {
        padding-left: 20px;
    }
</style>
