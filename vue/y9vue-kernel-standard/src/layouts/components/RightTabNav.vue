<template>
    <div class="indexlayout-top-tab-nav">
        <div class="left" @click="handleScroll(200)">
             <icon-svg class="icon" type="arrow-left"  />
        </div>
        <div class="middle" ref="scrollBox" @DOMMouseScroll="handleRolling" @mousewheel="handleRolling">
            <div class="tab" ref="scrollContent" :style="{transform: `translateX(${translateX}px)`}">
                <span :ref="tabNavSpanRef" v-for="(item, index) in tabNavList" :key="`tab-nav-${index}`" class="item" :class="{'active': equalTabNavRView(route, item.route, item.menu.tabNavType)}" @click="toRoute(item,index)">
                    <icon-svg class="icon-pre" type="refresh" @click.stop="refreshCurrentTabNav(item)"  />
                    <span>{{t(item.menu.title)}}</span> 
                    <icon-svg v-if="item.menu.path!==homeRouteItemPath" class="icon" type="close" @click.stop="closeCurrentTabNav(item, index)" />
                </span>
            </div>
        </div>
        <div class="right"  @click="handleScroll(-200)">
            <icon-svg class="icon" type="arrow-right"  />
        </div>
        <div class="down">
            
            <el-dropdown placement="bottom-end" @command="handleCommandMore">
                <span class="icon-box">
                <icon-svg class="icon" type="more"  />
                </span>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item command="closeleft"><icon-svg class="icon-dropdown-menu" type="arrow-left2"  /> 关闭左侧</el-dropdown-item>
                        <el-dropdown-item command="closeright"><icon-svg class="icon-dropdown-menu" type="arrow-right2"  /> 关闭右侧</el-dropdown-item>
                        <el-dropdown-item command="closeother"><icon-svg class="icon-dropdown-menu" type="close"  /> 关闭其他</el-dropdown-item>
                        <el-dropdown-item command="closeall"><icon-svg class="icon-dropdown-menu" type="close2"  /> 关闭所有</el-dropdown-item>
                    
                    </el-dropdown-menu>
                </template>
           </el-dropdown>
        </div>
    </div>
</template>
<script lang="ts">
import { computed, defineComponent, ComputedRef, ref, Ref, watch, PropType, toRefs, onMounted, onBeforeUpdate, onUpdated, nextTick, inject } from "vue";
import { useRoute, useRouter, RouteLocationNormalizedLoaded } from 'vue-router';
import IconSvg from "./IconSvg";
import { equalTabNavRoute, RoutesDataItem, TabNavItem, TabNavType } from '@/utils/routes';
import settings from '@/settings';
import { useSettingStore } from '@/store/modules/settingStore';

interface RightTabNavSetupData {  
    equalTabNavRView: (route1: RouteLocationNormalizedLoaded, route2: RouteLocationNormalizedLoaded, type?: TabNavType) => boolean,
    translateX: Ref<number>;
    scrollBox: Ref;
    scrollContent: Ref;
    handleScroll: (offset: number) => void;
    handleRolling: (e: any)=> void;
    tabNavSpanRef: (el: any) => void;
    tabNavList: ComputedRef<TabNavItem[]>;
    homeRouteItemPath: string;
    route: RouteLocationNormalizedLoaded;
    toRoute: (item: TabNavItem, index: number) => void;
    refreshCurrentTabNav: (item: TabNavItem) => void;
    closeCurrentTabNav: (item: TabNavItem, index: number) => void;
    handleCommandMore: (command: string) => void;
    fontSizeObj: Object

}

