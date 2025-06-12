<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2025-05-27 14:04:33
 * @Description: 公共角色管理
-->
<template>
    <fixedTreeModule
        ref="fixedTreeRef"
        :treeApiObj="treeApiObj"
        @onDeleteTree="roleRemove"
        @onTreeClick="handlerTreeClick"
    >
        <template v-slot:rightContainer>
            <!-- 右边卡片 -->
            <div v-if="currData.id">
                <y9Card :title="`${$t('基本信息')} - ${currData.name ? currData.name : ''}`">
                    <template v-slot>
                        <div v-if="managerLevel === 1" class="basic-btns">
                            <div class="btn-top">
                                <span v-if="currData.parentId !== null" style="margin-right: 15px">
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
                                <span v-if="currData.nodeType !== 'role'">
                                    <el-button
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
                                <el-button
                                    v-if="currData.nodeType !== 'role'"
                                    :size="fontSizeObj.buttonSize"
                                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                                    class="global-btn-second"
                                    @click="onSort('sort')"
                                >
                                    <i class="ri-order-play-line"></i>
                                    {{ $t('排序') }}
                                </el-button>
                            </div>
                        </div>
                        <BasicInfo
                            :id="currData.id"
                            :editFlag="editBtnFlag"
                            :saveClickFlag="saveBtnClick"
                            :type="currData.nodeType"
                            @getInfoData="handlerEditSave"
                        />
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
                        type="public"
                    ></Authorization>
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
        <treeSort ref="sortRef" :apiParams="{ parentId: currData.id }" :apiRequest="roleTreeList"></treeSort>
    </y9Dialog>

    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import y9_storage from '@/utils/storage';
    import { computed, inject, ref, watch } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import { useI18n } from 'vue-i18n';
    import {
        deleteRoleById,
        getPublicRoleTree,
        publicTreeSearch,
        roleTreeList,
        saveOrder,
        saveOrUpdate
    } from '@/api/role/index';
    // 基本信息
    import BasicInfo from './comps/BasicInfo.vue';
    import OrgBasesToRoles from './comps/OrgBasesToRoles.vue';
    import Authorization from './comps/Authorization.vue';
    import auditLog from '@/views/y9log/entityAuditLog/index.vue';
    import { useSettingStore } from '@/store/modules/settingStore';

    const settingStore = useSettingStore();
    const { t } = useI18n();
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
        topLevel: getPublicRoleTree,
        childLevel: {
            //子级（二级及二级以上）tree接口
            api: roleTreeList,
            params: {}
        },
        search: {
            //搜索接口及参数
            api: publicTreeSearch,
            params: {}
        }
    });

    // 树节点的 基本信息 获取
    function handlerTreeClick(data) {
        // 将拿到的节点信息 储存起来
        currData.value = data;
    }

    // 删除角色
    function roleRemove(data) {
        if (data.parentId == '' || data.parentId == null) {
            ElNotification({
                message: '顶节点不可删除',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }
        ElMessageBox.confirm(`${t('是否删除')}【${data.name}】?`, t('提示'), {
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
         * 手动更新节点信息
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
    // 系统的增加表单
    let roleForm = ref({
        name: '',
        customId: '',
        description: '',
        properties: '',
        type: 'role',
        parentId: ''
    });
    // config form
    const roleFormConfig = ref({
        model: roleForm.value,
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

    // 点击新增 角色 事件
    function handlerAddRole() {
        // 弹框出现 标题修改 角色事件  type
        addChildNodeDialog.value.title = computed(() => t('新增角色'));
        roleForm.value = {
            name: '',
            customId: '',
            description: '',
            properties: '',
            type: 'role',
            parentId: ''
        };
        roleForm.value.type = 'role';
        addChildNodeDialog.value.show = true;
    }

    // 增加子资源 弹框的 表单变量配置 控制
    let addChildNodeDialog = ref({
        show: false,
        title: '',
        width: '40%',
        onOkLoading: true,
        onOk: (newConfig) => {
            return new Promise(async (resolve, reject) => {
                const roleFormInstance = roleFormRef.value?.elFormRef;
                await roleFormInstance.validate(async (valid) => {
                    if (valid) {
                        loading.value = true;
                        const params = {
                            ...roleFormRef.value?.model
                        };
                        params.parentId = currData.value.id;
                        await saveOrUpdate(params)
                            .then(async (result) => {
                                /**
                                 * 对树进行操作：新增节点进入树
                                 */
                                //1.更新当前节点的子节点信息
                                const treeData = fixedTreeRef.value.getTreeData(); //获取tree数据
                                const currNode = fixedTreeRef.value.findNode(treeData, currData.value.id); //找到树节点对应的节点信息
                                const childData = await postNode({ id: currData.value.id }); //重新请求当前节点的子节点，获取格式化后的子节点信息
                                Object.assign(currNode, { children: childData }); //合并节点信息

                                //2.手动设置点击当前节点
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

                        // 表单 弹框消失 表单数据清空 重新刷新树列表
                        roleForm.value = {
                            name: '',
                            customId: '',
                            description: '',
                            properties: '',
                            type: 'role',
                            parentId: ''
                        };
                        loading.value = false;
                    } else {
                        reject();
                    }
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
        onOk: (newConfig) => {
            return new Promise(async (resolve, reject) => {
                let result = { success: false, msg: '' };
                let tableData = sortRef.value.tableConfig.tableData;
                const ids = [] as any;
                tableData.forEach((element) => {
                    ids.push(element.id);
                });
                await saveOrder(ids.toString())
                    .then((res) => {
                        result = res;
                    })
                    .catch(() => {});

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
                if (result.success) {
                    resolve();
                } else {
                    reject();
                }
            });
        }
    });

    function onSort(type) {
        if (type === 'sort') {
            Object.assign(sortDialogConfig.value, {
                show: true,
                title: computed(() => t('角色排序'))
            });
        }
    }

    // 监听 currData.type
    watch(
        () => currData.value,
        (newVal) => {}
    );

    //请求某个节点，返回格式化好的数据
    function postNode(node) {
        return new Promise((resolve, reject) => {
            fixedTreeRef.value.onTreeLazyLoad(node, (data) => {
                resolve(data);
            });
        });
    }

    //引入
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
