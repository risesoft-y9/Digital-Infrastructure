<!--  -->
<template>
    <div>
	
        <fixedTreeModule
            ref="fixedTreeRef"
            :treeApiObj="treeApiObj"
            @onDeleteTree="roleRemove"
            @onTreeClick="handlerTreeClick"
        >
            <template v-slot:rightContainer v-if="currData.id">
                <!-- 右边卡片 -->
                <y9Card :title="`${$t('基本信息')} - ${currData.name ? currData.name : ''}`">
                    <template v-slot>
                        <BasicInfo
                            :id="currData.id"
                            :type="currData.resourceType"
                            :editFlag="editBtnFlag"
                            :saveClickFlag="saveBtnClick"
                            @getInfoData="handlerEditSave"
                        />
                    </template>
                </y9Card>
                <!-- 角色关联 -->
                <y9Card :title="`${$t('角色关联')} - ${currData.name ? currData.name : ''}`">
                    <template v-slot>
                       <RelationRole :id="currData.id" :appId="currData.appId"  />
                    </template>
                </y9Card>
                <!-- 组织关联 -->
                <y9Card :title="`${$t('组织关联')} - ${currData.name ? currData.name : ''}`">
                    <RelationOrg :id="currData.id" />
                </y9Card>
            </template>
        </fixedTreeModule>
        <!-- 制造loading效果 -->
        <el-button style="display: none;"
        v-loading.fullscreen.lock="loading"></el-button>
    </div>
</template>

<script lang="ts" setup>
    import { useI18n } from "vue-i18n"
    import { resourceTreeList, menuDelete, operationDel, resourceTreeRoot, treeSearch } from '@/api/resource/index';
    import { applicationDel } from '@/api/system/index';
    // 基本信息
    import BasicInfo from './comps/BasicInfo.vue';
    // 角色 关联
    import RelationRole from "./comps/RelationRole.vue";
    // 组织 关联
    import RelationOrg from "./comps/RelationOrg.vue";
    
    const { t } = useI18n();

    // 单独变量
    // 点击树节点 对应数据的载体
     let currData = ref({ id: null });
    // 变量 对象
    let state = reactive({
        // loading
        loading: false,
        // 控制 基本信息 编辑按钮 与 保存，取消按钮的显示与隐藏
        editBtnFlag: true,
        // 保存 按钮的loading 控制
        saveBtnLoading: false,
        // 点击保存按钮 的 flag
        saveBtnClick: false,
        // 树 ref
        fixedTreeRef: ref<FormInstance>(),
        // 树的一级 子级的请求接口函数
        treeApiObj: {
            topLevel: resourceTreeList,//一级接口
            childLevel: {
                //子级（二级及二级以上）tree接口
                api: resourceTreeRoot,
                params: {
                },
            },
            search: {
                //搜索接口及参数
                api: treeSearch,
                params: {
                },
            },
        },
    })

    let { 
        editBtnFlag, 
        saveBtnLoading, 
        saveBtnClick, 
        fixedTreeRef,
        treeApiObj,
        loading,
    } = toRefs(state);


    // 树节点的 基本信息 获取
    function handlerTreeClick(data) {
        // 将拿到的节点信息 储存起来
        currData.value = data;
    }
    
     // 删除资源
    function roleRemove(data) {
        ElMessageBox.confirm(`${t('是否删除')}【${data.name}】?`, t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info',
        })
            .then(async () => {
                // 进行 删除 操作 --
                loading.value = true;
                let result;
                if (data.resourceType === 0) {
                    result = await applicationDel([data.id]);
                } else if (data.resourceType === 1) {
                    result = await menuDelete(data.id);
                } else {
                    result = await operationDel(data.id);
                }
				
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

    function handlerEditSave(data){

    }

 
</script>
<style scoped lang="scss">
// .btn-class {
//     // display: flex;
//     // justify-content: space-between;
// }
</style>
