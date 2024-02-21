<!--
 * @Author: your name
 * @Date: 2022-01-13 17:30:06
 * @LastEditTime: 2023-12-26 11:22:05
 * @LastEditors: mengjuhua
 * @Description: 消息提醒 
-->
<template>
    <router-link class="indexlayout-top-message" to="/">
        <icon-svg :style="{ fontSize: fontSizeObj.mediumFontSize }" type="message"></icon-svg>
        <el-badge :value="message" class="indexlayout-top-message-badge" type="danger" />
    </router-link>
</template>
<script lang="ts" setup>
    import { computed, ComputedRef, inject, onMounted } from 'vue';
    import IconSvg from './IconSvg';

    interface RightTopMessageSetupData {
        message: ComputedRef<number>;
        fontSizeObj: Object;
    }

    // const store = useStore<{user: UserStateType}>();

    const message = computed<number>(() => 2);

    // 注入 字体变量
    const fontSizeObj: any = inject('sizeObjInfo');

    onMounted(() => {
        // store.dispatch("user/fetchMessage");
    });
</script>
<style lang="scss" scoped>
    // @import '../../assets/css/global.scss';
    .indexlayout-top-message {
        height: $headerHeight;
        line-height: $headerHeight;
        /* display: inline-block; */
        display: inline;
        color: #c0c4cc;

        .indexlayout-top-message-badge {
            margin-left: -5px;
            margin-top: -20px;

            :deep(.el-badge__content) {
                border: 0;
            }
        }
    }
</style>
