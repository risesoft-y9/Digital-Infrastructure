<!--
 * @Author: your name
 * @Date: 2022-01-11 18:38:31
 * @LastEditTime: 2026-09-07 14:17:36
 * @LastEditors: mengjuhua
 * @Description: 菜单项
-->
<template>
    <el-menu
        :collapse="menuCollapsed"
        :collapse-transition="false"
        :default-active="defaultActive"
        :ellipsis="false"
        :mode="menuMode"
    >
        <sider-menu-item
            v-for="item in newMenuData"
            :key="item.path"
            :belongTopMenu="belongTopMenu"
            :routeItem="item"
        ></sider-menu-item>
    </el-menu>
</template>
<script lang="ts" setup>
    import { computed, ComputedRef, toRefs } from 'vue';
    import { RoutesDataItem } from '@/utils/routes';
    import SiderMenuItem from './SiderMenuItem.vue';

    interface SiderMenuSetupData {
        newMenuData: ComputedRef<RoutesDataItem[]>;
    }

    const props = defineProps({
        menuCollapsed: {
            type: Boolean,
            default: false
        },
        menuMode: {
            type: String,
            default: 'vertical'
        },
        belongTopMenu: {
            type: String,
            default: ''
        },
        defaultActive: {
            type: String,
            default: ''
        },
        menuData: {
            type: Array as () => RoutesDataItem[],
            default: () => []
        }
    });

    const { menuData } = toRefs(props);
    const newMenuData = computed<RoutesDataItem[]>(() => {
        // 增加对menuData.value的空值判断
        if (!menuData.value) {
            return [];
        }

        return menuData.value.reduce<RoutesDataItem[]>((accumulator, route) => {
            // 确保route是有效的RoutesDataItem类型
            if (!route) {
                return accumulator;
            }
            // 如果route.hidden为true，直接跳过该路由
            if (route.hidden) {
                return accumulator;
            }
            // 检查route.children是否为数组
            if (Array.isArray(route.children) && route.children.length === 1) {
                const child = route.children[0];
                // 确保子路由也是有效的RoutesDataItem类型
                if (child) {
                    accumulator.push(child);
                }
            } else {
                accumulator.push(route);
            }
            return accumulator;
        }, []);
    });
</script>
<style lang="scss" scoped></style>
