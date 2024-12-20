<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-01-12 10:45:46
 * @Description: 组织岗位
-->
<template>
    <fixedTreeModule
        ref="fixedTreeRef"
        :treeApiObj="treeApiObj"
        nodeLabel="newName"
        @onDeleteTree="onDeleteTree"
        @onTreeClick="onTreeClick"
    >
        <template v-if="Object.keys(currTreeNodeInfo).length > 0" #rightContainer>
            <baseInfo
                :currTreeNodeInfo="currTreeNodeInfo"
                :findNode="findNode"
                :getTreeData="getTreeData"
                :getTreeInstance="getTreeInstance"
                :handAssginNode="handAssginNode"
                :updateTreePositionCount="updateTreePositionCount"
            >
            </baseInfo>

            <template v-if="currTreeNodeInfo.nodeType == 'Position'">
                <personList :currTreeNodeInfo="currTreeNodeInfo" :handAssginNode="handAssginNode"></personList>
            </template>

            <positionRelation
                v-else
                :currTreeNodeInfo="currTreeNodeInfo"
                :handAssginNode="handAssginNode"
                :updateTreePositionCount="updateTreePositionCount"
            >
            </positionRelation>

            <template v-if="currTreeNodeInfo.nodeType == 'Department'">
                <setDepartmentPropList :currTreeNodeInfo="currTreeNodeInfo"></setDepartmentPropList>
                <inheritableDepartmentPropList :currTreeNodeInfo="currTreeNodeInfo" typeName="org"></inheritableDepartmentPropList>
            </template>
        </template>
    </fixedTreeModule>
    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { reactive, ref, toRefs } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import baseInfo from './comps/baseInfo.vue';
    import positionRelation from './comps/positionRelation.vue';
    import personList from './comps/personList.vue';

    import { getAllPersonsCount, getTreeItemById, removeOrg, searchByName, treeInterface } from '@/api/org/index';
    import { checkDeptManager } from '@/api/deptManager/index';
    import { removeDept } from '@/api/dept/index';
    import { removePosition } from '@/api/position/index';
    import setDepartmentPropList from '../org/comps/setDepartmentPropList.vue';
    import InheritableDepartmentPropList from "@/views/org/comps/inheritableDepartmentPropList.vue";

    const { t } = useI18n();
    let fixedTreeRef = ref(); //tree实例
    //数据
    const data = reactive({
        loading: false, // 全局loading
        treeApiObj: {
            //tree接口对象
            topLevel: {
                api: treeInterface,
                params: { treeType: 'tree_type_position' }
            },
            childLevel: {
                api: getTreeItemById,
                params: { treeType: 'tree_type_position', disabled: false }
            },
            search: {
                api: searchByName,
                params: {
                    treeType: 'tree_type_org_position'
                }
            }
        },
        currTreeNodeInfo: {} as any //当前tree节点的信息
    });

    const { treeApiObj, currTreeNodeInfo, loading } = toRefs(data);

    //点击tree的回调
    async function onTreeClick(currTreeNode) {
        const isGlobalManager = JSON.parse(sessionStorage.getItem('ssoUserInfo')).globalManager;
        let isDeptManager = true;
        if (currTreeNode.nodeType === 'Department') {
            const result = await checkDeptManager(currTreeNode.id);
            isDeptManager = result.data;
        } else if (currTreeNode.nodeType === 'Organization') {
            isDeptManager = isGlobalManager;
        }
        currTreeNodeInfo.value = currTreeNode;
        currTreeNodeInfo.value.haveEditAuth = isDeptManager; //是否有编辑权限
    }

    function onDeleteTree(data) {
        ElMessageBox.confirm(`${t('是否删除')}【${data.name}】?`, t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info'
        })
            .then(async () => {
                loading.value = true;
                let result = { success: false, msg: '' };
                if (data.nodeType == 'Organization') {
                    result = await removeOrg(data.id);
                } else if (data.nodeType == 'Department') {
                    result = await removeDept(data.id);
                } else if (data.nodeType == 'Position') {
                    result = await removePosition([data.id].toString());
                }
                loading.value = false;
                ElNotification({
                    title: result.success ? t('成功') : t('失败'),
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });

                if (result.success) {
                    const treeData = getTreeData(); //获取tree数据

                    //1.更新父节点计数
                    let count = -1;
                    if (data.personCount) {
                        count = -data.personCount;
                    }
                    updateParentPositionCount(data, treeData, count); //更新父节点计数

                    //2.删除后，需要手动点击的节点信息，如果有父节点则默认点击父节点，没有则点击tree数据的第一个节点
                    let handClickNode = null;
                    if (data?.parentId) {
                        handClickNode = findNode(treeData, data.parentId); //找到父节点的信息
                    } else if (treeData.length > 0) {
                        handClickNode = treeData[0];
                    }
                    if (handClickNode) {
                        fixedTreeRef.value.handClickNode(handClickNode, false); //手动设置点击当前节点
                    }

                    //3.删除此节点
                    getTreeInstance().remove(data);
                }
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

    /**手动更新节点信息
     * @param {Object} obj 需要合并的字段
     * @param {String} targetId 需要更新的节点id
     * @param {String} postChildId 请求的子节点id，如果存在该字段就请求子节点
     */
    async function handAssginNode(obj, targetId, postChildId, isRePostPersonCount) {
        if (postChildId) {
            const childData = await postNode({ id: postChildId }); //重新请求当前节点的子节点，获取格式化后的子节点信息
            obj.children = childData;
        }

        //1.更新当前节点的信息
        const currNode = findNode(getTreeData(), targetId); //找到树节点对应的节点信息
        // Object.assign(currNode, obj); //合并节点信息
        if (currNode) {
            if (isRePostPersonCount) {
                let res = await getAllPersonsCount(currNode.id, currNode.nodeType); //获取人员数量
                currNode.personCount = res.data; //人员数量
            }
            Object.assign(currNode, obj); //合并节点信息

            if (currNode.nodeType === 'Organization' || currNode.nodeType === 'Department') {
                //修改显示名称
                currNode.newName = currNode.name + `(${currNode.personCount})`;
            } else if (currNode.nodeType == 'Position') {
                // 岗位有禁用的文字需要显示
                if (currNode.disabled) {
                    currNode.newName = currNode.name + `[禁用]`;
                } else {
                    currNode.newName = currNode.name;
                }
            } else {
                currNode.newName = currNode.name;
            }

            //2.手动设置点击当前节点
            handClickNode(currNode); //手动设置点击当前节点
        } else {
            refreshTree();
        }
    }

    /**手动点击树节点
     * @param {Boolean} isExpand 是否展开节点
     */
    function handClickNode(node, isExpand?) {
        fixedTreeRef.value?.handClickNode(node, isExpand);
    }

    // 刷新 组织架构树
    function refreshTree() {
        fixedTreeRef.value.onRefreshTree();
    }

    /**递归更新当前节点的父节点岗位计数信息
     * 1.找到父节点信息
     * 2.修改岗位父节点的岗位计数
     * @param {Object} currNode 当前节点信息
     * @param {Array} treeData 树的数据
     * @param {Number} count 需要增加或减少的数量
     */
    function updateParentPositionCount(currNode, treeData, count) {
        if (currNode?.parentId) {
            //如果父节点存在
            const parentNode = findNode(treeData, currNode.parentId); //找到父节点的信息
            if (
                parentNode.nodeType === 'Organization' ||
                (parentNode.nodeType === 'Department' && parentNode.hasOwnProperty('personCount'))
            ) {
                parentNode.personCount = parentNode.personCount + count;
                parentNode.newName = parentNode.name + `(${parentNode.personCount})`;
            }
            if (parentNode?.parentId) {
                updateParentPositionCount(parentNode, treeData, count);
            }
        }
    }

    /**更新tree的岗位计数
     * 1.找到父节点信息
     * 2.修改岗位父节点的岗位计数
     * @param {Object} node 当前节点信息
     * @param {Number} count 需要增加或减少的数量
     * @param {String} postChildId 请求的子节点id，如果存在该字段就请求子节点
     */
    async function updateTreePositionCount(node, count, postChildId) {
        const treeData = getTreeData();
        const currNode = findNode(treeData, node.id);

        if (postChildId) {
            const childData = await postNode({ id: postChildId }); //重新请求当前节点的子节点，获取格式化后的子节点信息
            currNode.children = childData;
        }

        if (
            currNode?.nodeType === 'Organization' ||
            (currNode?.nodeType === 'Department' && currNode.hasOwnProperty('personCount'))
        ) {
            currNode.personCount = currNode.personCount + count;
            currNode.newName = currNode.name + `(${currNode.personCount})`;
        }

        updateParentPositionCount(currNode, treeData, count); //递归更新当前节点的父节点岗位计数信息
        fixedTreeRef.value.handClickNode(currNode); //手动设置点击当前节点
    }
</script>

<style lang="scss" scoped></style>
