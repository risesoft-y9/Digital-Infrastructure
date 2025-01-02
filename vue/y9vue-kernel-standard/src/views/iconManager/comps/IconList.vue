<!--
 * @Author: fuyu
 * @Date: 2022-06-06 11:47:27
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-01-12 10:52:51
 * @Description: 图标管理
-->
<template>
    <div>
        <div class="search-top">
            <el-select v-model="iconStore.searchCateValue" :placeholder="t('选择')" size="large" style="width: 240px">
                <el-option
                    v-for="item in iconStore.searchCate"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                    @click="changeCate(item.label)"
                />
            </el-select>
            <div class="divider"></div>
            <i class="ri-search-line"></i>
            <el-input
                v-model="iconStore.searchKey"
                @focus="highlightSearchIcon('focus')"
                @blur="highlightSearchIcon('blur')"
                :placeholder="searchBarPlaceholder"
            />
            <i class="ri-close-circle-line" @click="clearSearchValue"></i>
            <div class="divider"></div>
            <div class="add-images" @click="openUploadForm">
                <i class="ri-add-box-line"></i>
                <span>{{ t('图标') }}</span>
            </div>
        </div>
        <div class="images" v-loading="loading">
            <div class="cate-list" v-for="cate in iconStore.getIconList" :key="cate.id">
                <div class="cateName">{{ t(cate.cateName) }}</div>
                <div class="item-list">
                    <div
                        class="item"
                        v-for="(item, index) in cate.children"
                        :key="item.id"
                        @mouseenter="mouseEvent(item, 'enter')"
                        @mouseleave="mouseEvent(item, 'leave')"
                        @dblclick="mouseEvent(item, 'dblclick')"
                    >
                        <div class="wrapper">
                            <div class="img">
                                <img :src="item.iconData" />
                            </div>
                            <el-tooltip placement="top" effect="light">
                                <template #content> {{ item.name }} </template>
                                <div class="name">{{ item.name }}</div>
                            </el-tooltip>
                        </div>
                        <Transition name="IconItem">
                            <div class="mask" v-show="item.showMask">
                                <!-- <div class="mask" v-show="true"> -->
                                <div class="top">
                                    <el-button type="primary" link @click="closeMask(item)"
                                        ><el-icon><CircleClose /></el-icon
                                    ></el-button>
                                </div>
                                <div class="down">
                                    <el-button type="primary" link @click="editorIconById(item)">编辑</el-button>
                                    <el-button type="danger" link @click="deleteIconById(item)">删除</el-button>
                                </div>
                            </div>
                        </Transition>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <y9Dialog v-model:config="dialogConfig"
        ><UploadForm
            :iconData="IconData"
            :name="IconName"
            :remark="IconRemark"
            :reset="reset"
            :satus="status"
            v-on:update:fileList="updateIconFileList"
            v-on:update:edit="updateIconData"
        ></UploadForm>
    </y9Dialog>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { computed, inject, nextTick, onMounted, reactive, ref, toRefs, watch } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import { $keyNameAssign, $tableHandleRender } from '@/utils/object';
    import { deleteIcon, getAppIconPageList, saveIcon, searchIconPageByName, uploadIcon } from '@/api/appIcon/index';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useIconStore } from '@/store/modules/iconStore';
    import { Search } from '@element-plus/icons';
    import UploadForm from './uploadForm.vue';
    import { cloneDeep } from 'lodash';

    const settingStore = useSettingStore();
    const iconStore = useIconStore();
    // const imagesDivMinHeight = ref('200px');

    const loading = ref(true);
    async function getIconData() {
        await iconStore.setIconList().then(() => {
            loading.value = false;
        });
    }
    getIconData();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const { t } = useI18n();
    const lineHeight = ref('60px');
    const lastItemWidth = ref('64px');
    const emits = defineEmits(['refresh', 'chooseIcon']);
    const searchColor = ref('--el-border-color');
    const IconFileList = ref<UploadUserFile[]>([]);
    const IconData = ref('');
    const IconName = ref('');
    const IconRemark = ref('');
    const status = ref('upload');
    const currentNode = ref({});
    const reset = ref(false);
    const searchResult = ref([]);
    const dialogConfig = ref({
        //弹窗配置
        show: false,
        width: '50%',
        title: '',
        type: '',
        resetText: computed(() => t('重置')),
        onOk: (newConfig) => {
            // console.log(newConfig);

            return new Promise(async (resolve, reject) => {
                let res = null;
                switch (status.value) {
                    case 'upload':
                        res = await iconStore.saveIconByUpload(formatUploadData());
                        if (res.success) {
                            ElMessage({
                                message: '上传成功',
                                type: 'success'
                            });
                            resolve();
                        } else {
                            ElMessage({
                                message: '上传失败',
                                type: 'error'
                            });
                            reject();
                        }
                        break;
                    case 'edit':
                        res = await iconStore.saveIconById(currentNode.value);
                        if (res.success) {
                            ElMessage({
                                message: '编辑成功',
                                type: 'success'
                            });
                            resolve();
                        } else {
                            ElMessage({
                                message: '编辑失败',
                                type: 'error'
                            });
                            reject();
                        }
                        break;
                    default:
                        break;
                }
            });
        },
        onReset: (newConfig) => {
            IconFileList.value = [];
            IconName.value = '';
            IconRemark.value = '';
        },
        visibleChange: (visible) => {
            dialogConfig.value.show = visible;
        }
    });
    function formatUploadData() {
        const data = new FormData();
        const colorProp = 'colors';
        const filesProp = 'iconFiles';
        for (let i = 0; i < IconFileList.value.length; i++) {
            const file = IconFileList.value[i];
            if (file.raw) {
                let s = file.name.split('.')[0].split('-');
                switch (s[1]) {
                    case '橙色':
                    case '金盏黄':
                    case 'orange':
                        data.append(colorProp, 'orange');
                        break;
                    case '黑色':
                    case '古鼎灰':
                    case 'black':
                        data.append(colorProp, 'black');
                        break;
                    case '红色':
                    case '粉团花红':
                    case 'red':
                        data.append(colorProp, 'red');
                        break;
                    case '蓝色':
                    case '淡紫蓝':
                    case 'blue':
                        data.append(colorProp, 'blue');
                        break;
                    case '绿色':
                    case '竹篁绿':
                    case 'green':
                        data.append(colorProp, 'green');
                        break;
                    case '紫色':
                    case '山梗紫':
                    case 'purple':
                        data.append(colorProp, 'purple');
                        break;
                    default:
                        data.append(colorProp, 'other');
                        // return ElMessage({
                        //     message: '没有这个图标颜色定义! ' + s[1],
                        //     type: 'error'
                        // });
                        break;
                }
                data.append(filesProp, file.raw);
                // console.log(file, '是文件');
            } else {
                console.log(file, '不是文件');
            }
        }
        data.append('name', IconName.value);
        data.append('remark', IconRemark.value);
        return data;
    }
    function changeCate(value) {
        console.log(value, '改变分类');
        let eleArray = Array.from(document.querySelectorAll('.images .cate-list'));
        eleArray.forEach((category) => {
            if (category.children[0].innerHTML == value) {
                category.scrollIntoView({
                    behavior: 'smooth' // 平滑滚动效果
                });
            }
        });
        // document.querySelector('.images').scrollIntoView({
        //     behavior: 'smooth' // 平滑滚动效果
        // });
    }
    const searchBarPlaceholder = ref('');

    watch(
        () => settingStore.webLanguage,
        (newValue, oldValue) => {
            // console.log(`searchKey变化了，新值：${newValue}, 旧值：${oldValue}`);
            _initLang(newValue);
        }
    );
    watch(
        () => iconStore.total,
        (newValue, oldValue) => {
            _initLang(settingStore.webLanguage);
        }
    );
    function _initLang(lang) {
        switch (lang) {
            case 'zh':
                searchBarPlaceholder.value = `搜索 ${iconStore.getTotal} 图标`;
                break;
            case 'en':
                searchBarPlaceholder.value = `Search ${iconStore.getTotal} Icon`;
                break;
            default:
                break;
        }
    }
    function updateIconFileList(data) {
        IconFileList.value = data.fs;
        IconName.value = data.name;
        IconRemark.value = data.remark;
    }
    function updateIconData(data) {
        // console.log(data, '更新图标数据');
        currentNode.value.name = data.name;
        currentNode.value.remark = data.remark;
    }
    function highlightSearchIcon(value) {
        switch (value) {
            case 'focus':
                console.log('focus');
                searchColor.value = '--el-color-primary';
                break;
            case 'blur':
                if (!iconStore.getSearchKey) {
                    searchColor.value = '--el-border-color';
                }
                break;
            default:
                break;
        }
    }

    const show = ref(false);
    function mouseEvent(node, value) {
        // console.log(value);
        switch (value) {
            case 'enter':
                iconStore.searchByNodeId(node, 'enter');
                break;
            case 'leave':
                iconStore.searchByNodeId(node, 'leave');
                break;
            case 'dblclick':
                emits('chooseIcon', node);
                break;
            default:
                break;
        }
    }
    function closeMask(node) {
        iconStore.searchByNodeId(node, 'leave');
    }
    function editorIconById(node) {
        status.value = 'edit';
        IconData.value = node.iconData;
        IconName.value = node.name;
        IconRemark.value = node.remark;
        currentNode.value = node;
        dialogConfig.value.title = t('编辑图标');
        dialogConfig.value.show = true;
    }
    function deleteIconById(node) {
        ElMessageBox.confirm('此操作将永久删除该文件, 是否继续?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            iconStore.deleteIconById(node).then((res) => {
                if (res && res.success) {
                    ElMessage({
                        message: '删除图标成功!',
                        type: 'success'
                    });
                    getIconData();
                } else {
                    ElMessage({
                        message: '删除图标失败!',
                        type: 'error'
                    });
                }
            });
        });
    }
    function clearSearchValue() {
        iconStore.clearSearchKey();
        searchColor.value = '--el-border-color';
    }
    function openUploadForm() {
        status.value = 'upload';
        dialogConfig.value.title = t('上传图标');
        dialogConfig.value.show = true;
    }
    onMounted(() => {
        _initLang(settingStore.webLanguage);
        if (iconStore.getIconList.length) {
            iconStore.initData();
        }
    });
