import { remove } from 'lodash';
import { defineStore } from 'pinia';

export const useRouterStore = defineStore('routerStore', {
    state: () => {
        return {
            PermissionRoutes: [],
            tabs: [],
            activeRoute: '/index'
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
        removeTab(tabName: String, tabPosition: String) {
            // 位置是top时  移除时，如果有则留下一个名为首页的标签
            if (tabPosition === 'top' && tabName !== '首页') {
                this.tabs = remove(this.tabs, (item) => item.path !== tabName);
            }
            // 位置不是top时  移除时，不留任何标签
            if (tabPosition !== 'top') {
                this.tabs = remove(this.tabs, (item) => item.path !== tabName);
            }
        }
    }
});
