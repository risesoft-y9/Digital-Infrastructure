<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-11-11 15:39:03
 * @Description: 应用资源详情
-->
<template>
    <y9Card :title="`${$t('基本信息')} - ${currTreeNodeInfo.name ? currTreeNodeInfo.name : ''}`">
        <template v-slot>
            <div v-show="currTreeNodeInfo.isManageable" class="basic-btns">
                <span v-if="editBtnFlag">
                    <el-button
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        class="global-btn-main"
                        type="primary"
                        @click="onActions('edit')"
                    >
                        <i class="ri-edit-line"></i>
                        {{ $t('编辑') }}
                    </el-button>
                </span>
                <span v-else>
                    <el-button
                        :loading="saveBtnLoading"
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        class="global-btn-main"
                        type="primary"
                        @click="onActions('save')"
                    >
                        <i class="ri-save-line"></i>
                        {{ $t('保存') }}
                    </el-button>
                    <el-button
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        class="global-btn-second"
                        @click="onActions('cancel')"
                    >
                        <i class="ri-close-line"></i>
                        {{ $t('取消') }}
                    </el-button>
                </span>

                <span style="margin-right: 10px">
                    <el-button
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        class="global-btn-main"
                        type="primary"
                        @click="handlerMenuClick"
                    >
                        <i class="ri-add-line"></i>
                        {{ $t('菜单') }}
                    </el-button>
                    <el-button
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        class="global-btn-main"
                        type="primary"
                        @click="handlerOperaClick"
                    >
                        <i class="ri-add-line"></i>
                        {{ $t('按钮') }}
                    </el-button>
                    <el-button
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        class="global-btn-main"
                        type="primary"
                        @click="onSort"
                    >
                        <i class="ri-order-play-line"></i>
                        {{ $t('排序') }}
                    </el-button>
                    <!-- <el-button class="global-btn-second" @click="operaFlag = true" >
                        <i class="ri-settings-3-fill"></i>
                        操作类型
                    </el-button>-->
                    <!-- <el-button class="global-btn-second" :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }" 
                    @click="handlerExportApp" v-if="currTreeNodeInfo.resourceType === 0">
                        <i class="ri-file-upload-line"></i>
                        {{ $t("导出") }}
                    </el-button> -->
                </span>
            </div>
            <div>
                <y9Form ref="y9FormRef" v-loading="loading" :config="y9FormConfig"></y9Form>
            </div>
        </template>
    </y9Card>

    <!-- 新增 菜单弹框 -->
    <y9Dialog v-model:config="addDialogConfig">
        <y9Form ref="ruleFormRef" :config="menuFormConfig"></y9Form>
    </y9Dialog>
    <!-- 资源排序 弹框 -->
    <y9Dialog v-model:config="sortDialogConfig">
        <treeSort
            ref="sortRef"
            :apiParams="{ parentId: currTreeNodeInfo.id }"
            :apiRequest="resourceTreeRoot"
            :columns="sortDialogConfig.columns"
        ></treeSort>
    </y9Dialog>
    <!-- 按钮操作 -->
    <y9Dialog v-model:config="addOperationConfig" @click.capture="remixIconPopVisible = false">
        <y9Form ref="operationFormRef" :config="operationFormConfig">
            <template #iconSelect>
                <el-popover v-model:visible="remixIconPopVisible" :width="400" placement="bottom">
                    <remix @clickIcon="onClickRemixIcon"></remix>
                    <template #reference>
                        <div
                            v-if="!operationUrl"
                            style="color: var(--el-color-primary)"
                            @click="remixIconPopVisible = true"
                            >{{ $t('点击选择图标') }}
                        </div>
                        <div v-else style="display: flex; align-items: center" @click="remixIconPopVisible = true">
                            <i :class="operationUrl" :style="{ fontSize: fontSizeObj.baseFontSize }"></i>
                        </div>
                    </template>
                </el-popover>
            </template>
        </y9Form>
    </y9Dialog>
</template>

