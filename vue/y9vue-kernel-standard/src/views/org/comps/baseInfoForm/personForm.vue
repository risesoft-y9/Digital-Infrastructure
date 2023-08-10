<template>
    <table
        v-if="settingStore.device === 'pc'"
        border="0"
        cellpadding="0"
        cellspacing="1"
        class="layui-table"
        lay-skin="line row"
    >
        <tbody>
            <tr style="height: 40px">
                <td colspan="6" align="center">{{ $t('基本信息') }}</td>
            </tr>
            <tr>
                <td class="lefttd">{{ $t('人员名称') }}<font color="red" style="margin-left: 4px">*</font></td>
                <td class="rigthtd" style="width: 25%">
                    <el-input
                        ref="nameRef"
                        v-if="isEditState"
                        :size="fontSizeObj.buttonSize"
                        v-model="personForm.name"
                        v-elInputCheck="{ message: '请输入人员名称' }"
                    />
                    <span v-else>{{ currInfo.name }}</span>
                </td>

                <td class="lefttd">{{ $t('唯一标识') }}</td>
                <td class="rigthtd">
                    <span>{{ currInfo.id }}</span>
                </td>
                <td colspan="2" rowspan="7" style="text-align: center">
                    <el-upload
                        v-if="isEditState"
                        class="avatar-uploader"
                        :show-file-list="false"
                        :action="actionUrl"
                        :on-success="handleAvatarSuccess"
                        :before-upload="beforeAvatarUpload"
                        name="iconFile"
                    >
                        <div class="upload-image">
                            <el-avatar :icon="UserFilled" :src="imageUrl" />
                        </div>
                        <!-- <img :src="imageUrl" class="avatar" /> -->
                    </el-upload>
                    <!-- <img v-else :src="imageUrl" class="avatar upload-image" /> -->
                    <div v-else class="upload-image">
                        <el-avatar :icon="UserFilled" :src="imageUrl" />
                    </div>
                </td>
            </tr>
            <tr>
                <td class="lefttd">{{ $t('登录名称') }}<font color="red" style="margin-left: 4px">*</font> </td>
                <td class="rigthtd">
                    <el-input
                        ref="loginNameRef"
                        v-if="isEditState"
                        :size="fontSizeObj.buttonSize"
                        v-model="personForm.loginName"
                        v-elInputCheck="{ message: '请输入登录名称' }"
                    />
                    <span v-else>{{ currInfo.loginName }}</span>
                </td>
                <td class="lefttd">{{ $t('自定义ID') }}</td>
                <td class="rigthtd">
                    <span>{{ currInfo.customId }}</span>
                </td>
            </tr>
            <tr>
                <td class="lefttd">{{ $t('CA认证码') }}</td>
                <td class="rigthtd">
                    <el-input v-if="isEditState" v-model="personForm.caid" :size="fontSizeObj.buttonSize" />
                    <span v-else>{{ currInfo.caid }}</span>
                </td>
                <td class="lefttd">{{ $t('性别') }}</td>
                <td class="rigthtd">
                    <el-select
                        v-if="isEditState"
                        v-model="personForm.sex"
                        :placeholder="$t('请选择')"
                        :size="fontSizeObj.buttonSize"
                    >
                        <el-option
                            v-for="(item, index) in $dictionary().sex"
                            :key="index"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            :label="$t(item.name)"
                            :value="item.id"
                        />
                    </el-select>
                    <span v-else>{{ currInfo.sex == 1 ? $t('男') : $t('女') }}</span>
                </td>
            </tr>
            <tr>
                <td class="lefttd">{{ $t('人员职级') }}</td>
                <td class="rigthtd">
                    <el-select
                        v-if="isEditState"
                        v-model="personForm.dutyLevelName"
                        :placeholder="$t('请选择')"
                        :size="fontSizeObj.buttonSize"
                        clearable
                    >
                        <el-option
                            v-for="(item, index) in $dictionary().dutyLevel"
                            :key="index"
                            :label="$t(item.name)"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            :value="item.id"
                        />
                    </el-select>
                    <span v-else>{{ currInfo.dutyLevelName }}</span>
                </td>
                <td class="lefttd">{{ $t('人员职务') }}</td>
                <td class="rigthtd">
                    <el-select
                        v-if="isEditState"
                        v-model="personForm.duty"
                        :placeholder="$t('请选择')"
                        :size="fontSizeObj.buttonSize"
                        clearable
                    >
                        <el-option
                            v-for="(item, index) in $dictionary().duty"
                            :key="index"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            :label="$t(item.name)"
                            :value="item.id"
                        />
                    </el-select>
                    <span v-else>{{ currInfo.duty }}</span>
                </td>
            </tr>

            <tr>
                <td class="lefttd">{{ $t('是否在编') }}</td>
                <td class="rigthtd">
                    <el-select
                        v-if="isEditState"
                        v-model="personForm.official"
                        :placeholder="$t('请选择')"
                        :size="fontSizeObj.buttonSize"
                    >
                        <el-option
                            v-for="(item, index) in $dictionary().booleanNum"
                            :key="index"
                            :label="$t(item.name)"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            :value="item.id"
                        />
                    </el-select>
                    <span v-else>{{ currInfo.official == 1 ? $t('是') : $t('否') }}</span>
                </td>
                <td class="lefttd">{{ $t('编制类型') }}</td>
                <td class="rigthtd">
                    <el-select
                        v-if="isEditState"
                        v-model="personForm.officialType"
                        :placeholder="$t('请选择')"
                        :size="fontSizeObj.buttonSize"
                        clearable
                    >
                        <el-option
                            v-for="(item, index) in $dictionary().officialType"
                            :key="index"
                            :label="$t(item.name)"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            :value="item.id"
                        />
                    </el-select>
                    <span v-else>{{ currInfo.officialType }}</span>
                </td>
            </tr>
            <tr>
                <td class="lefttd">{{ $t('办公室') }}</td>
                <td class="rigthtd">
                    <el-input v-if="isEditState" v-model="personForm.officeAddress" :size="fontSizeObj.buttonSize" />
                    <span v-else>{{ currInfo.officeAddress }}</span>
                </td>
                <td class="lefttd">{{ $t('办公电话') }}</td>
                <td class="rigthtd">
                    <el-input v-if="isEditState" v-model="personForm.officePhone" :size="fontSizeObj.buttonSize" />
                    <span v-else>{{ currInfo.officePhone }}</span>
                </td>
            </tr>
            <tr>
                <td class="lefttd">{{ $t('办公传真') }}</td>
                <td class="rigthtd">
                    <el-input v-if="isEditState" v-model="personForm.officeFax" :size="fontSizeObj.buttonSize" />
                    <span v-else>{{ currInfo.officeFax }}</span>
                </td>
                <td class="lefttd">{{ $t('电子邮件') }}</td>
                <td class="rigthtd">
                    <el-input
                        v-if="isEditState"
                        v-model="personForm.email"
                        :size="fontSizeObj.buttonSize"
                        v-elInputCheck="{ required: false, regularType: 'email', regularValue: personForm.email }"
                    />
                    <span v-else>{{ currInfo.email }}</span>
                </td>
            </tr>

            <tr>
                <td class="lefttd">{{ $t('是否禁用') }}</td>
                <td class="rigthtd">
                    <el-select
                        v-if="isEditState"
                        v-model="personForm.disabled"
                        :placeholder="$t('请选择')"
                        :size="fontSizeObj.buttonSize"
                    >
                        <el-option
                            v-for="(item, index) in $dictionary().boolean"
                            :key="index"
                            :label="$t(item.name)"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            :value="item.id"
                        />
                    </el-select>
                    <span v-else>{{ currInfo.disabled ? $t('是') : $t('否') }}</span>
                </td>
                <td class="lefttd">{{ $t('移动电话') }}<font color="red" style="margin-left: 4px">*</font> </td>
                <td class="rigthtd">
                    <el-input
                        v-if="isEditState"
                        ref="mobileRef"
                        :size="fontSizeObj.buttonSize"
                        v-model="personForm.mobile"
                        v-elInputCheck="{
                            message: '请输入移动电话',
                            regularType: 'phone',
                            regularValue: personForm.mobile,
                        }"
                    />
                    <span v-else>{{ currInfo.mobile }}</span>
                </td>
                <td colspan="2" style="text-align: center; background-color: #f5f7fa; width: 36%">
                    {{ $t('小于2M的2寸蓝底照片') }}</td
                >
            </tr>
            <tr style="height: 40px">
                <td colspan="6" align="center">{{ $t('个人信息') }}</td>
            </tr>
            <tr>
                <td class="lefttd">{{ $t('婚姻状况') }}</td>
                <td class="rigthtd">
                    <el-select
                        v-if="isEditState"
                        v-model="personForm.maritalStatus"
                        :placeholder="$t('请选择')"
                        clearable
                        :size="fontSizeObj.buttonSize"
                    >
                        <el-option
                            v-for="(item, index) in $dictionary().maritalStatus"
                            :label="$t(item.name)"
                            :key="index"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            :value="item.id"
                        />
                    </el-select>
                    <span v-else>{{ currInfo.maritalStatus }}</span>
                </td>
                <td class="lefttd">{{ $t('专业') }}</td>
                <td class="rigthtd">
                    <el-input v-if="isEditState" v-model="personForm.professional" :size="fontSizeObj.buttonSize" />
                    <span v-else>{{ currInfo.professional }}</span>
                </td>
                <td class="lefttd">{{ $t('居住城市') }}</td>
                <td class="rigthtd">
                    <el-input v-if="isEditState" v-model="personForm.city" :size="fontSizeObj.buttonSize" />
                    <span v-else>{{ currInfo.city }}</span>
                </td>
            </tr>
            <tr>
                <td class="lefttd">{{ $t('居住国家') }}</td>
                <td class="rigthtd">
                    <el-input v-if="isEditState" v-model="personForm.country" :size="fontSizeObj.buttonSize" />
                    <span v-else>{{ currInfo.country }}</span>
                </td>
                <td class="lefttd">{{ $t('家庭电话') }}</td>
                <td class="rigthtd">
                    <el-input v-if="isEditState" v-model="personForm.homePhone" :size="fontSizeObj.buttonSize" />
                    <span v-else>{{ currInfo.homePhone }}</span>
                </td>
                <td class="lefttd">{{ $t('证件类型') }}</td>
                <td class="rigthtd">
                    <el-select
                        v-if="isEditState"
                        v-model="personForm.idType"
                        :placeholder="$t('请选择')"
                        clearable
                        :size="fontSizeObj.buttonSize"
                    >
                        <el-option
                            v-for="(item, index) in $dictionary().principalIDType"
                            :label="$t(item.name)"
                            :key="index"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            :value="item.id"
                        />
                    </el-select>
                    <span v-else>{{ currInfo.idType }}</span>
                </td>
            </tr>
            <tr>
                <td class="lefttd">{{ $t('证件号码') }}</td>
                <td class="rigthtd">
                    <el-input
                        v-if="isEditState"
                        v-model="personForm.idNum"
                        :size="fontSizeObj.buttonSize"
                        v-elInputCheck="{ required: false, regularType: 'idCard', regularValue: personForm.idNum }"
                    />
                    <span v-else>{{ currInfo.idNum }}</span>
                </td>

                <td class="lefttd">{{ $t('政治面貌') }}</td>
                <td class="rigthtd">
                    <el-select
                        v-if="isEditState"
                        v-model="personForm.politicalStatus"
                        :placeholder="$t('请选择')"
                        :size="fontSizeObj.buttonSize"
                        clearable
                    >
                        <el-option
                            v-for="(item, index) in $dictionary().politicalStatus"
                            :label="$t(item.name)"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            :key="index"
                            :value="item.id"
                        />
                    </el-select>
                    <span v-else>{{ currInfo.politicalStatus }}</span>
                </td>
                <td class="lefttd">{{ $t('入职日期') }}</td>
                <td class="rigthtd">
                    <el-date-picker
                        v-if="isEditState"
                        v-model="personForm.workTime"
                        format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD"
                        type="date"
                        :placeholder="$t('请选择')"
                        style="width: 100%"
                    >
                    </el-date-picker>
                    <span v-else>{{ currInfo.workTime }}</span>
                </td>
            </tr>
            <tr>
                <td class="lefttd">{{ $t('学历') }}</td>
                <td class="rigthtd">
                    <el-select
                        v-if="isEditState"
                        v-model="personForm.education"
                        :placeholder="$t('请选择')"
                        clearable
                        :size="fontSizeObj.buttonSize"
                    >
                        <el-option
                            v-for="(item, index) in $dictionary().education"
                            :label="$t(item.name)"
                            :key="index"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            :value="item.id"
                        />
                    </el-select>
                    <span v-else>{{ currInfo.education }}</span>
                </td>
                <td class="lefttd">{{ $t('人员籍贯') }}</td>
                <td class="rigthtd">
                    <el-input v-if="isEditState" v-model="personForm.province" :size="fontSizeObj.buttonSize" />
                    <span v-else>{{ currInfo.province }}</span>
                </td>
                <td class="lefttd">{{ $t('出生日期') }}</td>
                <td class="rigthtd">
                    <el-date-picker
                        v-if="isEditState"
                        v-model="personForm.birthday"
                        type="date"
                        format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD"
                        :placeholder="$t('请选择')"
                        style="width: 100%"
                    >
                    </el-date-picker>
                    <span v-else>{{ currInfo.birthday }}</span>
                </td>
            </tr>
            <tr>
                <td class="lefttd">{{ $t('家庭住址') }}</td>
                <td class="rigthtd" colspan="5">
                    <el-input v-if="isEditState" v-model="personForm.homeAddress" :size="fontSizeObj.buttonSize" />
                    <span v-else>{{ currInfo.homeAddress }}</span>
                </td>
            </tr>
            <tr>
                <td class="lefttd">{{ $t('人员描述') }}</td>
                <td class="rigthtd" colspan="5">
                    <el-input
                        v-if="isEditState"
                        v-model="personForm.description"
                        type="textarea"
                        :rows="3"
                        :size="fontSizeObj.buttonSize"
                    />
                    <span v-else>{{ currInfo.description }}</span>
                </td>
            </tr>
        </tbody>
    </table>

    <template v-if="settingStore.device === 'mobile'">
        <el-descriptions column="1" border :title="$t('基本信息')">
            <el-descriptions-item>
                <template #label>
                    <span>{{ $t('人员名称') }}</span>
                    <span v-if="isEditState" class="custom-validator-symbol">*</span>
                </template>
                <el-input
                    ref="nameRef"
                    v-if="isEditState"
                    v-model="personForm.name"
                    :size="fontSizeObj.buttonSize"
                    v-elInputCheck="{ message: '请输入人员名称' }"
                />
                <span v-else>{{ currInfo.name }}</span>
            </el-descriptions-item>
            <el-descriptions-item>
                <template #label>
                    <span>{{ $t('登录名称') }}</span>
                    <span v-if="isEditState" class="custom-validator-symbol">*</span>
                </template>
                <el-input
                    ref="loginNameRef"
                    v-if="isEditState"
                    :size="fontSizeObj.buttonSize"
                    v-model="personForm.loginName"
                    v-elInputCheck="{ message: '请输入登录名称' }"
                />
                <span v-else>{{ currInfo.loginName }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('唯一标识')" span="2">
                <span>{{ currInfo.id }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('自定义ID')">
                <span>{{ currInfo.customId }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('CA认证码')">
                <el-input v-if="isEditState" v-model="personForm.caid" />
                <span v-else>{{ currInfo.caid }}</span>
            </el-descriptions-item>

            <el-descriptions-item :label="$t('个人照片')" span="2" style="text-align: center">
                <el-upload
                    v-if="isEditState"
                    class="avatar-uploader"
                    :show-file-list="false"
                    :action="actionUrl"
                    :on-success="handleAvatarSuccess"
                    :before-upload="beforeAvatarUpload"
                    name="iconFile"
                >
                    <!-- <img :src="imageUrl" class="avatar" /> -->
                    <div class="upload-image">
                        <el-avatar :icon="UserFilled" :src="imageUrl" />
                    </div>
                </el-upload>
                <div class="upload-image" v-else>
                    <el-avatar :icon="UserFilled" :src="imageUrl" />
                </div>
                <!-- <img  :src="imageUrl" class="avatar upload-image" /> -->
                <p style="text-align: center; color: #999">{{ $t('小于2M的2寸蓝底照片') }}</p>
            </el-descriptions-item>

            <el-descriptions-item :label="$t('性别')">
                <el-select
                    v-if="isEditState"
                    v-model="personForm.sex"
                    :placeholder="$t('请选择')"
                    :size="fontSizeObj.buttonSize"
                >
                    <el-option
                        v-for="(item, index) in $dictionary().sex"
                        :key="index"
                        :label="$t(item.name)"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        :value="item.id"
                    />
                </el-select>
                <span v-else>{{ currInfo.sex == 1 ? $t('男') : $t('女') }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('人员职级')">
                <el-select
                    v-if="isEditState"
                    v-model="personForm.dutyLevelName"
                    :placeholder="$t('请选择')"
                    clearable
                    :size="fontSizeObj.buttonSize"
                >
                    <el-option
                        v-for="(item, index) in $dictionary().dutyLevel"
                        :key="index"
                        :label="item.name"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        :value="item.id"
                    />
                </el-select>
                <span v-else>{{ currInfo.dutyLevelName }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('人员职务')">
                <el-select
                    v-if="isEditState"
                    v-model="personForm.duty"
                    :placeholder="$t('请选择')"
                    clearable
                    :size="fontSizeObj.buttonSize"
                >
                    <el-option
                        v-for="(item, index) in $dictionary().duty"
                        :key="index"
                        :label="$t(item.name)"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        :value="item.id"
                    />
                </el-select>
                <span v-else>{{ currInfo.duty }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('是否在编')">
                <el-select
                    v-if="isEditState"
                    v-model="personForm.official"
                    :placeholder="$t('请选择')"
                    :size="fontSizeObj.buttonSize"
                >
                    <el-option
                        v-for="(item, index) in $dictionary().booleanNum"
                        :key="index"
                        :label="$t(item.name)"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        :value="item.id"
                    />
                </el-select>
                <span v-else>{{ currInfo.official == 1 ? $t('是') : $t('否') }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('编制类型')">
                <el-select
                    v-if="isEditState"
                    v-model="personForm.officialType"
                    :placeholder="$t('请选择')"
                    clearable
                    :size="fontSizeObj.buttonSize"
                >
                    <el-option
                        v-for="(item, index) in $dictionary().officialType"
                        :key="index"
                        :label="$t(item.name)"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        :value="item.id"
                    />
                </el-select>
                <span v-else>{{ currInfo.officialType }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('办公室')">
                <el-input v-if="isEditState" v-model="personForm.officeAddress" :size="fontSizeObj.buttonSize" />
                <span v-else>{{ currInfo.officeAddress }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('办公电话')">
                <el-input v-if="isEditState" v-model="personForm.officePhone" :size="fontSizeObj.buttonSize" />
                <span v-else>{{ currInfo.officePhone }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('办公传真')">
                <el-input v-if="isEditState" v-model="personForm.officeFax" :size="fontSizeObj.buttonSize" />
                <span v-else>{{ currInfo.officeFax }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('电子邮件')">
                <el-input
                    v-if="isEditState"
                    v-model="personForm.email"
                    :size="fontSizeObj.buttonSize"
                    v-elInputCheck="{ required: false, regularType: 'email', regularValue: personForm.email }"
                />
                <span v-else>{{ currInfo.email }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('是否禁用')">
                <el-select
                    v-if="isEditState"
                    v-model="personForm.disabled"
                    :placeholder="$t('请选择')"
                    :size="fontSizeObj.buttonSize"
                >
                    <el-option
                        v-for="(item, index) in $dictionary().boolean"
                        :key="index"
                        :label="$t(item.name)"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        :value="item.id"
                    />
                </el-select>
                <span v-else>{{ currInfo.disabled ? $t('是') : $t('否') }}</span>
            </el-descriptions-item>
            <el-descriptions-item>
                <template #label>
                    <span>{{ $t('移动电话') }}</span>
                    <span v-if="isEditState" class="custom-validator-symbol">*</span>
                </template>
                <el-input
                    v-if="isEditState"
                    ref="mobileRef"
                    v-model="personForm.mobile"
                    :size="fontSizeObj.buttonSize"
                    v-elInputCheck="{
                        message: '请输入移动电话',
                        regularType: 'phone',
                        regularValue: personForm.mobile,
                    }"
                />
                <span v-else>{{ currInfo.mobile }}</span>
            </el-descriptions-item>
        </el-descriptions>

        <el-descriptions column="1" border :title="$t('个人信息')" style="margin-top: 20px">
            <el-descriptions-item :label="$t('婚姻状况')">
                <el-select
                    v-if="isEditState"
                    v-model="personForm.maritalStatus"
                    :placeholder="$t('请选择')"
                    clearable
                    :size="fontSizeObj.buttonSize"
                >
                    <el-option
                        v-for="(item, index) in $dictionary().maritalStatus"
                        :label="$t(item.name)"
                        :key="index"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        :value="item.id"
                    />
                </el-select>
                <span v-else>{{ currInfo.maritalStatus }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('专业')">
                <el-input v-if="isEditState" v-model="personForm.professional" :size="fontSizeObj.buttonSize" />
                <span v-else>{{ currInfo.professional }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('居住城市')">
                <el-input v-if="isEditState" v-model="personForm.city" :size="fontSizeObj.buttonSize" />
                <span v-else>{{ currInfo.city }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('居住国家')">
                <el-input v-if="isEditState" v-model="personForm.country" :size="fontSizeObj.buttonSize" />
                <span v-else>{{ currInfo.country }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('家庭电话')">
                <el-input v-if="isEditState" v-model="personForm.homePhone" :size="fontSizeObj.buttonSize" />
                <span v-else>{{ currInfo.homePhone }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('证件类型')">
                <el-select
                    v-if="isEditState"
                    v-model="personForm.idType"
                    :placeholder="$t('请选择')"
                    clearable
                    :size="fontSizeObj.buttonSize"
                >
                    <el-option
                        v-for="(item, index) in $dictionary().principalIDType"
                        :label="$t(item.name)"
                        :key="index"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        :value="item.id"
                    />
                </el-select>
                <span v-else>{{ currInfo.idType }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('证件号码')">
                <el-input
                    v-if="isEditState"
                    v-model="personForm.idNum"
                    :size="fontSizeObj.buttonSize"
                    v-elInputCheck="{ required: false, regularType: 'idCard', regularValue: personForm.idNum }"
                />
                <span v-else>{{ currInfo.idNum }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('政治面貌')">
                <el-select
                    v-if="isEditState"
                    :size="fontSizeObj.buttonSize"
                    v-model="personForm.politicalStatus"
                    :placeholder="$t('请选择')"
                    clearable
                >
                    <el-option
                        v-for="(item, index) in $dictionary().politicalStatus"
                        :label="$t(item.name)"
                        :key="index"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        :value="item.id"
                    />
                </el-select>
                <span v-else>{{ currInfo.politicalStatus }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('入职日期')">
                <el-date-picker
                    v-if="isEditState"
                    v-model="personForm.workTime"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                    type="date"
                    :placeholder="$t('请选择')"
                    style="width: 100%"
                >
                </el-date-picker>
                <span v-else>{{ currInfo.workTime }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('学历')">
                <el-select
                    v-if="isEditState"
                    v-model="personForm.education"
                    :placeholder="$t('请选择')"
                    clearable
                    :size="fontSizeObj.buttonSize"
                >
                    <el-option
                        v-for="(item, index) in $dictionary().education"
                        :key="index"
                        :label="$t(item.name)"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        :value="item.id"
                    />
                </el-select>
                <span v-else>{{ currInfo.education }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('人员籍贯')">
                <el-input v-if="isEditState" v-model="personForm.province" :size="fontSizeObj.buttonSize" />
                <span v-else>{{ currInfo.province }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('出生日期')">
                <el-date-picker
                    v-if="isEditState"
                    v-model="personForm.birthday"
                    type="date"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                    :placeholder="$t('请选择')"
                    style="width: 100%"
                >
                </el-date-picker>
                <span v-else>{{ currInfo.birthday }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('家庭住址')">
                <el-input v-if="isEditState" v-model="personForm.homeAddress" :size="fontSizeObj.buttonSize" />
                <span v-else>{{ currInfo.homeAddress }}</span>
            </el-descriptions-item>
            <el-descriptions-item :label="$t('人员描述')">
                <el-input
                    v-if="isEditState"
                    v-model="personForm.description"
                    type="textarea"
                    :rows="3"
                    :size="fontSizeObj.buttonSize"
                />
                <span v-else>{{ currInfo.description }}</span>
            </el-descriptions-item>
        </el-descriptions>
    </template>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { inject, watch, reactive, toRefs } from 'vue';
    import { ElNotification } from 'element-plus';
    import { $keyNameAssign } from '@/utils/object';
    import { $dictionary } from '@/utils/data';
    import { loginNameCheck, getPersonExtById, getPersonExtByIdWithEncry } from '@/api/person/index';
    import moment from 'moment';
    import y9_storage from '@/utils/storage';
    import settings from '@/settings';
    import type { UploadProps } from 'element-plus';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { UserFilled } from '@element-plus/icons-vue';
    const { t } = useI18n();
    const settingStore = useSettingStore();
    // 注入 字体变量
    const fontSizeObj: any = inject('sizeObjInfo');
    const props = defineProps({
        isAdd: {
            //是否为添加模式，添加模式有些字段不需要显示
            type: Boolean,
            default: false,
        },
        isEditState: {
            //是否为编辑状态
            type: Boolean,
        },
        currInfo: {
            //当前信息
            type: Object,
            default: () => {
                return {};
            },
        },
    });

    const data = reactive({
        personForm: {
            //新增或编辑人员表单
            id: '',
            parentId: props.currInfo.id,
            name: '', //人员名称
            loginName: '', //登录名称
            wxId: '', //微信唯一标识
            sex: t('男'), //性别,
            workTime: '', //入职日期
            dutyLevelName: '', //人员职级
            duty: '', //人员职务
            // userType:"",//人员类型
            official: t('是'), //是否在编
            officialType: '', //编制类型
            officeAddress: '', //办公室
            officePhone: '', //办公电话
            officeFax: '', //办公传真
            politicalStatus: '', //政治面貌
            email: '', //电子邮件
            caid: '', //认证码

            birthday: '', //出生日期
            maritalStatus: '', //婚姻状况
            professional: '', //专业
            city: '', //居住城市
            country: '', //居住国家
            homePhone: '', //家庭电话
            idType: '', //证件类型
            idNum: '', //证件号码
            mobile: '', //移动电话
            disabled: '', //是否禁用
            education: '', //学历
            province: '', //人员籍贯
            homeAddress: '', //家庭住址
            description: '', //人员描述
            tabIndex: null, // 排序
        },
        actionUrl: '', //头像上传路径
        imageUrl: '', //头像路径
        nameRef: '',
        loginNameRef: '',
        mobileRef: '',
    });

    let { personForm, actionUrl, imageUrl, nameRef, loginNameRef, mobileRef } = toRefs(data);

    watch(
        () => props.isEditState,
        (newVal) => {
            if (newVal) {
                //编辑状态给表单赋值
                $keyNameAssign(personForm.value, props.currInfo);
            }
            getPersonExt();
        }
    );

    watch(
        () => props.currInfo,
        () => {
            getPersonExt();
            actionUrl.value =
                import.meta.env.VUE_APP_CONTEXT +
                'api/rest/person/savePersonPhoto?personId=' +
                props.currInfo.id +
                '&access_token=' +
                y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
            imageUrl.value =
                import.meta.env.VUE_APP_CONTEXT +
                'api/rest/person/showPersonPhoto?personId=' +
                props.currInfo.id +
                '&access_token=' +
                y9_storage.getObjectItem(settings.siteTokenKey, 'access_token') +
                '&time=' +
                Math.random();
        },
        {
            immediate: true,
        }
    );

    const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
        if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png' && rawFile.type !== 'image/jpg') {
            ElNotification({
                title: '失败',
                message: '请选择jpeg,png,jpg格式的图片!',
                type: 'error',
                duration: 2000,
                offset: 80,
            });
            return false;
        } else if (rawFile.size / 1024 / 1024 > 2) {
            ElNotification({
                title: '失败',
                message: '上传图片大小不能超过 2MB!',
                type: 'error',
                duration: 2000,
                offset: 80,
            });
            return false;
        }
        return true;
    };

    const handleAvatarSuccess: UploadProps['onSuccess'] = (response, uploadFile) => {
        imageUrl.value = URL.createObjectURL(uploadFile.raw!);
    };

    async function getPersonExt() {
        let result = { success: false, data: [] };
        if (props.isEditState) {
            result = await getPersonExtById(props.currInfo.id);
        } else {
            result = await getPersonExtByIdWithEncry(props.currInfo.id);
        }
        if (result.success) {
            for (const key in result.data) {
                props.currInfo[key] = result.data[key];
                if (key == 'birthday' || key == 'workTime') {
                    if (result.data[key] != null && result.data[key] != '') {
                        props.currInfo[key] = moment(result.data[key]).format('YYYY-MM-DD');
                        result.data[key] = moment(result.data[key]).format('YYYY-MM-DD');
                    }
                }
                if (key == 'idType') {
                    props.currInfo[key] = result.data['idType'];
                }
                if (key == 'maritalStatus') {
                    if (result.data[key] == 0) {
                        result.data[key] = '保密';
                        props.currInfo[key] = '保密';
                    } else if (result.data[key] == 1) {
                        result.data[key] = '已婚';
                        props.currInfo[key] = '已婚';
                    } else if (result.data[key] == 2) {
                        result.data[key] = '未婚';
                        props.currInfo[key] = '未婚';
                    }
                }
            }
            if (result.data) {
                $keyNameAssign(personForm.value, result.data);
            }
        }
    }

    defineExpose({
        personForm,
        getPersonExt,
        validForm,
    });

    async function validForm() {
        if (!personForm.value.name) {
            nameRef.value.focus();
            setTimeout(() => {
                nameRef.value.blur();
            }, 0);

            return false;
        } else if (!personForm.value.loginName) {
            loginNameRef.value.focus();
            setTimeout(() => {
                loginNameRef.value.blur();
            }, 0);

            return false;
        } else if (!personForm.value.mobile) {
            mobileRef.value.focus();
            setTimeout(() => {
                mobileRef.value.blur();
            }, 0);

            return false;
        } else if (personForm.value.mobile.length > 0) {
            mobileRef.value.focus();
            setTimeout(() => {
                mobileRef.value.blur();
            }, 0);

            if (!mobileRef.value.ref?.elInputCheckResult) {
                return false;
            }
        }

        if (personForm.value.loginName.length > 0) {
            let result = await loginNameCheck(personForm.value.id, personForm.value.loginName);

            if (!result.data) {
                ElNotification({
                    title: '失败',
                    message: '该登录名称已存在，请重新输入登录名称',
                    type: 'error',
                    duration: 2000,
                    offset: 80,
                });
                return false;
            }
        }

        return true;
    }
</script>

<style lang="scss" scoped>
    .layui-table {
        width: 100%;
        border-collapse: collapse;
        border-spacing: 0;

        td {
            position: revert;
            padding: 5px 10px;
            // min-height: 32px;
            line-height: v-bind('fontSizeObj.lineHeight');
            font-size: v-bind('fontSizeObj.baseFontSize');
            border-width: 1px;
            border-style: solid;
            border-color: #e6e6e6;
            display: table-cell;
            vertical-align: inherit;
            color: var(--el-text-color-primary);
        }

        .lefttd {
            background: var(--el-fill-color-light);
            text-align: center;
            color: var(--el-text-color-regular);
            width: 14%;
        }

        .rigthtd {
            div {
                font-size: v-bind('fontSizeObj.baseFontSize');
            }
            :deep(.el-input) {
                font-size: v-bind('fontSizeObj.baseFontSize');
            }
        }
    }

    .upload-image {
        width: 150px;
        height: 200px;
        margin: auto;
        border: 1px solid #ebeef5;
        :deep(.el-avatar) {
            width: 100%;
            height: 100%;
            i {
                font-size: v-bind('fontSizeObj.maximumFontSize');
            }
        }
        :deep(.el-avatar--circle) {
            border-radius: 0;
        }
    }
    :deep(.el-descriptions__label) {
        font-weight: normal !important;
    }
    :deep(.el-descriptions__cell) {
        font-weight: normal !important;
    }
</style>

<style lang="scss">
    .avatar-uploader {
        width: 150px;
        height: 200px;
        margin: auto;
        border: 1px solid #ebeef5;
        & > div {
            width: 100%;
            height: 100%;
            & > img {
                position: relative;
                width: 100%;
                height: 100%;
            }
        }
    }
</style>
