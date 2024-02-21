<template>
    <div class="remix-div">
        <div class="search">
            <el-input
                v-model="search"
                :placeholder="$t('请搜索')"
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                @input="onBtnIconsInput"
            />
        </div>
        <div class="icon-content">
            <div v-for="(item, index) in remixIcons" :key="index" class="icon-item" @click="onClickIcon(item)">
                <i :class="item.class"></i>
                <div>{{ item.name }}</div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
    import { remixIconData } from '@/utils/remixIconData';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { inject, ref, watch } from 'vue';

    const settingStore = useSettingStore();

    // 注入 字体变量
    const fontSizeObj: any = inject('sizeObjInfo');

    const emits = defineEmits(['clickIcon']);
    let search = ref('');

    //按钮图标数据
    let remixIcons = ref([]);

    //按钮 选择图标input框模糊搜索
    function onBtnIconsInput(name) {
        if (name) {
            remixIcons.value = remixIconData().filter((item) => item.name.indexOf(name) > -1);
        } else {
            remixIcons.value = remixIconData();
        }
    }

    watch(
        () => search.value,
        (newVal) => {
            onBtnIconsInput(search.value);
        },
        {
            immediate: true,
            deep: true
        }
    );

    //点击icon时触发
    function onClickIcon(item) {
        search.value = '';
        emits('clickIcon', item);
    }
</script>

<style lang="scss" scoped>
    .remix-div {
        .search {
            margin: 10px 10px 20px 10px;
        }

        .icon-content {
            display: flex;
            flex-wrap: wrap;
            max-height: 320px;
            overflow-y: auto;
            scrollbar-width: none;

            .icon-item {
                display: flex;
                flex-direction: column;
                align-items: center;
                text-align: center;
                color: #666;
                cursor: pointer;
                width: 60px;
                height: 60px;
                padding: 18px 15px;

                &:hover {
                    background-color: #eee;
                }

                i {
                    font-size: v-bind('fontSizeObj.largeFontSize');
                    cursor: pointer;
                }

                div {
                    width: 70px;
                    word-break: break-all;
                    cursor: pointer;
                    font-size: v-bind('fontSizeObj.baseFontSize');
                }
            }
        }
    }
</style>
