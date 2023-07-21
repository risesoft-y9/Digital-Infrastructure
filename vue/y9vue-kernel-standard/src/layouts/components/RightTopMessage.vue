<!--
 * @Author: your name
 * @Date: 2022-01-13 17:30:06
 * @LastEditTime: 2022-04-01 16:46:22
 * @LastEditors: hongzhew
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: /sz- team-frontend-9.6.x/y9vue-home/src/layouts/components/RightTopMessage.vue
-->
<template>
    <router-link to="/" class="indexlayout-top-message">
      <icon-svg type="message"  :style="{ fontSize: fontSizeObj.mediumFontSize }"></icon-svg>
      <el-badge
        class="indexlayout-top-message-badge"
        :value="message"
        type="danger"
      />
    </router-link>
</template>
<script lang="ts">
import { computed, ComputedRef, defineComponent, inject, onMounted, ref, watch } from "vue";
import IconSvg from "./IconSvg";

interface RightTopMessageSetupData {
    message: ComputedRef<number>;
        fontSizeObj: Object;
}

export default defineComponent({
    name: 'RightTopMessage',
    components: {
      IconSvg
    },
    setup(): RightTopMessageSetupData {

        // const store = useStore<{user: UserStateType}>();
        
        const message = computed<number>(()=> 2);

        // 注入 字体变量
        const fontSizeObj: any = inject('sizeObjInfo');


        onMounted(()=>{
            // store.dispatch("user/fetchMessage");
        })


        return {
            message,
            fontSizeObj
        }
    }
})
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