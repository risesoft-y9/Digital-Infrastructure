<!--
 * @Author: hongzhew
 * @Date: 2022-04-01 13:23:20
 * @LastEditors: mengjuhua
 * @LastEditTime: 2023-12-26 11:23:22
 * @Description:
-->
<!--  -->
<template>
    <div class="y9-divider">
        <el-divider content-position="left">容器测试</el-divider>
    </div>

    <el-container>
        <el-aside class="grid-content bg-primary-color" style="margin-right: 20px" width="33%"></el-aside>
        <el-main class="grid-content" style="border: 1px solid var(--el-color-primary-light-1)">
            <el-row :gutter="20">
                <el-col :span="16">
                    <div class="grid-content bg-primary-color" />
                </el-col>
                <el-col :span="8">
                    <div class="grid-content bg-color" />
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="8">
                    <div class="grid-content bg-white-color" />
                </el-col>
                <el-col :span="8">
                    <div class="grid-content bg-white-color" />
                </el-col>
                <el-col :span="4">
                    <div class="grid-content bg-primary-color" />
                </el-col>
                <el-col :span="4">
                    <div class="grid-content bg-white-color" />
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="6">
                    <div class="grid-content bg-primary-color" />
                </el-col>
                <el-col :span="6">
                    <div class="grid-content bg-white-color" />
                </el-col>
                <el-col :span="6">
                    <div class="grid-content bg-white-color" />
                </el-col>
                <el-col :span="6">
                    <div class="grid-content bg-primary-color" />
                </el-col>
            </el-row>
        </el-main>
    </el-container>

    <div class="y9-divider">
        <el-divider content-position="left">布局测试</el-divider>
    </div>
    <el-row :gutter="20">
        <el-col :span="12">
            <div class="grid-content bg-primary-color" />
        </el-col>
        <el-col :span="12">
            <div class="grid-content bg-white-color" />
        </el-col>
    </el-row>
    <el-row :gutter="20">
        <el-col :span="16">
            <div class="grid-content bg-primary-color" />
        </el-col>
        <el-col :span="8">
            <div class="grid-content bg-white-color" />
        </el-col>
    </el-row>
    <el-row :gutter="20">
        <el-col :span="8">
            <div class="grid-content bg-white-color" />
        </el-col>
        <el-col :span="8">
            <div class="grid-content bg-white-color" />
        </el-col>
        <el-col :span="4">
            <div class="grid-content bg-primary-color" />
        </el-col>
        <el-col :span="4">
            <div class="grid-content bg-white-color" />
        </el-col>
    </el-row>
    <el-row :gutter="20">
        <el-col :span="4">
            <div class="grid-content bg-white-color" />
        </el-col>
        <el-col :span="12">
            <div class="grid-content bg-primary-color" />
        </el-col>
        <el-col :span="8">
            <div class="grid-content bg-white-color" />
        </el-col>
    </el-row>
    <el-row :gutter="20">
        <el-col :span="20">
            <div class="grid-content bg-white-color" />
        </el-col>
        <el-col :span="4">
            <div class="grid-content bg-primary-color" />
        </el-col>
    </el-row>
    <el-row :gutter="20">
        <el-col :span="4">
            <div class="grid-content bg-primary-color" />
        </el-col>
        <el-col :span="4">
            <div class="grid-content bg-primary-color" />
        </el-col>
        <el-col :span="4">
            <div class="grid-content bg-white-color" />
        </el-col>
        <el-col :span="4">
            <div class="grid-content bg-white-color" />
        </el-col>
        <el-col :span="4">
            <div class="grid-content bg-white-color" />
        </el-col>
        <el-col :span="4">
            <div class="grid-content bg-primary-color" />
        </el-col>
    </el-row>
    <el-row :gutter="20">
        <el-col :span="6">
            <div class="grid-content bg-white-color" />
        </el-col>
        <el-col :span="6">
            <div class="grid-content bg-white-color" />
        </el-col>
        <el-col :span="6">
            <div class="grid-content bg-primary-color" />
        </el-col>
        <el-col :span="6">
            <div class="grid-content bg-primary-color" />
        </el-col>
    </el-row>

    <div class="y9-divider">
        <el-divider content-position="left">步骤流程测试</el-divider>
    </div>
    <el-row :gutter="20">
        <el-col :span="6">
            <div class="grid-content bg-primary-color y9-advertise">有生软件</div>
        </el-col>
        <el-col :span="18">
            <div class="grid-content bg-white-color" style="padding-top: 45px; padding-bottom: 27px">
                <el-steps :active="4" align-center>
                    <el-step description="开始" title="起草" />
                    <el-step description="并行" title="审批" />
                    <el-step description="串行" title="审批" />
                    <el-step description="串行" title="审批" />
                    <el-step description="并行" title="审批" />
                    <el-step description="并行" title="审批" />
                    <el-step description="结束" title="签发" />
                </el-steps>
                <el-steps :active="4" simple style="margin-top: 15px; padding: 13px 4%">
                    <el-step :icon="Right" title="王洪哲" />
                    <el-step :icon="Switch" title="胡弘祚" />
                    <el-step :icon="Right" title="张三" />
                    <el-step :icon="Right" title="李四" />
                    <el-step :icon="Switch" title="王五" />
                    <el-step :icon="Switch" title="曲金凤" />
                    <el-step :icon="Right" title="叶海峰" />
                </el-steps>
            </div>
        </el-col>
    </el-row>
    <div class="y9-divider">
        <el-divider content-position="left">时间线与表单测试</el-divider>
    </div>
    <el-row :gutter="20">
        <el-col :span="6">
            <div class="grid-content bg-white-color" style="padding-top: 35px; padding-bottom: 5px">
                <el-timeline>
                    <el-timeline-item
                        v-for="(activity, index) in activities"
                        :key="index"
                        :color="activity.color"
                        :hollow="activity.hollow"
                        :icon="activity.icon"
                        :size="activity.size"
                        :timestamp="activity.timestamp"
                        :type="activity.type"
                        >{{ activity.content }}
                    </el-timeline-item>
                </el-timeline>
            </div>
        </el-col>
        <el-col :span="18">
            <div class="grid-content bg-white-color" style="padding: 45px; padding-bottom: 22px">
                <el-form :model="form" label-width="120px">
                    <el-form-item :size="settingStore.getFontSize" label="企业名称">
                        <el-input v-model="form.name" />
                    </el-form-item>
                    <el-form-item :size="settingStore.getFontSize" label="所属地选择">
                        <el-select v-model="form.region" placeholder="请选择企业所在地">
                            <el-option label="北京" value="beijing" />
                            <el-option label="上海" value="shanghai" />
                            <el-option label="深圳" value="shenzhen" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :size="settingStore.getFontSize" label="注册有效期">
                        <el-col :span="11">
                            <el-date-picker
                                v-model="form.date1"
                                placeholder="选择准确日期"
                                style="width: 100%"
                                type="date"
                            />
                        </el-col>
                        <el-col :span="1" class="text-center">
                            <span class="text-gray-500">-</span>
                        </el-col>
                        <el-col :span="12">
                            <el-time-picker v-model="form.date2" placeholder="选择准确时间" style="width: 100%" />
                        </el-col>
                    </el-form-item>
                    <el-form-item :size="settingStore.getFontSize" label="申请副理事单位">
                        <el-switch v-model="form.delivery" />
                    </el-form-item>
                    <el-form-item :size="settingStore.getFontSize" label="企业类型">
                        <el-radio-group v-model="form.resource">
                            <el-radio label="中小微企业" />
                            <el-radio label="上市企业" />
                            <el-radio label="国有上市企业" />
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item :size="settingStore.getFontSize" label="参与联盟专题">
                        <el-checkbox-group v-model="form.type">
                            <el-checkbox label="元宇宙" name="type1" />
                            <el-checkbox label="金融资产" name="type2" />
                            <el-checkbox label="网络安全" name="type3" />
                            <el-checkbox label="智慧城市" name="type4" />
                        </el-checkbox-group>
                    </el-form-item>
                    <el-form-item :size="settingStore.getFontSize" label="备注信息">
                        <el-input v-model="form.desc" type="textarea" />
                    </el-form-item>
                    <el-form-item :size="settingStore.getFontSize">
                        <el-button style="background-color: var(--el-color-primary); color: #fff">申请</el-button>
                        <el-button>取消</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </el-col>
    </el-row>
    <div class="y9-divider">
        <el-divider content-position="left">折叠板与日历测试</el-divider>
    </div>
    <el-row :gutter="20">
        <el-col :span="15">
            <div class="grid-content bg-white-color" style="padding: 25px; padding-left: 40px; padding-right: 40px">
                <div class="y9-collapse">
                    <el-collapse v-model="activeName" accordion>
                        <el-collapse-item name="1" title="五年时光如沙似画 雄安绘芳华">
                            <div>
                                时光如沙似画,回望雄安的五年发展,一次次改革与创新,让“绿色低碳、信息智能、宜居宜业、人与自然和谐共生”的美好图景愈发清晰
                                春到雄安，草木返青，杏花初绽，一派生机盎然；建设工地，塔吊林立，机器轰鸣，处处热火朝天。
                                雄安新区画卷徐徐铺展，朝着令人“心向往之”的目标延伸，新时代“未来之城”正从规划蓝图变为现实样板
                                5年来，日夜兼程，风雨无阻，一系列顶层设计制定完成，一批批重大项目全面推进，一个个标志性工程投入使用，雄安新区进入承接北京非首都功能疏解和大规模建设同步推进的重要阶段。
                            </div>
                        </el-collapse-item>
                        <el-collapse-item name="2" title="春已至花已开 盼疫散愿人安 "></el-collapse-item>
                        <el-collapse-item
                            name="3"
                            title="蓝天保卫战三年行动计划期间我国大气环境持续改善"
                        ></el-collapse-item>
                        <el-collapse-item
                            name="4"
                            title="韩国两架军机相撞后双双坠毁 目击者拍下现场画面"
                        ></el-collapse-item>
                        <el-collapse-item name="5" title="支援疫情防控，京检青年在行动！"></el-collapse-item>
                        <el-collapse-item name="6" title="市委决定：肖丹任海淀区委常委"></el-collapse-item>
                        <el-collapse-item name="7" title="清明假期北京市属公园推出19项主题活动"></el-collapse-item>
                        <el-collapse-item name="8" title="贵州赫章：人间奇迹 悬崖公路"></el-collapse-item>
                        <el-collapse-item name="9" title="新芽吐翠满眼绿——黔山贵水春茶采摘正当时"></el-collapse-item>
                        <el-collapse-item name="10" title="途经毕节的这条高速已基本建成"></el-collapse-item>
                    </el-collapse>
                </div>
            </div>
        </el-col>
        <el-col :span="9">
            <div class="grid-content bg-white-color">
                <div class="y9-calendar">
                    <el-config-provider :locale="locale">
                        <el-calendar v-model="value" />
                    </el-config-provider>
                </div>
            </div>
        </el-col>
    </el-row>
    <div class="y9-divider">
        <el-divider content-position="left">表格与按钮测试</el-divider>
    </div>

    <el-row :gutter="20">
        <el-col :span="20">
            <div class="grid-content bg-white-color">
                <div class="y9-table">
                    <el-table :data="tableData" style="width: 100%">
                        <el-table-column
                            align="center"
                            label="日期"
                            prop="date"
                            sortable
                            style="background-color: blue"
                            width="150"
                        />
                        <el-table-column align="center" label="姓名" prop="name" width="160" />
                        <el-table-column align="center" label="省市" prop="state" width="160" />
                        <el-table-column align="center" label="市区" prop="city" width="180" />
                        <el-table-column align="center" label="地址" prop="address" width="420" />
                        <el-table-column align="center" label="邮政编号" prop="zip" width="180" />
                        <el-table-column align="center" fixed="right" label="操作" width="200">
                            <template #default>
                                <el-button size="small" type="text">详细内容</el-button>
                                <el-button size="small" type="text">{{ $t('编辑') }}</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>
            </div>
        </el-col>

        <el-col :span="4">
            <div
                class="grid-content bg-white-color"
                style="
                    display: flex;
                    flex-direction: column;
                    justify-content: center;
                    align-items: center;
                    padding-top: 29px;
                    padding-bottom: 29px;
                    border: 1px dashed var(--el-color-primary);
                "
            >
                <el-button style="background-color: var(--el-color-primary); color: #fff">主题色调</el-button>
                <br />
                <el-button type="success">成功色调</el-button>
                <br />
                <el-button type="warning">警告色调</el-button>
                <br />
                <el-button type="danger">错误色调</el-button>
                <br />
                <el-button type="info">提示色调</el-button>
            </div>
        </el-col>
    </el-row>
