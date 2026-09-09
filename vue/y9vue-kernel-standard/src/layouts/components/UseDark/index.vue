<!--
 * @Author: mengjuhua
 * @Date: 2026-09-08 11:39:07
 * @LastEditTime: 2026-09-08 11:39:07
 * @LastEditors: mengjuhua
 * @Description: 切换暗黑/白天模式
-->
<template>
    <div class="item isDark" @click="toggleDark">
        <i class="ri-moon-line" v-if="!isDark"></i>
        <i class="ri-sun-line" v-else></i>
        <span>{{ isDark ? '白天' : '黑夜' }}</span>
    </div>
</template>
<script lang="ts" setup>
    import { computed, inject, watch } from 'vue';
    import { useSettingStore } from '@/store/modules/settingStore';

    const fontSizeObj: any = inject('sizeObjInfo');
    const settingStore = useSettingStore();
    const isDark = computed({
        get: () => settingStore.getIsDark,
        set: (dark: boolean) => {
            settingStore.$patch({ isDark: dark });
        }
    });
    const setThemeClass = (themeName: string) => {
        const root = document.documentElement;
        Array.from(root.classList)
            .filter((className) => className.startsWith('theme-'))
            .forEach((className) => root.classList.remove(className));
        root.classList.add(themeName);
    };

    const setThemeLink = (themeName: string) => {
        const themeLink = document.getElementById('head') as HTMLLinkElement | null;
        if (!themeLink) return;

        const themeUrl = new URL(themeLink.href, window.location.href);
        themeUrl.pathname = themeUrl.pathname.replace(/[^/]+\.css$/, `${themeName}.css`);
        themeLink.href = themeUrl.toString();
    };

    const applyTheme = () => {
        const themeName = isDark.value ? 'theme-dark' : settingStore.getThemeName;
        setThemeClass(themeName);
        setThemeLink(themeName);
    };

    if (settingStore.getIsDark && settingStore.getThemeName !== 'theme-dark') {
        settingStore.$patch({
            lightThemeName: settingStore.getThemeName,
            themeName: 'theme-dark'
        });
    }

    watch(
        [isDark, () => settingStore.getThemeName],
        () => {
            applyTheme();
        },
        { immediate: true }
    );

    const toggleDark = () => {
        if (isDark.value) {
            settingStore.$patch({
                isDark: false,
                themeName: settingStore.lightThemeName || 'theme-default'
            });
            return;
        }

        settingStore.$patch({
            isDark: true,
            lightThemeName: settingStore.getThemeName,
            themeName: 'theme-dark'
        });
    };
</script>
<style lang="scss" scoped>
    .item {
        overflow: hidden;
        padding: 0 11px;
        min-width: 5px;
        color: var(--el-menu-text-color);
        cursor: pointer;
        display: flex;
        align-items: center;

        i {
            position: relative;
            font-size: v-bind('fontSizeObj.extraLargeFont');
        }

        span {
            font-size: v-bind('fontSizeObj.baseFontSize');
            margin-left: 5px;
        }

        &:hover {
            color: var(--el-color-primary);
        }
    }
</style>
