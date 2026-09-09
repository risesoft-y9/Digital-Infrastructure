<!--
 * @Author: your name
 * @Date: 2022-01-12 17:06:57
 * @LastEditTime: 2026-09-07 11:09:24
 * @LastEditors: mengjuhua
 * @Description: 自定义 svg icon 
-->
<template>
    <svg aria-hidden="true" class="svg-icon">
        <use :xlink:href="`#${type}`" />
    </svg>
</template>

<script lang="ts" setup>
    /**
     * 自定义 svg icon
     * @author LiQingSong
     * 使用说明：
     *   1、下载或制作svg文件，存放到/src/assets/iconsvg目录下
     *   2、项目根据/src/assets/iconsvg/svgo.yml配置自动压缩精简svg，也可以独立运行 npm run svgo 压缩精简svg
     *   3、使用Demo：
     *      import IconSvg from '@/layouts/components/IconSvg';
     *      import { defineComponent } from "vue";
     *      export default defineComponent({
     *          components: {
     *              IconSvg
     *          }
     *      })
     *      <IconSvg type="svg文件名" class="" style=""/>
     */
    // 定义 Props 接口
    interface IconSvgProps {
        type: string;
    }

    // 声明 Props
    const props = defineProps<IconSvgProps>();
</script>

<style scoped>
    .svg-icon {
        width: 1em;
        height: 1em;
        vertical-align: -0.15em;
        fill: currentColor;
        overflow: hidden;
        display: inline-block; /* 确保对齐行为一致 */
    }
</style>
