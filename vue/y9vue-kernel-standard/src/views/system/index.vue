<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-11-11 16:13:31
 * @Description: 应用系统管理
-->
<template>
    <fixedTreeModule
        ref="fixedTreeRef"
        :hiddenSearch="true"
        :treeApiObj="treeApiObj"
        nodeLabel="cnName"
        @onDeleteTree="systemRemove"
        @onTreeClick="handlerTreeClick"
    >
        <template v-slot:treeHeaderRight>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-second"
                @click="onClickBtn('sort', '排序')"
            >
                <i class="ri-arrow-up-down-line"></i>
                <span> {{ $t('排序') }}</span>
            </el-button>
            <el-upload
                :http-request="handlerUpload"
                :show-file-list="false"
                accept=".json"
                style="display: inline-block; margin: 0 10px"
            >
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-second"
                >
                    <i class="ri-file-download-line"></i>
                    {{ $t('导入') }}
                </el-button>
            </el-upload>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-main"
                type="primary"
                @click="onClickBtn('addSystem', '新增系统')"
            >
                <i class="ri-add-line"></i>
                <span>{{ $t('系统') }}</span>
            </el-button>
        </template>
        <template v-slot:rightContainer>
            <div v-if="currData.id">
                <y9Card :title="`${$t('基本信息')} - ${currData.cnName ? currData.cnName : ''}`">
                    <template v-slot>
                        <div v-show="currData.isManageable" class="basic-btns">
                            <span class="btn-top">
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
                                <!-- <el-upload accept=".json" :http-request="handlerUpload" style="display: inline-block; margin: 0 15px" :show-file-list="false">
                                            <el-button class="global-btn-second" :size="fontSizeObj.buttonSize"
                                            :style="{ fontSize: fontSizeObj.baseFontSize }">
                                                <i class="ri-file-download-line"></i>
                                                {{ $t("导入") }}
                                            </el-button>
                                        </el-upload> -->
                                <el-button
                                    :size="fontSizeObj.buttonSize"
                                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                                    class="global-btn-second"
                                    @click="handlerExport"
                                >
                                    <i class="ri-file-upload-line" />
                                    {{ $t('导出') }}
                                </el-button>
                                <el-button
                                    v-loading.fullscreen.lock="loading"
                                    :size="fontSizeObj.buttonSize"
                                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                                    class="global-btn-second"
                                    @click="handlerDisable"
                                >
                                    <i class="ri-user-unfollow-line"></i>
                                    {{ currData.enabled ? $t('禁用') : $t('启用') }}
                                </el-button>
                            </span>
                        </div>
                        <BasicInfo
                            :id="currData.id"
                            :editFlag="editBtnFlag"
                            :saveClickFlag="saveBtnClick"
                            @getSystemData="handlerEditSave"
                        />
                    </template>
                </y9Card>
                <y9Card
                    v-show="currData.isManageable"
                    :title="`${$t('应用管理')} - ${currData.cnName ? currData.cnName : ''}`"
                >
                    <template v-slot>
                        <ApplicatManager :id="currData.id" />
                    </template>
                </y9Card>
                <audit-log v-show="currData.isManageable" :currTreeNodeInfo="currData"></audit-log>
            </div>
        </template>
    </fixedTreeModule>
    <!-- 新增系统 弹框、 -->
    <y9Dialog v-model:config="dialogConfig">
        <y9Form v-if="dialogConfig.type === 'addSystem'" ref="ruleRef" :config="formSystem"></y9Form>
        <treeSort
            v-if="dialogConfig.type == 'sort'"
            ref="sortRef"
            :apiRequest="systemList"
            :columns="dialogConfig.columns"
            :currInfo="currData"
        ></treeSort>
    </y9Dialog>
    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import type { FormRules } from 'element-plus';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import { inject, reactive, ref } from 'vue';
    import { useI18n } from 'vue-i18n';
    import y9_storage from '@/utils/storage';
    import settings from '@/settings';
    // 基本信息
    import BasicInfo from './comps/BasicInfo.vue';
    // 应用管理
    import ApplicatManager from './comps/ApplicatMana.vue';
    import auditLog from '@/views/y9log/entityAuditLog/index.vue';
    import {
        importSystemJSON,
        removeSystem,
        systemAdd,
        systemDisabled,
        systemEnabled,
        systemList,
        systemSaveOrder
    } from '@/api/system/index';

    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    // loading
    let loading = ref(false);

    // 点击树节点 对应数据的载体
    let currData = ref({} as any);

    // 点击树  拿到对应数据
    function handlerTreeClick(currTreeNode) {
        if (!editBtnFlag.value) editBtnFlag.value = true;
        // 将拿到的数据 里的id赋值给 系统id变量
        currData.value = currTreeNode;
    }

    // 左边树 ref
    const fixedTreeRef = ref();
    // 树的一级 子级, 搜索的请求接口函数
    const treeApiObj = ref({
        topLevel: async () => {
            let data = [];
            const res = await systemList();
            data = res.data;
            data.forEach((item) => {
                item.isLeaf = true;
            });
            return data;
        }
        // childLevel:{//子级（二级及二级以上）tree接口
        //     api:applicationList,
        //     params:{
        //         systemId: currData.value.id
        //     }
        // },
        // search: {
        //     //搜索接口及参数
        //     api: systemList,
        //     params: {},
        // },
    });

    //请求某个节点，返回格式化好的数据
    function postNode(node) {
        return new Promise((resolve, reject) => {
            fixedTreeRef.value.onTreeLazyLoad(node, (data) => {
                resolve(data);
            });
        });
    }

    // 删除系统
    function systemRemove(data) {
        ElMessageBox.confirm(`${t('是否删除')}【${data.cnName}】?`, t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info'
        })
            .then(async () => {
                loading.value = true;
                // 删除系统
                const result = await removeSystem(data.id);
                loading.value = false;

                if (result.success) {
                    /**
                     * 对树进行操作
                     */
                    //1.需要手动点击的节点信息，如果有父节点则默认点击父节点，没有则点击tree数据的第一个节点
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
                }
                ElNotification({
                    title: result.success ? t('成功') : t('失败'),
                    message: result.success ? t('删除成功') : result.msg,
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

    // 名字 的验证规则
    const validateName = (rule: any, value: any, callback: any) => {
        // const reg = @zz
        if (!value) {
            callback(new Error(t('请输入系统名称')));
            return;
        }
        if (!/^[A-Za-z]\w{0,}$/.test(value)) {
            callback(new Error(t('不能输入中文，只能输入字母，数字和下划线')));
            return;
        }

        callback();
    };
    // 系统表单ref-------
    const ruleRef = ref();
    // 系统的增加表单
    let systemForm = ref({
        enabled: true
    });
    //
    // rule规则 表单 添加系统
    const rules = reactive<FormRules>({
        cnName: [
            {
                required: true,
                message: t('请输入系统中文名称'),
                trigger: 'blur'
            }
        ],
        name: [
            {
                required: true,
                validator: validateName,
                trigger: 'blur'
            }
        ]
    });
    // form 组件的config 值
    const formSystem = ref({
        model: systemForm.value,
        rules: rules,
        labelWidth: '120px',
        itemList: [
            {
                type: 'input',
                props: {
                    type: 'text'
                },
                label: t('系统中文名称'),
                prop: 'cnName',
                required: true
            },
            {
                type: 'input',
                props: {
                    type: 'text'
                },
                label: t('系统名称'),
                prop: 'name',
                required: true
            },
            {
                type: 'radio',
                props: {
                    radioType: 'radio',
                    options: [
                        { label: t('是'), value: true },
                        { label: t('否'), value: false }
                    ]
                },
                label: t('是否启用'),
                prop: 'enabled'
            },
            {
                type: 'input',
                props: {
                    type: 'text'
                },
                label: t('应用上下文'),
                prop: 'contextPath'
            },
            {
                type: 'input',
                props: {
                    type: 'text'
                },
                label: t('排列序号'),
                prop: 'tabIndex'
            },
            {
                type: 'input',
                props: {
                    type: 'textarea',
                    row: 3
                },
                label: t('系统概述'),
                prop: 'description'
            }
        ],
        descriptionsFormConfig: {
            labelWidth: '200px',
            labelAlign: 'center'
        }
    });

    let sortRef = ref();
    // 增加系统 弹框的变量配置 控制
    let dialogConfig = ref({
        show: false,
        title: '',
        width: '40%',
        onOkLoading: true,
        type: '',
        columns: [],
        onOk: (newConfig) => {
            return new Promise(async (resolve, reject) => {
                if (newConfig.value.type == 'addSystem') {
                    const ruleFormRef = ruleRef.value.elFormRef;
                    if (!ruleFormRef) return;
                    await ruleFormRef.validate(async (valid, fields) => {
                        if (valid) {
                            // 通过验证
                            // 请求 新增系统 接口
                            let res = { success: false, msg: '' } as any;
                            res = await systemAdd(ruleRef.value.model);
                            if (res.success) {
                                /**
                                 * 对树进行操作
                                 */
                                //1.更新一级节点数据
                                const treeData = await postNode({ $level: 0 }); //重新请求一级节点
                                await fixedTreeRef.value.setTreeData(treeData);

                                //2.手动设置点击当前节点
                                const currNode = fixedTreeRef.value.findNode(treeData, res.data.id); //找到树节点对应的节点信息
                                fixedTreeRef.value.handClickNode(currNode);
                            }
                            // 清空表单 数据
                            systemForm.value = { enabled: true };
                            ElNotification({
                                title: res.success ? t('成功') : t('失败'),
                                message: res.success ? t('保存成功') : res.msg,
                                type: res.success ? 'success' : 'error',
                                duration: 2000,
                                offset: 80
                            });
                            resolve();
                        } else {
                            reject();
                        }
                    });
                } else if (newConfig.value.type == 'sort') {
                    let result = { success: false, msg: '' };

                    let tableData = sortRef.value.tableConfig.tableData;
                    const ids = [];
                    tableData.forEach((element) => {
                        ids.push(element.id);
                    });
                    result = await systemSaveOrder(ids.toString());
                    // 重新刷新树 数据
                    fixedTreeRef.value.onRefreshTree();
                    ElNotification({
                        title: result.success ? t('成功') : t('失败'),
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    resolve();
                }
            });
        }
    });

    const onClickBtn = (type, title) => {
        Object.assign(dialogConfig.value, {
            show: true,
            title: t(`${title}`),
            type: type,
            showFooter: true,
            columns:
                type == 'sort'
                    ? [
                          {
                              type: 'radio',
                              title: '请选择',
                              width: 200
                          },
                          {
                              title: '名称',
                              key: 'name'
                          },
                          {
                              title: '中文名称',
                              key: 'cnName'
                          }
                      ]
                    : []
        });
    };

    // 点击保存按钮 的 flag
    let saveBtnClick = ref(false);
    // 控制 基本信息 编辑按钮 与 保存，取消按钮的显示与隐藏
    let editBtnFlag = ref(true);
    // 保存 按钮 loading
    let saveBtnLoading = ref(false);

    // 基本信息 点击保存 后 进行 接口操作
    async function handlerEditSave(data) {
        saveBtnLoading.value = true;
        // 更新基本信息 接口操作 --
        // data 为基本信息 数据
        let res = { success: false, msg: '' } as any;
        res = await systemAdd(data);
        if (res.success) {
            /**
             * 对树进行操作：手动更新节点信息
             */
            //1.更新当前节点的信息
            const treeData = fixedTreeRef.value.getTreeData(); //获取tree数据
            const currNode = fixedTreeRef.value.findNode(treeData, currData.value.id); //找到树节点对应的节点信息
            Object.assign(currNode, data); //合并节点信息
            //2.手动设置点击当前节点
            fixedTreeRef.value?.handClickNode(currNode); //手动设置点击当前节点
        }
        ElNotification({
            title: res.success ? t('成功') : t('失败'),
            message: res.success ? t('更新成功') : res.msg,
            type: res.success ? 'success' : 'error',
            duration: 2000,
            offset: 80
        });
        // loading为false 编辑 按钮出现 保存按钮未点击状态
        saveBtnLoading.value = false;
        editBtnFlag.value = true;
        saveBtnClick.value = false;
    }

    // 导入
    function handlerUpload(params) {
        importSystemJSON(params.file).then((res) => {
            ElNotification({
                title: t('成功'),
                message: res.msg,
                type: res.success ? 'success' : 'error',
                duration: 2000,
                offset: 80
            });
            if (res.success) {
                // 重新刷新树 数据
                fixedTreeRef.value.onRefreshTree();
            }
        });
    }

    // 导出
    function handlerExport() {
        const url =
            import.meta.env.VUE_APP_CONTEXT +
            'api/rest/impExp/exportSystemJSON?systemId=' +
            currData.value.id +
            '&access_token=' +
            y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
        window.open(url);
    }

    // 启用  禁用系统
    function handlerDisable() {
        const text = currData.value.enabled ? '禁用' : '启用';
        ElMessageBox.confirm(t(`是否${text}该系统?`), t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info'
        })
            .then(async () => {
                loading.value = true;
                let result;
                if (text === '禁用') {
                    // 禁用系统 接口操作
                    result = await systemDisabled(currData.value.id);
                } else {
                    // 启用 系统 接口操作
                    result = await systemEnabled(currData.value.id);
                }
                loading.value = false;
                if (result.success) {
                    ElNotification({
                        title: t('成功'),
                        message: t(`${text}成功`),
                        type: 'success',
                        duration: 2000,
                        offset: 80
                    });
                }
            })
            .catch(() => {
                loading.value = false;
                ElMessage({
                    type: 'info',
                    message: t(`已取消${text}`),
                    offset: 65
                });
            });
    }
</script>
<style lang="scss" scoped>
    :deep(.custom-right) {
        display: flex;
        align-items: center;
    }

    .basic-btns {
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        margin-bottom: 20px;
    }
</style>
