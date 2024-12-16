<template>
    <y9Card :title="`${$t('基本信息')} - ${currTreeNodeInfo.name ? currTreeNodeInfo.name : ''}`">
        <template v-slot>
            <div v-show="managerLevel === 1" class="basic-btns">
                <div v-if="currTreeNodeInfo.nodeType === 'DATA_CATALOG'">
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
                </div>
                <div v-else></div>

                <span style="margin-right: 10px">
                    <el-button
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        class="global-btn-second"
                        @click="handleClassificationClick"
                    >
                        <i class="ri-add-line"></i>
                        {{ $t('数据分类') }}
                    </el-button>
                    <el-button
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        class="global-btn-second"
                        @click="handleYearClick()"
                    >
                        <i class="ri-add-line"></i>
                        {{ $t('年份区间') }}
                    </el-button>
                    <el-button
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        class="global-btn-second"
                        @click="handleInitClick('组织架构', 'orgUnit')"
                    >
                        <i class="ri-add-line"></i>
                        {{ $t('组织架构') }}
                    </el-button>
                    <el-button
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        class="global-btn-second"
                        @click="handleInitClick('档案期限', 'retentionPeriod')"
                    >
                        <i class="ri-add-line"></i>
                        {{ $t('档案期限') }}
                    </el-button>
                    <el-button
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        class="global-btn-second"
                        @click="handleInitClick('保密期限', 'confidentialityPeriod')"
                    >
                        <i class="ri-add-line"></i>
                        {{ $t('保密期限') }}
                    </el-button>
                </span>
            </div>
            <div>
                <y9Form ref="y9FormRef" v-loading="loading" :config="y9FormConfig"></y9Form>
            </div>
        </template>
    </y9Card>

    <y9Dialog v-model:config="addClassificationDialogConfig">
        <y9Form ref="ruleFormRef" :config="catalogFormConfig"></y9Form>
    </y9Dialog>

    <y9Dialog v-model:config="addYearCatalogConfig">
        <y9Form ref="yearCatalogFormRef" :config="yearCatalogFormConfig"></y9Form>
    </y9Dialog>
</template>

