<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-07 17:44:07
 * @Description: 
-->
<!-- 三个弹框 -->
<template>
    <div>
        <!-- 操作类型 列表弹框 -->
        <y9Dialog  :config="operationDialogConfig" @config="handlerOpeConfig"  >
            <div style="margin-bottom: 10px">
                <el-button class="global-btn-main" :size="fontSizeObj.buttonSize" 
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                type="primary" @click="operaAddConfig.show = true">
                    <i class="ri-add-line"></i>
                    新增
                </el-button>
                <el-button class="global-btn-second" :size="fontSizeObj.buttonSize" 
                :style="{ fontSize: fontSizeObj.baseFontSize }" @click="extendValueHandler" >
                    <i class="ri-external-link-line"></i>
                    扩展属性
                </el-button>
                <el-button class="global-btn-second" :size="fontSizeObj.buttonSize" 
                :style="{ fontSize: fontSizeObj.baseFontSize }" @click="handlerMoveUp" >
                    <i class="ri-arrow-up-line"></i>
                    {{ $t('上移') }}
                </el-button>
                <el-button class="global-btn-second" :size="fontSizeObj.buttonSize" 
                :style="{ fontSize: fontSizeObj.baseFontSize }" @click="handlerMoveDown" >
                    <i class="ri-arrow-down-line"></i>
                    {{ $t('下移') }}
                </el-button>
                <el-button class="global-btn-second" :size="fontSizeObj.buttonSize" 
                :style="{ fontSize: fontSizeObj.baseFontSize }" @click="handlerSaveMove" >
                    <i class="ri-save-line"></i>
                    {{ $t('保存') }}
                </el-button>
                <el-button class="global-btn-second" :size="fontSizeObj.buttonSize" 
                :style="{ fontSize: fontSizeObj.baseFontSize }" @click="handlerCommonDo" >
                    <i class="ri-tools-line"></i>
                    常用操作
                </el-button>
            </div>
            <y9Table 
                v-model:selectedVal="sortValue"
                :config="operaTableConfig" border @on-change="handlerSelectData"  >
            </y9Table>
        </y9Dialog>
        <!-- 操作类型  增加 表单弹框-->
        <y9Dialog  :config="operaAddConfig" @config="handlerAddConfig" >
            <el-form
                ref="operaFormRef"
                :model="operaForm"
                :rules="operaRules"
                label-width="120px"
                status-icon
            >
                <el-form-item label="唯一标识" prop="name" :size="fontSizeObj.buttonSize"  >
                    <el-input v-model="operaForm.id" />
                </el-form-item>
                <el-form-item label="名称" prop="name"  :size="fontSizeObj.buttonSize" >
                    <el-input v-model="operaForm.name" />
                </el-form-item>
                <el-form-item label="描述" prop="value" :size="fontSizeObj.buttonSize"  >
                    <el-input v-model="operaForm.remark" />
                </el-form-item>
            </el-form>
        </y9Dialog>
        <!-- 扩展属性 -->
        <ExtendValue :flag="extendFlag" />
        <!-- 常用操作 表格弹框 -->
        <y9Dialog  :config="commonDialogConfig" @config="handlerCommonConfig" >
            <y9Table 
                :config="commonTableConfig" border   >
            </y9Table>
        </y9Dialog>
    </div>
</template>

<script lang='ts' setup>
// 扩展属性
import ExtendValue from "./ExtendValue.vue"
import { inject, ref, watch } from "vue";
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo');  
// 扩展属性 的 组件弹框 变量
let extendFlag = ref(false);
// 扩展属性 按钮 点击
function extendValueHandler() {
    if(!selectData.value.id){
        // 没有选择
        ElMessage({
            message: '请选择资源',
            type: 'warning',
            offset: 60,
        })
        extendFlag.value = false;
    }else {
        extendFlag.value = true;
    }
}

const props = defineProps({
    flag: {
        type: Boolean,
        default: false
    }
})
watch(() => props.flag, (new_) => {
    if(new_){
        // 对数据进行排序
        operaTableConfig.value.tableData.sort((a, b) => a.sort - b.sort );
        // 调用列表函数 --
        operationDialogConfig.show = true;
    }
})
// 操作类型列表 初始化 获取数据后 operaTableConfig.value.tableData赋值 并进行 sort 的排序
// ---

// 操作类型 弹框列表 的配置 
let operationDialogConfig = reactive({
    show: false,
    title: '操作类型列表',
    showFooter: false,
    onCancel:  (newConfig) => {
        extendDialogConfig.show = false;
    }
});
// 配置 赋值 emit事件
function handlerOpeConfig(data) {
    operationDialogConfig = data.value;
}
// 操作类型 弹框 的 表格配置
let operaTableConfig = ref({
    columns: [
        { title: '', type: 'radio', width: 90 },
        { title: '唯一标识', key: 'id' },
        { title: '名称', key: 'name' },
        { title: '描述', key: 'remark' },
        { title: '操作',
            render: (row) => {
                return h('span', {
                    onClick: () => {
                        ElMessageBox.confirm(
                            `是否删除【${row.name}】?`,
                            '提示', {
                            confirmButtonText: '确定',
                            cancelButtonText: '取消',
                            type: 'info',
                        }).then(async () => {
                            // 删除操作  --
                            ElNotification({
                                title: '成功',
                                message: '删除成功',
                                type: 'success',
                                duration: 2000,
                                offset: 80
                            });
                            // 重新请求 列表函数 --
                        }).catch(() => {
                            ElMessage({
                                type: 'info',
                                message: '已取消删除',
                                offset: 65
                            });
                        });
                    }
                }, '删除')
            }
        }
    ],
    tableData: [{ name: '还好', remark: 'ewh', id: 1, sort: 2 }, { name: '还好', remark: 'dsfd', id: 2, sort: 1 }]

})