</template>

<style lang="scss" scoped>
    @import '@/theme/global-vars.scss';

    .el-row {
        margin-bottom: 20px;
    }

    .el-col {
        border-radius: 4px;
    }

    .bg-transparent {
        background-color: transparent;
    }

    .bg-white-color {
        background: var(--el-bg-color);
    }

    .bg-primary-color {
        background-color: var(--el-color-primary);
    }

    .bg-color {
        background-color: var(--el-bg-color);
    }

    .grid-content {
        border-radius: 4px;
        min-height: 36px;
        box-shadow: 1px 2px 4px var(--el-border-color-base);
    }

    .el-carousel__item h3 {
        color: #475669;
        font-size: 14px;
        opacity: 0.75;
        line-height: 200px;
        margin: 0;
        text-align: center;
    }

    .el-carousel__item:nth-child(2n) {
        background-color: #99a9bf;
    }

    .el-carousel__item:nth-child(2n + 1) {
        background-color: #d3dce6;
    }

    .y9-collapse {
        :deep .el-collapse-item__header.is-active {
            border-bottom-color: var(--el-color-primary);
            color: var(--el-color-primary);
            font-weight: bold;
        }
    }

    .y9-divider {
        margin-bottom: 40px;
        margin-top: 40px;

        :deep .el-divider__text {
            background-color: transparent;
            font-size: 20px;
        }
    }

    .y9-advertise {
        text-align: center;
        vertical-align: middle;
        height: 210px;
        line-height: 210px;
        font-size: 30px;
        color: #fff;
        font-family: KaiTi;
    }

    .y9-calendar {
        & > .el-calendar {
            :deep(div) {
                background-color: salmon; // 整个时间表格的背景颜色

                & > .el-calendar__title {
                    color: sandybrown; // xxx年xx月 字体颜色
                }

                & > .el-calendar__button-group {
                    & > button {
                        background-color: salmon; // 上个月 今天 下个月 背景颜色
                        color: greenyellow; // 上个月 今天 下个月 字体颜色
                    }
                }

                & > .el-calendar-table {
                    & > thead {
                        & > th {
                            color: greenyellow; // 日 一 二 三 四 。。。字体颜色
                        }
                    }

                    & > tbody {
                        & > tr > td.prev {
                            color: greenyellow; // 今日（当前日期）之前的 字体颜色
                        }

                        & > tr > td.current {
                            color: greenyellow; //  今日（当前日期）之前的 字体颜色
                        }

                        & > tr > td.current.is-today {
                            & .el-calendar-day {
                                background-color: grey; //  今天 背景颜色
                            }
                        }

                        & > tr > td.current.is-selected {
                            & .el-calendar-day {
                                background-color: grey; //  鼠标选择的日期 点击后的背景颜色
                            }
                        }

                        & > tr > td.next {
                            color: greenyellow; //  今日（当前日期）之后的 字体颜色（最后一行）
                        }
                    }
                }
            }
        }

        // :deep .el-calendar-table td.is-selected {
        //     background-color: var(--el-color-primary-light-1);
        //     color: #fff;
        // }
        // :deep .el-calendar-table .el-calendar-day:hover {
        //     cursor: pointer;
        //     background-color: var(--el-color-primary-light-4);
        //     color: #fff;
        // }
    }

    .y9-table {
        :deep(div) {
            table {
                & > thead tr th:last-child {
                    background-color: grey; // 第一行最后一列背景颜色
                    color: red; // 第一行最后一列字体颜色
                }

                & > tbody tr td:last-child {
                    background-color: grey; // 最后一列背景颜色
                    & > div button {
                        color: red; // 最后一列字体颜色
                    }
                }
            }
        }
    }
