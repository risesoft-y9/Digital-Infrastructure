<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-01-11 17:28:38
 * @Description: 组织架构
-->
<template>
    <fixedTreeModule
        ref="fixedTreeRef"
        :treeApiObj="treeApiObj"
        nodeLabel="newName"
        @onDeleteTree="onDeleteTree"
        @onTreeClick="onTreeClick"
    >
        <template v-if="isGlobalManager" #treeHeaderRight>
            <el-popover placement="bottom" trigger="hover" @hide="onHidePopover">
                <template #reference>
                    <el-button
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        class="global-btn-main"
                        type="primary"
                    >
                        <i class="ri-menu-line"></i>
                        <span>{{ $t('组织机构') }}</span>
                    </el-button>
                </template>

                <y9List ref="y9ListRef" :config="actionListConfig" @on-click-row="onActionPopListClick"></y9List>
            </el-popover>
        </template>

        <template #rightContainer>
            <template v-if="Object.keys(currTreeNodeInfo).length > 0">
                <baseInfo
                    :currTreeNodeInfo="currTreeNodeInfo"
                    :findNode="findNode"
                    :getTreeData="getTreeData"
                    :getTreeInstance="getTreeInstance"
                    :handAssginNode="handAssginNode"
                    :postNode="postNode"
                    :refreshTree="refreshTree"
                    :updateTreePersonCount="updateTreePersonCount"
                >
                </baseInfo>

                <template v-if="currTreeNodeInfo.nodeType == 'Person'">
                    <positionList :currTreeNodeInfo="currTreeNodeInfo"></positionList>
                    <groupList :currTreeNodeInfo="currTreeNodeInfo"></groupList>
                    <QRcodeCom :currTreeNodeInfo="currTreeNodeInfo"></QRcodeCom>
                </template>
                <personList
                    v-else
                    :currTreeNodeInfo="currTreeNodeInfo"
                    :handAssginNode="handAssginNode"
                    :updateTreePersonCount="updateTreePersonCount"
                ></personList>

                <template v-if="currTreeNodeInfo.nodeType == 'Department'">
                    <setDepartmentPropList :currTreeNodeInfo="currTreeNodeInfo" typeName="org"></setDepartmentPropList>
                </template>

                <!-- <Log :currTreeNodeInfo="currTreeNodeInfo"></Log> -->
            </template>
        </template>
    </fixedTreeModule>

    <y9Dialog v-model:config="dialogConfig">
        <orgForm
            v-if="dialogConfig.type == 'addOrg'"
            ref="addOrgFormRef"
            :currInfo="currInfo"
            :isEditState="true"
            isAdd
        ></orgForm>
        <uploadOrgInfo
            v-if="dialogConfig.type == 'uploadOrgXML'"
            :refresh="refreshTree"
            @update="dialogConfig.show = false"
        ></uploadOrgInfo>
        <historyUploadDetails v-if="dialogConfig.type == 'uploadHistory'"></historyUploadDetails>
        <treeSort
            v-if="dialogConfig.type == 'sort'"
            ref="sortRef"
            :apiRequest="treeInterface"
            :columns="dialogConfig.columns"
            :currInfo="currInfo"
        ></treeSort>
    </y9Dialog>

    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { computed, inject, reactive, ref, toRefs } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import orgForm from './comps/baseInfoForm/orgForm.vue';
    import baseInfo from './comps/baseInfo.vue';
    import personList from './comps/personList.vue';
    import positionList from './comps/positionList.vue';
    import groupList from './comps/groupList.vue';
    import QRcodeCom from './comps/QRcode.vue';
    import setDepartmentPropList from './comps/setDepartmentPropList.vue';
    import uploadOrgInfo from './comps/dialogContent/uploadOrgInfo.vue';
    import historyUploadDetails from './comps/dialogContent/historyUploadDetails.vue';
    import {
        getAllPersonsCount,
        getTreeItemById,
        orgSaveOrder,
        orgSaveOrUpdate,
        removeOrg,
        searchByName,
        treeInterface
    } from '@/api/org/index';
    import { checkDeptManager } from '@/api/deptManager/index';
    import { removeDept } from '@/api/dept/index';
    import { removeGroup } from '@/api/group/index';
    import { delPerson } from '@/api/person/index';
    import { useSettingStore } from '@/store/modules/settingStore';

    const settingStore = useSettingStore();
    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');

    let fixedTreeRef = ref(); //tree实例
    let y9ListRef = ref(); //气泡框的列表实例
    let addOrgFormRef = ref();
    let sortRef = ref();
    let currInfo = ref({} as any);

    //数据
    const data = reactive({
        loading: false,
        treeApiObj: {
            //tree接口对象
            topLevel: {
                api: treeInterface,
                params: { treeType: 'tree_type_org_person' }
            },
            childLevel: {
                api: getTreeItemById,
                params: { treeType: 'tree_type_org_person', disabled: true }
            },
            search: {
                api: searchByName,
                params: {
                    treeType: 'tree_type_org_person'
                }
            }
        },
        currTreeNodeInfo: {} as any, //当前tree节点的信息

        //气泡操作列表配置
        actionListConfig: {
            padding: '0px',
            listData: [
                {
                    id: 'addOrg',
                    name: computed(() => t('新增组织机构'))
                },
                {
                    id: 'sort',
                    name: computed(() => t('组织机构排序'))
                },
                {
                    id: 'uploadOrgXML',
                    name: computed(() => t('上传'))
                }
                // {
                // 	id:"uploadHistory",
                // 	name:"历史上传详情"
                // },
            ]
        },

        //弹窗配置
        dialogConfig: {
            show: false,
            title: '',
            onOkLoading: true,
            type: '',
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {
                    let result = { success: false, msg: '', data: {} as any };

                    if (newConfig.value.type == 'addOrg') {
                        let valid = await addOrgFormRef.value?.y9FormRef?.elFormRef?.validate((valid) => valid); //获取表单验证结果
                        if (!valid) {
                            reject();
                            return;
                        }

                        result = await orgSaveOrUpdate(addOrgFormRef.value?.y9FormRef?.model);
                        if (result.success && result.data) {
                            //请求一级接口
                            const res = await treeInterface({ treeType: 'tree_type_org_person' });
                            if (res.data) {
                                await fixedTreeRef.value.setTreeData(res.data); //重新设置树数据
                            }
                            //手动设置点击当前节点
                            handClickNode(result.data, false); //手动设置点击当前节点
                        }
                    } else if (newConfig.value.type == 'sort') {
                        let tableData = sortRef.value.tableConfig.tableData;
                        const ids = [] as any;
                        tableData.forEach((element) => {
                            ids.push(element.id);
                        });
                        result = await orgSaveOrder(ids.toString());
                        if (result.success) {
                            refreshTree();
                        }
                    }
                    ElNotification({
                        title: result.success ? t('成功') : t('失败'),
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    if (result.success) {
                        resolve();
                    } else {
                        reject();
                    }
                });
            },
            columns: [] as any
        }
    });

    const { treeApiObj, currTreeNodeInfo, actionListConfig, dialogConfig, loading } = toRefs(data);

    const isGlobalManager = ref(JSON.parse(sessionStorage.getItem('ssoUserInfo')).globalManager);

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
                } else if (data.nodeType == 'Group') {
                    result = await removeGroup(data.id);
                } else if (data.nodeType == 'Person') {
                    result = await delPerson([data.id].toString());
                }

                if (result.success) {
                    const treeData = getTreeData(); //获取tree数据

                    //1.更新父节点计数
                    let count = 0;
                    if (data.nodeType === 'Person') {
                        if (data.disabled) {
                            count = 0;
                        } else {
                            count = -1;
                        }
                    } else if (data.personCount) {
                        count = -data.personCount;
                    }
                    updateParentPersonCount(data, treeData, count); //更新父节点计数

                    //2.删除后，需要手动点击的节点信息，如果有父节点则默认点击父节点，没有则点击tree数据的第一个节点
                    let clickNode = null;
                    if (data?.parentId) {
                        clickNode = findNode(treeData, data.parentId); //找到父节点的信息
                    } else if (treeData.length > 0) {
                        clickNode = treeData[0];
                    }
                    if (clickNode) {
                        handClickNode(clickNode, false); //手动设置点击当前节点
                    }

                    //3.删除此节点
                    getTreeInstance().remove(data);
                }

                loading.value = false;
                ElNotification({
                    title: result.success ? t('成功') : t('失败'),
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
            })
            .catch((e) => {
                ElMessage({
                    type: 'info',
                    message: t('已取消删除'),
                    offset: 65
                });
            });
    }

    //操作列表点击事件
    function onActionPopListClick(currRow) {
        Object.assign(dialogConfig.value, {
            show: true,
            title:
                currRow.id == 'uploadOrgXML'
                    ? computed(() => t('上传架构信息XML'))
                    : computed(() => t(`${currRow.name}`)),
            type: currRow.id,
            showFooter: currRow.id == 'addOrg' || currRow.id == 'sort' ? true : false,
            width: currRow.id == 'uploadOrgXML' ? '30%' : '60%',
            columns:
                currRow.id == 'sort'
                    ? [
                          {
                              type: 'radio',
                              title: computed(() => t('请选择')),
                              width: 200
                          },
                          {
                              title: computed(() => t('名称')),
                              key: 'name'
                          },
                          {
                              title: computed(() => t('类别')),
                              key: 'orgType'
                          }
                      ]
                    : []
        });
    }

    //隐藏气泡框时触发
    function onHidePopover() {
        y9ListRef.value && y9ListRef.value.removeHighlight(); //清空高亮
    }

    // 刷新 组织架构树
    function refreshTree() {
        fixedTreeRef.value.onRefreshTree();
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
     * @param {String} isRePostPersonCount 是否重新请求计数
     */
    async function handAssginNode(obj, targetId, postChildId, isRePostPersonCount) {
        if (postChildId) {
            const childData = await postNode({ id: postChildId }); //重新请求当前节点的子节点，获取格式化后的子节点信息
            obj.children = childData;
        }

        //1.更新当前节点的信息
        const currNode = findNode(getTreeData(), targetId); //找到树节点对应的节点信息
        if (currNode) {
            if (isRePostPersonCount) {
                let res = await getAllPersonsCount(currNode.id, currNode.nodeType); //获取人员数量
                currNode.personCount = res.data; //人员数量
            }
            Object.assign(currNode, obj); //合并节点信息

            if (currNode.nodeType === 'Organization' || currNode.nodeType === 'Department') {
                //修改显示名称
                currNode.newName = currNode.name + `(${currNode.personCount})`;
            } else if (currNode.nodeType == 'Person') {
                // 人员有禁用的文字需要显示
                if (currNode.disabled) {
                    currNode.newName = currNode.name + `[禁用]`;
                } else {
                    currNode.newName = currNode.name;
                }
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

    /**递归更新当前节点的父节点人员计数信息
     * 1.找到父节点信息
     * 2.修改人员父节点的人员计数
     * @param {Object} currNode 当前节点信息
     * @param {Array} treeData 树的数据
     * @param {Number} count 需要增加或减少的数量
     */
    function updateParentPersonCount(currNode, treeData, count) {
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
                updateParentPersonCount(parentNode, treeData, count);
            }
        }
    }

    /**更新tree的人员计数
     * 1.找到父节点信息
     * 2.修改人员父节点的人员计数
     * @param {Object} node 当前节点信息
     * @param {Number} count 需要增加或减少的数量
     * @param {String} postChildId 请求的子节点id，如果存在该字段就请求子节点
     */
    async function updateTreePersonCount(node, count, postChildId) {
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

        updateParentPersonCount(currNode, treeData, count); //递归更新当前节点的父节点人员计数信息

        handClickNode(currNode); //手动设置点击当前节点
    }
</script>

<style lang="scss" scoped></style>
