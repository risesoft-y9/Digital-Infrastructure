<template>
    <y9Card :title="`${currInfo.name ? currInfo.name : ''}`">
        <div style="height: 213px">
            <el-alert
                :title="$t('请点击左侧树，选择部门再进行操作。')"
                type="warning"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
            />
        </div>
    </y9Card>
</template>

<script lang="ts" setup>
    import { $deepAssignObject } from '@/utils/object';
    import { inject, ref, watch } from 'vue';
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const props = defineProps({
        currTreeNodeInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            },
        },
    });

    let currInfo = ref(props.currTreeNodeInfo);

    watch(
        () => props.currTreeNodeInfo,
        (newVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal);
        }
    );
</script>

<style lang="scss" scoped></style>