</style>
<script lang="ts" setup>
    import { reactive, ref } from 'vue';
    import { MoreFilled, Right, Switch } from '@element-plus/icons';
    import { ElConfigProvider } from 'element-plus';
    import zhCn from 'element-plus/dist/locale/zh-cn.mjs';
    import en from 'element-plus/dist/locale/en.mjs';
    import { useSettingStore } from '@/store/modules/settingStore';

    const settingStore = useSettingStore();
    const locale = settingStore.getWebLanguage == 'zh' ? zhCn : en;
    const value = ref(new Date());
    const activeName = ref('1');
    const activities = [
        {
            content: '联盟入会申请表',
            timestamp: '2022-03-12 20:46',
            size: 'large',
            type: 'primary',
            icon: MoreFilled
        },
        {
            content: '联盟入会预审核',
            timestamp: '2022-03-13 15:13',
            type: 'primary',
            size: 'large'
        },
        {
            content: '联盟入会审核',
            timestamp: '2022-03-13 20:13',
            type: 'primary',
            size: 'large'
        },
        {
            content: '理事长审批',
            timestamp: '2022-03-13 20:13',
            type: 'primary',
            size: 'large'
        },
        {
            content: '会费缴纳',
            timestamp: '2022-03-15 13:26',
            size: 'large'
        },
        {
            content: '会员信息确认',
            timestamp: '2022-03-31 15:22',
            size: 'large'
        },
        {
            content: '入会证明申领',
            timestamp: '2022-04-03 ',
            type: 'primary',
            size: 'large',
            hollow: true
        },
        {
            content: '入会流程结束',
            timestamp: '2022-04-05 ',
            color: '#999',
            size: 'large'
        }
    ];
    const form = reactive({
        name: '北京有生博大软件股份有限公司',
        region: '',
        date1: '',
        date2: '',
        delivery: true,
        type: ['元宇宙', '金融资产', '网络安全', '智慧城市'],
        resource: '',
        desc: ''
    });
    const tableData = [
        {
            date: '2021-05-03',
            name: '张三',
            state: '北京市',
            city: '通州区',
            address: '北京市通州区承安路3号院',
            zip: '101100',
            tag: '工作地点'
        },
        {
            date: '2021-05-02',
            name: '叶海峰',
            state: '深圳市',
            city: '南山区',
            address: '大冲商务中心1001',
            zip: '51800',
            tag: '分公司地点'
        },
        {
            date: '2021-05-04',
            name: '王洪哲',
            state: '北京市',
            city: '海淀区',
            address: '阜成路58号新洲商务大厦4层412',
            zip: '100142',
            tag: '公司地点'
        },
        {
            date: '2021-05-18',
            name: '胡弘祚',
            state: '北京市',
            city: '海淀区',
            address: '阜成路58号新洲商务大厦4层412',
            zip: '100142',
            tag: '公司地点'
        },
        {
            date: '2021-05-03',
            name: '钱六',
            state: '北京市',
            city: '通州区',
            address: '北京市通州区承安路3号院',
            zip: '101100',
            tag: '工作地点'
        },
        {
            date: '2021-06-13',
            name: '李四',
            state: '北京市',
            city: '通州区',
            address: '北京市通州区承安路4号院',
            zip: '101100',
            tag: '工作地点'
        }
    ];
</script>
