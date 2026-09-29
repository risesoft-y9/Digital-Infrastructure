import { remove } from 'lodash';
import { defineStore } from 'pinia';
import type { TabNavItem } from '@/utils/routes';

export const useRouterStore = defineStore('routerStore', {
    state: () => {
        return {
            PermissionRoutes: [],
            tabs: [] as any,
            activeRoute: '/index',
            // 补全原有缺失的标签导航状态，完全匹配项目原有字段
            headTabNavList: [] as TabNavItem[]
        };
    },
    getters: {
        getPermissionRoutes: (state) => {
            return state.PermissionRoutes;
        },
        getTabs: (state) => {
            return state.tabs;
        },
        getActiveRoute: (state) => {
            return state.activeRoute;
        },
        // 补全原代码中用到的标签列表 getter
        getHeadTabNavList: (state): TabNavItem[] => {
            return state.headTabNavList;
        }
    },
    actions: {
        addTab(item) {
            // 当前 active路由
            this.activeRoute = item.path;

            // 检查是否存在
            function checkItem(it) {
                return it.path === item.path;
            }

            // 添加
            if (!this.tabs.some(checkItem)) {
                this.tabs.push(item);
            }
            // tabs是否超过6个
        },
        removeTab(tabName: string, tabPosition: string) {
            // 位置是top时  移除时，如果有则留下一个名为首页的标签
            if (tabPosition === 'top' && tabName !== '首页') {
                this.tabs = remove(this.tabs, (item) => item.path !== tabName);
            }
            // 位置不是top时  移除时，不留任何标签
            if (tabPosition !== 'top') {
                this.tabs = remove(this.tabs, (item) => item.path !== tabName);
            }
        },
        // 补全缺失的标签列表更新action，完全兼容原有调用逻辑
        setHeadTabNavList(list: TabNavItem[]) {
            this.headTabNavList = list;
        }
    }
});
