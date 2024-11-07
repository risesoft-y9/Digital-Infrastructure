<!--
 * @Author: your name
 * @Date: 2022-01-13 17:31:19
 * @LastEditTime: 2023-08-03 09:46:26
 * @LastEditors: mengjuhua
 * @Description:    卡片式标签页
-->
<template>
    <div id="kernel-tabs">
        <el-tabs
            v-model="activeName"
            :class="{
                top: tabPosition === 'top',
                left: tabPosition === 'left',
                right: tabPosition === 'right',
                bottom: tabPosition === 'bottom'
            }"
            :tab-position="tabPosition"
            stretch
            @tab-click="handleClick"
        >
            <el-tab-pane v-for="item in tabs" :name="item.path">
                <template #label>
                    <span class="custom-tabs-label">
                        <i v-show="showTabicon && item.meta.icon" :class="['icon', item.meta.icon]" />
                        <span>{{ item.meta && item.meta.title }}</span>
                    </span>
                </template>
            </el-tab-pane>
        </el-tabs>
        <el-tooltip content="关闭所有Tab" effect="light">
            <i
                v-show="settingStore.getLabelStyle === 'top'"
                class="ri-close-fill close-all-icon"
                @click="closeAllTabs"
            ></i>
        </el-tooltip>
    </div>
</template>
<script lang="ts" setup>
    import { computed } from 'vue';
    import { useRouter } from 'vue-router';
    import { useRouterStore } from '@/store/modules/routerStore';
    import { useSettingStore } from '@/store/modules/settingStore';

    const routerStore = useRouterStore();
    const settingStore = useSettingStore();
    const tabPosition = computed(() => settingStore.getLabelStyle);
    const showTabicon = computed(() => settingStore.getShowLabelIcon);
    const router = useRouter();
    const props = defineProps({
        menuCollapsed: {
            type: Boolean as computed<Boolean>,
            required: false
        },
        layoutSubName: {
            type: String as Ref<string>,
            required: false
        }
    });
    // 当前tab
    const activeName = computed(() => routerStore.getActiveRoute);

    // 所有tab
    const tabs = computed(() => routerStore.getTabs);

    // 单击 or 双击
    let count = 0,
        Timer;
    const { removeTab } = routerStore;
    const handleClick = (event: Event) => {
        count += 1;
        if (Timer) {
            clearTimeout(Timer);
        }
        Timer = setTimeout(() => {
            if (count === 1) {
                routerStore.$patch({
                    activeRoute: event.props.name
                });
            } else {
                removeTab(event.props.name, tabPosition.value);
            }
            count = 0;
        }, 250);
    };

    // 关闭所有tabs
    const closeAllTabs = () => {
        routerStore.$patch({
            tabs: []
        });
    };
</script>
<style lang="scss" scoped>
    #kernel-tabs {
        & > .close-all-icon {
            position: fixed;
            right: 15px;
            top: 72px;
        }

        & > .top {
            height: 40px;
            width: 94%;
        }

        & > .left,
        .right {
            position: absolute;
            top: 50%;
            transform: translate(0, -50%);
        }

        & > .right {
            right: 0;
        }

        .custom-tabs-label {
            max-width: 150px;
            overflow: hidden;

            i {
                margin-right: 3px;
                position: relative;
                top: 2px;
            }
        }
    }
</style>
