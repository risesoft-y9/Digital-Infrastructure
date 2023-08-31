<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2023-08-03 15:20:37
 * @Description: 公共角色管理
-->
<template>
    <div>
        <fixedTreeModule
            ref="fixedTreeRef"
            :treeApiObj="treeApiObj"
            :hiddenSearch="true"
            @onDeleteTree="roleRemove"
            @onTreeClick="handlerTreeClick"
        >
            <template v-slot:rightContainer>
                <!-- 右边卡片 -->
                <div v-if="currData.id">
                    <y9Card :title="`${$t('基本信息')} - ${currData.name ? currData.name : ''}`">
                        <template v-slot>
                            <div class="basic-btns" v-if="managerLevel === 1">
                                <div class="btn-top">
                                    <span style="margin-right: 15px" v-if="currData.parentId !== null">
                                        <el-button
                                            class="global-btn-main"
                                            type="primary"
                                            :size="fontSizeObj.buttonSize"
                                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                                            v-if="editBtnFlag"
                                            @click="editBtnFlag = false"
                                        >
                                            <i class="ri-edit-line"></i>
                                            {{ $t('编辑') }}
                                        </el-button>
                                        <span v-else>
                                            <el-button
                                                class="global-btn-main"
                                                :size="fontSizeObj.buttonSize"
                                                :style="{ fontSize: fontSizeObj.baseFontSize }"
                                                type="primary"
                                                :loading="saveBtnLoading"
                                                @click="saveBtnClick = true"
                                            >
                                                <i class="ri-save-line"></i>
                                                {{ $t('保存') }}
                                            </el-button>
                                            <el-button
                                                class="global-btn-second"
                                                :size="fontSizeObj.buttonSize"
                                                :style="{ fontSize: fontSizeObj.baseFontSize }"
                                                @click="editBtnFlag = true"
                                            >
                                                <i class="ri-close-line"></i>
                                                {{ $t('取消') }}
                                            </el-button>
                                        </span>
                                    </span>
                                    <span v-if="currData.type !== 'role'">
                                        <el-button
                                            class="global-btn-main"
                                            type="primary"
                                            :size="fontSizeObj.buttonSize"
                                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                                            @click="handlerAddRole"
                                        >
                                            <i class="ri-add-line"></i>
                                            {{ $t('角色') }}
                                        </el-button>
                                    </span>
                                </div>
                                <div>
                                    <el-button
                                        v-if="currData.type !== 'role'"
                                        class="global-btn-second"
                                        :size="fontSizeObj.buttonSize"
                                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                                        @click="onSort('sort')"
                                    >
                                        <i class="ri-order-play-line"></i>
                                        {{ $t('排序') }}
                                    </el-button>
                                </div>
                            </div>
                            <BasicInfo
                                :id="currData.id"
                                :type="currData.resourceType"
                                :editFlag="editBtnFlag"
                                :saveClickFlag="saveBtnClick"
                                @getInfoData="handlerEditSave"
                            />
                        </template>
                    </y9Card>
                    <!-- 角色成员 -->
                    <!--   -->
                    <y9Card
                        v-if="managerLevel === 2 && currData.type === 'role'"
                        :title="`${$t('角色成员')} - ${currData.name ? currData.name : ''}`"
                    >
                        <y9Table
                            v-model:selectedVal="tableCurrSelectedVal"
                            :config="tableConfig"
                            :filterConfig="filterConfig"
                        >
                            <template v-slot:filterBtnSlot>
                                <el-button
                                    class="global-btn-main"
                                    :size="fontSizeObj.buttonSize"
                                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                                    @click="initList"
                                    type="primary"
                                >
                                    <i class="ri-search-line"></i>
                                    {{ $t('搜索') }}
                                </el-button>
                                <div class="widthBtn">
                                    <el-button
                                        @click="handlerClick('positive')"
                                        :size="fontSizeObj.buttonSize"
                                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                                        class="global-btn-second"
                                    >
                                        <i class="ri-add-line"></i>
                                        {{ $t('正权限人员') }}
                                    </el-button>
                                    <el-button
                                        @click="handlerClick('negative')"
                                        :size="fontSizeObj.buttonSize"
                                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                                        class="global-btn-second"
                                    >
                                        <i class="ri-add-line"></i>
                                        {{ $t('负权限人员') }}
                                    </el-button>
                                </div>
                                <el-button
                                    @click="handlerDeleteClick('roleMember')"
                                    :size="fontSizeObj.buttonSize"
                                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                                    class="global-btn-second"
                                >
                                    <i class="ri-delete-bin-line"></i>
                                    {{ $t('删除') }}
                                </el-button>
                            </template>
                        </y9Table>
                    </y9Card>

                    <y9Card
                        v-if="managerLevel === 2 && currData.type === 'role'"
                        :title="`${$t('资源列表')} - ${currData.name ? currData.name : ''}`"
                    >
                        <y9Table
                            v-model:selectedVal="resourceTableCurrSelectedVal"
                            :config="resourceTableConfig"
                            :filterConfig="resourcefilterConfig"
                            @on-curr-page-change="onCurrPageChange"
                            @on-page-size-change="onPageSizeChange"
                        >
                            <template v-slot:filterBtnSlot>
                                <!-- <el-button class="global-btn-main" @click="initList" type="primary">
					                <i class="ri-search-line"></i>
					                {{ $t('搜索') }}
					            </el-button> -->
                                <div class="widthBtn">
                                    <el-button
                                        @click="handlerClickResource"
                                        :size="fontSizeObj.buttonSize"
                                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                                        class="global-btn-second"
                                    >
                                        <i class="ri-add-line"></i>
                                        {{ $t('资源授权') }}
                                    </el-button>
                                </div>
                                <el-button
                                    @click="handlerDeleteClick('resource')"
                                    :size="fontSizeObj.buttonSize"
                                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                                    class="global-btn-second"
                                >
                                    <i class="ri-close-line"></i>
                                    {{ $t('删除') }}
                                </el-button>
                            </template>
                        </y9Table>
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
            <treeSort ref="sortRef" :apiRequest="roleTreeList" :apiParams="{ parentId: currData.id }"></treeSort>
        </y9Dialog>
        <!-- 正权限 负权限 -->
        <y9Dialog v-model:config="negativeConfigDialog">
            <selectTree
                ref="selectTreeRef"
                checkStrictly
                :treeApiObj="negativeTreeApiObj"
                :selectField="selectField"
            ></selectTree>
        </y9Dialog>

        <!-- 资源授权 -->
        <y9Dialog v-model:config="iconSourceConfigDialog">
            <y9Filter
                ref="resourceFilterRef"
                :itemList="filtersListSource"
                :filtersValueCallBack="sourceFiltersValueCallBack"
                showBorder
            >
                <template #treeFilter>
                    <div class="custom-select-tree-filter">
                        <el-button
                            @click="onRefreshTree"
                            class="global-btn-second refresh-btn"
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                        >
                            <i class="ri-refresh-line"></i>
                            <span>{{ $t('刷新') }}</span>
                        </el-button>

                        <input type="password" hidden autocomplete="new-password" />
                        <el-input
                            class="search-input"
                            type="search"
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            name="select-tree-search"
                            v-model="searchKey"
                            :placeholder="$t('请搜索')"
                            autocomplete
                            @input="onSearchKeyChange"
                        >
                            <template #prefix>
                                <i class="ri-search-line"></i>
                            </template>
                        </el-input>
                    </div>
                </template>
            </y9Filter>
            <!-- tree树 -->
            <selectTree
                ref="resourceSelectTree"
                checkStrictly
                :treeApiObj="sourceTreeApiObj"
                :selectField="selectResourceField"
                :showHeader="false"
            ></selectTree>
        </y9Dialog>
        <el-button style="display: none" v-loading.fullscreen.lock="loading"></el-button>
    </div>
