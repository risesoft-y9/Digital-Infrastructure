<template>
    <y9Card :title="`${$t('统一码')}${currInfo.name ? ' - ' + currInfo.name : ''}`">
        <el-button
            :disabled="!!iconUrl"
            :size="fontSizeObj.buttonSize"
            :style="{ fontSize: fontSizeObj.baseFontSize }"
            class="global-btn-main"
            type="primary"
            @click="handleQRcode"
            ><i class="ri-add-line"></i>生成
        </el-button>
        <div class="qr-code">
            <img :src="iconUrl" alt="点击按钮生成用户统一码" />
        </div>
    </y9Card>
    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { computed, h, inject, onMounted, reactive, ref, toRefs, watch } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import { $deepAssignObject } from '@/utils/object';
    import { getPersonQRcode, createPersonQRcode, getPersonInfo } from '@/api/org';

    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const props = defineProps({
        currTreeNodeInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            }
        }
    });

    const data = reactive({
        loading: false, // 全局loading
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        iconUrl: ''
    });

    let { currInfo, loading, iconUrl } = toRefs(data);

    watch(
        () => props.currTreeNodeInfo,
        (newVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal);
            getPersonQRcodeInfo();
        },
        {
            deep: true
        }
    );

    onMounted(getPersonQRcodeInfo);

    async function getPersonQRcodeInfo() {
        let result = await getPersonQRcode({ personId: currInfo.value.id });
        if (result.code == 0) {
            iconUrl.value = result.data && result.data?.imgUrl;
        }
    }

    // 生成二维码
    async function handleQRcode() {
        loading.value = true;
        let result = await createPersonQRcode({ personId: currInfo.value.id });
        if (result.code == 0) {
            iconUrl.value = result.data && result.data?.imgUrl;
        }
        ElNotification({
            title: result.success ? t('成功') : t('失败'),
            message: result.msg,
            type: result.success ? 'success' : 'error',
            duration: 2000,
            offset: 80
        });
        loading.value = false;
    }
</script>

<style lang="scss" scoped>
    .qr-code {
        border: 1px solid #efefef;
        border-radius: 4px;
        width: 220px;
        height: 220px;
        margin: 20px 0;

        img {
            width: 100%;
            height: 100%;
        }
    }
</style>
