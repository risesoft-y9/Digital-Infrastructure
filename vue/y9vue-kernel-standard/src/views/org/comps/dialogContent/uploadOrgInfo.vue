<template>
    <!-- 上传组织机构信息application/vnd.ms-excel -->
    <div style="min-height: 200px">
        <div style="margin-bottom: 10px;">
            <el-upload
                ref="uploadRef"
                v-model:file-list="fileList"
                action=""
                :headers="headers"
                :limit="1"
                :accept="type === 'xml' ? 'text/xml' : ''"
                :http-request="httpRequest"
                :auto-upload="false"
            >
                <template #trigger>
                    <el-input style="margin-right: 10px" :placeholder="$t('点击可选择文件')"></el-input>
                </template>

                <el-button type="primary" class="global-btn-main" 
                :size="fontSizeObj.buttonSize" :style="{ fontSize: fontSizeObj.baseFontSize }"
                @click="submitUpload" style="margin-left: 10px">
                    <i class="ri-file-upload-line" :style="{ 'font-size': fontSizeObj.baseFontSize }"></i>
                    <span>{{ $t('上传') }}</span>
                </el-button>
            </el-upload>
        </div>
        <div :style="{ 'color': '#606266', 'font-size': fontSizeObj.baseFontSize }">{{ type === 'xml'?$t('(仅支持相应的XML文件)') : $t('(仅支持相应的XLS文件)')}}</div>
        <el-button style="display: none" v-loading.fullscreen.lock="loading"></el-button>
    </div>
</template>

<script lang="ts" setup>
	import { useI18n } from "vue-i18n"
    import { impOrg4xml,impOrgTreeExcel } from '@/api/impExp/index';
    import settings from '@/settings';
    import y9_storage from '@/utils/storage';
    import type { UploadInstance } from 'element-plus';
    import { inject, onMounted, reactive, ref, watch } from 'vue';
    import { useSettingStore } from "@/store/modules/settingStore";
	const settingStore = useSettingStore();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const { t } = useI18n();
    const uploadRef = ref<UploadInstance>();
    const data = reactive({
        fileList: [],
        headers: {},
        actions: import.meta.env.VUE_APP_CONTEXT + 'api/rest/impExp/importOrgXml',
    });

    const props = defineProps({
        refresh: Function,
		type:{
			type:String,
			default:'xml',//类型有xml和xls
		},
		id:{//type=xls时，必传
			type:[String,Number]
		}
    });

    const emits = defineEmits(['update']);
    
    let loading = ref(false);

    let { fileList, headers, actions } = toRefs(data);


    const submitUpload = () => {
		
		if(fileList.value.length > 0){
			
			 uploadRef.value!.submit();
		}else{
			ElNotification({title: t('失败'),message: t('请选择文件'),type: 'error',duration: 2000,offset: 80});
		}
    
    };

    async function httpRequest(params) {
		loading.value = true;
		
		let result = {success:false,msg:''};
		
		if(props.type === 'xml'){
			result = await impOrg4xml(params.file);
			
		}else if(props.type === 'xls'){
			result = await impOrgTreeExcel(params.file,props.id);
		}
		ElNotification({
			 title: result.success ? t('成功') : t('失败'),
			 message: result.msg,
			 type: result.success ? 'success' : 'error',
			 duration: 2000,
			 offset: 80,
		});
		if (result.success) {
			 // 重新刷新树 数据
				props.refresh && props.refresh();
				loading.value = false;
				emits('update');
		}
    }
    onMounted(() => {
        const access_token = y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
        if (access_token) {
            headers.value['Authorization'] = 'Bearer ' + access_token;
        }
    });
</script>

<style lang="scss" scoped>
    .upload-demo {
        // width: 100%;
        display: flex;
        flex-direction: initial;
        float: left;
        flex-wrap: wrap;
        align-items: center;
        :deep(.el-upload-list__item-name) {
            text-align: center;
        }
    }
</style>
