<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-11-11 09:13:38
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
                    <y9Table
                        v-model:selectedVal="tableCurrSelectedVal"
                        :config="tableConfig"
                        :filterConfig="filterConfig"
                        @on-curr-page-change="onTableCurrPageChange"
                        @on-page-size-change="onTablePageSizeChange"
                    >
                        <template v-slot:filterBtnSlot>
                            <el-button
                                :size="fontSizeObj.buttonSize"
                                :style="{ fontSize: fontSizeObj.baseFontSize }"
                                class="global-btn-main"
                                type="primary"
                                @click="initList"
                            >
                                <i class="ri-search-line"></i>
                                {{ $t('搜索') }}
                            </el-button>
                            <div class="widthBtn">
                                <el-button
                                    :size="fontSizeObj.buttonSize"
                                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                                    class="global-btn-second"
                                    @click="handlerClick('positive')"
                                >
                                    <i class="ri-add-line"></i>
                                    {{ $t('正权限成员') }}
                                </el-button>
                                <el-button
                                    :size="fontSizeObj.buttonSize"
                                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                                    class="global-btn-second"
                                    @click="handlerClick('negative')"
                                >
                                    <i class="ri-add-line"></i>
                                    {{ $t('负权限成员') }}
                                </el-button>
                            </div>
                            <el-button
                                :size="fontSizeObj.buttonSize"
                                :style="{ fontSize: fontSizeObj.baseFontSize }"
                                class="global-btn-second"
                                @click="handlerDeleteClick('roleMember')"
                            >
                                <i class="ri-delete-bin-line"></i>
                                {{ $t('删除') }}
                            </el-button>
                        </template>
                        <template #expandRowSlot="props">
                            <div class="expand-rows">
                                <p>名称: {{ props.row.orgUnitNamePath }}</p>
                                <p>成员类型: {{ props.row.orgTypeStr }}</p>
                                <p>负权限成员: {{ props.row.negative ? '是' : '否' }}</p>
                                <p>添加时间: {{ props.row.createTime }}</p>
                            </div>
                        </template>
                        <template #negativeSlot="props">
                            <boolWarningCell :is-true="props.row.negative"></boolWarningCell>
                        </template>
                    </y9Table>
                </y9Card>

                <y9Card
                    v-if="managerLevel === 2 && currData.nodeType === 'role'"
                    :title="`${$t('资源列表')} - ${currData.name ? currData.name : ''}`"
                >
                    <y9Table
                        v-model:selectedVal="resourceTableCurrSelectedVal"
                        :config="resourceTableConfig"
                        :filterConfig="resourcefilterConfig"
                        @on-curr-page-change="onResourceTableCurrPageChange"
                        @on-page-size-change="onResourceTablePageSizeChange"
                    >
                        <template v-slot:filterBtnSlot>
                            <!-- <el-button class="global-btn-main" @click="initList" type="primary">
					                <i class="ri-search-line"></i>
					                {{ $t('搜索') }}
					            </el-button> -->
                            <div class="widthBtn">
                                <el-button
                                    :size="fontSizeObj.buttonSize"
                                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                                    class="global-btn-second"
                                    @click="handlerClickResource"
                                >
                                    <i class="ri-add-line"></i>
                                    {{ $t('资源授权') }}
                                </el-button>
                            </div>
                            <el-button
                                :size="fontSizeObj.buttonSize"
                                :style="{ fontSize: fontSizeObj.baseFontSize }"
                                class="global-btn-second"
                                @click="handlerDeleteClick('resource')"
                            >
                                <i class="ri-close-line"></i>
                                {{ $t('删除') }}
                            </el-button>
                        </template>
                        <template #expandRowSlot="props">
                            <div class="expand-rows">
                                <p>资源名称: {{ props.row.resourceNamePath }}</p>
                                <p>资源类型: {{ props.row.resourceTypeStr }}</p>
                                <p>所属系统: {{ props.row.systemCnName }}</p>
                                <p>权限: {{ props.row.authorityStr }}</p>
                                <p>授权者: {{ props.row.authorizer }}</p>
                                <p>授权时间: {{ props.row.authorizeTime }}</p>
                            </div>
                        </template>
                        <template #authoritySlot="props">
                            <boolWarningCell
                                :is-true="props.row.authorityStr === '隐藏'"
                                :true-text="props.row.authorityStr"
                                :false-text="props.row.authorityStr"
                            ></boolWarningCell>
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
        <treeSort ref="sortRef" :apiParams="{ parentId: currData.id }" :apiRequest="roleTreeList"></treeSort>
    </y9Dialog>
    <!-- 正权限 负权限 -->
    <y9Dialog v-model:config="negativeConfigDialog">
        <selectTree
            ref="selectTreeRef"
            :defaultCheckedKeys="selectTreeDefaultCheckedKeys"
            :treeApiObj="negativeTreeApiObj"
            checkStrictly
        ></selectTree>
    </y9Dialog>

    <!-- 资源授权 -->
    <y9Dialog v-model:config="addAuthorizationConfigDialog">
        <y9Filter
            ref="resourceFilterRef"
            :filtersValueCallBack="sourceFiltersValueCallBack"
            :itemList="filtersListSource"
            showBorder
        >
            <template #treeFilter>
                <div class="custom-select-tree-filter">
                    <el-button
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        class="global-btn-second refresh-btn"
                        @click="onRefreshTree"
                    >
                        <i class="ri-refresh-line"></i>
                        <span>{{ $t('刷新') }}</span>
                    </el-button>

                    <input autocomplete="new-password" hidden type="password" />
                    <el-input
                        v-model="searchKey"
                        :placeholder="$t('请搜索')"
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        autocomplete
                        class="search-input"
                        name="select-tree-search"
                        type="search"
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
            :selectField="selectResourceField"
            :defaultCheckedKeys="resourceTreeDefaultCheckedKeys"
            :showHeader="false"
            :treeApiObj="sourceTreeApiObj"
            checkStrictly
        ></selectTree>
    </y9Dialog>
    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import y9_storage from '@/utils/storage';
    import { computed, inject, ref, watch } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import { useI18n } from 'vue-i18n';
    import {
        addOrgUnits,
        deleteRoleById,
        getPublicRoleTree,
        getRelateResourceList,
        listOrgUnitIdByRoleId,
        listResourceIdByRoleId,
        publicTreeSearch,
        removeAuthPermissionRecord,
        removeOrgUnits,
        roleTreeList,
        saveOrder,
        saveOrUpdate,
        saveOrUpdateRelateResource,
        searchByUnitName
    } from '@/api/role/index';
    import { getTreeItemById, searchByName, treeInterface } from '@/api/org/index';
    import { resourceTreeList, resourceTreeRoot, treeSearch } from '@/api/resource/index';
    // 基本信息
    import BasicInfo from './comps/BasicInfo.vue';
    import auditLog from '@/views/y9log/entityAuditLog/index.vue';
    import boolWarningCell from '@/components/BoolWarningCell/index.vue';
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

    // 角色成员  ------
    // 搜索条件
    let filterData = ref({ orgUnitName: '' });
    // 树 ref
    const selectTreeRef = ref();
    let selectTreeDefaultCheckedKeys = ref([]);

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
                key: 'orgUnitName',
                label: computed(() => t('名称')),
                span: settingStore.device === 'mobile' ? 24 : 5,
                clearable: true
            },
            {
                type: 'slot',
                slotName: 'filterBtnSlot',
                span: settingStore.device === 'mobile' ? 24 : 14
            }
        ]
    });

    // 表格 配置属性
    let tableConfig = ref({
        columns: [
            { type: 'selection', fixed: 'left', width: 70 },
            { type: 'expand', width: 40, slot: 'expandRowSlot' },
            { title: computed(() => t('序号')), type: 'index', width: 60, showOverflowTooltip: false },
            { title: computed(() => t('名称')), align: 'left', key: 'orgUnitNamePath' },
            { title: computed(() => t('成员类型')), width: 100, key: 'orgTypeStr' },
            {
                title: computed(() => t('负权限成员')),
                width: 100,
                key: 'negative',
                slot: 'negativeSlot'
            }
        ],
        tableData: [],
        pageConfig: {
            currentPage: 1, //当前页数，支持 v-model 双向绑定
            pageSize: 10, //每页显示条目个数，支持 v-model 双向绑定
            total: 10 //总条目数
        }
    });
    // 表格选中的数据
    let tableCurrSelectedVal = ref([] as any);

    // 监听 currData.type
    watch(
        () => currData.value,
        (newVal) => {
            if (newVal.nodeType === 'role') {
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
        let result = await searchByUnitName(
            tableConfig.value.pageConfig.currentPage,
            tableConfig.value.pageConfig.pageSize,
            currData.value.id,
            filterData.value.orgUnitName
        );

        tableConfig.value.pageConfig.total = result.total;
        tableConfig.value.tableData = result.rows;
    }

    //当前页改变时触发
    function onTableCurrPageChange(currPage) {
        tableConfig.value.pageConfig.currentPage = currPage;
        initList(); //获取列表
    }

    //每页条数改变时触发
    function onTablePageSizeChange(pageSize) {
        tableConfig.value.pageConfig.pageSize = pageSize;
        initList(); //获取列表
    }

    // 正权限 负权限 弹框
    let negativeConfigDialog = ref({
        show: false,
        title: '',
        width: '30%',
        onOk: () => {
            return new Promise(async (resolve, reject) => {
                let checkedIds = selectTreeRef.value?.y9TreeRef?.getCheckedKeys();
                // 只需要新选中的 id 数组
                const ids = checkedIds.filter((item) => !selectTreeDefaultCheckedKeys.value.includes(item));

                if (ids.length === 0) {
                    ElNotification({
                        title: t('失败'),
                        message: t('请选择需要添加权限的节点'),
                        type: 'error',
                        duration: 2000,
                        offset: 80
                    });
                    reject();
                    return;
                }

                let result;
                if (negativeConfigDialog.value.title == computed(() => t('添加正权限成员')).value) {
                    await addOrgUnits(currData.value.id, ids.join(','), false)
                        .then((res) => {
                            result = res;
                        })
                        .catch(() => {});
                } else if (negativeConfigDialog.value.title == computed(() => t('添加负权限成员')).value) {
                    await addOrgUnits(currData.value.id, ids.join(','), true)
                        .then((res) => {
                            result = res;
                        })
                        .catch(() => {});
                }
                ElNotification({
                    title: result.success ? t('成功') : t('失败'),
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
                // 重新请求接口
                initList();
                if (result.success) {
                    resolve();
                } else {
                    reject();
                }
            });
        }
    });

    async function getResourceTreeDefaultCheckedKeys() {
        let result = await listResourceIdByRoleId(currData.value.id, resourceOperationType.value);
        resourceTreeDefaultCheckedKeys.value = result.data;
    }

    function onSort(type) {
        if (type === 'sort') {
            Object.assign(sortDialogConfig.value, {
                show: true,
                title: computed(() => t('角色排序'))
            });
        }
    }

    // 点击 添加正权限 添加负权限
    async function handlerClick(type) {
        if (type === 'positive') {
            let result = await listOrgUnitIdByRoleId(currData.value.id, false);
            selectTreeDefaultCheckedKeys.value = result.data;
            negativeConfigDialog.value.title = computed(() => t('添加正权限成员'));
        } else {
            let result = await listOrgUnitIdByRoleId(currData.value.id, true);
            selectTreeDefaultCheckedKeys.value = result.data;
            negativeConfigDialog.value.title = computed(() => t('添加负权限成员'));
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
                    offset: 65
                });
                return;
            }
        } else if (type === 'resource') {
            if (!resourceTableCurrSelectedVal.value.length) {
                ElMessage({
                    type: 'warning',
                    message: t('请选择需要删除的资源'),
                    offset: 65
                });
                return;
            }
        }

        ElMessageBox.confirm(`${t('是否删除选中的数据')}?`, t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info'
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
                        offset: 80
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
                        offset: 80
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
                    offset: 65
                });
            });
    }

    // 正权限  负权限 请求的tree接口
    let negativeTreeApiObj = ref({
        topLevel: treeInterface,
        childLevel: {
            //子级（二级及二级以上）tree接口
            api: getTreeItemById,
            params: {
                treeType: 'tree_type_org',
                disabled: false
            }
        },
        search: {
            //搜索接口及参数
            api: searchByName,
            params: {
                treeType: 'tree_type_org',
                disabled: false
            }
        }
    });

    //资源列表选中的数据
    let resourceTableCurrSelectedVal = ref([]);
    //资源列表表格配置
    const resourceTableConfig = ref({
        columns: [
            { type: 'selection', fixed: 'left', width: 60 },
            { type: 'expand', width: 40, slot: 'expandRowSlot' },
            { title: computed(() => t('序号')), type: 'index', width: 60, showOverflowTooltip: false },
            { title: computed(() => t('资源名称')), align: 'left', key: 'resourceNamePath' },
            { title: computed(() => t('资源类型')), width: 120, key: 'resourceTypeStr' },
            { title: computed(() => t('所属系统')), key: 'systemCnName' },
            {
                title: computed(() => t('权限')),
                width: 80,
                key: 'authority',
                slot: 'authoritySlot'
            }
        ],
        tableData: [],
        pageConfig: {
            currentPage: 1, //当前页数，支持 v-model 双向绑定
            pageSize: 10, //每页显示条目个数，支持 v-model 双向绑定
            total: 10 //总条目数
        }
    });

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
                span: settingStore.device === 'mobile' ? 24 : 6
            }
        ]
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
    function onResourceTableCurrPageChange(currPage) {
        resourceTableConfig.value.pageConfig.currentPage = currPage;
        getRelateResourceListFn(); //获取列表
    }

    //每页条数改变时触发
    function onResourceTablePageSizeChange(pageSize) {
        resourceTableConfig.value.pageConfig.pageSize = pageSize;
        getRelateResourceListFn(); //获取列表
    }

    // 资源授权 ref
    const resourceFilterRef = ref(null);
    // 资源授权的 操作权限
    let resourceOperationType = ref(1);

    watch(resourceOperationType, (current, prev) => {
        getResourceTreeDefaultCheckedKeys();
    });

    const resourceSelectTree = ref();
    let resourceTreeDefaultCheckedKeys = ref([]);

    // 资源授权  弹框
    let addAuthorizationConfigDialog = ref({
        show: false,
        title: computed(() => t('添加资源授权')),
        width: '45%',
        onOk: () => {
            return new Promise(async (resolve, reject) => {
                let checkedIds = resourceSelectTree.value?.y9TreeRef?.getCheckedKeys();
                // 只需要新选中的 id 数组
                const ids = checkedIds.filter((item) => !resourceTreeDefaultCheckedKeys.value.includes(item));

                if (ids.length == 0) {
                    ElNotification({
                        title: t('失败'),
                        message: t('请选择需要授权的资源'),
                        type: 'error',
                        duration: 2000,
                        offset: 80
                    });
                    reject();
                    return;
                }

                // 保存操作
                const params = {
                    authority: resourceOperationType.value,
                    principalId: currData.value.id,
                    principalType: 0,
                    resourceIds: ids.toString()
                };

                await saveOrUpdateRelateResource(params)
                    .then((result) => {
                        ElNotification({
                            title: result.success ? t('成功') : t('失败'),
                            message: result.msg,
                            type: result.success ? 'success' : 'error',
                            duration: 2000,
                            offset: 80
                        });
                        // 重新请求接口
                        getRelateResourceListFn();
                        resolve();
                    })
                    .catch(() => {
                        reject();
                    });
            });
        }
    });

    function handlerClickResource() {
        getResourceTreeDefaultCheckedKeys();

        addAuthorizationConfigDialog.value.show = true;
        addAuthorizationConfigDialog.value.title = computed(() => t('添加资源授权'));
    }

    // 资源授权 弹框 选择树过滤
    let filtersListSource = ref([
        {
            type: 'slot',
            slotName: 'treeFilter',
            span: settingStore.device === 'mobile' ? 24 : 16
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
                        value: 0
                    },
                    {
                        label: computed(() => t('浏览')),
                        value: 1
                    },
                    {
                        label: computed(() => t('维护')),
                        value: 2
                    },
                    {
                        label: computed(() => t('管理')),
                        value: 3
                    }
                ]
            }
        }
    ]);

    function sourceFiltersValueCallBack(filters) {
        resourceOperationType.value = filters.operationType;
    }

    // 选择树的选择 框
    let selectResourceField = [
        {
            fieldName: 'nodeType',
            value: ['APP', 'MENU', 'OPERATION']
        }
    ];
    // 资源授权 请求的tree接口
    let sourceTreeApiObj = ref({
        topLevel: resourceTreeList,
        childLevel: {
            //子级（二级及二级以上）tree接口
            api: resourceTreeRoot,
            params: {}
        },
        search: {
            //搜索接口及参数
            api: treeSearch,
            params: {}
        }
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
