<template>
  <div id="indexlayout-left" :class="{ 'narrow': menuCollapsed, 'sidebar-separate': layoutSubName==='sidebar-separate'? true: false }">
    <div class="indexlayout-left-logo">
      <router-link to="/" class="logo-url">
        <img alt="y9-logo" src="@/assets/images/yunLogo.png">
        <span class="logo-title" v-if="!menuCollapsed">{{ $t("数字底座") }}</span>
      </router-link>
    </div>
    <div class="indexlayout-left-menu">
      <sider-menu
        :menuCollapsed="menuCollapsed"
        :belongTopMenu="belongTopMenu"
        :defaultActive="defaultActive"
        :menuData="menuData"
      ></sider-menu>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { watch, inject, ref} from 'vue'
import SiderMenu from '@/layouts/components/SiderMenu.vue';
// 注入 字体变量
const fontSizeObj: any = inject('sizeObjInfo');
const props = defineProps({
  menuCollapsed: {
    type: Boolean as computed<Boolean>,
    required: true
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
  },
  layoutSubName: {
    type: String as Ref<string>,
    required: true
  }
})
</script>

<style lang="scss" scoped>
@import "@/theme/global-vars.scss";


$sidebar-separate-margin-top: calc(
  #{$sidebar-separate-margin-left} + #{$headerHeight}
);
$sidebar-separate-menu-height: calc(100vh - #{$sidebar-separate-margin-top});

#indexlayout-left {
  display: flex;
  height: 100vh;
  flex-direction: column;
  width: $leftSideBarWidth;
  // background-color: #f40;
  background-color: var(--el-bg-color);
  // border-right: var(--el-border-base);
  transition-duration: 0.1s;

  &.sidebar-separate {
    position: absolute;
    z-index: 1;
    left: $sidebar-separate-margin-left;
    top: $sidebar-separate-margin-top;
    height: $sidebar-separate-menu-height;
  }
  .indexlayout-left-logo {
    width: 100%;
    height: $headerHeight;
    line-height: $headerHeight;
    text-align: left;
    vertical-align: middle;
    .logo-url {
      display: inline-flex;
      align-items: center;
      width: 100%;
      height: 100%;
      overflow: hidden;
      .logo-title {
        display: inline-block;
        margin-left: 15px;
        font-size: v-bind('fontSizeObj.largeFontSize');
        font-family: Roboto, sans-serif;
        color: var(--el-text-color-primary);
      }
    }
    img {
      width: $logoWidth;
      vertical-align: middle;
    }
  }

  .indexlayout-left-menu {
    flex: 1;
    overflow: hidden auto;
    & > ul {
      border-right: none;
      background-color: var(--el-bg-color);
      :deep(a) {
        text-decoration: none;
		.is-active{
			color: var(--el-color-primary);
		}
        & > li {
          color: var(--el-text-color-primary);

          i {
            margin-right: 10px;
            font-size: v-bind('fontSizeObj.largeFontSize');
          }
        }
        & > li:hover {
          background-color: var(--el-bg-color);
        }
      }
    }
    .left-scrollbar {
      width: 100%;
      height: 100%;
    }
  }

  &.narrow {
    width: $menu-collapsed-width;
  }

  @include scrollbar;
}
</style>