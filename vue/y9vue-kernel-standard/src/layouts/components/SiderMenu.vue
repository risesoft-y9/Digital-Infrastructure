<!--
 * @Author: your name
 * @Date: 2022-01-11 18:38:31
 * @LastEditTime: 2023-08-03 09:52:54
 * @LastEditors: mengjuhua
 * @Description: 菜单项 
-->
<template>
    <!-- <div id="y9-menu"> -->
    <el-menu
        :collapse="menuCollapsed"
        :collapse-transition="false"
        :default-active="defaultActive"
        :mode="menuMode"
        :ellipsis="false"
    >
        <sider-menu-item
            v-for="item in newMenuData"
            :key="item.path"
            :routeItem="item"
            :belongTopMenu="belongTopMenu"
        ></sider-menu-item>
    </el-menu>
    <!-- </div> -->
</template>
<script lang="ts" setup>
    import { computed, ComputedRef, defineComponent, toRefs } from 'vue';
    import { RoutesDataItem } from '@/utils/routes';
    import SiderMenuItem from './SiderMenuItem.vue';

    interface SiderMenuSetupData {
        newMenuData: ComputedRef<RoutesDataItem[]>;
    }

    const props = defineProps({
        menuCollapsed: {
            type: Boolean,
            default: false,
        },
        menuMode: {
            type: String,
            default: 'vertical',
        },
        belongTopMenu: {
            type: String,
            default: '',
        },
        defaultActive: {
            type: String,
            default: '',
        },
        menuData: {
            type: Array,
            default: () => {
                return [];
            },
        },
    });

    const { menuData } = toRefs(props);
    const newMenuData = computed<RoutesDataItem[]>(() => {
        // 对每一个路由模块（位置：src->router->modules）（即菜单）进一步数据处理，以适配菜单列表样式
        const MenuItems: RoutesDataItem[] = [];
        for (let index = 0, len = menuData.value.length; index < len; index += 1) {
            const route: RoutesDataItem = menuData.value[index];
            // 如果对应的路由模块（位置：src->router->modules）只有一个子路由，提升为单个菜单（即它不需要菜单下拉显示）
            if (!route.hidden && route.children) {
                route.children.length === 1
                    ? MenuItems.push(...(route.children as RoutesDataItem))
                    : MenuItems.push(route as RoutesDataItem[]);
            }
        }
        return MenuItems;
    });
</script>
<style scoped lang="scss">
    //  #y9-menu{
    //    & > ul{
    //      :deep(a){
    //        text-decoration: none;
    //      }
    //    }
    //  }
</style>
