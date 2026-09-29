<!--
 * @Author: your name
 * @Date: 2022-01-12 17:06:29
 * @LastEditTime: 2026-09-07 11:46:13
 * @LastEditors: mengjuhua
 * @Description: 菜单图标  
-->
<template>
    <Icons :type="type" />
</template>

<script lang="ts" setup>
    /**
     * IndexLayout icon , 主要用于菜单
     * @author LiQingSong
     * 使用说明：
     *   >>> 在这里可以对以下字体图标进行引入替换，达到 IndexLayout icon 统一效果：
     *     import Icons from '@/components/IconSvg';
     *     import Icons from '@/components/IconFont';
     */
    import Icons from './IconSvg';
    // import Icons from '@/components/IconFont';

    // 定义 Props
    const props = defineProps({
        type: {
            type: String,
            required: true
        }
    });
</script>
