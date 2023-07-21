<template>
  <el-dialog
    :title="dialogTitle"
    v-model="dialogVisible"
    width="600px"
    top="100px"
    destroy-on-close="true"
    append-to-body
  >
    <div style="margin-left: 0%">
      <el-tabs tab-position="left" style="height:600px;" class="demo-tabs">
        <el-tab-pane :label="$t('基本信息')">
          <div slot="header" class="clearfix" style="padding-bottom:10px;">
            <!-- <span style="text-align: center;">基本信息</span> -->
          </div>
          <el-container>
            <el-aside width="70%">
              <el-form ref="personInfoForm" :model="form" :rules="rules" label-width="150px">
                <el-form-item :label="$t('姓名')" prop="name" :size="fontSizeObj.buttonSize">
                  <el-input v-model="form.name" readonly disabled ></el-input>
                </el-form-item>
                <el-form-item :label="$t('登录名称')" prop="loginName" :size="fontSizeObj.buttonSize">
                  <el-input v-model="form.loginName" readonly disabled></el-input>
                </el-form-item>
                <el-form-item :label="$t('性别')" :size="fontSizeObj.buttonSize">
                  <el-radio-group v-model="sex" prop="sex" @change="setSexValue">
                    <el-radio label="1" value="1">{{ $t('男') }}</el-radio>
                    <el-radio label="0" value="0">{{ $t('女') }}</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item :label="$t('电子邮件')" prop="email" :size="fontSizeObj.buttonSize">
                  <el-input v-model="form.email"></el-input>
                </el-form-item>
                <el-form-item :label="$t('移动电话')" prop="mobile" :size="fontSizeObj.buttonSize">
                  <el-input v-model="form.mobile"></el-input>
                </el-form-item>
                <el-form-item :label="$t('上次修改密码时间')" prop="modifyPwdTime" :size="fontSizeObj.buttonSize">{{ form.modifyPwdTime }}</el-form-item>
                <el-form-item :label="$t('密码修改周期')" prop="pwdCycle" :size="fontSizeObj.buttonSize">{{ form.pwdCycle }}{{ $t('天') }}</el-form-item>
                <el-form-item
                  :label="$t('下次修改密码时间')"
                  :size="fontSizeObj.buttonSize"
                  prop="nextModifyPwdTime"
                >{{ otherProp.nextModifyPwdTime }}{{ $t('之前') }}</el-form-item>
                <template v-if="form.managerLevel == 2">
                  <el-form-item :label="$t('上次审查时间')" prop="checkTime" :size="fontSizeObj.buttonSize">{{ form.checkTime }}</el-form-item>
                  <el-form-item :label="$t('审查周期')" prop="checkCycle" :size="fontSizeObj.buttonSize">{{ form.checkCycle }}{{ $t('天') }}</el-form-item>
                  <el-form-item :label="$t('下次审查时间')" prop="nextCheckTime" :size="fontSizeObj.buttonSize">{{ otherProp.nextCheckTime }}{{ $t('之前') }}</el-form-item>
                </template>
              </el-form>
            </el-aside>
            <el-main>
              <div class="uploadImg"></div>
            </el-main>
          </el-container>
          <div class="submitForm">
            <el-button type="primary" @click="submitForm(personInfoForm)" :style="{ fontSize: fontSizeObj.baseFontSize }"
            :size="fontSizeObj.buttonSize">{{ $t('提交') }}</el-button>
            <el-button @click="dialogVisible = false" :style="{ fontSize: fontSizeObj.baseFontSize }"
            :size="fontSizeObj.buttonSize">{{ $t('取消') }}</el-button>
          </div>
        </el-tab-pane>
        <el-tab-pane :label="$t('修改密码')">
          <div slot="header" class="clearfix" style="padding-bottom:10px;">
            <!-- <span style="text-align: center;">修改密码</span> -->
          </div>
          <el-container>
            <el-aside width="90%">
              <el-form ref="pswForm" :model="passWordForm" :rules="rules" label-width="120px">
                <el-form-item
                  :label="$t('密码规范')"
                  :size="fontSizeObj.buttonSize"
                  style="margin-bottom: 26px;color:red;"
                >{{ $t('密码必须由【数字、大写字母、小写字母、特殊符】其中三种组成且长度在10到20之间。') }}</el-form-item>
                <el-form-item :label="$t('当前密码')" prop="agopassword" style="margin-bottom: 26px;" :size="fontSizeObj.buttonSize">
                  <el-input v-model="passWordForm.agopassword" show-password></el-input>
                </el-form-item>
                <el-form-item :label="$t('新密码')" prop="newpassword" style="margin-bottom: 26px;" :size="fontSizeObj.buttonSize">
                  <el-input
                    type="password"
                    v-model="passWordForm.newpassword"
                    autocomplete="off"
                    show-password
                  ></el-input>
                </el-form-item>
                <el-form-item :label="$t('确认密码')" prop="checknewpassword" :size="fontSizeObj.buttonSize" style="margin-bottom: 26px;">
                  <el-input
                    type="password"
                    v-model="passWordForm.checknewpassword"
                    autocomplete="off"
                    show-password
                  ></el-input>
                </el-form-item>
              </el-form>
            </el-aside>
          </el-container>
          <div class="submitForm">
            <el-button type="primary" @click="submitPassWordForm(pswForm)" :style="{ fontSize: fontSizeObj.baseFontSize }"
            :size="fontSizeObj.buttonSize">{{ $t('提交') }}</el-button>
            <el-button @click="resetPwd(pswForm)" :size="fontSizeObj.buttonSize"
            :style="{ fontSize: fontSizeObj.baseFontSize }">{{ $t('重置') }}</el-button>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </el-dialog>
