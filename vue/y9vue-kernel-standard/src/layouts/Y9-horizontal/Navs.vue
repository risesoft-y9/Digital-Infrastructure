<!--
 * @Author: hongzhew
 * @Date: 2022-03-28 09:48:44
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-07 17:37:43
 * @Description: 
-->
<script lang="ts" setup>
import { ref, inject, watch } from "vue";
import SiderMenu from '@/layouts/components/SiderMenu.vue';
// 注入 字体变量
const fontSizeObj: any = inject('sizeObjInfo');

const props = defineProps({
  menuCollapsed: {
    type: Boolean,
    default: false
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
    type: Array,
    default: () => {
      return [];
    }
  }
})
</script>

<template>
  <div id="header-menus">
    <sider-menu
      :menuCollapsed="menuCollapsed"
      :belongTopMenu="belongTopMenu"
      :defaultActive="defaultActive"
      :menuData="menuData"
      menuMode="horizontal"
    ></sider-menu>
  </div>
</template>

<style lang="scss" scoped>
@import "@/theme/global-vars.scss";
#header-menus {
  width: 100%;
  overflow: auto;
  scrollbar-width: none;
  box-shadow: 2px 2px 2px 1px rgb(0 0 0 / 6%);
  z-index: 2;
  & > ul {
    border-right: none;
    :deep(a) {
      text-decoration: none;
      & > li {
        color: var(--el-text-color-primary);
        background-color: var(--el-bg-color);
        height: 100%;
        
        i {
          margin-right: 10px;
          font-size: v-bind('fontSizeObj.largeFontSize');
        }

        &.is-active{
          color: var(--el-color-primary);
          background-color: $background-color;
        }
      }
      & > li:hover {
        background-color: var(--el-color-primary-light-9);
      }
    }
  }
}

:deep(.y9-el-sub-menu.is-active) {
    background-color: var(--el-color-primary-light-9);
  }
  :deep(.y9-el-sub-menu) {
    background: var(--el-bg-color);
  }

.el-menu--horizontal {
  width: 100%;
}
  
</style>

<!-- Workaround bug #6378 -->
<style lang="scss">
// 精确定位，尽量避开全局污染
.el-menu--horizontal
  > ul.el-menu.el-menu--popup.el-menu--popup-bottom-start
  > a {
  text-decoration: none;
  & > li.el-menu-item {
    text-align: center;
    color: var(--el-text-color-primary);
    background-color: var(--el-bg-color);
    i {
      margin-right: 10px;
    }
  }
  & > li.el-menu-item:hover {
    background-color: var(--el-color-primary-light-9);
  }
}
</style>