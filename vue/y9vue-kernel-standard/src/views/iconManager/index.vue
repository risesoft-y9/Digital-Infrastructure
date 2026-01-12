<!--
 * @Author: fuyu
 * @Date: 2022-06-06 11:47:27
 * @LastEditors: mengjuhua
 * @LastEditTime: 2025-12-24 09:25:02
 * @Description: 图标管理
-->
<template>
    <div>
        <IconList />
    </div>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { inject } from 'vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import IconList from './comps/IconList.vue';

    const settingStore = useSettingStore();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const { t } = useI18n();
</script>
<style lang="scss" scoped></style>