export default defineComponent({
    name: 'RightTabNav',
    components: {
        IconSvg
    },
    props: {
        routeItem: {
            type: Object as PropType<RoutesDataItem>,
            required: true
        }
    },
    setup(props): RightTabNavSetupData {

        const settingStore = useSettingStore();
        // 注入 字体变量
        const fontSizeObj: any = inject('sizeObjInfo');

        const { routeItem } = toRefs(props);
        const equalTabNavRView = equalTabNavRoute;

        
        const translateX = ref<number>(0);
        const scrollBox = ref<HTMLDivElement>();
        const scrollContent = ref<HTMLDivElement>();
        const handleScroll = (offset: number): void => {
            const boxWidth = scrollBox.value ? scrollBox.value.offsetWidth : 0;
            const contentWidth = scrollContent.value ? scrollContent.value.offsetWidth : 0;
            if(offset > 0) {
                translateX.value = Math.min(0, translateX.value + offset)
            } else {
                if (boxWidth < contentWidth) {
                    if (translateX.value >= -(contentWidth - boxWidth)) {
                        translateX.value = Math.max(translateX.value + offset, boxWidth - contentWidth)
                    } 
                } else {
                    translateX.value = 0;
                }
            }

        }

        // 鼠标滚动
        const handleRolling = (e: any) => {
            const type = e.type
            let delta = 0
            if (type === 'DOMMouseScroll' || type === 'mousewheel') {
                delta = (e.wheelDelta) ? e.wheelDelta : -(e.detail || 0) * 40
            }
            handleScroll(delta)
        }


        // 设置tabItem位置
        let tabNavSpanRefs: any = [];
        const tabNavSpanRef = (el: any) => {
            tabNavSpanRefs.push(el)
        }
        onBeforeUpdate(() => {
            tabNavSpanRefs = []
        })
        /* 
        onUpdated(() => {
        }) 
        */
        const tabNavPadding = 10;
        const moveToView = (index: number): void => {
            if(!tabNavSpanRefs[index]) {
                return;
            }
            const tabItemEl = tabNavSpanRefs[index];
            const tabItemElOffsetLeft = tabItemEl.offsetLeft;
            const tabItemOffsetWidth = tabItemEl.offsetWidth;

            const boxWidth = scrollBox.value ? scrollBox.value.offsetWidth : 0;
            const contentWidth = scrollContent.value ? scrollContent.value.offsetWidth : 0;
            if(contentWidth < boxWidth || tabItemElOffsetLeft===0) {
                translateX.value = 0;
            } else if (tabItemElOffsetLeft < -translateX.value) {
                // 标签在可视区域左侧
                translateX.value = -tabItemElOffsetLeft + tabNavPadding;
            }else if (tabItemElOffsetLeft > -translateX.value && tabItemElOffsetLeft + tabItemOffsetWidth < -translateX.value + boxWidth) {
                // 标签在可视区域
                translateX.value = Math.min(0, boxWidth - tabItemOffsetWidth - tabItemElOffsetLeft - tabNavPadding)
            } else {
                // 标签在可视区域右侧
                translateX.value = -(tabItemElOffsetLeft - (boxWidth - tabNavPadding - tabItemOffsetWidth))
            }

           
        }
        


        const tabNavList = computed<TabNavItem[]>(()=> store.state.global.headTabNavList);
        const router = useRouter();
        const route = useRoute();

     

        // 设置TabNav 
        const setTabNav = (): void => {

            /**
             * 只有当前路由的path和当前定义路由规则的path一致才会继续执行，
             * 因为 routeItem 是经过computed获取后传过来的，存在异步情况
             */
            if(route.path!==routeItem.value.path) {
                return;
            }

            // 数组里是否已经存在当前route规则
            /* 
            const isRoute: boolean = tabNavList.value.some(item =>{
                if(equalTabNavRoute(item.route, route, routeItem.value.tabNavType)) {
                    return true;
                }                
            }); 
            if(!isRoute) {
                store.commit('global/setHeadTabNavList', [
                    ...tabNavList.value,
                    {
                        route: {
                            ...route
                        },
                        menu: {
                            ...routeItem.value
                        }
                    }
                ]);
            }
            */
            // 数组里是否已经存在当前route规则，不存在下标为-1
            let index = tabNavList.value.findIndex(item => equalTabNavRoute(item.route, route, routeItem.value.tabNavType))            
            if(index < 0) {
                index = tabNavList.value.length;
                store.commit('global/setHeadTabNavList', [
                    ...tabNavList.value,
                    {
                        route: {
                            ...route
                        },
                        menu: {
                            ...routeItem.value
                        }
                    }
                ]);
            }

            nextTick(() => {
              moveToView(index)
            })
         
        }

        // 关闭TabNav
        const closeTabNav = (item: TabNavItem, index: number): void => {

            // 判断关闭的是否是当前打开的tab
            let isRouterPush: boolean | TabNavItem = false;
            if(equalTabNavRoute(route, item.route, item.menu.tabNavType)) {
                isRouterPush = tabNavList.value[index-1]
            }

            let navList: TabNavItem[] = tabNavList.value.filter((item2: TabNavItem) => !equalTabNavRoute(item2.route, item.route, item.menu.tabNavType))
            store.commit('global/setHeadTabNavList', [
                ...navList
            ]);

            if(isRouterPush!==false) {
                router.push(isRouterPush.route)
            }
            
        }

        // 关闭TabNav所有
        const closeTabNavAll = (): void => {
            // 首页
            const homeRoute: TabNavItem = tabNavList.value[0];

            // 有关闭回调的无法关闭
            let navList: TabNavItem[] = tabNavList.value.filter((item: TabNavItem) => item.menu.tabNavCloseBefore && typeof item.menu.tabNavCloseBefore === 'function')
            store.commit('global/setHeadTabNavList', [
                {
                    ...homeRoute
                },
                ...navList
            ]);

            router.push(homeRoute.route)
        }

        // 关闭TabNav其他
        const closeTabNavOther = (): void => {

            // 有关闭回调的和当前打开的和首页无法关闭
            let navList: TabNavItem[] = tabNavList.value.filter((item: TabNavItem, i: number) => (item.menu.tabNavCloseBefore && typeof item.menu.tabNavCloseBefore === 'function') || equalTabNavRoute(route, item.route, item.menu.tabNavType) || i===0)
            store.commit('global/setHeadTabNavList', [
                ...navList
            ]);
        }

        // 关闭TabNav左侧和右侧
        const closeTabNavLeftRight = (param: 'left' | 'right'): void => {
            // 获取当前打开tabNav索引
            const index = tabNavList.value.findIndex(item => equalTabNavRoute(route, item.route, item.menu.tabNavType))

            // 有关闭回调的和当前打开的和首页和左侧或右侧无法关闭
            let navList: TabNavItem[] = tabNavList.value.filter((item: TabNavItem, i: number) => (item.menu.tabNavCloseBefore && typeof item.menu.tabNavCloseBefore === 'function') || ( param === 'left' ? i>=index : i<=index ) || i===0);

            store.commit('global/setHeadTabNavList', [
                ...navList
            ]);
        }




        watch([route, routeItem], ()=> {
            setTabNav()
        })

        onMounted(()=> {
            setTabNav()
        })


        // 路由链接
        const toRoute = (item: TabNavItem, index: number): void => {
            router.push(item.route);
        }

        // 刷新当前tabNav
        const refreshCurrentTabNav = (item: TabNavItem): void => {
            router.replace('/refresh')
        }

        // 关闭当前tabNav
        const closeCurrentTabNav = (item: TabNavItem, index: number): void => {
            if(item.menu.tabNavCloseBefore && typeof item.menu.tabNavCloseBefore === 'function' ) {
                item.menu.tabNavCloseBefore(()=> {
                    closeTabNav(item, index);
                })
            } else {
                 closeTabNav(item, index);
            }
        }

        // 更多操作
        const handleCommandMore = (command: string): void => {
            switch (command) {
                case 'closeleft':
                    closeTabNavLeftRight('left')
                    break;
                case 'closeright':
                    closeTabNavLeftRight('right')
                    break;
                case 'closeother':
                    closeTabNavOther()
                    break;
                case 'closeall':
                    closeTabNavAll()
                    break;
                default:
                    break;
            }
        }


        return {  
            equalTabNavRView,    
            translateX,
            scrollBox,
            scrollContent,
            handleScroll,
            handleRolling,
            tabNavSpanRef,
            tabNavList,
            homeRouteItemPath: settings.homeRouteItem.path,
            route,
            toRoute,
            refreshCurrentTabNav,
            closeCurrentTabNav,
            handleCommandMore,
            fontSizeObj,
        }
    }
})

