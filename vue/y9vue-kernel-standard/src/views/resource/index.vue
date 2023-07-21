<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-07 17:44:07
 * @Description: 
-->
<!-- 两个弹框 -->
<template>
    <div>
        <fixedTreeModule ref="fixedTreeRef" :treeApiObj="treeApiObj" @onDeleteTree="resourceRemove" @onTreeClick="handlerTreeClick">
            <template v-slot:treeHeaderRight>
                <!-- <el-button 
                    type="primary">
                    <i class="ri-add-line"></i>
                    <span>资源</span>
                </el-button>-->
            </template>
            <template v-slot:rightContainer>
                <!-- 右边卡片 -->
                <div v-if="currData.id">
                    <y9Card :title="`${$t('基本信息')} - ${currData.name ? currData.name : ''}`">
                        <template v-slot>
                            <div class="basic-btns">
                                <div class="btn-top">
                                    <span style="margin-right: 10px">
                                        <el-button class="global-btn-main" :size="fontSizeObj.buttonSize"
                                        :style="{ fontSize: fontSizeObj.baseFontSize }" 
                                        type="primary" @click="handlerMenuClick">
                                            <i class="ri-add-line"></i>
                                            {{ $t("菜单") }}
                                        </el-button>
                                        <el-button class="global-btn-main" :size="fontSizeObj.buttonSize"
                                        :style="{ fontSize: fontSizeObj.baseFontSize }" 
                                        type="primary" @click="handlerOperaClick">
                                            <i class="ri-add-line"></i>
                                            {{ $t("按钮") }}
                                        </el-button>
                                    </span>
                                    <el-button class="global-btn-second" :size="fontSizeObj.buttonSize"
                                        :style="{ fontSize: fontSizeObj.baseFontSize }" 
                                    v-if="editBtnFlag" @click="editBtnFlag = false">
                                        <i class="ri-edit-line"></i>
                                        {{ $t('编辑') }}
                                    </el-button>
                                    <span v-else>
                                        <el-button class="global-btn-second" :size="fontSizeObj.buttonSize"
                                        :style="{ fontSize: fontSizeObj.baseFontSize }" 
                                        :loading="saveBtnLoading" @click="saveBtnClick = true">
                                            <i class="ri-save-line"></i>
                                            {{ $t("保存") }}
                                        </el-button>
                                        <el-button class="global-btn-second" :size="fontSizeObj.buttonSize"
                                        :style="{ fontSize: fontSizeObj.baseFontSize }"  @click="editBtnFlag = true">
                                            <i class="ri-close-line"></i>
                                            {{ $t("取消") }}
                                        </el-button>
                                    </span>
                                </div>
                                <div>
                                    <!-- <el-button class="global-btn-second" @click="operaFlag = true" >
                                        <i class="ri-settings-3-fill"></i>
                                        操作类型
                                    </el-button>-->
                                    <!-- <el-button class="global-btn-second" :size="fontSizeObj.buttonSize"
                                        :style="{ fontSize: fontSizeObj.baseFontSize }" 
                                    @click="handlerExportApp" v-if="currData.resourceType === 0">
                                        <i class="ri-file-upload-line"></i>
                                        {{ $t("导出") }}
                                    </el-button> -->
                                    <span style="margin-left: 15px">
                                        <!-- <el-button class="global-btn-second">
                                            <i class="ri-route-line"></i>
                                            {{ $t("移动") }}
                                        </el-button> -->
                                        <el-button class="global-btn-second" :size="fontSizeObj.buttonSize"
                                        :style="{ fontSize: fontSizeObj.baseFontSize }"   @click="onSort">
                                            <i class="ri-order-play-line"></i>
                                            {{ $t("排序") }}
                                        </el-button>
                                    </span>
                                </div>
                            </div>
                            <BasicInfo :id="currData.id" :type="currData.resourceType" :editFlag="editBtnFlag" :saveClickFlag="saveBtnClick" @getInfoData="handlerEditSave" />
                        </template>
                    </y9Card>
                    <!-- <y9Card :title="`查看修改日志 - ${ currData.name? currData.name : '' }`" >
                        <y9Table 
                            :config="modifyLogTableConfig" border >
                        </y9Table>
                    </y9Card>
                    <y9Card :title="`角色关联 - ${currData.name? currData.name : ''}`">
                        <RoleRelation />
                    </y9Card>-->
                </div>
            </template>
        </fixedTreeModule>
        <!-- 新增 菜单弹框 -->
        <y9Dialog v-model:config="addDialogConfig">
            <y9Form ref="ruleFormRef" :config="menuFormConfig" ></y9Form>
          
        </y9Dialog>
        <!-- 资源排序 弹框 -->
        <y9Dialog v-model:config="sortDialogConfig">
			<treeSort ref="sortRef" :apiRequest="resourceTreeRoot" :apiParams="{parentId: currData.id}"></treeSort>
        </y9Dialog>
        <!-- 按钮操作 -->remix
        <y9Dialog v-model:config="addOperationConfig" @click.capture="remixIconPopVisible=false">
            <y9Form ref="operationFormRef" :config="operationFormConfig">
                <template #iconSelect>
                    <el-popover
                        v-model:visible="remixIconPopVisible"
                        placement="bottom"
                        :width="400"
                        >
                        <remix @clickIcon="onClickRemixIcon"></remix>
                        <template #reference>
                            
                            <div v-if="!operationUrl" style="color: var(--el-color-primary);" @click="remixIconPopVisible=true">{{$t('点击选择图标') }}</div>
                            <div v-else style="display: flex;align-items: center;" @click="remixIconPopVisible=true">
                                <i :class="operationUrl"  :style="{ fontSize: fontSizeObj.baseFontSize }" ></i>
                            </div>
                            
                        </template>
                    </el-popover>
                </template>
            </y9Form>
        </y9Dialog>
        <!-- 制造loading效果 -->
        <el-button style="display: none;" v-loading.fullscreen.lock="loading"></el-button>
    </div>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus';
