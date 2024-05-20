<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-04-02 09:59:22
 * @Description: 应用管理
-->
<template>
    <div class="application">
        <!-- 应用列表表格 -->
        <y9Table
            ref="y9TableRef"
            :config="appListTableConfig"
            :filterConfig="filterConfig"
            @on-change="handlerGetData"
            @on-curr-page-change="onCurrPageChange"
            @on-page-size-change="onPageSizeChange"
        >
            <template v-slot:slotSearch>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-main"
                    type="primary"
                    @click="getAppList"
                >
                    <i class="ri-search-line"></i>
                    {{ $t('搜索') }}
                </el-button>
            </template>
            <template v-slot:slotBtns>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-second"
                    @click="handlerSort"
                >
                    <i class="ri-arrow-up-down-line"></i>
                    <span> {{ $t('排序') }}</span>
                </el-button>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-second"
                    @click="handlerAppAdd"
                >
                    <i class="ri-add-line"></i>
                    {{ $t('新增') }}
                </el-button>
                <el-button
                    v-loading.fullscreen.lock="loading"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-second"
                    @click="handlerDelete"
                >
                    <i class="ri-close-line" />
                    {{ $t('删除') }}
                </el-button>
                <el-button
                    v-loading.fullscreen.lock="loading"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-second"
                    @click="handlerDisableEnable(1)"
                >
                    <i class="ri-user-follow-line"></i>
                    {{ $t('启用') }}
                </el-button>
                <el-button
                    v-loading.fullscreen.lock="loading"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-second"
                    @click="handlerDisableEnable(2)"
                >
                    <i class="ri-user-unfollow-line"></i>
                    {{ $t('禁用') }}
                </el-button>
                <el-upload
                    :http-request="handlerUpload"
                    :show-file-list="false"
                    accept=".json"
                    style="display: inline-block; margin: 0 15px"
                >
                    <el-button class="global-btn-second">
                        <i class="ri-file-download-line"></i>
                        {{ $t('导入') }}
                    </el-button>
                </el-upload>
                <!-- <el-button  class="global-btn-second" >
                    <i class="ri-edit-line"></i>
                    查看修改日志
                </el-button> -->
            </template>
        </y9Table>
        <!-- 增加应用 -->
        <y9Dialog v-model:config="addDialogConfig">
            <template v-slot>
                <y9Form ref="ruleFormRef" :config="ruleFormConfig"></y9Form>
            </template>
        </y9Dialog>
        <!-- 应用图标的选择 -->
        <y9Dialog v-model:config="iconSelectDialog">
            <y9Table
                :config="iconSelectTable"
                :filterConfig="filterIconConfig"
                @on-curr-page-change="handelrPageChange"
                @on-page-size-change="handlerSizeChange"
            >
                <template v-slot:slotSearch>
                    <el-button
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        class="global-btn-main"
                        style="margin-left: 15px"
                        type="primary"
                        @click="handlerSearchIcon"
                    >
                        <i class="ri-search-line"></i>
                        {{ $t('搜索') }}
                    </el-button>
                </template>
            </y9Table>
        </y9Dialog>
        <!-- 查看 修改日志  -->
        <y9Dialog v-model:config="dialogModifyLog">
            <y9Table :config="modifyLogTableConfig" border></y9Table>
        </y9Dialog>
        <!-- 排序 -->
        <y9Dialog v-model:config="sortDialogConfig">
            <treeSort ref="sortRef" :apiParams="id" :apiRequest="getApplicationList"></treeSort>
        </y9Dialog>
    </div>
</template>