<script lang="ts" setup>
    import { computed, h, inject, onMounted, ref, watch } from 'vue';
    import { ElNotification } from 'element-plus';
    import { useI18n } from 'vue-i18n';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { $validCheck } from '@/utils/validate';
    import { getDataCatalog, saveDataCatalog, saveDataCatalogByType, saveDataCatalogByYears } from '@/api/dataCatalog';
    import y9_storage from '@/utils/storage';

    const managerLevel = y9_storage.getObjectItem('ssoUserInfo', 'managerLevel');
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

        handClickNode: Function, //点击节点

        treeType: Object
    });

    let loading = ref(false);

    // 基本信息
    let basicInfo: any = ref({});
    let y9FormRef = ref();

    function handleClassificationClick() {
        addClassificationDialogConfig.value.show = true;
    }

    // 菜单表单ref
    const ruleFormRef = ref();
    // 菜单 表单
    let catalogForm: any = ref({});
    let catalogFormConfig = ref({
        model: catalogForm.value,
        rules: {
            name: [{ required: true, message: computed(() => t('请输入目录名称')), trigger: 'blur' }]
        },
        itemList: [
            {
                type: 'input',
                prop: 'name',
                label: computed(() => t('目录名称')),
                required: true
            },
            {
                type: 'input',
                prop: 'customId',
                label: computed(() => t('自定义ID'))
            },
            {
                type: 'input',
                prop: 'tabIndex',
                label: computed(() => t('排列序号'))
            },
            {
                type: 'textarea',
                prop: 'description',
                label: computed(() => t('描述')),
                rows: 3
            }
        ],
        descriptionsFormConfig: {
            labelWidth: '200px',
            labelAlign: 'center'
        }
    });

    // 增加菜单 弹框的变量配置 控制
    let addClassificationDialogConfig = ref({
        show: false,
        title: computed(() => t('新增业务分类目录')),
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
                            treeType: props.treeType
                        };

                        let result = await saveDataCatalog(params);
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
                        catalogForm.value = { inherit: false, enabled: true };
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
                catalogForm.value = { inherit: false, enabled: true };
            }
        }
    });

    //按钮表单ref
    const yearCatalogFormRef = ref();
    // 按钮 增加表单
    let yearCatalogForm: any = ref({});
    let yearCatalogFormConfig = ref({
        model: yearCatalogForm.value,
        rules: {
            yearRange: [{ required: true, message: computed(() => t('请选择年份')), trigger: 'blur' }]
        },
        itemList: [
            {
                type: 'date',
                prop: 'yearRange',
                label: computed(() => t('年份选择')),
                required: true,
                props: {
                    dateType: 'yearrange',
                    valueFormat: 'YYYY',
                    rangeSeparator: '至',
                    startPlaceholder: '开始年份',
                    endPlaceholder: '结束年份'
                }
            }
        ],
        descriptionsFormConfig: {
            labelWidth: '200px',
            labelAlign: 'center'
        }
    });

    let addYearCatalogConfig = ref({
        show: false,
        title: computed(() => t('新增年份目录')),
        width: '40%',
        onOkLoading: true,
        onOk: (newConfig) => {
            return new Promise(async (resolve, reject) => {
                const yearCatalogFormInstance = yearCatalogFormRef.value?.elFormRef;
                await yearCatalogFormInstance.validate(async (valid) => {
                    if (valid) {
                        const params = {
                            parentId: basicInfo.value.id,
                            treeType: props.treeType,
                            startYear: yearCatalogFormRef.value?.model['yearRange'][0],
                            endYear: yearCatalogFormRef.value?.model['yearRange'][1],
                            ...yearCatalogFormRef.value?.model
                        };

                        // 将数值为''的值去除
                        Object.keys(params).forEach((key) => {
                            if (params[key] === '' && params[key] !== false) {
                                delete params[key];
                                return;
                            }
                        });
                        delete params['yearRange'];

                        let result = await saveDataCatalogByYears(params);
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
                        yearCatalogForm.value = { inherit: false, enabled: true, displayType: 0 };
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
                yearCatalogForm.value = { inherit: false, enabled: true, displayType: 0 };
            }
        }
    });

    // 点击按钮  按钮
    function handleInitClick(title, dataCatalogType) {
        ElMessageBox.confirm(`是否创建 ${title} ？`, '提示', {
            confirmButtonText: '创建',
            cancelButtonText: '取消',
            type: 'info',
            center: true
        }).then(async () => {
            let params = {
                type: dataCatalogType,
                treeType: props.treeType,
                parentId: basicInfo.value.id
            };
            let result = await saveDataCatalogByType(params);

            if (result.success) {
                //1.更新当前节点的子节点信息
                const treeData = props.getTreeData(); //获取tree数据
                const currNode = props.findNode(treeData, basicInfo.value.id); //找到树节点对应的节点信息
                const childData = await props.postNode({ id: basicInfo.value.id }); //重新请求当前节点的子节点，获取格式化后的子节点信息
                Object.assign(currNode, { children: childData }); //合并节点信息
                //2.手动设置点击当前节点
                props.handClickNode(currNode); //手动设置点击当前节点
            }

            ElNotification({
                message: result.success ? t('创建成功') : t('创建失败'),
                type: result.success ? 'success' : 'error',
                duration: 2000,
                offset: 80
            });
        });
    }

    function handleYearClick() {
        addYearCatalogConfig.value.show = true;
    }

    // 控制 基本信息 编辑按钮 与 保存，取消按钮的显示与隐藏
    let editBtnFlag = ref(true);
    // 保存 按钮的loading 控制
    let saveBtnLoading = ref(false);

    // 基本信息 编辑后保存
    async function handlerEditSave(data) {
        saveBtnLoading.value = true;
        // 更新基本信息 接口按钮--
        // data 为基本信息 数据
        let result = await saveDataCatalog(data);
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
            label: computed(() => t('是否继承父权限')),
            prop: 'inherit',
            props: {
                radioType: 'radio',
                options: [{
                    label: '是',
                    value: true
                }, {
                    label: '否',
                    value: false
                }],
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.inherit ? t('是') : t('否'));
                }
            }
        },
        {
            type: 'text',
            type1: 'text', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('节点类型')),
            prop: 'nodeType',
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.nodeType);
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
        let responseInfo = await getDataCatalog(props.currTreeNodeInfo.id);
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
            y9FormConfig.value.rules = {
                name: [{ required: true, message: computed(() => t('请输入名称')), trigger: 'blur' }]
            };
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
                let result = await saveDataCatalog(basicInfo.value);
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
