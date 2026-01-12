<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2025-12-24 10:39:03
 * @Description: 应用资源管理
-->
<template>
    <fixedTreeModule
        ref="fixedTreeRef"
        :treeApiObj="treeApiObj"
        nodeLabel="newName"
        @onDeleteTree="resourceRemove"
        @onTreeClick="handlerTreeClick"
    >
        <template v-slot:treeHeaderRight>
            <!-- <el-button 
                    type="primary">
                    <i class="ri-add-line"></i>
                    <span>资源</span>
                </el-button>-->
        </template>
        <template v-slot:rightContainer>
            <!-- 右边卡片 -->
            <div v-if="currData.id">
                <!-- <y9Card :title="`查看修改日志 - ${ currData.name? currData.name : '' }`" >
                        <y9Table 
                            :config="modifyLogTableConfig" border >
                        </y9Table>
                    </y9Card>
                    <y9Card :title="`角色关联 - ${currData.name? currData.name : ''}`">
                        <RoleRelation />
                    </y9Card>-->

                <y9Card
                    v-if="currData.nodeType === 'SYSTEM'"
                    :title="`${$t('基本信息')} - ${currData.name ? currData.name : ''}`"
                >
                    <template v-slot>
                        <SystemBasicInfo :id="currData.id" :editFlag="true" />
                    </template>
                </y9Card>
                <BasicInfo
                    v-else
                    :currTreeNodeInfo="currData"
                    :findNode="findNode"
                    :getTreeData="getTreeData"
                    :getTreeInstance="getTreeInstance"
                    :handClickNode="handClickNode"
                    :postNode="postNode"
                />

                <audit-log v-show="currData.isManageable" :currTreeNodeInfo="currData"></audit-log>
            </div>
        </template>
    </fixedTreeModule>
    <!-- 制造loading效果 -->
    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { inject, ref } from 'vue';
    import { menuDelete, operationDel, resourceTree, treeSearch } from '@/api/resource/index';
    import { applicationDel } from '@/api/system/index';
    // 基本信息
    import BasicInfo from './comps/BasicInfo.vue';
    import SystemBasicInfo from '@/views/system/comps/BasicInfo.vue';
    import auditLog from '@/views/y9log/entityAuditLog/index.vue';
    import { useI18n } from 'vue-i18n';
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');

    const { t } = useI18n();

    // loading
    let loading = ref(false);

    // 点击树节点 对应数据的载体
    let currData: any = ref({ id: null });

    // 节点的 基本信息 获取
    function handlerTreeClick(data) {
        // 将拿到的节点信息 储存起来
        currData.value = data;
    }

    // 树 ref
    const fixedTreeRef = ref();
    // 树的一级 子级的请求接口函数
    const treeApiObj = ref({
        topLevel: resourceTree, //顶级（一级）tree接口,
        childLevel: {
            //子级（二级及二级以上）tree接口
            api: resourceTree
        },
        search: {
            //搜索接口及参数
            api: treeSearch,
            params: {}
        }
    });

    // 删除资源
    function resourceRemove(data) {
        ElMessageBox.confirm(`${t('是否删除')}【${data.name}】?`, t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info'
        })
            .then(async () => {
                loading.value = true;
                // 进行 删除 操作 --
                let result;
                if (data.nodeType === 'APP') {
                    result = await applicationDel([data.id]);
                } else if (data.nodeType === 'MENU') {
                    result = await menuDelete(data.id);
                } else {
                    result = await operationDel(data.id);
                }

                if (result.success) {
                    /**
                     * 对树进行操作
                     */
                    //1.删除前，需要手动点击的节点信息，如果有父节点则默认点击父节点，没有则点击tree数据的第一个节点
                    const treeData = getTreeData(); //获取tree数据
                    let clickNode = null;
                    if (data.parentId) {
                        clickNode = findNode(treeData, data.parentId); //找到父节点的信息
                        fixedTreeRef.value?.y9TreeRef?.remove(data); //删除此节点
                    } else if (treeData.length > 0) {
                        fixedTreeRef.value?.y9TreeRef?.remove(data); //删除此节点
                        clickNode = treeData[0];
                    }
                    if (clickNode) {
                        handClickNode(clickNode); //手动设置点击当前节点
                    }
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

    //获取tree数据
    function getTreeData() {
        return fixedTreeRef.value.getTreeData();
    }

    //获取树的实例
    function getTreeInstance() {
        return fixedTreeRef.value.y9TreeRef;
    }

    //请求某个节点，返回格式化好的数据
    function postNode(node) {
        return new Promise((resolve, reject) => {
            fixedTreeRef.value.onTreeLazyLoad(node, (data) => {
                resolve(data);
            });
        });
    }

    //在树数据中根据id找到对应的节点并返回
    function findNode(treeData, targetId) {
        return fixedTreeRef.value.findNode(treeData, targetId);
    }

    /**手动点击树节点
     * @param {Boolean} isExpand 是否展开节点
     */
    function handClickNode(node, isExpand?) {
        fixedTreeRef.value?.handClickNode(node, isExpand);
    }
</script>
<style lang="scss" scoped></style>