// 操作类型 新增表单ref
const operaFormRef = ref<FormInstance>()
// 操作类型 增加表单
let operaForm = ref({});
// rule规则 添加 操作类型
const operaRules = reactive<FormRules>({
    id: [
        {
        required: true,
        message: '请输入唯一标识',
        trigger: 'blur',
        },
    ],
    name: [
        {
        required: true,
        message: '请输入名称',
        trigger: 'blur',
        },
    ],
    remark: [
        { 
        required: true,
        message: '请输入描述', 
        trigger: 'blur' 
        },
    ],
})
// 新增 操作类型 表单变量配置
let operaAddConfig = reactive({
    show: false,
    title: '新增属性',
    okText: '提交',
    width: '30%',
    onOkLoading: true,
    onOk: (newConfig) => {
        return new Promise(async (resolve, reject) => {
            if (!operaFormRef.value) return
            await operaFormRef.value.validate((valid, fields) => {
                if (valid) {
                    // 通过验证
                    // 进行新增 操作 ---
                    setTimeout(() => {
                        ElNotification({
                            title: '成功',
                            message: '保存成功',
                            type: 'success',
                            duration: 2000,
                            offset: 80
                        });
                        // 表单 弹框消失 表单数据清空 
                        operaAddConfig.show = false;
                        operaForm.value = {};
                        // 重新获取列表 数据 --
                        resolve();
                    }, 2000)
                } else {
                    reject();
                }
            })
        })
    },
    onCancel:  (newConfig) => {
        operaAddConfig.show = false;
    }
    
});
// emit 事件赋值
function handlerAddConfig(data){
    operaAddConfig = data.value;
}

// 点击单选 选择的数据 容器
let selectData = ref({});
// 选择单选框后 拿到的数据
function handlerSelectData(id, data) {
    selectData.value = data;
}

// 排序的表格 双向绑定的 变量
let sortValue = ref(null);
// 上移 事件  
async function handlerMoveUp() {
    if(!selectData.value.id){
        // 没有选择
        ElMessage({
            message: '请选择资源',
            type: 'warning',
            offset: 60,
        })
    }else {
        // 选择 后 进行 上移操作
        await operaTableConfig.value.tableData.forEach(item => {
            if(item.id === selectData.value.id){
                if(item.sort === 1) {
                    ElMessage({
                        message: '已经是最顶部了',
                        type: 'info',
                        offset: 60,
                    })
                }else {
                    item.sort -= 1;
                }
            }else {
                item.sort += 1;
            }
        })
        operaTableConfig.value.tableData = operaTableConfig.value.tableData.sort((a, b) => a.sort - b.sort);
        sortValue.value = selectData.value.id;
    }
}
// 下移  事件 
async function handlerMoveDown() {
    if(!selectData.value.id){
        // 没有选择
        ElMessage({
            message: '请选择资源',
            type: 'warning',
            offset: 60,
        })
    }else {
        // 选择 后 进行 下移操作
        await operaTableConfig.value.tableData.forEach(item => {
            if(item.id === selectData.value.id){
                if(item.sort === operaTableConfig.value.tableData.length) {
                    ElMessage({
                        message: '已经是最底部了',
                        type: 'info',
                        offset: 60,
                    })
                }else {
                    item.sort += 1;
                }
            }else {
                item.sort -= 1;
            }
        })
        operaTableConfig.value.tableData = operaTableConfig.value.tableData.sort((a, b) => a.sort - b.sort);
        sortValue.value = selectData.value.id;
    }
}
// 排序事件 的保存 
function handlerSaveMove() {
    // 进行 保存 按钮 操作 ---
    ElMessage({
        message: '保存成功',
        type: 'success',
        offset: 60,
    })
}

// 点击 常用操作 按钮
function handlerCommonDo() {
    commonDialogConfig.show = true;
    // 执行常用操作列表 请求-- 并赋值commonTableConfig.value.tableData数组
}
// 常用操作 表格弹框 配置
let commonDialogConfig = reactive({
    show: false,
    title: '常用操作',
    showFooter: false,
    width: '50%',
    onCancel:  (newConfig) => {
        commonDialogConfig.show = false;
    }
});
// emit 事件
function handlerCommonConfig(data) {
    commonDialogConfig = data.value;
}
// 常用操作 弹框 里的表格 配置
let commonTableConfig = ref({
    columns: [
        { title: '', type: 'index' },
        { title: '唯一标识', key: 'id' },
        { title: '名称', key: 'name' },
        { title: '描述', key: 'remark' },
    ],
    tableData: [{ name: '还好', remark: 'ewh', id: 1 }, { name: '还好', remark: 'dsfd', id: 2 }]
})

</script>
<style scoped lang="scss">


</style>