</script>
<style lang="scss" scoped>
    .search-top {
        display: flex;
        align-items: center;
        border: var(--el-border-color) 1px solid;
        height: v-bind(lineHeight);
        line-height: v-bind(lineHeight);
        background-color: #fff;
        border-radius: 3px;
        padding: 0 15px;
        box-shadow: 2px 2px 2px 1px rgba(0, 0, 0, 0.06);
        :deep(.el-select) {
            .el-select__wrapper {
                box-shadow: none;
                background-color: var(--el-color-primary-light-9);
            }
        }
        .divider {
            width: 1px;
            height: 40px;
            background-color: var(--el-border-color);
            margin: 0 30px 0 30px;
        }
        & > i {
            font-size: v-bind('fontSizeObj.largeFontSize');
            color: var(--el-border-color);
            &.ri-search-line {
                color: v-bind('"var("+searchColor+")"');
                margin-right: 24px;
            }
            &.ri-close-circle-line {
                color: v-bind('"var("+searchColor+")"');
                &:hover {
                    cursor: pointer;
                }
            }
        }
        :deep(.el-input) {
            .el-input__wrapper {
                box-shadow: none;
                .el-input__inner::placeholder {
                    font-size: v-bind('fontSizeObj.baseFontSize');
                    color: var(--el-border-color);
                }
            }
        }
        .add-images {
            min-width: 78px;
            display: flex;
            align-items: center;
            font-size: v-bind('fontSizeObj.largeFontSize');
            color: var(--el-color-primary-light-3);
            & > i {
                margin-right: 5px;
            }
            &:hover {
                cursor: pointer;
                color: var(--el-color-primary);
            }
        }
    }
    .images {
        width: 100%;
        // min-height: b-bind(imagesDivMinHeight);
        // min-height: calc(100vh - 60px - 80px - 62px - 35px - 35px);
        height: calc(100vh - 60px - 80px - 62px - 35px - 35px);
        overflow-y: scroll;
        margin-top: 35px;
        background: #fff;
        border-radius: 3px;
        padding: 25px;
        box-sizing: border-box;
        box-shadow: 2px 2px 2px 1px rgba(0, 0, 0, 0.06);
        .cate-list {
            display: flex;
            flex-direction: column;
            .cateName {
                font-size: v-bind('fontSizeObj.largerFontSize');
                font-weight: bold;
                color: var(--el-text-color-primary);
                padding: 15px;
            }
            .item-list {
                display: flex;
                flex-wrap: wrap;
                align-content: flex-start;
                column-gap: 2%;
                .item {
                    position: relative;
                    .wrapper {
                        display: flex;
                        flex-direction: column;
                        justify-content: center;
                        padding: 15px;
                        width: 95px;
                        height: 130px;
                        box-sizing: border-box;
                        .img {
                            width: 100%;
                            min-height: 60px;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            img {
                                width: 64%;
                            }
                        }
                        div.name {
                            font-size: v-bind('fontSizeObj.baseFontSize');
                            line-height: 40px;
                            text-align: center;
                            color: #555555;
                            overflow: hidden;
                            text-overflow: ellipsis;
                            white-space: nowrap;
                        }
                    }

                    .mask {
                        position: absolute;
                        margin-top: -130px;
                        background-color: rgba(0, 0, 0, 0.2);
                        border-radius: 3px;
                        & > div.top {
                            width: 95px;
                            height: 40px;
                            display: flex;
                            justify-content: end;
                            align-items: start;
                            padding: 0px;
                            box-sizing: border-box;
                        }
                        & > div.down {
                            width: 95px;
                            height: 96px;
                            display: flex;
                            justify-content: center;
                            align-items: end;
                            .el-button {
                                height: 32px;
                            }
                        }
                    }
                }
            }
        }
    }
    .IconItem-enter-active,
    .IconItem-leave-active {
        transition: opacity 0.5s ease;
    }

    .IconItem-enter-from,
    .IconItem-leave-to {
        opacity: 0;
    }
</style>