import y9_storage from '@/utils/storage';
import settings from '@/settings';
import {
    resourceTreeList,
    resourceTreeRoot,
    menuAdd,
    menuDelete,
    resourceAdd,
    operationAdd,
    operationDel,
    treeSearch,
    sort
} from '@/api/resource/index';
import { roleTreeList } from '@/api/role/index';
import { applicationDel } from '@/api/system/index';
// 基本信息
import BasicInfo from './comps/BasicInfo.vue';

import remix from './comps/remix.vue';
import { useI18n } from "vue-i18n" 
import { inject, ref, watch } from 'vue';
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo'); 

const { t } = useI18n();

// loading
let loading = ref(false);

// 点击树节点 对应数据的载体
let currData = ref({ id: null });
// 节点的 基本信息 获取
function handlerTreeClick(data) {
    // 将拿到的节点信息 储存起来
    currData.value = data;
}


// 树 ref
const fixedTreeRef = ref<FormInstance>();
// 树的一级 子级的请求接口函数
const treeApiObj = ref({
    topLevel: resourceTreeList,//顶级（一级）tree接口,
    childLevel: {
        //子级（二级及二级以上）tree接口
        api: resourceTreeRoot,
    },
    search: {
        //搜索接口及参数
        api: treeSearch,
        params: {
        },
    },
});


// 删除资源
function resourceRemove(data) {
    ElMessageBox.confirm(`${t('是否删除')}【${data.name}】?`, t('提示'), {
        confirmButtonText: t('确定'),
        cancelButtonText: t('取消'),
        type: 'info',
    })
        .then(async () => {
            loading.value = true;
            // 进行 删除 操作 --
            let result;
            if (data.resourceType === 0) {
                result = await applicationDel([data.id]);
            } else if (data.resourceType === 1) {
                result = await menuDelete(data.id);
            } else {
                result = await operationDel(data.id);
            }
			
			if(result.success){
				
				/**
				 * 对树进行操作
				 */
				//1.删除前，需要手动点击的节点信息，如果有父节点则默认点击父节点，没有则点击tree数据的第一个节点
				const treeData = fixedTreeRef.value.getTreeData();//获取tree数据
				let clickNode = null;
				if(data.parentId){
					clickNode = fixedTreeRef.value.findNode(treeData,data.parentId);//找到父节点的信息
					fixedTreeRef.value?.y9TreeRef?.remove(data);//删除此节点
				}else if(treeData.length > 0){
					fixedTreeRef.value?.y9TreeRef?.remove(data);//删除此节点
					clickNode = treeData[0]
				}
				if(clickNode){
					fixedTreeRef.value?.handClickNode(clickNode)//手动设置点击当前节点
				}
				
					
			}
			
            loading.value = false;
            ElNotification({
                message: result.success ? t('删除成功') : t('删除失败'),
                type: result.success ? 'success' : 'error',
                duration: 2000,
                offset: 80,
            });
        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: t('已取消删除'),
                offset: 65,
            });
        });
}



