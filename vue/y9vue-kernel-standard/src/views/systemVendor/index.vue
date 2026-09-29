<!-- 系统开发商管理 -->
<template>
    <fixedTreeModule
        ref="fixedTreeRef"
        :showNodeDelete="false"
        :treeApiObj="treeApiObj"
        nodeLabel="name"
        @onTreeClick="onVendorClick"
    >
        <template #treeHeaderRight>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-main"
                type="primary"
                @click="openVendorDialog"
            >
                <i class="ri-add-line"></i>
                <span>{{ $t('系统开发商') }}</span>
            </el-button>
        </template>

        <template #actions="{ item }">
            <i class="ri-delete-bin-7-line" :title="$t('删除')" @click.stop="removeVendor(item)"></i>
        </template>

        <template #rightContainer>
            <div v-if="currentVendor.id">
                <BasicInfo :currTreeNodeInfo="currentVendor" @saved="onVendorSaved" />

                <y9Card :title="`${$t('管理的系统')} - ${currentVendor.name}`">
                    <div class="add-action">
                        <el-button
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            type="primary"
                            @click="openSystemDialog"
                        >
                            <i class="ri-add-line"></i>
                            <span>{{ $t('添加系统') }}</span>
                        </el-button>
                    </div>
                    <y9Table :config="systemTableConfig"></y9Table>
                </y9Card>
            </div>
            <y9Card v-else>
                <el-empty :description="$t('暂无系统开发商')"></el-empty>
            </y9Card>
        </template>
    </fixedTreeModule>

    <y9Dialog v-model:config="vendorDialogConfig">
        <y9Form ref="y9FormRef" :config="formConfig"></y9Form>
    </y9Dialog>

    <y9Dialog v-model:config="systemDialogConfig">
        <y9Table v-model:selectedVal="selectedSystems" :config="selectSystemTableConfig"></y9Table>
    </y9Dialog>

    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { computed, h, inject, reactive, ref, toRefs } from 'vue';
    import { useI18n } from 'vue-i18n';

    import BasicInfo from '@/views/systemVendor/comps/BasicInfo.vue';
    import {
        checkSystemVendorLoginName,
        getSystemVendors,
        getSystemsByManagerId,
        getUnrelatedSystemsByManagerId,
        removeSystemVendor,
        removeVendorSystem,
        saveSystemVendor,
        saveVendorSystems
    } from '@/api/systemVendor/index';

    const { t } = useI18n();
    const fontSizeObj: any = inject('sizeObjInfo');

    const fixedTreeRef = ref();
    const y9FormRef = ref();
    const currentVendor = ref({} as any);
    const selectedSystems = ref([] as any[]);

    function formatVendorList(vendorList) {
        vendorList.forEach((vendor) => {
            vendor.isLeaf = true;
            vendor.title_icon = 'ri-admin-line';
        });
        return vendorList;
    }

    const treeApiObj = ref({
        topLevel: async () => {
            const result = await getSystemVendors();
            return formatVendorList(result.data || []);
        },
        search: {
            api: async (params) => {
                const result = await getSystemVendors(params.key);
                if (result.success) {
                    result.data = formatVendorList(result.data || []);
                }
                return result;
            },
            params: {}
        }
    });

    const checkLoginName = (rule, value, callback) => {
        if (!value) {
            callback(new Error(t('请输入登录名称')));
            return;
        }
        checkSystemVendorLoginName(formConfig.value.model.id, value).then((result) => {
            if (result.data) {
                callback();
            } else {
                callback(new Error(t('该登录名称已存在，请重新输入登录名称')));
            }
        });
    };

    const data = reactive({
        loading: false,
        systemTableConfig: {
            columns: [
                { title: computed(() => t('序号')), type: 'index', width: 90 },
                { title: computed(() => t('系统中文名称')), key: 'cnName' },
                { title: computed(() => t('系统名称')), key: 'name' },
                {
                    title: computed(() => t('操作')),
                    width: 100,
                    fixed: 'right',
                    render: (row) =>
                        h(
                            'span',
                            {
                                style: { display: 'inline-flex', alignItems: 'center' },
                                onClick: () => removeSystem(row)
                            },
                            [
                                h('i', { class: 'ri-delete-bin-line', style: { marginRight: '4px' } }),
                                h('span', t('删除'))
                            ]
                        )
                }
            ],
            tableData: [],
            pageConfig: false,
            loading: false
        },
        selectSystemTableConfig: {
            columns: [
                { type: 'selection', width: 70 },
                { title: computed(() => t('系统中文名称')), key: 'cnName' },
                { title: computed(() => t('系统名称')), key: 'name' }
            ],
            tableData: [],
            pageConfig: false,
            loading: false
        },
        vendorDialogConfig: {
            width: '32%',
            show: false,
            title: '',
            resetText: t('重置'),
            onOk: () => {
                return new Promise((resolve, reject) => {
                    y9FormRef.value.elFormRef.validate(async (valid) => {
                        if (!valid) {
                            ElMessage({ type: 'error', message: t('验证不通过，请检查'), offset: 65 });
                            reject();
                            return;
                        }
                        loading.value = true;
                        const result = await saveSystemVendor(y9FormRef.value.model);
                        loading.value = false;
                        showNotification(result);
                        if (!result.success) {
                            reject();
                            return;
                        }
                        await refreshVendors(result.data.id);
                        resolve();
                    });
                });
            },
            onReset: () => y9FormRef.value.elFormRef.resetFields()
        },
        systemDialogConfig: {
            width: '55%',
            show: false,
            title: computed(() => t('添加系统')),
            onOk: () => {
                return new Promise(async (resolve, reject) => {
                    if (selectedSystems.value.length === 0) {
                        ElMessage({ type: 'warning', message: t('请选择系统'), offset: 65 });
                        reject();
                        return;
                    }
                    const systemIds = selectedSystems.value.map((system) => system.id);
                    const result = await saveVendorSystems(currentVendor.value.id, systemIds);
                    showNotification(result);
                    if (!result.success) {
                        reject();
                        return;
                    }
                    await getVendorSystems();
                    resolve();
                });
            }
        },
        formConfig: {
            model: createVendorModel(),
            rules: {
                name: [{ required: true, message: computed(() => t('请输入姓名')), trigger: 'blur' }],
                loginName: [{ required: true, validator: checkLoginName, trigger: 'blur' }]
            },
            itemList: [
                { type: 'input', label: computed(() => t('人员名称')), prop: 'name' },
                { type: 'input', label: computed(() => t('登录名称')), prop: 'loginName' },
                { type: 'input', label: computed(() => t('电子邮件')), prop: 'email' },
                { type: 'input', label: computed(() => t('移动电话')), prop: 'mobile' },
                { type: 'textarea', label: computed(() => t('人员描述')), prop: 'description' }
            ]
        }
    });

    const { formConfig, loading, vendorDialogConfig, selectSystemTableConfig, systemDialogConfig, systemTableConfig } =
        toRefs(data);

    function createVendorModel() {
        return {
            id: '',
            parentId: '',
            sex: 1,
            orgType: 'Manager',
            managerLevel: 7
        };
    }

    function showNotification(result) {
        ElNotification({
            title: result.success ? t('成功') : t('失败'),
            message: result.msg,
            type: result.success ? 'success' : 'error',
            duration: 2000,
            offset: 80
        });
    }

    async function onVendorClick(vendor) {
        currentVendor.value = vendor;
        await getVendorSystems();
    }

    async function getVendorSystems() {
        if (!currentVendor.value.id) {
            systemTableConfig.value.tableData = [];
            return;
        }
        systemTableConfig.value.loading = true;
        const result = await getSystemsByManagerId(currentVendor.value.id);
        systemTableConfig.value.loading = false;
        if (result.success) {
            systemTableConfig.value.tableData = result.data;
        }
    }

    async function refreshVendors(selectVendorId = '') {
        const result = await getSystemVendors();
        if (!result.success) {
            return;
        }
        const vendorList = formatVendorList(result.data || []);
        await fixedTreeRef.value.setTreeData(vendorList);
        const selectedVendor = vendorList.find((vendor) => vendor.id === selectVendorId) || vendorList[0];
        if (selectedVendor) {
            fixedTreeRef.value.handClickNode(selectedVendor, false);
        } else {
            currentVendor.value = {};
            systemTableConfig.value.tableData = [];
        }
    }

    function openVendorDialog() {
        formConfig.value.model = createVendorModel();
        Object.assign(vendorDialogConfig.value, {
            show: true,
            title: computed(() => t('新增系统开发商')),
            showFooter: true
        });
    }

    async function onVendorSaved(vendor) {
        await refreshVendors(vendor.id);
    }

    function removeVendor(vendor) {
        ElMessageBox.confirm(`${t('是否删除')}【${vendor.name}】?`, t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info'
        })
            .then(async () => {
                loading.value = true;
                const result = await removeSystemVendor(vendor.id);
                loading.value = false;
                showNotification(result);
                if (result.success) {
                    await refreshVendors();
                }
            })
            .catch(() => undefined);
    }

    async function openSystemDialog() {
        selectedSystems.value = [];
        selectSystemTableConfig.value.loading = true;
        const result = await getUnrelatedSystemsByManagerId(currentVendor.value.id);
        selectSystemTableConfig.value.loading = false;
        if (!result.success) {
            showNotification(result);
            return;
        }
        selectSystemTableConfig.value.tableData = result.data;
        systemDialogConfig.value.show = true;
    }

    function removeSystem(system) {
        ElMessageBox.confirm(`${t('是否解除系统关联')}【${system.cnName}】?`, t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info'
        })
            .then(async () => {
                const result = await removeVendorSystem(currentVendor.value.id, system.id);
                showNotification(result);
                if (result.success) {
                    await getVendorSystems();
                }
            })
            .catch(() => undefined);
    }
</script>

<style lang="scss" scoped>
    .add-action {
        margin-bottom: 10px;
    }

    :deep(.action-icons) {
        i + i {
            margin-left: 8px;
        }
    }
</style>