</script>
<style lang="scss" scoped>
// @import '../../assets/css/global.scss';
.indexlayout-top-tab-nav {
    height: ($headerTabNavHeight - 4px);
    padding-top: 4px;
    /* background-color: #f0f0f0; */
    box-shadow: 0 -1px 4px rgba(0, 21, 41, 0.08);
    display: flex;
    align-items:center;
    .left,
    .right,
    .down {
        width: ($headerTabNavHeight - 10px);
        height: ($headerTabNavHeight - 8px);
        line-height: ($headerTabNavHeight - 8px);
       /*  background-color: #FFFFFF; */
        text-align: center;
        font-size: v-bind('fontSizeObj.baseFontSize');
        cursor: pointer;  
        .icon-box {
            display: block;
            width: ($headerTabNavHeight - 10px);
            height: ($headerTabNavHeight - 8px);
            line-height: ($headerTabNavHeight - 8px);
        }      
        .icon {
            color: rgba(0,0,0,.45);
        }
        &:hover {
            .icon {
                color: rgba(0,0,0,.75);
            }
        }
    }
     .down {
        padding-right: 10px;
        line-height: normal;
    } 
    .middle {
        flex: 1;
        overflow: hidden;
        .tab {
            position: relative;           
            float: left;
            list-style: none;
            overflow: visible;
            white-space: nowrap;
            transition: transform .5s ease-in-out;
            .item {
                height: ($headerTabNavHeight - 6px);
                line-height: ($headerTabNavHeight - 6px);
                background: #fafafa;
                box-sizing: border-box;
                white-space: nowrap;
                display: inline-block;
                padding: 0 10px;
                border-radius: 4px 4px 0 0;
                transition: all .3s cubic-bezier(.645,.045,.355,1);
                cursor: pointer;
                font-size: v-bind('fontSizeObj.baseFontSize');
                color: rgba(0,0,0,.65);
                border: 1px solid  $mainBgColor;
                &+.item {
                    margin-left: 3px;
                }
                &:hover {
                    color: var(--el-color-primary);
                }
                .icon {
                    font-size: v-bind('fontSizeObj.baseFontSize');
                    margin: 0 0 2px 5px;
                    color: rgba(0,0,0,.45);
                    &:hover {
                        color: rgba(0,0,0,.75);
                    }

                }
                .icon-pre {
                    display: none;
                    font-size: v-bind('fontSizeObj.baseFontSize');
                    margin: 0 5px 0 0;
                    color: rgba(var(--el-color-primary), 0.75);
                    &:hover {
                        color: rgba(var(--el-color-primary), 1);
                    }
                }
            }            
            .active {
                color: var(--el-color-primary);
                background:#FFFFFF;
                border-color:#FFFFFF;
                .icon-pre { 
                    display: inline-block;
                }
            }
        }
    }

}
.icon-dropdown-menu {
    font-size: v-bind('fontSizeObj.baseFontSize');
    margin-right: 5px;
}
</style>