</template>

<script lang="ts" setup>
    import y9_storage from '@/utils/storage';
    import { inject, watch, computed, ref, toRefs } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import { useI18n } from 'vue-i18n';
    import {
        roleTreeList,
        saveOrUpdate,
        deleteRoleById,
        treeSelect,
        searchByUnitNameAndUnitDN,
        addOrgUnits, // 对正权限的保存
        removeOrgUnits, // 移除
        saveOrder,
        getRelateResourceList,
        removeAuthPermissionRecord,
        saveOrUpdateRelateResource,
        getPublicRoleTree,
    } from '@/api/role/index';
    import { treeInterface, getTreeItemById, searchByName } from '@/api/org/index';
    import { resourceTreeList, resourceTreeRoot, treeSearch } from '@/api/resource/index';
    // 基本信息
    import BasicInfo from './comps/BasicInfo.vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    const settingStore = useSettingStore();
    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const managerLevel = y9_storage.getObjectItem('ssoUserInfo', 'managerLevel');

    // 全局 loading
    let loading = ref(false);

    // 点击树节点 对应数据的载体
    let currData = ref({ id: '', name: '', parentId: '', type: 'node', resourceType: '' });

    // 树 ref
    const fixedTreeRef = ref();
    // 树的一级 子级的请求接口函数
    const treeApiObj = ref({
        topLevel: getPublicRoleTree,
        childLevel: {
            //子级（二级及二级以上）tree接口
            api: roleTreeList,
            params: {},
        },
        search: {
            //搜索接口及参数
            api: treeSelect,
            params: {},
        },
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
                offset: 80,
            });
            return;
        }
        ElMessageBox.confirm(`${t('是否删除')}【${data.name}】?`, t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info',
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
                    message: result.success ? t('删除成功') : t('删除失败'),
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80,
                });
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: t('已取消删除'),
                    offset: 65,
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
            message: result.success ? t('保存成功') : t('保存失败'),
            type: result.success ? 'success' : 'error',
            duration: 2000,
            offset: 80,
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
        systemCnName: '系统公共顶节点',
        systemName: 'Y9OrgHierarchyManagement',
        appCnName: '系统公共角色',
        appId: '',
        type: 'role',
        parentId: '',
    });
    // config form
    const roleFormConfig = ref({
        model: roleForm.value,
        rules: {
            name: [{ required: true, message: computed(() => t('请输入名称')), trigger: 'blur' }],
        },
        itemList: [
            {
                type: 'input',
                prop: 'name',
                label: computed(() => t('名称')),
                required: true,
            },
            {
                type: 'input',
                prop: 'systemName',
                label: computed(() => t('系统名称')),
            },
            {
                type: 'input',
                prop: 'systemCnName',
                label: computed(() => t('系统中文名称')),
            },
            {
                type: 'input',
                prop: 'customId',
                label: computed(() => t('ID')),
            },
            {
                type: 'textarea',
                prop: 'description',
                label: computed(() => t('描述')),
                rows: 3,
            },
            {
                type: 'textarea',
                prop: 'properties',
                label: computed(() => t('扩展属性')),
                rows: 3,
            },
        ],
        descriptionsFormConfig: {
            labelWidth: '200px',
            labelAlign: 'center',
        },
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
            systemCnName: '系统公共顶节点',
            systemName: 'Y9OrgHierarchyManagement',
            appCnName: '系统公共角色',
            appId: '',
            type: 'role',
            parentId: '',
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
                            ...roleFormRef.value?.model,
                        };
                        params.appId = currData.value.id;
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
                                    message: result.success ? t('新增成功') : t('新增失败'),
                                    type: result.success ? 'success' : 'error',
                                    duration: 2000,
                                    offset: 80,
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
                            systemCnName: '系统公共顶节点',
                            systemName: 'Y9OrgHierarchyManagement',
                            appCnName: '系统公共角色',
                            appId: '',
                            type: 'role',
                            parentId: '',
                        };
                        loading.value = false;
                    } else {
                        reject();
                    }
                });
            });
        },
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
                    offset: 80,
                });
                if (result.success) {
                    resolve();
                } else {
                    reject();
                }
            });
        },
    });

    // 角色成员  ------
    // 搜索条件
    let filterData = ref({ rolePersonName: '', deptName: '' });
    // 树 ref
    const selectTreeRef = ref();
    // 表格 过滤条件
    const filterConfig = ref({
        filtersValueCallBack: (filters) => {
            filterData.value = filters;
        },
        showBorder: true,
        itemList: [
            {
                type: 'input',
                value: '',
                key: 'rolePersonName',
                label: computed(() => t('名称')),
                span: settingStore.device === 'mobile' ? 24 : 5,
                clearable: true,
            },
            {
                type: 'input',
                value: '',
                key: 'deptName',
                label: computed(() => t('部门')),
                span: settingStore.device === 'mobile' ? 24 : 5,
                clearable: true,
            },
            {
                type: 'slot',
                slotName: 'filterBtnSlot',
                span: settingStore.device === 'mobile' ? 24 : 14,
            },
        ],
    });

    // 表格 配置属性
    let tableConfig = ref({
        columns: [
            { type: 'selection', fixed: 'left', width: 70 },
            { title: computed(() => t('序号')), type: 'index', width: 100, showOverflowTooltip: false },
            { title: computed(() => t('名称')), key: 'unitName' },
            { title: computed(() => t('类型')), key: 'unitTypeName' },
            { title: computed(() => t('所属部门')), key: 'unitDn' },
            { title: computed(() => t('是否为负权限成员')), key: 'negative' },
        ],
        tableData: [],
        pageConfig: false,
    });
    // 表格选中的数据
    let tableCurrSelectedVal = ref([] as any);

    // 监听 currData.type
    watch(
        () => currData.value,
        (newVal) => {
            if (newVal.type === 'role') {
                if (managerLevel === 2) {
                    initList();
                    getRelateResourceListFn();
                }
            }
        }
    );

    //请求某个节点，返回格式化好的数据
    function postNode(node) {
        return new Promise((resolve, reject) => {
            fixedTreeRef.value.onTreeLazyLoad(node, (data) => {
                resolve(data);
            });
        });
    }
    // 角色成员 请求 列表接口
    async function initList() {
        // filterData
        let result = await searchByUnitNameAndUnitDN(
            currData.value.id,
            filterData.value.rolePersonName,
            filterData.value.deptName
        );
        tableConfig.value.tableData = result.data;
    }
    // 正权限 负权限 弹框
    let negativeConfigDialog = ref({
        show: false,
        title: '',
        width: '30%',
        onOk: () => {
            return new Promise(async (resolve, reject) => {
                let ids = selectTreeRef.value?.y9TreeRef?.getCheckedKeys();

                if (ids.length === 0) {
                    ElNotification({
                        title: t('失败'),
                        message: t('请选择需要添加权限的节点'),
                        type: 'error',
                        duration: 2000,
                        offset: 80,
                    });
                    reject();
                    return;
                }

                let result;
                if (negativeConfigDialog.value.title == computed(() => t('添加正权限人员')).value) {
                    await addOrgUnits(currData.value.id, ids.join(','), false)
                        .then((res) => {
                            result = res;
                        })
                        .catch(() => {});
                } else if (negativeConfigDialog.value.title == computed(() => t('添加负权限人员')).value) {
                    await addOrgUnits(currData.value.id, ids.join(','), true)
                        .then((res) => {
                            result = res;
                        })
                        .catch(() => {});
                }
                ElNotification({
                    message: result.success ? t('添加权限成功') : t('添加权限失败'),
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80,
                });
                // 重新请求接口
                initList();
                if (result.success) {
                    resolve();
                } else {
                    reject();
                }
            });
        },
    });

    function onSort(type) {
        if (type === 'sort') {
            Object.assign(sortDialogConfig.value, {
                show: true,
                title: computed(() => t('角色排序')),
            });
        }
    }

    // 点击 添加正权限 添加负权限
    function handlerClick(type) {
        if (type === 'positive') {
            negativeConfigDialog.value.title = computed(() => t('添加正权限人员'));
        } else {
            negativeConfigDialog.value.title = computed(() => t('添加负权限人员'));
        }
        negativeConfigDialog.value.show = true;
    }

    // 点击删除 按钮
    function handlerDeleteClick(type) {
        if (type === 'roleMember') {
            if (!tableCurrSelectedVal.value.length) {
                ElMessage({
                    type: 'warning',
                    message: t('请选择需要删除的人员'),
                    offset: 65,
                });
                return;
            }
        } else if (type === 'resource') {
            if (!resourceTableCurrSelectedVal.value.length) {
                ElMessage({
                    type: 'warning',
                    message: t('请选择需要删除的资源'),
                    offset: 65,
                });
                return;
            }
        }

        ElMessageBox.confirm(`${t('是否删除选中的数据')}?`, t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info',
        })
            .then(async () => {
                loading.value = true;
                let ids = [] as any;
                if (type === 'roleMember') {
                    ids = tableCurrSelectedVal.value.map((item) => {
                        return item.id;
                    });
                    let result = await removeOrgUnits(ids.join(','));
                    loading.value = false;
                    ElNotification({
                        title: result.success ? t('成功') : t('失败'),
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80,
                    });
                    // 初始化数据
                    initList();
                    tableCurrSelectedVal.value = [];
                } else if (type === 'resource') {
                    ids = resourceTableCurrSelectedVal.value.map((item) => {
                        return item.id;
                    });
                    let result = await removeAuthPermissionRecord({ ids: ids.join(',') });
                    loading.value = false;
                    ElNotification({
                        title: result.success ? t('成功') : t('失败'),
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80,
                    });
                    // 初始化数据
                    getRelateResourceListFn();
                    resourceTableCurrSelectedVal.value = [];
                }
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: t('已取消删除'),
                    offset: 65,
                });
            });
    }

    // 选择树的选择 框
    let selectField = [
        {
            fieldName: 'orgType',
            value: ['Person', 'Position', 'Organization', 'Department'],
        },
    ];
    // 正权限  负权限 请求的tree接口
    let negativeTreeApiObj = ref({
        topLevel: treeInterface,
        childLevel: {
            //子级（二级及二级以上）tree接口
            api: getTreeItemById,
            params: {
                treeType: 'tree_type_org',
                disabled: false,
            },
        },
        search: {
            //搜索接口及参数
            api: searchByName,
            params: {
                treeType: 'tree_type_org',
            },
        },
    });

    //资源列表选中的数据
    let resourceTableCurrSelectedVal = ref([]);
    //资源列表表格配置
    const resourceTableConfig = ref({
        columns: [
            { type: 'selection', fixed: 'left', width: 60 },
            { title: computed(() => t('序号')), type: 'index', width: 60, showOverflowTooltip: false },
            { title: computed(() => t('资源名称')), key: 'resourceName' },
            {
                title: computed(() => t('操作')),
                key: 'authority',
                render: (row) => {
                    return getAuthrity(row.authority);
                },
            },
            { title: computed(() => t('授权者')), key: 'authorizer' },
            { title: computed(() => t('授权时间')), key: 'authorizeTime', width: settingStore.getDatetimeSpan },
            { title: computed(() => t('是否继承')), key: 'inherit' },
        ],
        tableData: [],
        pageConfig: {
            currentPage: 1, //当前页数，支持 v-model 双向绑定
            pageSize: 10, //每页显示条目个数，支持 v-model 双向绑定
            total: 10, //总条目数
        },
    });
    // 操作对应显示
    function getAuthrity(type) {
        switch (type) {
            case 0:
                return '隐藏';
            case 1:
                return '浏览';
            case 2:
                return '维护';
            case 3:
                return '管理';
            default:
                break;
        }
    }
    //资源列表搜索条件
    const resourceFilterData = ref({});
    // 资源列表表格 过滤条件
    const resourcefilterConfig = ref({
        filtersValueCallBack: (filters) => {
            resourceFilterData.value = filters;
        },
        showBorder: true,
        itemList: [
            {
                type: 'slot',
                slotName: 'filterBtnSlot',
                span: settingStore.device === 'mobile' ? 24 : 6,
            },
        ],
    });
    async function getRelateResourceListFn() {
        const result = await getRelateResourceList(
            currData.value.id,
            resourceTableConfig.value.pageConfig.currentPage,
            resourceTableConfig.value.pageConfig.pageSize
        );
        resourceTableConfig.value.tableData = result.rows;
        resourceTableConfig.value.pageConfig.total = result.total;
    }
    //当前页改变时触发
    function onCurrPageChange(currPage) {
        resourceTableConfig.value.pageConfig.currentPage = currPage;
        getRelateResourceListFn(); //获取列表
    }
    //每页条数改变时触发
    function onPageSizeChange(pageSize) {
        resourceTableConfig.value.pageConfig.pageSize = pageSize;
        getRelateResourceListFn(); //获取列表
    }

    // 资源授权 ref
    const resourceFilterRef = ref(null);
    // 资源授权的 操作权限
    let resourceOperationType = ref(1);

    const resourceSelectTree = ref();

    // 资源授权  弹框
    let iconSourceConfigDialog = ref({
        show: false,
        title: computed(() => t('添加资源授权')),
        width: '45%',
        onOk: () => {
            return new Promise(async (resolve, reject) => {
                let ids = resourceSelectTree.value?.y9TreeRef?.getCheckedKeys();
                if (ids.length == 0) {
                    ElNotification({
                        title: t('失败'),
                        message: t('请选择需要授权的资源'),
                        type: 'error',
                        duration: 2000,
                        offset: 80,
                    });
                    reject();
                    return;
                }

                // 保存操作
                const params = {
                    authority: resourceOperationType.value,
                    principalId: currData.value.id,
                    principalType: 0,
                    resourceIds: ids.toString(),
                };

                await saveOrUpdateRelateResource(params)
                    .then((result) => {
                        ElNotification({
                            message: result.success ? t('添加权限成功') : t('添加权限失败'),
                            type: result.success ? 'success' : 'error',
                            duration: 2000,
                            offset: 80,
                        });
                        // 重新请求接口
                        getRelateResourceListFn();
                        resolve();
                    })
                    .catch(() => {
                        reject();
                    });
            });
        },
    });
    function handlerClickResource() {
        iconSourceConfigDialog.value.show = true;
        iconSourceConfigDialog.value.title = computed(() => t('添加资源授权'));
    }

    // 资源授权 弹框 选择树过滤
    let filtersListSource = ref([
        {
            type: 'slot',
            slotName: 'treeFilter',
            span: settingStore.device === 'mobile' ? 24 : 16,
        },
        {
            type: 'select',
            value: 1,
            span: settingStore.device === 'mobile' ? 24 : 8,
            label: computed(() => t('操作权限')),
            key: 'operationType',
            props: {
                clearable: false,
                options: [
                    {
                        label: computed(() => t('隐藏')),
                        value: 0,
                    },
                    {
                        label: computed(() => t('浏览')),
                        value: 1,
                    },
                    {
                        label: computed(() => t('维护')),
                        value: 2,
                    },
                    {
                        label: computed(() => t('管理')),
                        value: 3,
                    },
                ],
            },
        },
    ]);

    function sourceFiltersValueCallBack(filters) {
        resourceOperationType.value = filters.operationType;
    }

    // 选择树的选择 框
    let selectResourceField = [
        {
            fieldName: 'resourceType',
            value: [0, 1, 2],
        },
    ];
    // 资源授权 请求的tree接口
    let sourceTreeApiObj = ref({
        topLevel: resourceTreeList,
        childLevel: {
            //子级（二级及二级以上）tree接口
            api: resourceTreeRoot,
            params: {},
        },
        search: {
            //搜索接口及参数
            api: treeSearch,
            params: {},
        },
    });
    const searchKey = ref();
    function onRefreshTree() {
        resourceSelectTree.value.onRefreshTree();
    }

    let searchTimer = null;
    function onSearchKeyChange(searchVal) {
        clearTimeout(searchTimer);
        searchTimer = setTimeout(() => {
            sourceTreeApiObj.value.search.params.key = searchVal;
        }, 500);
    }

    //引入
</script>
<style scoped lang="scss">
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
</style>