</template>
<script lang="ts" setup>
import { useI18n } from "vue-i18n"
import { ref, defineProps, defineExpose, onMounted, reactive, watch, defineEmits, inject } from 'vue';
import type { ElMessage, FormInstance, ElLoading } from 'element-plus';
import { getManagerById, updateManager, checkPassword, modifyPassword } from '@/api/manager/index';
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo');

const { t } = useI18n();

const dialogTitle = ref('');
const dialogVisible = ref(false);
const img = ref('');
const imageUrl = ref('');
const baseimg = ref('');
const pic = reactive({
  picnote: ""
});

const personInfoForm = ref<FormInstance>();
const pswForm = ref<FormInstance>();
const file = ref([]);
const sex = ref(1);
const form = ref({
  id: "",
  parentId: "",
  tenantId: "",
  name: "",
  loginName: "",
  sex: "",
  email: "",
  mobile: "",
  pass1: "",
  managerLevel: 0
});
const otherProp = ref({
  nextModifyPwdTime: '',
  nextCheckTime: ''

});
const passWordForm = ref({
  agopassword: '',
  newpassword: '',
  checknewpassword: ''
});
const mobile = (rule, value, callback) => {
  if (value != '' && value != null) {
    let rex = /^1[3-9][0-9]{9}$/;
    let validator = rex.test(value);
    if (!validator) {
      return callback(new Error(t('请输入正确电话或手机格式')));
    } else {
      callback();
    }
  }
  callback();
};

var re = new RegExp("^(?![A-Za-z]+$)(?![A-Z\\d]+$)(?![A-Z\\W]+$)(?![a-z\\d]+$)(?![a-z\\W]+$)(?![\\d\\W]+$)\\S{10,20}$");
var msg = "密码必须由【数字、大写字母、小写字母、特殊符】至少其中三种组成且长度至少10且不超过20。";
var checkagopassword = (rule, value, callback) => {
  if (!value) {
    return callback(new Error(t('当前密码不能为空')));
  }
  callback();
};
var validatePass = (rule, value, callback) => {
  if (!value) {
    return callback(new Error(t('请先输入新密码')));
  } else {
    if (!passWordForm.value.checknewpassword) {
      pswForm.value.validateField('checknewpassword');
    }
    if (!re.test(value)) {
      return callback(new Error(t(`${msg}`)));
    }
    callback();
  }
};

var validatePass2 = (rule, value, callback) => {
  if (!value) {
    callback(new Error(t('请输入确认的密码!')));
  } else if (value != passWordForm.value.newpassword) {
    callback(new Error(t('两次输入密码不一致!')));
  } else {
    callback();
  }
};

const rules = {
  //email: [{ validator: checkEmail, trigger: 'blur' }],
  mobile: [{ validator: mobile, trigger: 'blur' }],
  agopassword: [{ required: true, validator: checkagopassword, trigger: 'blur' }],
  newpassword: [{ required: true, validator: validatePass, trigger: 'blur' }],
  checknewpassword: [{ required: true, validator: validatePass2, trigger: 'blur' }],
};

defineExpose({ show });
async function show(id) {
  dialogVisible.value = true;
  dialogTitle.value = '个人中心';
  otherProp.value.nextModifyPwdTime = '';
  otherProp.value.nextCheckTime = '';
  getMannager(id);
}

//获取个人信息
async function getMannager(id) {
  await getManagerById(id).then((res) => {
    if (res.success) {
      form.value = res.data.manager;
      sex.value = form.value.sex + '';
      otherProp.value.nextModifyPwdTime = res.data.nextModifyPwdTime;
      otherProp.value.nextCheckTime = res.data.nextCheckTime;
    }
  });
}

const setSexValue = (val) => {
  form.value.sex = val;
}

const resetPwd = (pwd) => {
  pwd.resetFields();
}
//提交表单
const submitForm = (formEl) => {
  if (!formEl) return;
  formEl.validate(valid => {
    if (valid) {
      const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
      updateManager(form.value).then(res => {
        loading.close();
        if (res.success) {
          ElMessage({ message: "保存成功！", type: "success", offset: 65 });
        } else {
          ElMessage({ type: 'error', message: '保存失败！', offset: 65 });
        }
      }).catch(() => {
        loading.close();
      });
    } else {
      ElMessage({ message: '验证失败，请重新确认表单信息!', type: 'error', offset: 65 });
      return false;
    }
  });
}

const submitPassWordForm = (formEl) => {
  if (!formEl) return;
  formEl.validate(valid => {
    if (valid) {
      const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
      checkPassword(form.value.id, passWordForm.value.agopassword).then(res => {
        loading.close();
        if (!res.data) {
          ElMessage({ message: "当前密码与原密码不一致！", type: "error", offset: 65 });
        } else {
          const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
          modifyPassword(form.value.id, passWordForm.value.checknewpassword).then(res => {
            loading.close();
            if (res.success) {
              ElMessage({ message: "修改成功！", type: "success", offset: 65 });
            } else {
              ElMessage({ type: 'error', message: '修改失败！', offset: 65 });
            }
          }).catch(() => {
            loading.close();
          });
        }
      }).catch(() => {
        loading.close();
      });

    } else {
      ElMessage({ message: '验证失败，请重新确认表单信息!', type: 'error', offset: 65 });
      return false;
    }
  });
}
</script>
<style>
.el-dialog__body {
  padding-top: 0px;
}
.box-card {
  width: 100%;
}
.clearfix {
  text-align: center;
  font-size: v-bind('fontSizeObj.baseFontSize');
  font-weight: 600;
}
.uploadImg {
  padding-left: 15px;
}
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: v-bind('fontSizeObj.baseFontSize');
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
.submitForm {
  text-align: center;
}
</style>
