<template>
    <div class="person-form-div">
        <div class="person-form-title-base">{{ $t('基本信息') }}</div>
        <y9Form ref="y9FormBaseRef" :config="y9FormBaseConfig"></y9Form>
        <div class="person-form-title-person">{{ $t('个人信息') }}</div>
        <y9Form ref="y9FormPersonRef" :config="y9FormPersonConfig">
            <template #avator>
                <el-upload
                    v-if="isEditState"
                    :action="avatorActionUrl"
                    :before-upload="beforeAvatarUpload2"
                    :on-success="handleAvatarSuccess0"
                    :show-file-list="false"
                    class="avatar-uploader"
                    name="iconFile"
                >
                    <div class="upload-image avator">
                        <el-avatar :icon="UserFilled" :src="avatorUrl" shape="circle" />
                    </div>
                </el-upload>
                <div v-else class="upload-image avator">
                    <el-avatar :icon="UserFilled" :src="avatorUrl" shape="circle" />
                </div>
            </template>
            <template #photo>
                <el-upload
                    v-if="isEditState"
                    :action="actionUrl"
                    :before-upload="beforeAvatarUpload"
                    :on-success="handleAvatarSuccess"
                    :show-file-list="false"
                    class="avatar-uploader"
                    name="iconFile"
                >
                    <div class="upload-image photo">
                        <el-avatar :icon="UserFilled" :src="imageUrl" />
                    </div>
                </el-upload>
                <div v-else class="upload-image photo">
                    <el-avatar :icon="UserFilled" :src="imageUrl" />
                </div>
            </template>
            <template #sign>
                <el-upload
                    v-if="isEditState"
                    :action="saveSignUrl"
                    :before-upload="beforeAvatarUpload"
                    :on-success="handleAvatarSuccess2"
                    :show-file-list="false"
                    class="avatar-uploader"
                    name="iconFile"
                >
                    <div class="upload-image sign">
                        <el-avatar :icon="Picture" :src="signUrl" />
                    </div>
                </el-upload>
                <div v-else class="upload-image sign">
                    <el-avatar :icon="Picture" :src="signUrl" />
                </div>
            </template>
        </y9Form>
    </div>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { computed, h, inject, onMounted, reactive, ref, toRefs, watch } from 'vue';
    import type { UploadProps } from 'element-plus';
    import { ElNotification } from 'element-plus';
    import { $keyNameAssign } from '@/utils/object';
    import { getPersonExtById, getPersonExtByIdWithEncry, loginNameCheck } from '@/api/person/index';
    import moment from 'moment';
    import y9_storage from '@/utils/storage';
    import settings from '@/settings';
    import { Picture, UserFilled } from '@element-plus/icons-vue';
    import { $validCheck } from '@/utils/validate';
    import { $dictionary, $dictionaryFunc, $dictionaryNameFunc } from '@/utils/data';
    import { listByType } from '@/api/dictionary/index';
    import { useSettingStore } from '@/store/modules/settingStore';

    const { t } = useI18n();
    const settingStore = useSettingStore();
    // 注入 字体变量
    const fontSizeObj: any = inject('sizeObjInfo');
    const props = defineProps({
        isAdd: {
            //是否为添加模式，添加模式有些字段不需要显示
            type: Boolean,
            default: false
        },
        isEditState: {
            //是否为编辑状态
            type: Boolean
        },
        currInfo: {
            //当前信息
            type: Object,
            default: () => {
                return {};
            }
        }
    });

    const data = reactive({
        avatorActionUrl: '', //头像上传路径
        actionUrl: '', //头像上传路径
        imageUrl: '', //头像路径
        avatorUrl: '',
        saveSignUrl: '', //签名上传路径
        signUrl: ''
    });

    let { actionUrl, imageUrl, avatorActionUrl, avatorUrl, saveSignUrl, signUrl } = toRefs(data);

    //移动电话号码格式校验规则
    const mobileValidator = (rule, value, callback) => {
        let result = $validCheck('phone', value, true);
        if (!result.valid) {
            callback(new Error(t(result.msg)));
        } else {
            callback();
        }
    };
    //是否存在登录名的校验规则
    const hasLoginNameValidator = async (rule, value, callback) => {
        let result = await loginNameCheck(y9FormBaseRef.value?.model.id, value);
        if (!result.data) {
            callback(new Error(t('该登录名称已存在，请重新输入登录名称')));
        } else {
            callback();
        }
    };

    //基本信息表单配置
    const y9FormBaseConfig = ref({
        descriptionsFormConfig: {
            //描述表单配置
            column: settingStore.device === 'mobile' ? 1 : 3,
            labelAlign: 'center',
            labelWidth: '150px',
            contentWidth: '220px'
        },
        inline: true,
        model: {
            //新增或编辑人员表单
            id: '',
            parentId: '',
            name: '', //人员名称
            loginName: '', //登录名称
            caid: '', //认证码
            wxId: '', //微信唯一标识
            sex: t('男'), //性别,
            // userType:"",//人员类型
            official: t('是'), //是否在编
            officialType: '', //编制类型
            officeAddress: '', //办公室
            officePhone: '', //办公电话
            officeFax: '', //办公传真
            email: '', //电子邮件
            mobile: '', //移动电话
            disabled: '', //是否禁用
            tabIndex: null // 排序
        }, //表单属性
        rules: {} as any,
        itemList: [
            //表单显示列表
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'name',
                label: computed(() => t('人员名称')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.name);
                    }
                }
            },
            {
                type: 'text',
                type1: 'text', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'id',
                label: computed(() => t('唯一标识')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.id);
                    }
                }
            },
            {
                type: 'text',
                type1: 'text', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'tenantId',
                label: computed(() => t('租户唯一标识')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.tenantId);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'loginName',
                label: computed(() => t('登录名称')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.loginName);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'customId',
                label: computed(() => t('自定义ID')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.customId);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'caid',
                label: computed(() => t('CA认证码')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.caid);
                    }
                }
            },
            {
                type: 'text',
                type1: 'select', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'sex',
                label: computed(() => t('性别')),
                props: {
                    options: [],
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.sex == 1 ? t('男') : t('女'));
                    }
                }
            },
            {
                type: 'text',
                type1: 'select', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'official',
                label: computed(() => t('是否在编')),
                props: {
                    options: [],
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.official == 1 ? t('是') : t('否'));
                    }
                }
            },
            {
                type: 'text',
                type1: 'select', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'officialType',
                label: computed(() => t('编制类型')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        let name = $dictionaryNameFunc('officialType', props.currInfo?.officialType);
                        //text类型渲染的内容
                        return h('span', name);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'officeAddress',
                label: computed(() => t('办公室')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.officeAddress);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'officePhone',
                label: computed(() => t('办公电话')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.officePhone);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'officeFax',
                label: computed(() => t('办公传真')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.officeFax);
                    }
                }
            },
            {
                type: 'text',
                type1: 'select', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'disabled',
                label: computed(() => t('是否禁用')),
                props: {
                    options: [],
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.disabled ? t('是') : t('否'));
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'email',
                label: computed(() => t('电子邮件')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.email);
                    }
                }
            },

            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'mobile',
                label: computed(() => t('移动电话')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.mobile);
                    }
                }
            }
        ]
    });
    //个人信息表单配置
    const y9FormPersonConfig = ref({
        descriptionsFormConfig: {
            //描述表单配置
            column: settingStore.device === 'mobile' ? 1 : 3,
            labelAlign: 'center',
            labelWidth: '150px',
            contentWidth: '220px'
        },
        model: {
            personId: props.currInfo.id,
            workTime: '', //入职日期
            politicalStatus: '', //政治面貌
            birthday: '', //出生日期
            maritalStatus: 0, //婚姻状况
            professional: '', //专业
            city: '', //居住城市
            country: '', //居住国家
            homePhone: '', //家庭电话
            idType: '', //证件类型
            idNum: '', //证件号码
            education: '', //学历
            province: '', //人员籍贯
            homeAddress: '', //家庭住址
            description: '' //人员描述
        } as any, //表单属性
        rules: {} as any, //表单验证规则
        itemList: [
            //表单显示列表
            {
                type: 'slot',
                type1: 'slot', //自定义字段-编辑时显示的类型
                type2: 'slot', //自定义字段-非编辑状态显示文本类型
                label: '头像',
                props: {
                    slotName: 'avator' //插槽名称
                }
            },
            {
                type: 'slot',
                type1: 'slot', //自定义字段-编辑时显示的类型
                type2: 'slot', //自定义字段-非编辑状态显示文本类型
                label: '签名照 （小于2M的照片）',
                props: {
                    slotName: 'sign' //插槽名称
                }
            },
            {
                type: 'slot',
                type1: 'slot', //自定义字段-编辑时显示的类型
                type2: 'slot', //自定义字段-非编辑状态显示文本类型
                label: '证件照 （小于2M的蓝底照片）',
                props: {
                    slotName: 'photo' //插槽名称
                }
            },

            {
                type: 'text',
                type1: 'select', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'maritalStatus',
                label: computed(() => t('婚姻状况')),
                props: {
                    options: [],
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.maritalStatus);
                    }
                }
            },

            {
                type: 'text',
                type1: 'select', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'idType',
                label: computed(() => t('证件类型')),
                props: {
                    options: [],
                    render: () => {
                        //text类型渲染的内容
                        let name = $dictionaryNameFunc('principalIDType', props.currInfo?.idType);
                        return h('span', name);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'idNum',
                label: computed(() => t('证件号码')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.idNum);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'professional',
                label: computed(() => t('专业')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.professional);
                    }
                }
            },
            {
                type: 'text',
                type1: 'select', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'education',
                label: computed(() => t('学历')),
                props: {
                    options: [],
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.education);
                    }
                }
            },
            {
                type: 'text',
                type1: 'select', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'politicalStatus',
                label: computed(() => t('政治面貌')),
                props: {
                    options: [],
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.politicalStatus);
                    }
                }
            },
            {
                type: 'text',
                type1: 'date', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'birthday',
                label: computed(() => t('出生日期')),
                props: {
                    dataType: 'date',
                    format: 'YYYY-MM-DD',
                    valueFormat: 'YYYY-MM-DD',
                    options: [],
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.birthday);
                    }
                }
            },
            {
                type: 'text',
                type1: 'date', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'workTime',
                label: computed(() => t('入职日期')),
                props: {
                    dataType: 'date',
                    format: 'YYYY-MM-DD',
                    valueFormat: 'YYYY-MM-DD',
                    options: [],
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.workTime);
                    }
                }
            },

            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'city',
                label: computed(() => t('居住城市')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.city);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'country',
                label: computed(() => t('居住国家')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.country);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'homePhone',
                label: computed(() => t('家庭电话')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.homePhone);
                    }
                }
            },

            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'province',
                label: computed(() => t('人员籍贯')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.province);
                    }
                }
            },

            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'homeAddress',
                label: computed(() => t('家庭住址')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.homeAddress);
                    }
                },
                span: 3
            },
            {
                type: 'text',
                type1: 'textarea', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'description',
                label: computed(() => t('人员描述')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.description);
                    }
                },
                span: 3
            }
        ]
    });

    onMounted(async () => {
        await $dictionaryFunc('officialType', listByType, 'officialType'); //请求编制类型
        await $dictionaryFunc('principalIDType', listByType, 'principalIDType'); //请求证件类型

        for (let i = 0; i < y9FormBaseConfig.value.itemList.length; i++) {
            const item = y9FormBaseConfig.value.itemList[i];

            if (item.prop === 'sex') {
                item.props.options = $dictionary().sex?.map((item) => {
                    return {
                        label: computed(() => t(item.name)),
                        value: item.id
                    };
                });
            } else if (item.prop === 'official') {
                item.props.options = $dictionary().booleanNum?.map((item) => {
                    return {
                        label: computed(() => t(item.name)),
                        value: item.id
                    };
                });
            } else if (item.prop === 'officialType') {
                item.props.options = $dictionary().officialType?.map((item) => {
                    return {
                        label: computed(() => t(item.name)),
                        value: item.id
                    };
                });
            } else if (item.prop === 'disabled') {
                item.props.options = $dictionary().boolean?.map((item) => {
                    return {
                        label: computed(() => t(item.name)),
                        value: item.id
                    };
                });
            }
        }

        for (let i = 0; i < y9FormPersonConfig.value.itemList.length; i++) {
            const item = y9FormPersonConfig.value.itemList[i];

            if (item.prop === 'maritalStatus') {
                item.props.options = $dictionary().maritalStatus?.map((item) => {
                    return {
                        label: computed(() => t(item.name)),
                        value: item.id
                    };
                });
            } else if (item.prop === 'idType') {
                item.props.options = $dictionary().principalIDType?.map((item) => {
                    return {
                        label: computed(() => t(item.name)),
                        value: item.id
                    };
                });
            } else if (item.prop === 'politicalStatus') {
                item.props.options = $dictionary().politicalStatus?.map((item) => {
                    return {
                        label: computed(() => t(item.name)),
                        value: item.id
                    };
                });
            } else if (item.prop === 'education') {
                item.props.options = $dictionary().education?.map((item) => {
                    return {
                        label: computed(() => t(item.name)),
                        value: item.id
                    };
                });
            }
        }
    });

    watch(
        () => props.isEditState,
        (isEdit) => {
            if (isEdit) {
                //编辑状态给表单赋值
                $keyNameAssign(y9FormBaseConfig.value.model, props.currInfo); //编辑状态给表单赋值
            }
            getPersonExt();
            changeY9FormType(isEdit); //显示编辑表单
        }
    );

    //改变y9Form显示类型
    function changeY9FormType(isEdit) {
        if (isEdit) {
            //编辑状态设置表单校验规则
            y9FormBaseConfig.value.rules = {
                //表单校验规则
                name: [{ required: true, message: computed(() => t('请输入人员名称')), trigger: 'blur' }],
                loginName: [
                    { required: true, message: computed(() => t('请输入登录名称')), trigger: 'blur' },
                    { validator: hasLoginNameValidator, trigger: 'blur' }
                ],
                mobile: [
                    { required: true, message: computed(() => t('请输入移动电话')), trigger: 'blur' },
                    { validator: mobileValidator, trigger: 'blur' }
                ]
            };
        } else {
            y9FormBaseConfig.value.rules = {};
        }

        //编辑模式显示type1类型 非编辑模式显示type2类型
        y9FormBaseConfig.value.itemList.forEach((item) => {
            item.type = isEdit ? item.type1 : item.type2;
        });
        y9FormPersonConfig.value.itemList.forEach((item) => {
            item.type = isEdit ? item.type1 : item.type2;
        });
    }

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
            saveSignUrl.value =
                import.meta.env.VUE_APP_CONTEXT +
                'api/rest/person/savePersonSign?personId=' +
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
            signUrl.value =
                import.meta.env.VUE_APP_CONTEXT +
                'api/rest/person/showPersonSign?personId=' +
                props.currInfo.id +
                '&access_token=' +
                y9_storage.getObjectItem(settings.siteTokenKey, 'access_token') +
                '&time=' +
                Math.random();
            avatorActionUrl.value =
                import.meta.env.VUE_APP_CONTEXT +
                'api/rest/person/saveAvator?personId=' +
                props.currInfo.id +
                '&access_token=' +
                y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
            if (props.currInfo.avator != null && props.currInfo.avator.indexOf('/') != -1) {
                avatorUrl.value =
                    import.meta.env.VUE_APP_CONTEXT + props.currInfo.avator.substring(1, props.currInfo.avator.lenght);
            }
        },
        {
            immediate: true
        }
    );

    const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
        if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png' && rawFile.type !== 'image/jpg') {
            ElNotification({
                title: '失败',
                message: '请选择jpeg,png,jpg格式的图片!',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return false;
        } else if (rawFile.size / 1024 / 1024 > 2) {
            ElNotification({
                title: '失败',
                message: '上传图片大小不能超过 2MB!',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return false;
        }
        return true;
    };

    const beforeAvatarUpload2: UploadProps['beforeUpload'] = (rawFile) => {
        if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png' && rawFile.type !== 'image/jpg') {
            ElNotification({
                title: '失败',
                message: '请选择jpeg,png,jpg格式的图片!',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return false;
        } else if (rawFile.size / 1024 / 1024 > 5) {
            ElNotification({
                title: '失败',
                message: '上传图片大小不能超过 2MB!',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return false;
        }
        return true;
    };

    const handleAvatarSuccess0: UploadProps['onSuccess'] = (response, uploadFile) => {
        avatorUrl.value = URL.createObjectURL(uploadFile.raw!);
    };
    const handleAvatarSuccess: UploadProps['onSuccess'] = (response, uploadFile) => {
        imageUrl.value = URL.createObjectURL(uploadFile.raw!);
    };

    const handleAvatarSuccess2: UploadProps['onSuccess'] = (response, uploadFile) => {
        signUrl.value = URL.createObjectURL(uploadFile.raw!);
    };

    async function getPersonExt() {
        let result = { success: false, data: [] as any };
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
                $keyNameAssign(y9FormPersonConfig.value.model, result.data); //编辑状态给表单赋值
            }
        }
    }

    const y9FormBaseRef = ref();
    const y9FormPersonRef = ref();
    const personForm = computed(() => {
        return Object.assign({}, y9FormBaseRef.value?.model, y9FormPersonRef.value?.model);
    });

    function validForm() {
        return y9FormBaseRef.value.elFormRef?.validate((valid) => valid);
    }

    defineExpose({
        personForm,
        getPersonExt,
        validForm
    });
</script>

<style lang="scss" scoped>
    .person-form-div {
        display: flex;
        flex-direction: column;

        .person-form-title-base {
            color: var(--el-text-color-primary);
            text-align: center;
            line-height: 40px;
            border-top: 1px solid var(--el-border-color-lighter);
            border-left: 1px solid var(--el-border-color-lighter);
            border-right: 1px solid var(--el-border-color-lighter);
        }

        .person-form-title-person {
            color: var(--el-text-color-primary);
            text-align: center;
            line-height: 40px;
            border-left: 1px solid var(--el-border-color-lighter);
            border-right: 1px solid var(--el-border-color-lighter);
        }

        p {
            padding: 0px;
            margin: 0px;
        }
    }

    .upload-image {
        height: 3.5cm;
        margin: auto;

        :deep(.el-avatar) {
            width: 100%;
            height: 100%;

            i {
                font-size: v-bind('fontSizeObj.maximumFontSize');
            }
        }
    }

    .avator {
        width: 2.5cm;
        height: 2.5cm;
    }

    .photo {
        width: 2.5cm;
        height: 3.5cm;

        :deep(.el-avatar--circle) {
            border-radius: 0;
        }
    }

    .sign {
        width: 150px;
        background-color: transparent;
        height: 3cm;
        display: flex;
        text-align: center;
        justify-content: center;

        :deep(.el-avatar) {
            background-color: transparent;

            img {
                max-width: 100%;
                height: auto;
                -webkit-filter: blur(0.5px);
                filter: blur(0.5px);
            }
        }

        :deep(.el-avatar--circle) {
            border-radius: 0;
        }
    }
</style>

<style lang="scss">
    .avatar-uploader {
        min-width: 2.5cm;
        height: 3.5cm;
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
