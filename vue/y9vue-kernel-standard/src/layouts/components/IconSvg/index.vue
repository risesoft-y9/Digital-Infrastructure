<!--
 * @Author: your name
 * @Date: 2022-01-12 17:06:57
 * @LastEditTime: 2022-01-20 15:33:29
 * @LastEditors: your name
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: /sz- team-frontend-9.6.x/y9vue-home/src/layouts/components/IconSvg/index.vue
-->
<template>
    <svg class="svg-icon" aria-hidden="true">
        <use :xlink:href="`#${type}`" />
    </svg>
</template>
<script lang="ts">
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
import { defineComponent } from "vue";
export default defineComponent({
    name: 'IconSvg',
    props: {
        type: {
            type: String,
            required: true
        }
    }
});
</script>
<style scoped>
.svg-icon {
  width: 1em;
  height: 1em;
  vertical-align: -0.15em;
  fill: currentColor;
  overflow: hidden;
}
</style>