// 点击菜单 按钮
function handlerMenuClick() {
    menuForm.value.parentId = currData.value.id;
    menuForm.value.systemId = currData.value.systemId;
    menuForm.value.appId = currData.value.appId;
    addDialogConfig.value.show = true;
    addDialogConfig.value.title = computed(() => t("新增菜单"));
}
// 菜单表单ref
const ruleFormRef = ref<FormInstance>();
// 菜单 表单
let menuForm = ref({ inherit: false, enabled: true });
let menuFormConfig = ref({
    model: menuForm.value,
    rules: {
        name: [{ required: true, message: computed(() => t("请输入菜单名称")), trigger: 'blur', }],
    },
    itemList: [
        {
            type: 'input',
            prop: 'name',
            label: computed(() => t("菜单名称")),
            required: true,
        },
        {
            type: 'input',
            prop: 'customId',
            label: computed(() => t("自定义ID")),
        },
        {
            type: 'input',
            prop: 'url',
            label: computed(() => t("链接地址")),
        },
        {
            type: "radio",
            label: computed(() => t("是否可用")),
            prop: "enabled",
            required: true,
            props: {
                radioType: "radio",
                options: [
                    { label: computed(() => t("是")), value: true },
                    { label: computed(() => t("否")),  value: false },
                ],
            }
        },
        {
            type: "radio",
            label: computed(() => t("是否继承")),
            required: true,
            prop: "inherit",
            props: {
                radioType: "radio",
                options: [
                    { label: computed(() => t("是")), value: true },
                    { label: computed(() => t("否")),  value: false },
                ],
            }
        },
        {
            type: 'input',
            prop: 'target',
            label: computed(() => t("打开位置")),
        },
        {
            type: 'input',
            prop: 'component',
            label: computed(() => t("菜单部件")),
        },
        {
            type: 'input',
            prop: 'iconUrl',
            label: computed(() => t("图标地址")),
        },
        {
            type: 'input',
            prop: 'meta',
            label: computed(() => t("元信息")),
        },
        {
            type: 'input',
            prop: 'tabIndex',
            label: computed(() => t("排列序号")),
        },
        {
            type: 'textarea',
            prop: 'description',
            label: computed(() => t("资源描述")),
            rows: 3
        },
        
    ],
    descriptionsFormConfig: {
        labelWidth: '200px',
        labelAlign: 'center'
    },
})

