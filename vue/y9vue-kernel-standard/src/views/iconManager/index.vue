<!--
 * @Author: fuyu
 * @Date: 2022-06-06 11:47:27
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-01-12 10:52:51
 * @Description: 图标管理
-->
<template>
    <div>
        <IconList />
    </div>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { computed, inject, onMounted, reactive, ref, toRefs, watch } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import { $keyNameAssign, $tableHandleRender } from '@/utils/object';
    import { deleteIcon, getAppIconPageList, saveIcon, searchIconPageByName, uploadIcon } from '@/api/appIcon/index';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { Search } from '@element-plus/icons';
    import IconList from './comps/IconList.vue';

    const settingStore = useSettingStore();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const { t } = useI18n();
</script>
<style lang="scss" scoped></style>