<script lang="ts" setup>
    import { computed, h, inject, onMounted, reactive, ref, watch } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import { useI18n } from 'vue-i18n';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { $validCheck } from '@/utils/validate';
    import {
        applicationAdd,
        applicationDel,
        applicationDisable,
        applicationEnable,
        applicationInfoGet,
        applicationList,
        appSaveOrder,
        getApplicationList
    } from '@/api/system/index';

    // 应用图标列表 搜索接口
    import { getAppIconPageList, searchIconPageByName } from '@/api/appIcon/index';
    import settings from '@/settings';
    import y9_storage from '@/utils/storage';
    import { importAppJSON } from '@/api/impExp';

    const { t } = useI18n();
    const settingStore = useSettingStore();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    // import { iconList } from '../data.js';

    // loading
    let loading = ref(false);
    // 表格行内表单 搜索应用的 表单
    const formInline = ref({
        name: ''
    });

    // 接收传过来的 系统id
    const props = defineProps({
        id: String
    });
    // 监听系统id 当发生改变时重新请求数据
    watch(
        () => props.id,
        async (new_, old_) => {
            if (new_ && new_ !== old_) {
                getAppList(true);
            }
        }
    );

    // 列表 总数
    let total = ref(0);
    // 选择应用的 id数据信息
    const ids = ref([]);

    // 表格 选择框 选择后获取数据
    function handlerGetData(id, data) {
        ids.value = id;
    }

    let y9TableRef = ref(null);

    // 请求 应用列表
    onMounted(() => {
        formInline.value.name = '';
        getAppList();
    });

    //当前页改变时触发
    function onCurrPageChange(currPage) {
        appListTableConfig.value.pageConfig.currentPage = currPage;
        getAppList();
    }

    //每页条数改变时触发
    function onPageSizeChange(pageSize) {
        appListTableConfig.value.pageConfig.pageSize = pageSize;
        getAppList();
    }

    // 应用 请求 列表 初始函数
    async function getAppList(flag = false) {
        appListTableConfig.value.loading = true;

        // 重置搜索条件
        if (flag == true && y9TableRef.value?.elTableFilterRef) {
            formInline.value.name = '';
            await y9TableRef.value?.elTableFilterRef.onReset();
        }

        let sendData = {
            page: appListTableConfig.value.pageConfig.currentPage,
            size: appListTableConfig.value.pageConfig.pageSize,
            systemId: props.id,
            name: formInline.value.name
        };
        const result = await applicationList(sendData);

        if (result.code == 0) {
            appListTableConfig.value.tableData = result.rows;
            appListTableConfig.value.pageConfig.total = result.total;
        }

        appListTableConfig.value.loading = false;
    }

    // 应用列表表格 配置 信息
    const appListTableConfig = ref({
        columns: [
            { title: '', type: 'selection', fixed: 'left', width: 70 },
            { title: computed(() => t('应用名称')), key: 'name', minWidth: 120 },
            { title: computed(() => t('链接地址')), key: 'url', minWidth: 200 },
            {
                title: computed(() => t('显示数字')),
                render: (row) => {
                    return row.showNumber ? '是' : '否';
                }
            },
            { title: computed(() => t('获取数字地址')), key: 'numberUrl', minWidth: 120 },
            {
                title: computed(() => t('所属类别')),
                render: (row) => {
                    switch (row.type) {
                        case 1:
                            return '业务协同';
                        case 2:
                            return '事项办理';
                        default:
                            return '数据服务';
                    }
                }
            },
            // { title: '排列序号', key: 'tabIndex' },
            {
                title: computed(() => t('是否启用')),
                render: (row) => {
                    return row.enabled ? '是' : '否';
                }
            },
            {
                title: computed(() => t('审核状态')),
                key: 'checked',
                render: (row) => {
                    return row.checked ? '已审核' : '未审核';
                }
            },
            { title: computed(() => t('创建日期')), key: 'createTime', width: 165 },
            { title: computed(() => t('更新日期')), key: 'updateTime', width: 165 },
            {
                title: computed(() => t('操作')),
                fixed: 'right',
                width: 100,
                showOverflowTooltip: false,
                render: (row) => {
                    return h('div', [
                        h(
                            'span',
                            {
                                onClick: async () => {
                                    const result = await applicationInfoGet(row.id);

                                    ruleFormConfig.value.model = result.data;
                                    addDialogConfig.value.show = true;
                                    addDialogConfig.value.title = computed(() => t('编辑应用'));
                                }
                            },
                            t('编辑')
                        ),
                        h(
                            'span',
                            {
                                style: {
                                    marginLeft: '10px',
                                    display: 'inline-flex',
                                    alignItems: 'center'
                                },
                                onClick: async () => {
                                    const url =
                                        import.meta.env.VUE_APP_CONTEXT +
                                        'api/rest/impExp/exportAppJSON?appId=' +
                                        row.id +
                                        '&access_token=' +
                                        y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
                                    window.open(url);
                                }
                            },
                            t('导出')
                        )
                    ]);
                }
            }
        ],
        loading: false,
        tableData: [],
        pageConfig: {
            // 分页配置，false隐藏分页
            currentPage: 1, //当前页数，支持 v-model 双向绑定
            pageSize: 10, //每页显示条目个数，支持 v-model 双向绑定
            total: total.value //总条目数
        }
    });
    let filterConfig = ref({
        showBorder: true,
        filtersValueCallBack: (filter) => {
            formInline.value = filter;
        },
        itemList: [
            {
                type: 'input',
                value: '',
                key: 'name',
                label: computed(() => t('应用名称')),
                span: settingStore.device === 'mobile' ? 16 : 5
            },
            {
                type: 'slot',
                slotName: 'slotSearch',
                span: settingStore.device === 'mobile' ? 8 : 4
            },
            {
                type: 'slot',
                slotName: 'slotBtns',
                span: settingStore.device === 'mobile' ? 24 : 15,
                justify: 'flex-end'
            }
        ]
    });

    // 应用添加  图标选择
    // 搜索 和src地址 id
    let name = ref('');
    // let iconData = ref(null);
    let iconUrl = ref(null);

    // 应用图标的选择
    async function handlerIconSelect() {
        iconSelectTable.value.loading = true;
        const params = {
            page: iconSelectTable.value.pageConfig.currentPage,
            rows: iconSelectTable.value.pageConfig.pageSize
        };
        let result = await getAppIconPageList(params);
        iconSelectTable.value.tableData = result.rows;
        iconSelectTable.value.pageConfig.total = result.total;
        iconSelectDialog.value.show = true;
        iconSelectDialog.value.title = computed(() => t('选择图标'));
        iconSelectTable.value.loading = false;
    }

    // 搜索 图标
    function handlerSearchIcon() {
        iconSelectTable.value.loading = true;
        setTimeout(async () => {
            const params = {
                page: iconSelectTable.value.pageConfig.currentPage,
                rows: iconSelectTable.value.pageConfig.pageSize,
                name: name.value
            };
            let result;
            if (name.value) {
                // 有搜索条件 请求搜索接口
                result = await searchIconPageByName(params);
            } else {
                result = await getAppIconPageList(params);
            }
            iconSelectTable.value.tableData = result.rows;
            iconSelectTable.value.pageConfig.total = result.total;
            iconSelectTable.value.loading = false;
        }, 300);
    }

    // dialog 图标选择
    let iconSelectDialog = ref({
        show: false,
        title: computed(() => t('选择图标')),
        width: '40%',
        showFooter: false
    });
    // 选择图标 列表 配置
    let iconSelectTable = ref({
        columns: [
            { title: computed(() => t('序号')), type: 'index', width: '100px' },
            {
                title: computed(() => t('图标')),
                showOverflowTooltip: false,
                key: 'iconData',
                imgConfig: {
                    type: 'base64'
                }
            },
            { title: computed(() => t('应用图标名称')), key: 'name' },
            {
                title: computed(() => t('操作')),
                showOverflowTooltip: false,
                render: (row) => {
                    return h(
                        'span',
                        {
                            onClick: () => {
                                // base64 转 url
                                ruleFormConfig.value.model = ruleFormRef.value?.model;
                                ruleFormConfig.value.model.iconData = `data:image/${row.type};base64,${row.iconData}`;

                                iconSelectDialog.value.show = false;
                                iconUrl.value = row.path;
                                name.value = '';
                            }
                        },
                        t('选择')
                    );
                }
            }
        ],
        tableData: [],
        pageConfig: {
            currentPage: 1, //当前页数，支持 v-model 双向绑定
            pageSize: 10, //每页显示条目个数，支持 v-model 双向绑定
            total: 0 //总条目数
        },
        loading: false
    });

    //当前页改变时触发
    function handelrPageChange(currPage) {
        iconSelectTable.value.pageConfig.currentPage = currPage;
        handlerIconSelect();
    }

    //每页条数改变时触发
    function handlerSizeChange(pageSize) {
        iconSelectTable.value.pageConfig.pageSize = pageSize;
        handlerIconSelect();
    }

    const filterIconConfig = ref({
        filtersValueCallBack: (filter) => {
            name.value = filter.name;
        },
        itemList: [
            {
                type: 'input',
                value: '',
                key: 'name',
                label: computed(() => t('应用图标名称')),
                span: 9
            },
            {
                type: 'slot',
                slotName: 'slotSearch',
                span: 6
            }
        ],
        showBorder: true
    });

    // 应用 添加 修改表单ref
    const ruleFormRef = ref();
    const validateUrl = (rule: any, value: any, callback: any) => {
        let result = $validCheck('url', value, true);
        if (!result.valid) {
            callback(new Error(result.msg));
        } else {
            callback();
        }
    };
    // 表单
    let ruleFormConfig = ref({
        //表单配置
        model: {
            name: '',
            url: '',
            iconData: null,
            opentype: 0,
            type: 1,
            enabled: true,
            showNumber: false
        },
        rules: {
            //	表单验证规则。类型：FormRules
            name: [{ required: true, message: computed(() => t('请输入应用名称')), trigger: 'blur' }],
            url: [
                { required: true, message: computed(() => t('请输入链接地址')), trigger: 'blur' },
                { validator: validateUrl, trigger: 'blur' }
            ]
        },
        itemList: [
            {
                type: 'input',
                label: computed(() => t('应用名称')),
                prop: 'name'
            },
            {
                type: 'input',
                label: computed(() => t('链接地址')),
                prop: 'url'
            },
            {
                type: 'select',
                label: computed(() => t('打开方式')),
                prop: 'opentype',
                props: {
                    options: [
                        //选项列表
                        { label: computed(() => t('在桌面窗口打开')), value: 0 },
                        { label: computed(() => t('在新浏览器窗口打开')), value: 1 }
                    ]
                }
            },
            {
                type: 'select',
                label: computed(() => t('所属类别')),
                prop: 'type',
                props: {
                    options: [
                        { value: 1, label: computed(() => t('业务协同')) },
                        { value: 2, label: computed(() => t('事项办理')) },
                        { value: 3, label: computed(() => t('数据服务')) }
                    ]
                }
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
                label: computed(() => t('是否显示数字')),
                prop: 'showNumber',
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
                type: 'text', //文本类型类型
                props: {
                    render: () => {
                        //文本内容
                        return h('div', { onClick: handlerIconSelect, className: 'icon-select' }, [
                            ruleFormConfig.value.model?.iconData
                                ? h('img', { src: ruleFormConfig.value.model?.iconData })
                                : h('span', { class: 'icon-text' }, t('点击获取图标'))
                        ]);
                    }
                },
                label: computed(() => t('应用图标')),
                prop: 'iconData'
            },
            {
                type: 'input',
                label: computed(() => t('角色管理的URL')),
                prop: 'roleAdminUrl'
            },
            {
                type: 'input',
                label: computed(() => t('资源管理的URL')),
                prop: 'resourceAdminUrl'
            },
            {
                type: 'input',
                label: computed(() => t('自定义ID')),
                prop: 'customId'
            },
            {
                type: 'input',
                label: computed(() => t('排序序号')),
                prop: 'tabIndex'
            },
            {
                type: 'textarea',
                label: computed(() => t('应用概述')),
                prop: 'description',
                props: {
                    //文本域类型的属性
                    rows: 3 //输入框行数,类型：number
                }
            }
        ],
        descriptionsFormConfig: {
            labelWidth: '200px',
            labelAlign: 'center'
        }
    });

    watch(
        () => ruleFormRef.value?.model?.showNumber,
        (newVal) => {
            if (newVal === true || newVal === false) {
                handlerChange(newVal);
            }
        }
    );

    // 单选框change
    function handlerChange(value) {
        ruleFormConfig.value.model = ruleFormRef.value?.model;
        ruleFormConfig.value.model.showNumber = value;
        if (value) {
            ruleFormConfig.value.itemList.splice(6, 0, {
                type: 'input',
                label: computed(() => t('获取数字地址')),
                prop: 'numberUrl',
                props: {
                    placeholder: '返回数据格式如:{count: 12345},默认参数为tenantId、userId'
                }
            });
        } else {
            ruleFormConfig.value.itemList = ruleFormConfig.value.itemList.filter((item) => item.prop !== 'numberUrl');
            ruleFormConfig.value.model.numberUrl = '';
        }
    }

    // 增加 修改应用 弹框的变量配置 控制
    let addDialogConfig = ref({
        show: false,
        title: computed(() => t('新增应用')),
        width: '40%',
        onOkLoading: true,
        onOk: (newConfig) => {
            return new Promise(async (resolve, reject) => {
                const y9RuleFormInstance = ruleFormRef.value?.elFormRef;
                await y9RuleFormInstance.validate(async (valid) => {
                    if (valid) {
                        ruleFormRef.value.model.iconUrl = iconUrl.value;

                        // Id 为null 表示当时为 添加
                        const params = {
                            systemId: props.id,
                            ...ruleFormRef.value?.model
                        };

                        // 将数值为''的值去除
                        Object.keys(params).forEach((key) => {
                            if (key == 'numberUrl') {
                                // 数字地址没有得传给后端 保存后链接地址才会没有
                                return;
                            } else if (params[key] === '' || params[key] === null) {
                                delete params[key];
                                return;
                            }
                        });
                        let result = { success: false, msg: '' } as any;
                        result = await applicationAdd(params);
                        ElNotification({
                            title: result.success ? t('成功') : t('失败'),
                            message: result.success ? t('操作成功') : result.msg,
                            type: result.success ? 'success' : 'error',
                            duration: 2000,
                            offset: 80
                        });
                        // 选中的数据容器 清空
                        ids.value = [];
                        // 更新成功后 表单的数据 清空
                        ruleFormConfig.value.model = {
                            name: '',
                            url: '',
                            iconData: null,
                            opentype: 0,
                            type: 1,
                            enabled: true,
                            showNumber: false
                        };
                        iconUrl.value = '';
                        ruleFormConfig.value.itemList = ruleFormConfig.value.itemList.filter(
                            (item) => item.prop !== 'numberUrl'
                        );
                        // 重新获取应用列表 数据
                        getAppList(true);
                        resolve();
                    } else {
                        reject();
                    }
                });
            });
        }
    });

    // 新增应用  函数事件
    function handlerAppAdd() {
        addDialogConfig.value.title = computed(() => t('新增应用'));
        ruleFormConfig.value.model = {
            name: '',
            url: '',
            iconData: null,
            opentype: 0,
            type: 1,
            enabled: true,
            showNumber: false
        };
        // editFlag.value = false;
        addDialogConfig.value.show = true;
    }

    // 删除
    function handlerDelete() {
        if (!ids.value.length > 0) {
            ElMessage({
                message: t('请选择应用'),
                type: 'warning',
                offset: 60
            });
        } else {
            // 选择某个应用后的 正常操作
            ElMessageBox.confirm(t(`是否删除所选应用?`), t('提示'), {
                confirmButtonText: t('确定'),
                cancelButtonText: t('取消'),
                type: 'info'
            })
                .then(async () => {
                    loading.value = true;
                    const result = await applicationDel(ids.value);
                    loading.value = false;
                    ElNotification({
                        title: t('成功'),
                        message: result.success ? t('删除成功') : t('删除失败'),
                        type: 'success',
                        duration: 2000,
                        offset: 80
                    });
                    // 重新获取应用列表 数据
                    getAppList();
                })
                .catch(() => {
                    ElMessage({
                        type: 'info',
                        message: t('已取消删除'),
                        offset: 65
                    });
                });
        }
    }

    // 导入
    function handlerUpload(params) {
        importAppJSON(params.file, props.id).then((res) => {
            ElNotification({
                title: t('成功'),
                message: res.msg,
                type: res.success ? 'success' : 'error',
                duration: 2000,
                offset: 80
            });
            if (res.success) {
                // 重新获取 数据
                getAppList(true);
            }
        });
    }

    function handlerSort() {
        sortDialogConfig.value.show = true;
        sortDialogConfig.value.title = computed(() => t('排序'));
    }

    // 查看 修改日志 的弹框 变量 配置控制
    let dialogModifyLog = reactive({
        show: false,
        title: computed(() => t('查看修改日志')),
        showFooter: false //是否显示底部
    });
    // 查看修改日志 的表格 配置信息
    const modifyLogTableConfig = ref({
        columns: [
            { title: '', type: 'index' },
            { title: computed(() => t('操作信息')), key: '' },
            { title: computed(() => t('描述')), key: '' },
            { title: 'id', key: '' }
        ],
        tableData: []
    });
    // 查看修改日志 事件
    // async function handlerModifyLog() {
    //     if (!ids.value.length > 0) {
    //         ElMessage({
    //             message: '请选择其中一个应用',
    //             type: 'warning',
    //             offset: 60,
    //         });
    //     } else {
    //         // 选择某个应用后的 正常操作 请求数据
    //         dialogModifyLog.show = true;
    //         // 赋值给表格数组
    //         // modifyLogTableConfig.value.tableData
    //     }
    // }

    // 禁启用 函数 事件
    function handlerDisableEnable(value: number) {
        if (!ids.value.length > 0) {
            ElMessage({
                message: t('请选择应用'),
                type: 'warning',
                offset: 60
            });
        } else {
            // 选择某个应用后的 正常操作
            // value 为1 启用 为2 为禁用
            const boxText = value === 1 ? '启用' : '禁用';
            ElMessageBox.confirm(t(`是否${boxText}该应用?`), t('提示'), {
                confirmButtonText: t('确定'),
                cancelButtonText: t('取消'),
                type: 'info'
            })
                .then(async () => {
                    loading.value = true;
                    let result;
                    if (boxText === '禁用') {
                        // 禁用操作
                        result = await applicationDisable(ids.value);
                    } else {
                        // 启用操作
                        result = await applicationEnable(ids.value);
                    }
                    loading.value = false;
                    // 成功提示
                    ElNotification({
                        message: result.success ? t(`${boxText}成功`) : t(`${boxText}失败`),
                        type: 'success',
                        duration: 2000,
                        offset: 80
                    });
                    // 重新获取应用列表 数据
                    getAppList();
                })
                .catch(() => {
                    loading.value = false;
                    ElMessage({
                        type: 'info',
                        message: t(`已取消${boxText}`),
                        offset: 65
                    });
                });
        }
    }

    let sortRef = ref();
    let sortDialogConfig = ref({
        show: false,
        title: '',
        width: '40%',
        onOkLoading: true,
        onOk: (newConfig) => {
            return new Promise(async (resolve, reject) => {
                let result = { success: false, msg: '' };

                let tableData = sortRef.value.tableConfig.tableData;
                const ids = [];
                tableData.forEach((element) => {
                    ids.push(element.id);
                });
                await appSaveOrder(ids.toString())
                    .then((res) => {
                        result = res;
                    })
                    .catch(() => {});

                ElNotification({
                    title: result.success ? t('成功') : t('失败'),
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
                if (result.success) {
                    // 重新获取应用列表 数据
                    getAppList(true);
                    resolve();
                } else {
                    reject();
                }
            });
        }
    });
</script>
<style lang="scss" scoped>
    .application {
        i {
            margin-right: 5px;
        }

        .form-img {
            width: 120px;
            height: 120px;
            border: 1px solid #eee;
        }
    }

    :deep(.is-plain) {
        border: 1px solid var(--el-color-primary);
        margin-right: 10px;
        border-radius: 5px;

        i {
            margin-right: 5px;
        }
    }

    // form 表单的布局
    // :deep(.y9-dialog-overlay .y9-dialog .y9-dialog-body .y9-dialog-content[data-v-0c2bb858]) {
    //     padding: 21px 45px 21px 21px !important;
    // }

    :deep(.el-form-item__content) {
        // 图标选择
        .icon-select {
            width: 100px;
            height: 100px;
            padding: 10px;
            text-align: center;
            display: flex;
            border: 1px solid #ddd;
            justify-content: center;
            align-items: center;

            img {
                width: 100%;
                height: 100%;
            }

            // 图标文字
            .icon-text {
                color: #bbb;
                cursor: pointer;
            }
        }
    }
</style>