// 增加菜单 弹框的变量配置 控制
let addDialogConfig = ref({
    show: false,
    title: computed(() => t("新增菜单")),
    width: '40%',
    onOkLoading: true,
    onOk: (newConfig) => {
        return new Promise(async (resolve, reject) => {
            const ruleFormInstance = ruleFormRef.value?.elFormRef;
            await ruleFormInstance.validate(async (valid) => {
                if(valid) {
                    // 将数值为''的值去除
                    Object.keys(ruleFormRef.value?.model).forEach(key => {
                        if(ruleFormRef.value?.model[key] == '' && ruleFormRef.value?.model[key] !== false){
                            delete ruleFormRef.value?.model[key]
                            return;
                        }
                    })
                    
                    await menuAdd(ruleFormRef.value?.model).then(async result => {
                        if(result.success) {
                            /**
                             * 对树进行操作：新增节点进入树
                             */
                            //1.更新当前节点的子节点信息
                            const treeData = fixedTreeRef.value.getTreeData();//获取tree数据
                            const currNode = fixedTreeRef.value.findNode(treeData,currData.value.id);//找到树节点对应的节点信息
                            const childData = await postNode({id:currData.value.id})//重新请求当前节点的子节点，获取格式化后的子节点信息
                            Object.assign(currNode,{children:childData})//合并节点信息
                            //2.手动设置点击当前节点
                            fixedTreeRef.value?.handClickNode(currNode)//手动设置点击当前节点
                        }
                        // 表单弹框消失 表单数据清空
                        menuForm.value = { inherit: false, enabled: true };
                        ElNotification({
                            title: result.success ? t('成功') : t('失败'),
                            message: result.success ? t('保存成功') : t('保存失败'),
                            type: result.success ? 'success' : 'error',
                            duration: 2000,
                            offset: 80,
                        });
                        resolve()
                    }).catch(() => {
                        reject();
                    });

                }else {
                    reject()
                }
            })
        });
    },
	visibleChange:(visible) => {
		if(!visible){
			  menuForm.value = { inherit: false, enabled: true };
		}
	}
});



// 点击按钮  按钮
function handlerOperaClick() {
    operationForm.value.parentId = currData.value.id;
    operationForm.value.systemId = currData.value.systemId;
    operationForm.value.appId = currData.value.appId;
	operationForm.value.displayType = 0;
    addOperationConfig.value.show = true;
    addOperationConfig.value.title = computed(() => t("新增按钮"));
}

//按钮表单ref
const operationFormRef = ref<FormInstance>();
// 按钮 增加表单
let operationForm = ref({ inherit: false, enabled: true, displayType: 0 });
let operationFormConfig = ref({
    model: operationForm.value,
    rules: {
        name: [
            { required: true, message: computed(() => t("请输入按钮名称")), trigger: 'blur', },
        ],
    },
    itemList: [
        {
            type: 'input',
            prop: 'name',
            label: computed(() => t("按钮名称")),
            required: true,
        },
        {
            type: 'input',
            prop: 'customId',
            label: computed(() => t("自定义ID")),
        },
        {
            type: "radio",
            label: computed(() => t("是否可用")),
            prop: "enabled",
            required: true,
            props: {
                radioType: "radio",
                options: [
                    { label: computed(() => t("是")), value: true },
                    { label: computed(() => t("否")),  value: false },
                ],
            }
        },
        {
            type: "radio",
            label: computed(() => t("是否继承")),
            required: true,
            prop: "inherit",
            props: {
                radioType: "radio",
                options: [
                    { label: computed(() => t("是")), value: true },
                    { label: computed(() => t("否")),  value: false },
                ],
            }
        },
        {
            type: 'slot',
            props: {
                slotName: 'iconSelect',
            },
            label: computed(() => t("选择图标"))
        },
        {
            type: 'radio',
            prop: 'displayType',
            label: computed(() => t("展示方式")),
            required: true,
            props: {
                radioType: "radio",
                options: [
                    { label: computed(() => t("图标文本")), value: 0 },
                    { label: computed(() => t("图标")),  value: 1 },
                    { label: computed(() => t("文本")),  value: 2 },
                ],
            }
        },
        {
            type: 'input',
            prop: 'url',
            label: computed(() => t("链接地址")),
        },
        {
            type: 'input',
            prop: 'eventName',
            label: computed(() => t("事件")),
        },
        {
            type: 'input',
            prop: 'tabIndex',
            label: computed(() => t("排列序号")),
        },
        {
            type: 'input',
            prop: 'description',
            label: computed(() => t("资源描述")),
            rows: 3
        },        
    ],
    descriptionsFormConfig: {
        labelWidth: '200px',
        labelAlign: 'center'
    },
})

