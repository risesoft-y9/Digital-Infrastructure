<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2025-05-30 13:52:18
 * @Description: 应用角色关联 + 应用角色管理
-->
<template>
    <fixedTreeModule
        ref="fixedTreeRef"
        :hiddenSearch="false"
        :treeApiObj="treeApiObj"
        @onDeleteTree="roleRemove"
        @onTreeClick="handlerTreeClick"
    >
        <template v-slot:rightContainer>
            <!-- 右边卡片 -->
            <div v-if="currData.id">
                <y9Card :title="`${$t('基本信息')} - ${currData.name ? currData.name : ''}`">
                    <template v-slot>
                        <div v-show="currData.isManageable" class="basic-btns">
                            <div class="btn-top">
                                <span
                                    v-if="currData.nodeType == 'role' || currData.nodeType == 'folder'"
                                    style="margin-right: 15px"
                                >
                                    <el-button
                                        v-if="editBtnFlag"
                                        :size="fontSizeObj.buttonSize"
                                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                                        class="global-btn-main"
                                        type="primary"
                                        @click="editBtnFlag = false"
                                    >
                                        <i class="ri-edit-line"></i>
                                        {{ $t('编辑') }}
                                    </el-button>
                                    <span v-else>
                                        <el-button
                                            :loading="saveBtnLoading"
                                            :size="fontSizeObj.buttonSize"
                                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                                            class="global-btn-main"
                                            type="primary"
                                            @click="saveBtnClick = true"
                                        >
                                            <i class="ri-save-line"></i>
                                            {{ $t('保存') }}
                                        </el-button>
                                        <el-button
                                            :size="fontSizeObj.buttonSize"
                                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                                            class="global-btn-second"
                                            @click="editBtnFlag = true"
                                        >
                                            <i class="ri-close-line"></i>
                                            {{ $t('取消') }}
                                        </el-button>
                                    </span>
                                </span>
                                <span>
                                    <el-button
                                        v-if="currData.nodeType == 'APP' || currData.nodeType == 'folder'"
                                        :size="fontSizeObj.buttonSize"
                                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                                        class="global-btn-main"
                                        type="primary"
                                        @click="handlerAddNode"
                                    >
                                        <i class="ri-add-line"></i>
                                        {{ $t('子节点') }}
                                    </el-button>
                                    <el-button
                                        v-if="currData.nodeType != 'role'"
                                        :size="fontSizeObj.buttonSize"
                                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                                        class="global-btn-main"
                                        type="primary"
                                        @click="handlerAddRole"
                                    >
                                        <i class="ri-add-line"></i>
                                        {{ $t('角色') }}
                                    </el-button>
                                </span>
                            </div>
                            <div>
                                <template v-if="currData.appId">
                                    <el-button
                                        v-if="currData.nodeType == 'APP' || currData.nodeType == 'folder'"
                                        :size="fontSizeObj.buttonSize"
                                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                                        class="global-btn-second"
                                        @click="onSort('sort')"
                                    >
                                        <i class="ri-order-play-line"></i>
                                        {{ $t('排序') }}
                                    </el-button>
                                    <!-- v-if="currData.resourceType !== 0" -->
                                    <el-button
                                        v-if="currData.nodeType == 'role' || currData.nodeType == 'folder'"
                                        :size="fontSizeObj.buttonSize"
                                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                                        class="global-btn-second"
                                        @click="onSort('move')"
                                    >
                                        <i class="ri-route-line"></i>
                                        {{ $t('移动') }}
                                    </el-button>
                                    <!-- <el-button class="global-btn-second" @click="extendFlag = true"  >
                                            <i class="ri-external-link-line"></i>
                                            扩展属性
                                        </el-button>
                                        <span style="margin-left: 10px">
                                            <el-button class="global-btn-second" @click="upRoleDialog.show = true" >
                                                <i class="ri-file-download-line"></i>
                                                上传角色信息
                                            </el-button>
                                            <el-button class="global-btn-second">
                                                <i class="ri-file-upload-line"></i>
                                                导出角色XML
                                            </el-button>
                                        </span> -->
                                </template>
                            </div>
                        </div>
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
                <audit-log v-show="currData.isManageable" :currTreeNodeInfo="currData"></audit-log>
                <!-- 角色成员 -->
                <y9Card
                    v-if="managerLevel === 2 && currData.nodeType === 'role'"
                    :title="`${$t('角色成员')} - ${currData.name ? currData.name : ''}`"
                >
                    <OrgBasesToRoles :id="currData.id"></OrgBasesToRoles>
                </y9Card>

                <y9Card
                    v-if="managerLevel === 2 && currData.nodeType === 'role'"
                    :title="`${$t('资源列表')} - ${currData.name ? currData.name : ''}`"
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
    <!-- 新增子节点 角色 弹框 -->
    <y9Dialog v-model:config="addChildNodeDialog">
        <y9Form ref="roleFormRef" :config="roleFormConfig"></y9Form>
    </y9Dialog>
    <!-- 排序 -->
    <y9Dialog v-model:config="sortDialogConfig">
        <treeSort
            ref="sortRef"
            :apiParams="{ parentId: currData.id }"
            :apiRequest="roleTreeList"
            :columns="sortDialogConfig.columns"
        ></treeSort>
    </y9Dialog>
    <!-- 移动 -->
    <y9Dialog v-model:config="moveConfigDialog">
        <selectTree
            ref="moveSelectTreeRef"
            :selectField="[{ fieldName: 'nodeType', value: ['folder', 'APP'] }]"
            :showHeader="false"
            :treeApiObj="treeMoveApiObj"
            checkStrictly
            @onCheckChange="handlerMoveTreeCheckChange"
            @onNodeExpand="onNodeExpand"
        ></selectTree>
    </y9Dialog>
    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { computed, inject, ref } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import y9_storage from '@/utils/storage';
    import {
        appRoleTree,
        deleteRoleById,
        roleTree,
        roleTreeList,
        saveMoveRole,
        saveOrder,
        saveOrUpdate,
        treeSelect
    } from '@/api/role/index';
    // 基本信息
    import BasicInfo from './comps/BasicInfo.vue';
    import SystemBasicInfo from '@/views/system/comps/BasicInfo.vue';
    import auditLog from '@/views/y9log/entityAuditLog/index.vue';
    import OrgBasesToRoles from './comps/OrgBasesToRoles.vue';
    import Authorization from './comps/Authorization.vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    const settingStore = useSettingStore();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const managerLevel = y9_storage.getObjectItem('ssoUserInfo', 'managerLevel');

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

    //请求某个节点，返回格式化好的数据
    function postNode(node) {
        return new Promise((resolve, reject) => {
            fixedTreeRef.value.onTreeLazyLoad(node, (data) => {
                resolve(data);
            });
        });
    }

    // 删除角色
    function roleRemove(data) {
        let infoText = `${t('是否删除')}【${data.name}】?`;
        if (data.nodeType == 'folder') {
            infoText = `${t('删除后会级联删除其子节点')}，${t('是否删除')}【${data.name}】?`;
        }
        ElMessageBox.confirm(infoText, t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info'
        })
            .then(async () => {
                loading.value = true;
                let result = await deleteRoleById(data.id);

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
                    title: result.success ? t('成功') : t('失败'),
                    message: result.msg,
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

    // 表单ref
    const roleFormRef = ref();
    // config 配置表单
    let roleFormConfig = ref({
        model: {},
        rules: {
            name: [{ required: true, message: computed(() => t('请输入名称')), trigger: 'blur' }]
        },
        itemList: [
            {
                type: 'input',
                prop: 'name',
                label: computed(() => t('名称')),
                required: true
            },
            {
                type: 'input',
                prop: 'customId',
                label: computed(() => t('自定义ID'))
            },
            {
                type: 'textarea',
                prop: 'description',
                label: computed(() => t('描述')),
                rows: 3
            },
            {
                type: 'textarea',
                prop: 'properties',
                label: computed(() => t('扩展属性')),
                rows: 3
            }
        ],
        descriptionsFormConfig: {
            labelWidth: '200px',
            labelAlign: 'center'
        }
    });
    // 系统的增加表单
    let roleForm = ref({} as any);

    // 点击新增 子节点 事件
    function handlerAddNode() {
        // 弹框出现 标题修改 子节点事件  type
        addChildNodeDialog.value.title = computed(() => t('新增子节点'));
        roleForm.value = {};
        roleForm.value.nodeType = 'folder';
        addChildNodeDialog.value.show = true;
    }

    // 点击新增 角色 事件
    function handlerAddRole() {
        // 弹框出现 标题修改 角色事件  type
        addChildNodeDialog.value.title = computed(() => t('新增角色'));
        roleForm.value = {};
        roleForm.value.nodeType = 'role';
        addChildNodeDialog.value.show = true;
    }

    // 增加子资源 弹框的 表单变量配置 控制
    let addChildNodeDialog = ref({
        show: false,
        title: computed(() => t('新增子节点')),
        width: '40%',
        onOkLoading: true,
        onOk: (newConfig) => {
            return new Promise(async (resolve, reject) => {
                const y9RoleFormInstance = roleFormRef.value?.elFormRef;
                await y9RoleFormInstance.validate(async (valid) => {
                    if (valid) {
                        const params = {
                            appId: currData.value.nodeType === 'SYSTEM' ? null : currData.value.appId,
                            parentId: currData.value.id,
                            systemId: currData.value.nodeType === 'SYSTEM' ? currData.value.id : null,
                            type: roleForm.value.nodeType,
                            ...roleFormRef.value?.model
                        };
                        await saveOrUpdate(params)
                            .then(async (result) => {
                                /**
                                 * 对树进行操作：新增节点进入树
                                 */
                                if (result.success) {
                                    //1.更新当前节点的子节点信息
                                    const treeData = fixedTreeRef.value.getTreeData(); //获取tree数据
                                    const currNode = fixedTreeRef.value.findNode(treeData, currData.value.id); //找到树节点对应的节点信息
                                    const childData = await postNode({ id: currNode.id, nodeType: currNode.nodeType }); //重新请求当前节点的子节点，获取格式化后的子节点信息
                                    Object.assign(currNode, { children: childData }); //合并节点信息
                                    //2.手动设置点击当前节点
                                    fixedTreeRef.value?.handClickNode(currNode); //手动设置点击当前节点
                                }
                                ElNotification({
                                    title: result.success ? t('成功') : t('失败'),
                                    message: result.msg,
                                    type: result.success ? 'success' : 'error',
                                    duration: 2000,
                                    offset: 80
                                });

                                // 表单 弹框消失 表单数据清空 重新刷新树列表
                                roleForm.value = {};
                                resolve();
                            })
                            .catch(() => {
                                reject();
                            });
                    } else {
                        reject();
                    }
                });
            });
        }
    });

    //移动-选择树节点展开时触发
    const selectTreeExpandNode = ref();

    function onNodeExpand(node) {
        selectTreeExpandNode.value = node;
    }

    // treeObj
    const treeMoveApiObj = ref({
        topLevel: async () => {
            let data: any = [];
            const res = await appRoleTree(currData.value.appId);
            return res.data;
        },
        childLevel: {
            api: async () => {
                const res = await roleTreeList({
                    parentId: selectTreeExpandNode.value.id
                });

                const data = res.data || [];

                for (let i = 0; i < data.length; i++) {
                    const item = data[i];
                    if (item.id == currData.value.id) {
                        item.disabled = true;
                        break;
                    }
                }

                return data;
            },
            params: {}
        }
    });

    //移动树实例
    const moveSelectTreeRef = ref();

    //移动树的复选框被点击时触发
    function handlerMoveTreeCheckChange(node, isChecked, childIsHaveChecked) {
        //已经选择的节点
        const alreadyCheckedNode = moveSelectTreeRef.value?.y9TreeRef?.getCheckedNodes();
        if (isChecked && alreadyCheckedNode.length > 1) {
            alreadyCheckedNode.forEach((item) => {
                //取消其他选择，做成单选效果
                if (item.id !== node.id) {
                    moveSelectTreeRef.value?.y9TreeRef?.setChecked(item, false, false);
                }
            });
        }
    }

    // 移动弹框的 配置
    let moveConfigDialog = ref({
        show: false,
        title: computed(() => t('移动')),
        width: '30%',
        onOkLoading: true,
        okText: computed(() => t('保存')),
        cancelText: computed(() => t('关闭')),
        onOk: (newConfig) => {
            return new Promise(async (resolve, reject) => {
                const removeIds = moveSelectTreeRef.value?.y9TreeRef?.getCheckedKeys();
                if (removeIds.length === 0) {
                    ElNotification({
                        title: t('失败'),
                        message: t('请选择移动目标节点'),
                        type: 'error',
                        duration: 2000,
                        offset: 80
                    });
                    reject();
                    return;
                }

                const removeId = removeIds[0];
                let params = {
                    id: currData.value.id,
                    parentId: removeId
                };

                await saveMoveRole(params)
                    .then(async (result) => {
                        /**
                         * 对树进行操作：手动更新当前节点的子节点信息
                         */
                        //1.删除被移动的节点
                        fixedTreeRef.value.y9TreeRef.remove(currData.value);

                        //2.重新请求，移动的目标节点，获得最新的子节点信息
                        const treeData = fixedTreeRef.value.getTreeData(); //获取tree数据
                        const currNode = fixedTreeRef.value.findNode(treeData, removeId); //找到移动的目标树节点对应的节点信息
                        const childData = await postNode({ id: removeId }); //重新请求当前节点，获取格式化后的子节点信息
                        Object.assign(currNode, { children: childData }); //合并节点信息

                        //3.手动设置点击当前节点
                        fixedTreeRef.value?.handClickNode(currNode); //手动设置点击当前节点

                        ElNotification({
                            title: result.success ? t('成功') : t('失败'),
                            message: result.msg,
                            type: result.success ? 'success' : 'error',
                            duration: 2000,
                            offset: 80
                        });
                        resolve();
                    })
                    .catch(() => {
                        reject();
                    });
            });
        }
    });

    let sortRef = ref();
    // 资源排序 弹框的变量配置 控制
    let sortDialogConfig = ref({
        show: false,
        title: computed(() => t('角色排序')),
        width: '40%',
        showFooter: true, //是否显示底部
        columns: [
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
                title: computed(() => t('类型')),
                key: 'nodeType'
            }
        ],
        onOk: (newConfig) => {
            return new Promise(async (resolve, reject) => {
                let tableData = sortRef.value.tableConfig.tableData;
                const ids = [] as any;
                tableData.forEach((element) => {
                    ids.push(element.id);
                });
                await saveOrder(ids.toString())
                    .then(async (result) => {
                        /**
                         * 对树进行操作：手动更新当前节点的子节点信息
                         */
                        //1.更新当前节点的子节点信息
                        const treeData = fixedTreeRef.value.getTreeData(); //获取tree数据
                        const currNode = fixedTreeRef.value.findNode(treeData, currData.value.id); //找到树节点对应的节点信息
                        const childData = await postNode({ id: currData.value.id }); //重新请求当前节点的子节点，获取格式化后的子节点信息
                        Object.assign(currNode, { children: childData }); //合并节点信息

                        //2.手动设置重新点击当前节点
                        fixedTreeRef.value?.handClickNode(currNode); //手动设置点击当前节点

                        ElNotification({
                            title: result.success ? t('成功') : t('失败'),
                            message: result.msg,
                            type: result.success ? 'success' : 'error',
                            duration: 2000,
                            offset: 80
                        });
                        resolve();
                    })
                    .catch(() => {
                        reject();
                    });
            });
        }
    });

    //排序按钮点击时触发
    const onSort = (type) => {
        if (type === 'sort') {
            Object.assign(sortDialogConfig.value, {
                show: true,
                title: computed(() => t('角色排序'))
            });
        } else if (type === 'move') {
            Object.assign(moveConfigDialog.value, {
                show: true,
                title: computed(() => t('移动'))
            });
        }
    };
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
