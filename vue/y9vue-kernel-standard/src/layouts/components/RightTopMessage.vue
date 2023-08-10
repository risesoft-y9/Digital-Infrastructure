<!--
 * @Author: your name
 * @Date: 2022-01-13 17:30:06
 * @LastEditTime: 2023-08-03 10:12:40
 * @LastEditors: mengjuhua
 * @Description: 消息提醒 
-->
<template>
    <router-link to="/" class="indexlayout-top-message">
        <icon-svg type="message" :style="{ fontSize: fontSizeObj.mediumFontSize }"></icon-svg>
        <el-badge class="indexlayout-top-message-badge" :value="message" type="danger" />
    </router-link>
</template>
<script lang="ts" setup>
    import { computed, ComputedRef, defineComponent, inject, onMounted, ref, watch } from 'vue';
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