//选择图标气泡框
let remixIconPopVisible = ref(false);
let operationUrl = ref('');
//点击选择图标
function onClickRemixIcon(item) {
	operationUrl.value = item.class;
	remixIconPopVisible.value = false;
}
// 增加 按钮 按钮 的配置
let addOperationConfig = ref({
    show: false,
    title: computed(() => t("新增按钮")),
    width: '40%',
    onOkLoading: true,
    onOk: (newConfig) => {
        return new Promise(async (resolve, reject) => {
            const operationFormInstance = operationFormRef.value?.elFormRef;
            await operationFormInstance.validate(async (valid) => {
                if(valid) {

                    const params = {
                        iconUrl: operationUrl.value,
                        ...operationFormRef.value?.model
                    }

                     // 将数值为''的值去除
                     Object.keys(params).forEach(key => {
                        if(params[key] == '' && params[key] !== false){
                            delete params[key]
                            return;
                        }
                    })
                    
                    await operationAdd(params).then(async result => {
                        /**
                         * 对树进行操作：新增节点进入树
                         */
                        if(result.success) {
                            //1.更新当前节点的子节点信息
                            const treeData = fixedTreeRef.value.getTreeData();//获取tree数据
                            const currNode = fixedTreeRef.value.findNode(treeData,currData.value.id);//找到树节点对应的节点信息
                            const childData = await postNode({id:currData.value.id})//重新请求当前节点的子节点，获取格式化后的子节点信息
                            Object.assign(currNode,{children:childData})//合并节点信息
                            //2.手动设置点击当前节点
                            fixedTreeRef.value?.handClickNode(currNode)//手动设置点击当前节点
                        }
                        
                        // 表单弹框消失 表单数据清空
                        operationForm.value = { inherit: false, enabled: true };
                        ElNotification({
                            title: result.success ? t('成功') : t('失败'),
                            message: result.success ? t('保存成功') : t('保存失败'),
                            type: result.success ? 'success' : 'error',
                            duration: 2000,
                            offset: 80,
                        });
                        resolve()
                    }).catch(() => {
                        reject();
                    });
	
                }else {
                    reject()
                }
            })
        });
    },
	visibleChange:(visible) => {
		if(!visible){
			     operationForm.value = { inherit: false, enabled: true };
		}
	}
});



// 点击保存按钮 的 flag
let saveBtnClick = ref(false);
// 控制 基本信息 编辑按钮 与 保存，取消按钮的显示与隐藏
let editBtnFlag = ref(true);
// 保存 按钮的loading 控制
let saveBtnLoading = ref(false);
// 基本信息 编辑后保存
async function handlerEditSave(data) {
    saveBtnLoading.value = true;
    // 更新基本信息 接口按钮--
    // data 为基本信息 数据
    let result;
    if (data.resourceType === 0) {
        result = await resourceAdd(data);
    } else if (data.resourceType === 1) {
        result = await menuAdd(data);
    } else {
        result = await operationAdd(data);
    }
  
	/**
	* 对树进行操作：手动更新节点信息
	*/
	//1.更新当前节点的信息
	const treeData = fixedTreeRef.value.getTreeData();//获取tree数据
	const currNode = fixedTreeRef.value.findNode(treeData,currData.value.id);//找到树节点对应的节点信息
	Object.assign(currNode,data)//合并节点信息
	//2.手动设置点击当前节点
	fixedTreeRef.value?.handClickNode(currNode)//手动设置点击当前节点
	
	
	ElNotification({
	    message: result.success ? t('保存成功') : t('保存失败'),
	    type: result.success ? 'success' : 'error',
	    duration: 2000,
	    offset: 80,
	});
	// loading为false 编辑 按钮出现 保存按钮未点击状态
	saveBtnLoading.value = false;
	editBtnFlag.value = true;
	saveBtnClick.value = false;
}



