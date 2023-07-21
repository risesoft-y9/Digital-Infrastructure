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
        <!-- 扩展属性 列表弹框 -->
        <y9Dialog :config="extendDialogConfig" @config="handlerExtendConfig"  >
            <div style="margin-bottom: 10px">
                <el-button  type="primary" @click="handlerExtendAdd" :style="{ fontSize: fontSizeObj.baseFontSize }"
                :size="fontSizeObj.buttonSize"  class="global-btn-main">
                    <i class="ri-add-line"></i>
                    新增
                </el-button>
            </div>
            <y9Table 
                :config="extendTableConfig" border  >
            </y9Table>
        </y9Dialog>
        <!-- 扩展属性 弹框 扩展属性增加 -->
        <y9Dialog  :config="extendAddConfig" @config="handlerAddConfig"  >
            <el-form
                ref="extendFormRef"
                :model="extendForm"
                :rules="extendRules"
                label-width="120px"
                status-icon
            >
                <el-form-item label="属性名称" prop="name" :size="fontSizeObj.buttonSize" >
                    <el-input v-model="extendForm.name" />
                </el-form-item>
                <el-form-item label="属性值" prop="value" :size="fontSizeObj.buttonSize" >
                    <el-input v-model="extendForm.value" />
                </el-form-item>
            </el-form>
        </y9Dialog>
    </div>
</template>

<script lang='ts' setup>
import { useSettingStore } from "@/store/modules/settingStore";
import { inject, ref, watch } from "vue";
const settingStore = useSettingStore()
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo'); 
const props = defineProps({
    flag: {
        type: Boolean,
        default: false
    }
})
watch(() => props.flag, (new_) => {
    if(new_){
        // 当打开弹框时 
        // 进行列表请求 
        // ---
        extendDialogConfig.show = true;
    }
})

// 初始化 列表请求函数 赋值给extendTableConfig.value.tableData 表格
// ----

// 扩展属性 弹框的变量 配置
let extendDialogConfig = reactive({
    show: false,
    title: computed(() => t("扩展属性列表")),
    width: '50%',
    showFooter: false,
    onCancel:  (newConfig) => {
        extendDialogConfig.show = false;
    }
})
// 扩展属性 的表格配置信息
const extendTableConfig = ref({
    columns: [
        { title: '', type: 'index' },
        { title: computed(() => t("标识")), key: 'name' },
        { title: computed(() => t("属性值")), key: 'value' },
        { title: computed(() => t("操作")), width: settingStore.getTwoBtnWidth,
            render: (row) => {
                return h('div',[
                    h('span',{
                        onClick: () => {
                            // 编辑函数 给extendForm赋值
                            extendAddConfig.show = true;
                            extendAddConfig.title = computed(() => t("编辑属性"));
                        },
                        style: {
                            marginRight: '20px'
                        }
                    },'编辑'),
                     h('span',{
                        onClick: () => {
                            ElMessageBox.confirm(
                                `是否删除【${row.name}】?`,
                                '提示', {
                                confirmButtonText: '确定',
                                cancelButtonText: '取消',
                                type: 'info',
                            }).then(async () => {
                                // 进行删除 操作 ---
                               ElNotification({
                                    title: '成功',
                                    message: '删除成功',
                                    type: 'success',
                                    duration: 2000,
                                    offset: 80
                                });
                                // 重新请求列表数据 --
                            }).catch(() => {
                                ElMessage({
                                    type: 'info',
                                    message: '已取消删除',
                                    offset: 65
                                });
                            });
                        }
                     },'删除')
                ])
            },
        }
    ],
    tableData: [{ name: '还好', value: 'ewh', id: 1 }, { name: '还好', value: 'dsfd', id: 2 }]
})
// 扩展属性 弹框 配置的赋值
function handlerExtendConfig(data: any) {
    extendDialogConfig = data.value;
}

// 扩展属性 表单ref
const extendFormRef = ref<FormInstance>()
// 扩展属性 增加表单
let extendForm = ref({});
// rule规则 添加扩展属性
const extendRules = reactive<FormRules>({
    name: [
        {
        required: true,
        message: '请输入属性名称',
        trigger: 'blur',
        },
    ],
    value: [
        { 
        required: true,
        message: '请输入属性值', 
        trigger: 'blur' 
        },
    ],
})
// 扩展属性 表单 的 新增 操作
function handlerExtendAdd() {
    extendAddConfig.show = true;
    extendAddConfig.title = computed(() => t("新增属性"));
}
// 扩展属性 的增加 表单变量配置
let extendAddConfig = reactive({
    show: false,
    title: computed(() => t("新增属性")),
    okText: computed(() => t("提交")),
    width: '30%',
    onOkLoading: true,
    onOk: (newConfig) => {
        return new Promise(async (resolve, reject) => {
            if (!extendFormRef.value) return
            await extendFormRef.value.validate((valid, fields) => {
                if (valid) {
                    // 通过验证
                    // 利用 title 判断是编辑 还是增加
                    let text = '';
                    if(extendAddConfig.title === computed(() => t("新增属性"))) {
                        // 新增操作 ---
                        text = '添加';
                    }else {
                        text = "更新";
                        // 编辑操作 ---
                    }
                    setTimeout(() => {
                        ElNotification({
                            title: '成功',
                            message: `${text}成功`,
                            type: 'success',
                            duration: 2000,
                            offset: 80
                        });
                        // 表单弹框 消失 表单数据清空 
                        extendAddConfig.show = false;
                        extendForm.value = {};
                        // 重新请求 数据列表
                        // ---
                        resolve();
                    }, 2000)
                } else {
                    reject();
                }
            })
        })
    },
    onCancel:  (newConfig) => {
        extendAddConfig.show = false;
    }
    
})
// 配置
function handlerAddConfig(data) {
    extendAddConfig = data.value;
}


</script>
<style scoped lang="scss">


</style>