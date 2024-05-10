<!--
 * @Author: your name
 * @Date: 2022-01-12 17:06:29
 * @LastEditTime: 2023-08-03 09:47:21
 * @LastEditors: mengjuhua
 * @Description: 菜单图标  
-->
<template>
    <Icons :type="type" />
</template>
<script lang="ts">
    /**
     * IndexLayout icon , 主要用于菜单
     * @author LiQingSong
     * 使用说明：
     *   >>> 在这里可以对以下字体图标进行引入替换，达到 IndexLayout icon 统一效果：
     *     import Icons from '@/components/IconSvg';
     *     import Icons from '@/components/IconFont';
     */
    import { defineComponent } from 'vue';
    import Icons from './IconSvg';
    // import Icons from '@/components/IconFont';
    export default defineComponent({
        name: 'Icon',
        props: {
            type: {
                type: String,
                required: true
            }
        },
        components: {
            Icons
        }
    });
</script>
