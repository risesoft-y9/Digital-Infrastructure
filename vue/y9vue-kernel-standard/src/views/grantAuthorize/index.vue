<!--
 * @Author: fuyu
 * @Date: 2022-06-06 11:47:27
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-04-16 10:40:29
 * @Description: 授权管理
-->
<template>
    <div>
        <fixedTreeModule
            ref="fixedTreeRef"
            :showNodeDelete="false"
            :treeApiObj="treeApiObj"
            @onDeleteTree="roleRemove"
            @onTreeClick="handlerTreeClick"
        >
            <template v-if="currData.id" v-slot:rightContainer>
                <!-- 右边卡片 -->
                <div v-if="currData.nodeType === 'SYSTEM'">
                    <y9Card
                        v-if="currData.nodeType === 'SYSTEM'"
                        :title="`${$t('基本信息')} - ${currData.name ? currData.name : ''}`"
                    >
                        <template v-slot>
                            <SystemBasicInfo :id="currData.id" :editFlag="true" />
                        </template>
                    </y9Card>
                </div>
                <div v-else>
                    <y9Card :title="`${$t('基本信息')} - ${currData.name ? currData.name : ''}`">
                        <template v-slot>
                            <BasicInfo
                                :id="currData.id"
                                :editFlag="editBtnFlag"
                                :saveClickFlag="saveBtnClick"
                                :type="currData.nodeType"
                                @getInfoData="handlerEditSave"
                            />
                        </template>
                    </y9Card>
                    <!-- 角色关联 -->
                    <y9Card :title="`${$t('角色关联')} - ${currData.name ? currData.name : ''}`">
                        <template v-slot>
                            <RelationRole :id="currData.id" :appId="currData.appId" />
                        </template>
                    </y9Card>
                    <!-- 组织关联 -->
                    <y9Card :title="`${$t('组织关联')} - ${currData.name ? currData.name : ''}`">
                        <RelationOrg :id="currData.id" />
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
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import { useI18n } from 'vue-i18n';
    import { menuDelete, operationDel, resourceTree, treeSearch } from '@/api/resource/index';
    import { applicationDel } from '@/api/system/index';
    // 基本信息
    import BasicInfo from './comps/BasicInfo.vue';
    // 角色 关联
    import RelationRole from './comps/RelationRole.vue';
    // 组织 关联
    import RelationOrg from './comps/RelationOrg.vue';
    import SystemBasicInfo from '@/views/system/comps/BasicInfo.vue';

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

    // 删除资源
    function roleRemove(data) {
        ElMessageBox.confirm(`${t('是否删除')}【${data.name}】?`, t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info'
        })
            .then(async () => {
                // 进行 删除 操作 --
                loading.value = true;
                let result;
                if (data.nodeType === 'APP') {
                    result = await applicationDel([data.id]);
                } else if (data.nodeType === 'MENU') {
                    result = await menuDelete(data.id);
                } else {
                    result = await operationDel(data.id);
                }

                /**
                 * 对树进行操作
                 */
                //1.删除前，需要手动点击的节点信息，如果有父节点则默认点击父节点，没有则点击tree数据的第一个节点
                const treeData = fixedTreeRef.value.getTreeData(); //获取tree数据
                let clickNode = null;
                if (data.parentId) {
                    clickNode = fixedTreeRef.value.findNode(treeData, data.parentId); //找到父节点的信息
                    fixedTreeRef.value?.y9TreeRef?.remove(data); //删除此节点
                } else if (treeData.length > 0) {
                    fixedTreeRef.value?.y9TreeRef?.remove(data); //删除此节点
                    clickNode = treeData[0];
                }
                if (clickNode) {
                    fixedTreeRef.value?.handClickNode(clickNode); //手动设置点击当前节点
                }

                loading.value = false;
                ElNotification({
                    message: result.success ? t('删除成功') : t('删除失败'),
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: t('已取消删除'),
                    offset: 65
                });
            });
    }

    function handlerEditSave(data) {}
</script>
<style lang="scss" scoped>
    // .btn-class {
    //     // display: flex;
    //     // justify-content: space-between;
    // }
</style>