<script lang="ts" setup>
    import { computed, h, inject, onMounted, ref, watch } from 'vue';
    import { ElNotification } from 'element-plus';
    import y9_storage from '@/utils/storage';
    import { useI18n } from 'vue-i18n';
    import { getMenuInfo, getOperationInfo, menuAdd, operationAdd, resourceTreeRoot, sort } from '@/api/resource/index';
    import { applicationAdd, applicationInfoGet } from '@/api/system/index';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { $validCheck } from '@/utils/validate';
    import settings from '@/settings';

    const settingStore = useSettingStore();
    const { t } = useI18n();
    const fontSizeObj: any = inject('sizeObjInfo');

    const props = defineProps({
        currTreeNodeInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            }
        },

        getTreeData: Function, //获取树数据

        postNode: Function, //请求某个节点，返回格式化好的数据

        findNode: Function, //在树数据中根据id找到对应的节点并返回

        getTreeInstance: Object, //树的实例

        handClickNode: Function //点击节点
    });

    let loading = ref(false);

    // 基本信息
    let basicInfo: any = ref({});
    let y9FormRef = ref();

    // 点击菜单 按钮
    function handlerMenuClick() {
        addDialogConfig.value.show = true;
        addDialogConfig.value.title = computed(() => t('新增菜单'));
    }

    // 菜单表单ref
    const ruleFormRef = ref();
    // 菜单 表单
    let menuForm: any = ref({ inherit: false, enabled: true });
    let menuFormConfig = ref({
        model: menuForm.value,
        rules: {
            name: [{ required: true, message: computed(() => t('请输入菜单名称')), trigger: 'blur' }]
        },
        itemList: [
            {
                type: 'input',
                prop: 'name',
                label: computed(() => t('菜单名称')),
                required: true
            },
            {
                type: 'input',
                prop: 'customId',
                label: computed(() => t('自定义ID'))
            },
            {
                type: 'input',
                prop: 'url',
                label: computed(() => t('链接地址'))
            },
            {
                type: 'radio',
                label: computed(() => t('是否启用')),
                prop: 'enabled',
                required: true,
                props: {
                    radioType: 'radio',
                    options: [
                        { label: computed(() => t('是')), value: true },
                        { label: computed(() => t('否')), value: false }
                    ]
                }
            },
            {
                type: 'radio',
                label: computed(() => t('是否继承')),
                required: true,
                prop: 'inherit',
                props: {
                    radioType: 'radio',
                    options: [
                        { label: computed(() => t('是')), value: true },
                        { label: computed(() => t('否')), value: false }
                    ]
                }
            },
            {
                type: 'input',
                prop: 'target',
                label: computed(() => t('打开位置'))
            },
            {
                type: 'input',
                prop: 'component',
                label: computed(() => t('菜单部件'))
            },
            {
                type: 'input',
                prop: 'iconUrl',
                label: computed(() => t('图标地址'))
            },
            {
                type: 'input',
                prop: 'meta',
                label: computed(() => t('元信息'))
            },
            {
                type: 'input',
                prop: 'tabIndex',
                label: computed(() => t('排列序号'))
            },
            {
                type: 'textarea',
                prop: 'description',
                label: computed(() => t('资源描述')),
                rows: 3
            }
        ],
        descriptionsFormConfig: {
            labelWidth: '200px',
            labelAlign: 'center'
        }
    });

    // 增加菜单 弹框的变量配置 控制
    let addDialogConfig = ref({
        show: false,
        title: computed(() => t('新增菜单')),
        width: '40%',
        onOkLoading: true,
        onOk: (newConfig) => {
            return new Promise(async (resolve, reject) => {
                const ruleFormInstance = ruleFormRef.value?.elFormRef;
                await ruleFormInstance.validate(async (valid) => {
                    if (valid) {
                        // 将数值为''的值去除
                        Object.keys(ruleFormRef.value?.model).forEach((key) => {
                            if (ruleFormRef.value?.model[key] == '' && ruleFormRef.value?.model[key] !== false) {
                                delete ruleFormRef.value?.model[key];
                                return;
                            }
                        });

                        let params = {
                            ...ruleFormRef.value.model,
                            parentId: basicInfo.value.id,
                            systemId: basicInfo.value.systemId,
                            appId: basicInfo.value.appId
                        };

                        let result = await menuAdd(params);
                        if (result.success) {
                            /**
                             * 对树进行操作：新增节点进入树
                             */
                            //1.更新当前节点的子节点信息
                            const treeData = props.getTreeData(); //获取tree数据
                            const currNode = props.findNode(treeData, basicInfo.value.id); //找到树节点对应的节点信息
                            const childData = await props.postNode({ id: basicInfo.value.id }); //重新请求当前节点的子节点，获取格式化后的子节点信息
                            Object.assign(currNode, { children: childData }); //合并节点信息
                            //2.手动设置点击当前节点
                            props.handClickNode(currNode); //手动设置点击当前节点
                        }
                        // 表单弹框消失 表单数据清空
                        menuForm.value = { inherit: false, enabled: true };
                        ElNotification({
                            title: result.success ? t('成功') : t('失败'),
                            message: result.success ? t('保存成功') : t('保存失败'),
                            type: result.success ? 'success' : 'error',
                            duration: 2000,
                            offset: 80
                        });
                        resolve();
                    } else {
                        reject();
                    }
                });
            });
        },
        visibleChange: (visible) => {
            if (!visible) {
                menuForm.value = { inherit: false, enabled: true };
            }
        }
    });

    // 点击按钮  按钮
    function handlerOperaClick() {
        addOperationConfig.value.show = true;
        addOperationConfig.value.title = computed(() => t('新增按钮'));
    }

    //按钮表单ref
    const operationFormRef = ref();
    // 按钮 增加表单
    let operationForm: any = ref({ inherit: false, enabled: true, displayType: 0 });
    let operationFormConfig = ref({
        model: operationForm.value,
        rules: {
            name: [{ required: true, message: computed(() => t('请输入按钮名称')), trigger: 'blur' }]
        },
        itemList: [
            {
                type: 'input',
                prop: 'name',
                label: computed(() => t('按钮名称')),
                required: true
            },
            {
                type: 'input',
                prop: 'customId',
                label: computed(() => t('自定义ID'))
            },
            {
                type: 'radio',
                label: computed(() => t('是否启用')),
                prop: 'enabled',
                required: true,
                props: {
                    radioType: 'radio',
                    options: [
                        { label: computed(() => t('是')), value: true },
                        { label: computed(() => t('否')), value: false }
                    ]
                }
            },
            {
                type: 'radio',
                label: computed(() => t('是否继承')),
                required: true,
                prop: 'inherit',
                props: {
                    radioType: 'radio',
                    options: [
                        { label: computed(() => t('是')), value: true },
                        { label: computed(() => t('否')), value: false }
                    ]
                }
            },
            {
                type: 'slot',
                props: {
                    slotName: 'iconSelect'
                },
                label: computed(() => t('选择图标'))
            },
            {
                type: 'radio',
                prop: 'displayType',
                label: computed(() => t('展示方式')),
                required: true,
                props: {
                    radioType: 'radio',
                    options: [
                        { label: computed(() => t('图标文本')), value: 0 },
                        { label: computed(() => t('图标')), value: 1 },
                        { label: computed(() => t('文本')), value: 2 }
                    ]
                }
            },
            {
                type: 'input',
                prop: 'url',
                label: computed(() => t('链接地址'))
            },
            {
                type: 'input',
                prop: 'eventName',
                label: computed(() => t('事件'))
            },
            {
                type: 'input',
                prop: 'tabIndex',
                label: computed(() => t('排列序号'))
            },
            {
                type: 'input',
                prop: 'description',
                label: computed(() => t('资源描述')),
                rows: 3
            }
        ],
        descriptionsFormConfig: {
            labelWidth: '200px',
            labelAlign: 'center'
        }
    });

    //选择图标气泡框
    let remixIconPopVisible = ref(false);
    let operationUrl = ref('');

    //点击选择图标
    function onClickRemixIcon(item) {
        operationUrl.value = item.class;
        remixIconPopVisible.value = false;
    }

    // 增加 按钮 按钮 的配置
    let addOperationConfig = ref({
        show: false,
        title: computed(() => t('新增按钮')),
        width: '40%',
        onOkLoading: true,
        onOk: (newConfig) => {
            return new Promise(async (resolve, reject) => {
                const operationFormInstance = operationFormRef.value?.elFormRef;
                await operationFormInstance.validate(async (valid) => {
                    if (valid) {
                        const params = {
                            iconUrl: operationUrl.value,
                            parentId: basicInfo.value.id,
                            systemId: basicInfo.value.systemId,
                            appId: basicInfo.value.appId,
                            ...operationFormRef.value?.model
                        };

                        // 将数值为''的值去除
                        Object.keys(params).forEach((key) => {
                            if (params[key] === '' && params[key] !== false) {
                                delete params[key];
                                return;
                            }
                        });

                        let result = await operationAdd(params);
                        /**
                         * 对树进行操作：新增节点进入树
                         */
                        if (result.success) {
                            //1.更新当前节点的子节点信息
                            const treeData = props.getTreeData(); //获取tree数据
                            const currNode = props.findNode(treeData, basicInfo.value.id); //找到树节点对应的节点信息
                            const childData = await props.postNode({ id: basicInfo.value.id }); //重新请求当前节点的子节点，获取格式化后的子节点信息
                            Object.assign(currNode, { children: childData }); //合并节点信息
                            //2.手动设置点击当前节点
                            props.handClickNode(currNode); //手动设置点击当前节点
                        }

                        // 表单弹框消失 表单数据清空
                        operationForm.value = { inherit: false, enabled: true, displayType: 0 };
                        ElNotification({
                            title: result.success ? t('成功') : t('失败'),
                            message: result.success ? t('保存成功') : t('保存失败'),
                            type: result.success ? 'success' : 'error',
                            duration: 2000,
                            offset: 80
                        });
                        resolve();
                    } else {
                        reject();
                    }
                });
            });
        },
        visibleChange: (visible) => {
            if (!visible) {
                operationForm.value = { inherit: false, enabled: true, displayType: 0 };
            }
        }
    });

    // 控制 基本信息 编辑按钮 与 保存，取消按钮的显示与隐藏
    let editBtnFlag = ref(true);
    // 保存 按钮的loading 控制
    let saveBtnLoading = ref(false);

    // 基本信息 编辑后保存
    async function handlerEditSave(data) {
        saveBtnLoading.value = true;
        // 更新基本信息 接口按钮--
        // data 为基本信息 数据
        let result;
        if (data.resourceType === 0) {
            result = await applicationAdd(data);
        } else if (data.resourceType === 1) {
            result = await menuAdd(data);
        } else {
            result = await operationAdd(data);
        }
        if (result.success) {
            /**
             * 对树进行操作：手动更新节点信息
             */
            //1.更新当前节点的信息
            const treeData = props.getTreeData(); //获取tree数据
            const currNode = props.findNode(treeData, basicInfo.value.id); //找到树节点对应的节点信息
            Object.assign(currNode, data); //合并节点信息
            //2.手动设置点击当前节点
            props.handClickNode(currNode); //手动设置点击当前节点
        }
        ElNotification({
            message: result.success ? t('保存成功') : t('保存失败'),
            type: result.success ? 'success' : 'error',
            duration: 2000,
            offset: 80
        });
        // loading为false 编辑 按钮出现 保存按钮未点击状态
        saveBtnLoading.value = false;
        editBtnFlag.value = true;
    }

    let sortRef = ref();
    let sortDialogConfig = ref({
        show: false,
        title: computed(() => t('综合排序')),
        width: '40%',
        onOkLoading: true,
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
                const ids = [];
                tableData.forEach((element) => {
                    ids.push(element.id);
                });
                let result = await sort(ids.toString());
                if (result.success) {
                    /**
                     * 对树进行操作：手动更新当前节点的子节点信息
                     */
                    //1.更新当前节点的子节点信息
                    const treeData = props.getTreeData(); //获取tree数据
                    const currNode = props.findNode(treeData, basicInfo.value.id); //找到树节点对应的节点信息
                    const childData = await props.postNode({ id: basicInfo.value.id }); //重新请求当前节点的子节点，获取格式化后的子节点信息
                    Object.assign(currNode, { children: childData }); //合并节点信息
                    //2.手动设置重新点击当前节点
                    props.handClickNode(currNode); //手动设置点击当前节点
                }
                ElNotification({
                    title: result.success ? t('成功') : t('失败'),
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });

                resolve();
            });
        }
    });

    // 导出 app
    function handlerExportApp() {
        const url =
            import.meta.env.VUE_APP_CONTEXT +
            'api/rest/impExp/exportAppJSON?appId=' +
            basicInfo.value.id +
            '&access_token=' +
            y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
        window.open(url);
    }

    //排序按钮点击时触发
    const onSort = () => {
        Object.assign(sortDialogConfig.value, {
            show: true,
            title: computed(() => t('综合排序'))
        });
    };

    const validateUrl = (rule: any, value: any, callback: any) => {
        let result = $validCheck('url', value, true);
        if (!result.valid) {
            callback(new Error(result.msg));
        } else {
            callback();
        }
    };

    const formList = [
        //表单显示列表
        {
            type: 'text',
            type1: 'input', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            prop: 'name',
            label: computed(() => t('名称')),
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.name);
                }
            }
        },
        {
            type: 'text',
            type1: 'text', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            prop: 'id',
            label: computed(() => t('唯一标识')),
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.id);
                }
            }
        },
        {
            type: 'text',
            type1: 'radio', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            prop: 'enabled',
            label: computed(() => t('是否启用')),
            props: {
                options: [
                    { label: computed(() => t('是')), value: true },
                    { label: computed(() => t('否')), value: false }
                ],
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.enabled ? t('是') : t('否'));
                }
            }
        },
        {
            type: 'text',
            type1: 'input', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            prop: 'customId',
            label: computed(() => t('自定义ID')),
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.customId);
                }
            }
        },
        {
            type: 'text',
            type1: 'radio', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            prop: 'hidden',
            label: computed(() => t('是否隐藏')),
            props: {
                options: [
                    { label: computed(() => t('是')), value: true },
                    { label: computed(() => t('否')), value: false }
                ],
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.hidden ? t('是') : t('否'));
                }
            }
        },
        {
            type: 'text',
            type1: 'radio', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            prop: 'inherit',
            label: computed(() => t('是否继承')),
            props: {
                options: [
                    { label: computed(() => t('是')), value: true },
                    { label: computed(() => t('否')), value: false }
                ],
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.inherit ? t('是') : t('否'));
                }
            }
        },
        {
            type: 'text',
            type1: 'radio', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            prop: 'displayType',
            label: computed(() => t('展示方式')),
            props: {
                options: [
                    { label: computed(() => t('图标文本')), value: 0 },
                    { label: computed(() => t('图标')), value: 1 },
                    { label: computed(() => t('文本')), value: 2 }
                ],
                render: () => {
                    //text类型渲染的内容
                    return h(
                        'span',
                        basicInfo.value?.displayType === 0
                            ? t('图标文本')
                            : basicInfo.value?.displayType === 1
                            ? t('图标')
                            : t('文本')
                    );
                }
            }
        },
        {
            type: 'text',
            type1: 'input', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('排列序号')),
            prop: 'tabIndex',
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.tabIndex);
                }
            }
        },
        {
            type: 'text',
            type1: 'radio', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            prop: 'showNumber',
            label: computed(() => t('是否显示数字')),
            props: {
                options: [
                    { label: computed(() => t('是')), value: true },
                    { label: computed(() => t('否')), value: false }
                ],
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.showNumber ? t('是') : t('否'));
                }
            }
        },
        {
            type: 'text',
            type1: 'input', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('获取数字的URL')),
            span: 2,
            prop: 'numberUrl',
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.numberUrl);
                }
            }
        },
        {
            type: 'text',
            type1: 'input', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('打开位置')),
            prop: 'target',
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.target);
                }
            }
        },
        {
            type: 'text',
            type1: 'input', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('菜单部件')),
            prop: 'component',
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.component);
                }
            }
        },
        {
            type: 'text',
            type1: 'input', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('链接地址')),
            prop: 'url',
            span: 2,
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.url);
                }
            }
        },
        {
            type: 'text',
            type1: 'input', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('副链接地址')),
            span: 2,
            prop: 'url2',
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.url2);
                }
            }
        },
        {
            type: 'text',
            type1: 'input', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('事件')),
            span: 2,
            prop: 'eventName',
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.eventName);
                }
            }
        },
        {
            type: 'text',
            type1: 'input', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('图标地址')),
            span: 2,
            prop: 'iconUrl',
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.iconUrl);
                }
            }
        },
        {
            type: 'text',
            type1: 'textarea', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('元信息')),
            span: 2,
            prop: 'meta',
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.meta);
                }
            }
        },
        {
            type: 'text',
            type1: 'input', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('角色管理URL')),
            span: 2,
            prop: 'roleAdminUrl',
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.roleAdminUrl);
                }
            }
        },
        {
            type: 'text',
            type1: 'textarea', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('资源管理URL')),
            span: 2,
            prop: 'resourceAdminUrl',
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.resourceAdminUrl);
                }
            }
        },
        {
            type: 'text',
            type1: 'textarea', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            prop: 'description',
            label: computed(() => t('描述')),
            span: 2,
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.description);
                }
            }
        }
    ];

    //表单配置
    let y9FormConfig = ref({
        descriptionsFormConfig: {
            //描述表单配置
            column: settingStore.device === 'mobile' ? 1 : 2,
            labelAlign: 'center',
            labelWidth: '150px',
            contentWidth: '200px'
        },
        model: {},
        rules: {}, //表单验证规则
        itemList: formList
    });

    // 请求详情 函数
    async function getInfo() {
        loading.value = true;
        let responseInfo;
        if (props.currTreeNodeInfo.nodeType === 'APP') {
            // 应用
            y9FormConfig.value.itemList = formList.filter(
                (item) =>
                    item.prop !== 'meta' &&
                    item.prop !== 'component' &&
                    item.prop !== 'target' &&
                    item.prop !== 'inherit' &&
                    item.prop !== 'displayType' &&
                    item.prop !== 'eventName'
            );
            y9FormConfig.value.itemList.forEach((item) => {
                if (item.prop === 'name') {
                    item.label = computed(() => t('应用名称'));
                }
            });
            responseInfo = await applicationInfoGet(props.currTreeNodeInfo.id);
        } else if (props.currTreeNodeInfo.nodeType === 'MENU') {
            // 菜单
            y9FormConfig.value.itemList = formList.filter(
                (item) =>
                    item.prop !== 'displayType' &&
                    item.prop !== 'eventName' &&
                    item.prop !== 'hidden' &&
                    item.prop !== 'showNumber' &&
                    item.prop !== 'numberUrl' &&
                    item.prop !== 'url2' &&
                    item.prop !== 'roleAdminUrl' &&
                    item.prop !== 'resourceAdminUrl'
            );
            y9FormConfig.value.itemList.forEach((item) => {
                if (item.prop === 'name') {
                    item.label = computed(() => t('菜单名称'));
                }
            });
            responseInfo = await getMenuInfo(props.currTreeNodeInfo.id);
        } else {
            // 按钮
            y9FormConfig.value.itemList = formList.filter(
                (item) =>
                    item.prop !== 'meta' &&
                    item.prop !== 'hidden' &&
                    item.prop !== 'showNumber' &&
                    item.prop !== 'numberUrl' &&
                    item.prop !== 'url2' &&
                    item.prop !== 'roleAdminUrl' &&
                    item.prop !== 'resourceAdminUrl' &&
                    item.prop !== 'component' &&
                    item.prop !== 'target'
            );
            y9FormConfig.value.itemList.forEach((item) => {
                if (item.prop === 'name') {
                    item.label = computed(() => t('按钮名称'));
                }
            });
            responseInfo = await getOperationInfo(props.currTreeNodeInfo.id);
        }
        basicInfo.value = responseInfo.data;
        loading.value = false;
    }

    onMounted(() => {
        getInfo();
    });
    // 监听系统id 当发生改变时重新请求数据
    watch(
        () => props.currTreeNodeInfo.id,
        async (new_, old_) => {
            if (new_ && new_ !== old_) {
                getInfo();
            }
        }
    );

    //改变y9Form显示类型
    function changeY9FormType(isEdit) {
        if (isEdit) {
            //编辑状态设置表单校验规则
            if (props.currTreeNodeInfo.nodeType === 'APP') {
                y9FormConfig.value.rules = {
                    name: [{ required: true, message: computed(() => t('请输入名称')), trigger: 'blur' }],
                    url: [
                        { required: true, message: computed(() => t('请输入链接地址')), trigger: 'blur' },
                        { validator: validateUrl, trigger: 'blur' }
                    ]
                };
            } else {
                y9FormConfig.value.rules = {
                    name: [{ required: true, message: computed(() => t('请输入名称')), trigger: 'blur' }]
                };
            }
        } else {
            y9FormConfig.value.rules = {};
        }

        //编辑模式显示type1类型 非编辑模式显示type2类型
        y9FormConfig.value.itemList.forEach((item) => {
            item.type = isEdit ? item.type1 : item.type2;
        });
    }

    async function onActions(type) {
        if (type == 'edit') {
            editBtnFlag.value = false;
            y9FormConfig.value.model = basicInfo.value;
            changeY9FormType(true);
        } else if (type == 'cancel') {
            editBtnFlag.value = true;
            changeY9FormType(false);
        } else if (type == 'save') {
            editBtnFlag.value = true;
            let valid = await y9FormRef?.value.elFormRef?.validate((valid) => valid); //获取表单验证结果;
            if (valid) {
                basicInfo.value = y9FormRef.value?.model;
                saveBtnLoading.value = true;
                // 更新基本信息 接口按钮--
                // data 为基本信息 数据
                let result;
                if (basicInfo.value.resourceType === 0) {
                    result = await applicationAdd(basicInfo.value);
                } else if (basicInfo.value.resourceType === 1) {
                    result = await menuAdd(basicInfo.value);
                } else {
                    result = await operationAdd(basicInfo.value);
                }
                if (result.success) {
                    /**
                     * 对树进行操作：手动更新节点信息
                     */
                    //1.更新当前节点的信息
                    const treeData = props.getTreeData(); //获取tree数据
                    const currNode = props.findNode(treeData, basicInfo.value.id); //找到树节点对应的节点信息
                    Object.assign(currNode, result.data); //合并节点信息
                    if (result.data.enabled) {
                        currNode.newName = currNode.name; //显示名称
                    } else {
                        currNode.newName = currNode.name + '[禁用]'; //显示名称
                    }
                    //2.手动设置点击当前节点
                    props.handClickNode(currNode); //手动设置点击当前节点
                }
                ElNotification({
                    message: result.success ? t('保存成功') : t('保存失败'),
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
                // loading为false 编辑 按钮出现 保存按钮未点击状态
                saveBtnLoading.value = false;
                changeY9FormType(false);
            }
        }
    }
</script>
<style lang="scss" scoped>
    .basic-btns {
        display: flex;
        justify-content: space-between;
        margin-bottom: 20px;
        flex-wrap: wrap;

        .btn-top {
            margin-bottom: 10px;
        }
    }
</style>