// 导出 app
function handlerExportApp() {
    const url =
        import.meta.env.VUE_APP_CONTEXT +
        'api/rest/impExp/exportAppJSON?appId=' +
        currData.value.id +
        '&access_token=' +
        y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
    window.open(url);
}
let sortRef = ref()
let sortDialogConfig = ref({
    show: false,
    title: computed(() => t("综合排序")),
    width: '40%',
    onOkLoading: true,
    showFooter: true,//是否显示底部
    onOk: (newConfig) => {
    	return new Promise(async (resolve, reject) => {
    		
    	    let tableData = sortRef.value.tableConfig.tableData;
    		const ids = [];
		    tableData.forEach((element) => {
			   ids.push(element.id);
		    });
			await sort(ids.toString()).then(async result => {
                /**
                * 对树进行操作：手动更新当前节点的子节点信息
                */
                //1.更新当前节点的子节点信息
                const treeData = fixedTreeRef.value.getTreeData();//获取tree数据
                const currNode = fixedTreeRef.value.findNode(treeData,currData.value.id);//找到树节点对应的节点信息
                const childData = await postNode({id:currData.value.id})//重新请求当前节点的子节点，获取格式化后的子节点信息
                Object.assign(currNode,{children:childData})//合并节点信息
                //2.手动设置重新点击当前节点
                fixedTreeRef.value?.handClickNode(currNode)//手动设置点击当前节点
                
                ElNotification({
                    title: result.success ? t('成功') : t('失败'),
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80,
                });
                
                resolve();
            }).catch(() => {
                reject();
            })
    		
    	})
    },
});

//请求某个节点，返回格式化好的数据
function postNode(node){
	return new Promise((resolve,reject) => {
		fixedTreeRef.value.onTreeLazyLoad(node,data => {
			resolve(data)
		})
	})
}

//排序按钮点击时触发
const onSort = () => {
	Object.assign(sortDialogConfig.value,{
		show:true,
        title: computed(() => t("综合排序"))
	})
}

async function getDataList() {
    const result = await resourceTreeRoot({
        parentId: currData.value.id
    });
    sortTableConfig.value.tableData = result.data;
}
const sortTableConfig = ref({
    columns: [
        { title: '', type: 'radio', fixed: 'left', width: 90 },
        { title: computed(() => t("名称")), key: 'name' },
        {
            title: 'type',
            key: 'type',
            width: 100,
            render: (row) => {
                switch (row.type) {
                    case 0:
                        return '应用';
                        break;
                    case 2:
                        return '按钮';
                        break;
                    default:
                        return '菜单';
                        break;
                }
            },
        },

    ],
    tableData: [],
    pageConfig: false,
});

let currentRow = ref({});
function handlerSelectData(data) {
    currentRow.value = data;
}

async function handlerMoveUp() {
    if (currentRow.value == '' || currentRow.value == null) {
        ElNotification({ title: t('失败'), message: t('请选择岗位'), type: 'error', duration: 2000, offset: 80 });
        return;
    }
    let tableData = sortTableConfig.value.tableData;
    tableData.forEach(function (element, index) {
        if (index == 0 && element.id == currentRow.value.id) {
            ElNotification({ title: t('失败'), message: t('处于顶端，不能继续上移'), type: 'error', duration: 2000, offset: 80 });
            return;
        }
        if (element.id == currentRow.value.id) {
            let obj = tableData[index - 1];
            tableData[index - 1] = currentRow.value;
            tableData[index] = obj;
            sortTableConfig.value.tableData = tableData;
            return;
        }
    });
}

async function handlerMoveDown() {
    if (currentRow.value == '' || currentRow.value == null) {
        ElNotification({ title: t('失败'), message: t('请选择岗位'), type: 'error', duration: 2000, offset: 80 });
        return;
    }
    let tableData = sortTableConfig.value.tableData;
    for (let i = 0; i < tableData.length; i++) {
        if ((tableData.length - 1) == i && tableData[i].id == currentRow.value.id) {
            ElNotification({ title: t('失败'), message: t('处于末端，不能继续下移'), type: 'error', duration: 2000, offset: 80 });
            return;
        }
        if (tableData[i].id == currentRow.value.id) {
            let obj = tableData[i + 1];
            tableData[i] = obj;
            tableData[i + 1] = currentRow.value;
            sortTableConfig.value.tableData = tableData;
            break;
        }
    }
}

</script>
<style scoped lang="scss">
.basic-btns {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20px;
    flex-wrap: wrap;

    .btn-top {
        margin-bottom: 10px;
    }
}

// :deep(.y9-dialog-overlay .y9-dialog .y9-dialog-body .y9-dialog-content[data-v-0c2bb858]) {
//     padding: 21px 45px 21px 21px !important;
// }

</style>
