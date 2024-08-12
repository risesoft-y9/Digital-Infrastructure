<template>
    <y9Card style="height: calc(100vh - 2px)" title="人员信息">
        <y9Form :config="y9FormConfig"></y9Form>
    </y9Card>
</template>

<script lang="ts" setup>
    import { ref, computed, onMounted } from 'vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';
    import y9_storage from '@/utils/storage';
    import { getPersonInfo } from '@/api/org';

    const { t } = useI18n();
    const settingStore = useSettingStore();

    // 请求参数
    let personParams = ref({});
    // 人员信息
    let personInfo = ref({});
    //表单配置
    let y9FormConfig = ref({
        descriptionsFormConfig: {
            //描述表单配置
            column: 1,
            labelAlign: 'center',
            labelWidth: '150px'
        },
        model: {},
        rules: {}, //表单验证规则
        itemList: [
            //表单显示列表
            {
                type: 'text',
                prop: 'name',
                label: computed(() => t('姓名')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', personInfo.value.name);
                    }
                }
            },
            {
                type: 'text',
                prop: 'sex',
                label: computed(() => t('性别')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', personInfo.value.sex == 1 ? t('男') : t('女'));
                    }
                }
            },
            {
                type: 'text',
                prop: 'deptName',
                label: computed(() => t('部门')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', personInfo.value?.dn?.split(',')[1]?.split('=')[1]);
                    }
                }
            },
            {
                type: 'text',
                prop: 'mobile',
                label: computed(() => t('手机')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', personInfo.value.mobile);
                    }
                }
            },
            {
                type: 'text',
                prop: 'email',
                label: computed(() => t('邮箱')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', personInfo.value.email ?? '无');
                    }
                }
            }
        ]
    });

    onMounted(() => {
        let queryParams = y9_storage.getObjectItem('query');
        let codeValue = queryParams.code;
        if (queryParams.code.slice(-1) !== '/') {
            codeValue += '/';
        }
        personParams.value = { tenantId: queryParams.tenantId, code: codeValue };
        initPersonInfo();
    });

    async function initPersonInfo() {
        if (!personParams.value.tenantId || !personParams.value.code) return;
        let result = await getPersonInfo(personParams.value);
        if (result.code == 0) {
            personInfo.value = result.data;
        }
    }
</script>

<style lang="scss" scoped></style>
