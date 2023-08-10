<!--
 * @Author: your name
 * @Date: 2022-01-13 17:31:19
 * @LastEditTime: 2022-04-01 19:07:27
 * @LastEditors: hongzhew
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: /sz- team-frontend-9.6.x/y9vue-home/src/layouts/components/BreadCrumbs/index.vue
-->
<template>
    <!-- :style="theStyle"  // // beta-0.1(因最初的原型稿而增加的代码) -->
    <div
		id="breadcrumbs"
        :class="{
            'breadcrumbs': true,
            'sidebar-separate-uncollapsed': !menuCollapsed && layoutSubName === 'sidebar-separate',
            'sidebar-separate-menuCollapsed': menuCollapsed && layoutSubName === 'sidebar-separate'
        }"
    >
        <span class="title">{{ $t(`${list[0].meta.title}`) }}</span>
        <div style="display: flex;align-items: center; cursor: pointer;">
            <i class="ri-map-pin-line ri-lx" style="color: var(--el-color-primary);"></i>
            &nbsp;&nbsp;
            <el-breadcrumb>
                <el-breadcrumb-item v-for="item in list" :key="item.path">
                    <a-link :to="item.path" class="title-link">{{ $t(`${item.meta.title}`) }}</a-link>
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
    </div>
</template>
<script lang="ts">
import { defineComponent, PropType, watch, ref, inject } from 'vue';
import { BreadcrumbType } from '@/utils/routes';
import ALink from '../ALink/index.vue';

interface SiderMenuItemSetupData {
    fontSizeObj: Object
}
export default defineComponent({
    name: 'BreadCrumbs',
    data() {
        return {
            // theStyle: 'width: 1000px;'  // beta-0.1(因最初的原型稿而增加的代码)
        }
    },
    props: {
        layoutSubName: {
            type: String as Ref<string>,
            required: true
        },
        list: {
            type: Array as PropType<BreadcrumbType[]>,
            default: () => {
                return [];
            }
        },
        menuCollapsed: {
            type: Boolean as computed<Boolean>,
            required: true
        },
    },
    components: {
        ALink
    },
    setup(props): SiderMenuItemSetupData {
        // 注入 字体变量
        const fontSizeObj: any = inject('sizeObjInfo');
        
        return {
            fontSizeObj
        }

    }
    // // beta-0.1(因最初的原型稿而增加的代码)  begin -->
    // 需要调整breadCrumbs宽度的情况
    // watch: {
    //     // 路由变化
    //     $route: {
    //         handler: function (route) {
    //             const settingStore = useSettingStore()
    //             const layout = settingStore.getLayout
    //             const isMenuCollapsed = settingStore.getMenuCollapsed
    //             if (route.name === "homeIndex") {
    //                 this.theStyle = "width: 94%;"
    //             }
    //             if (route.name === "homeIndex" && layout.indexOf('sidebar-separate') > 0) {
    //                 if (!isMenuCollapsed) {
    //                     this.theStyle = "width: 82.3%;"
    //                 } else {
    //                     this.theStyle = "width: 92.5%;"
    //                 }
    //             }
    //             if (route.name !== "homeIndex") {
    //                 this.theStyle = 'width: 1000px; margin: 0 auto;'
    //             }
    //         },
    //         immediate: true
    //     },
    //     // 只点击右上角收缩菜单的情况
    //     menuCollapsedChange(isMenuCollapsed) {
    //         const settingStore = useSettingStore()
    //         const layout = settingStore.getLayout
    //         if (layout.indexOf('sidebar-separate') > 0 && !isMenuCollapsed) {
    //             this.theStyle = "width: 82.3%;"
    //         } 
    //         if (layout.indexOf('sidebar-separate') > 0 && isMenuCollapsed) {
    //             this.theStyle = "width: 92.5%;"
    //         }
    //     },
    //     // 只切换layout的情况
    //     layoutChange(layout) {
    //         const settingStore = useSettingStore()
    //         const isMenuCollapsed = settingStore.getMenuCollapsed
    //         if (layout === "Y9Default" && !isMenuCollapsed) {
    //             this.theStyle = "width: 96%;"
    //         }
    //         if (layout === "Y9Default" && isMenuCollapsed) {
    //             this.theStyle = "width: 96%;"
    //         }
    //         if (layout.indexOf('sidebar-separate') > 0 && !isMenuCollapsed) {
    //             this.theStyle = "width: 82.3%;"
    //         }
    //         if (layout.indexOf('sidebar-separate') > 0 && isMenuCollapsed) {
    //             this.theStyle = "width: 96%;"
    //         }
    //     }
    // },
    // computed: {
    //     menuCollapsedChange() {
    //         const settingStore = useSettingStore()
    //         return settingStore.getMenuCollapsed
    //     },
    //     layoutChange() {
    //         const settingStore = useSettingStore()
    //         return settingStore.getLayout
    //     }
    // }
    // // beta-0.1(因最初的原型稿而增加的代码)  <-- end
})
</script>
<style lang="scss" scoped>
.title {
    font-size: v-bind('fontSizeObj.largerFontSize');
}
.title-link {
    font-size: v-bind('fontSizeObj.baseFontSize');
}
</style>
