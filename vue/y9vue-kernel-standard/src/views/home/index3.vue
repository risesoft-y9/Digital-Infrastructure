<script lang="ts" setup>
    import echarts from '@/utils/echarts'; // echarts图表插件
    import { inject, onBeforeUnmount, onMounted, ref, computed, watch } from 'vue';
    import { useI18n } from 'vue-i18n';
    import { useSettingStore } from '@/store/modules/settingStore';
    const settingStore = useSettingStore();
    const { t } = useI18n();

    // col变量
    const spanValue = ref(12);
    // echarts 对象 定义
    // 左边下角圆形
    let leftBottom = ref(null);
    // 右上角 两个图形之一
    let RightOne = ref(null);
    // 右上角两个图形 之二
    let RightTwo = ref(null);
    // 右下角两个图形之一
    let BottomOne = ref(null);
    // 右下角两个图形之二
    let BottomTwo = ref(null);

    let loading = ref(false);

    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');

    // 收缩左侧
    const menuCollapsed = computed<Boolean>(() => settingStore.getMenuCollapsed);

    onMounted(() => {
        initChart();
        // 所有图案响应容器的大小变化
        window.onresize = function () {
            chartInit();
        };
    });

    let themeType = {
        'theme-green': '#4e9876',
        'theme-blue': '#1e5896',
        'theme-default': '#586cb1'
    };
    // echarts 的颜色随主题颜色的变化而变化
    let echartsColor = ref(themeType[settingStore.getThemeName]);
    watch(
        () => settingStore.getThemeName,
        (newVal) => {
            echartsColor.value = themeType[newVal];
            initChart();
        }
    );

    // 初始化echarts 图
    function initChart() {
        // 左边下角的圆形
        const gaugeData = [
            {
                value: 60,
                detail: {
                    valueAnimation: true,
                    offsetCenter: ['0%', '0%']
                }
            }
        ];
        const homeLeftBottom = {
            color: [echartsColor.value],
            series: [
                {
                    type: 'gauge',
                    startAngle: 90,
                    endAngle: -270,
                    pointer: {
                        show: false
                    },
                    progress: {
                        show: true,
                        overlap: false,
                        roundCap: true,
                        clip: false,
                        itemStyle: {
                            borderWidth: 1,
                            borderColor: '#464646'
                        }
                    },
                    axisLine: {
                        lineStyle: {
                            width: 40
                        }
                    },
                    splitLine: {
                        show: false,
                        distance: 0,
                        length: 10
                    },
                    axisTick: {
                        show: false
                    },
                    axisLabel: {
                        show: false,
                        distance: 50
                    },
                    data: gaugeData,
                    title: {
                        fontSize: fontSizeObj.baseFontSize
                    },
                    detail: {
                        width: 50,
                        height: 14,
                        fontSize: 24,
                        formatter: '{value}%'
                    }
                }
            ]
        };
        leftBottom.value = echarts.init(document.getElementById('home-left-circle'));
        leftBottom.value.setOption(homeLeftBottom);

        // 右上角 两个图形之一
        const rightTopOne = {
            xAxis: {
                show: false,
                type: 'category',
                boundaryGap: false,
                data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
            },
            yAxis: {
                show: false,
                type: 'value'
            },
            tooltip: {
                trigger: 'axis'
            },
            color: [echartsColor.value],
            series: [
                {
                    data: [730, 832, 1320, 900, 800, 1330, 801],
                    type: 'line',
                    areaStyle: {},
                    smooth: true
                }
            ]
        };
        RightOne.value = echarts.init(document.getElementById('right-top-one'));
        RightOne.value.setOption(rightTopOne);

        // 右上角两个图形 之二
        const rightTopTwo = {
            tooltip: {
                trigger: 'item'
            },
            legend: {
                top: '30%',
                left: '-8%',
                orient: 'vertical',
                itemGap: 20
            },
            color: [echartsColor.value, '#dceae4'],
            series: [
                {
                    type: 'pie',
                    radius: ['40%', '70%'],
                    avoidLabelOverlap: false,
                    label: {
                        show: false,
                        position: 'center'
                    },
                    labelLine: {
                        show: false
                    },
                    data: [
                        { value: 1048, name: t('活跃') },
                        { value: 735, name: t('不活跃') }
                    ]
                }
            ]
        };
        RightTwo.value = echarts.init(document.getElementById('right-top-two'));

        RightTwo.value.setOption(rightTopTwo);

        // 右下角两个图形之一
        const rightBottomOne = {
            xAxis: {
                show: false,
                type: 'category',
                data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
            },
            yAxis: {
                show: false,
                type: 'value'
            },
            color: ['#333'],
            series: [
                {
                    data: [
                        80,
                        70,
                        {
                            value: 200,
                            itemStyle: {
                                color: echartsColor.value
                            }
                        },
                        90,
                        110,
                        130
                    ],
                    type: 'bar'
                }
            ]
        };
        BottomOne.value = echarts.init(document.getElementById('right-bottom-one'));
        BottomOne.value.setOption(rightBottomOne);

        // 右下角两个图形之二
        const rightBottomTwo = {
            angleAxis: {
                max: 2,
                startAngle: 30,
                splitLine: {
                    show: false
                }
            },
            radiusAxis: {
                type: 'category',
                data: ['v', 'w', 'x', 'y', 'z'],
                z: 10
            },
            polar: {},
            color: [echartsColor.value, '#95d475'],
            series: [
                {
                    type: 'bar',
                    data: [4, 3, 2, 1, 0],
                    coordinateSystem: 'polar',
                    name: 'Finished'
                },
                {
                    type: 'bar',
                    data: [4, 3, 2, 1, 0],
                    coordinateSystem: 'polar',
                    name: 'Rejected',
                    roundCap: true
                }
            ]
        };
        BottomTwo.value = echarts.init(document.getElementById('right-bottom-two'));
        BottomTwo.value.setOption(rightBottomTwo);

        // 所有echart图表 - 响应容器大小的变化
        // window.onresize = function () {
        //     leftBottom.value.resize();
        //     RightOne.value.resize();
        //     RightTwo.value.resize();
        //     BottomOne.value.resize();
        //     BottomTwo.value.resize();
        // }
        // if(settingStore.device === 'mobile') {
        //     spanValue.value = 24;
        // }else {
        //     spanValue.value = 12;
        // }
    }

    function chartInit() {
        loading.value = true;
        setTimeout(() => {
            leftBottom.value.resize();
            RightOne.value.resize();
            RightTwo.value.resize();
            BottomOne.value.resize();
            BottomTwo.value.resize();
        }, 300);
        setTimeout(() => {
            initChart();
            loading.value = false;
        }, 300);
    }

    watch(
        () => menuCollapsed.value,
        () => {
            chartInit();
        }
    );

    onBeforeUnmount(() => {
        if (!leftBottom.value || !RightOne.value || !RightTwo.value || !BottomOne.value || !BottomTwo.value) {
            return;
        }
        // 对资源进行释放
        leftBottom.value.dispose();
        leftBottom.value = null;
        RightOne.value.dispose();
        RightOne.value = null;
        RightTwo.value.dispose();
        RightTwo.value = null;
        BottomOne.value.dispose();
        BottomOne.value = null;
        BottomTwo.value.dispose();
        BottomTwo.value = null;
    });
</script>

<template>
    <div style="height: 100%; overflow-y: auto; overflow-x: hidden; scrollbar-width: none">
        <el-row :gutter="20" style="height: 100%">
            <el-col :span="spanValue">
                <div class="left">
                    <el-card class="box-card">
                        <div class="center">
                            <img src="@/assets/images/app-icon.png" class="image" />
                            <h1>{{ $t('数字底座') }}</h1>
                            <div class="remark">
                                <span>{{ $t('邮件系统') }}</span>
                                <span>{{ $t('工作流') }}</span>
                                <span>{{ $t('即时通讯') }}</span>
                            </div>
                        </div>
                    </el-card>
                    <el-card class="box-card left-two" style="background-color: #fff">
                        <div class="card-header">
                            <span>{{ $t('内存最高占用比例图') }}</span>
                            <el-dropdown>
                                <el-button
                                    disabled
                                    :size="fontSizeObj.buttonSize"
                                    :style="{ 'font-size': fontSizeObj.baseFontSize }"
                                >
                                    {{ $t('最近一个月') }}
                                </el-button>
                                <!-- <el-button>
                                    Last 7 days<i class="ri-arrow-down-s-line"></i>
                                </el-button>
                                <template #dropdown>
                                    <el-dropdown-menu>
                                    <el-dropdown-item>last 12 days</el-dropdown-item>
                                    <el-dropdown-item>last 13 days</el-dropdown-item>
                                    <el-dropdown-item>last 14 days</el-dropdown-item>
                                    <el-dropdown-item>last 15 days</el-dropdown-item>
                                    </el-dropdown-menu>
                                </template> -->
                            </el-dropdown>
                        </div>
                        <div class="two-content">
                            <div class="row" v-loading="loading">
                                <div class="left">
                                    <h1>60%</h1>
                                    <small>{{ $t('占用') }}</small>
                                </div>
                                <div>
                                    <!-- echarts图 -->
                                    <div id="home-left-circle" style="width: 300px; height: 276px"></div>
                                </div>
                            </div>
                            <div class="footer">
                                <div class="center">
                                    <p>{{ $t('栈区') }}</p>
                                    <span>29</span>
                                </div>
                                <div class="center">
                                    <p>{{ $t('堆区') }}</p>
                                    <span>63</span>
                                </div>
                                <div class="center">
                                    <p>{{ $t('静态区') }}</p>
                                    <span>1d</span>
                                </div>
                            </div>
                        </div>
                    </el-card>
                </div>
            </el-col>
            <el-col :span="spanValue" style="padding-left: 0px">
                <div class="right">
                    <div class="right-top">
                        <el-col :span="spanValue">
                            <el-card class="box-card">
                                <div class="card-header">
                                    <span>{{ $t('在线用户数量折线图') }}</span>
                                    <el-dropdown>
                                        <el-button
                                            :size="fontSizeObj.buttonSize"
                                            :style="{ 'font-size': fontSizeObj.baseFontSize }"
                                        >
                                            {{ $t('最近一周') }}<i class="ri-arrow-down-s-line"></i>
                                        </el-button>
                                        <template #dropdown>
                                            <el-dropdown-menu>
                                                <el-dropdown-item>{{ $t('最近两周') }}</el-dropdown-item>
                                                <el-dropdown-item>{{ $t('最近三周') }}</el-dropdown-item>
                                                <el-dropdown-item>{{ $t('最近一个月') }}</el-dropdown-item>
                                                <!-- <el-dropdown-item>last 15 days</el-dropdown-item> -->
                                            </el-dropdown-menu>
                                        </template>
                                    </el-dropdown>
                                </div>
                                <div class="metric-content" v-loading="loading">
                                    <div class="one-content">
                                        <h2>89.2k</h2>
                                        <span>{{ $t('在线用户') }}</span>
                                    </div>
                                    <div style="width: 100%">
                                        <!-- echarts图 -->
                                        <div id="right-top-one" style="width: 125%; height: 74px"></div>
                                    </div>
                                </div>
                            </el-card>
                        </el-col>
                        <el-col
                            :span="spanValue"
                            style="padding-right:0;max-width: settingStore.device === 'mobile'? 100% : 49%;flex: 0 0 49.5%;"
                        >
                            <el-card class="box-card">
                                <div class="card-header">
                                    <span>{{ $t('活跃用户数量比例图') }}</span>
                                    <el-button
                                        disabled
                                        :size="fontSizeObj.buttonSize"
                                        :style="{ 'font-size': fontSizeObj.baseFontSize }"
                                    >
                                        {{ $t('最近一个月') }}
                                    </el-button>
                                </div>
                                <div style="display: flex; justify-content: space-around" v-loading="loading">
                                    <!-- echarts图 -->
                                    <div id="right-top-two" style="width: 235px; height: 132px"></div>
                                </div>
                            </el-card>
                        </el-col>
                    </div>
                    <div class="right-bottom">
                        <el-card class="box-card">
                            <div class="card-header">
                                <span>{{ $t('对应岗位操作次数折线图') }}</span>
                                <el-dropdown>
                                    <el-button
                                        :size="fontSizeObj.buttonSize"
                                        :style="{ 'font-size': fontSizeObj.baseFontSize }"
                                    >
                                        {{ $t('最近一周') }}<i class="ri-arrow-down-s-line"></i>
                                    </el-button>
                                    <template #dropdown>
                                        <el-dropdown-menu>
                                            <el-dropdown-item>{{ $t('最近两周') }}</el-dropdown-item>
                                            <el-dropdown-item>{{ $t('最近三周') }}</el-dropdown-item>
                                            <el-dropdown-item>{{ $t('最近一个月') }}</el-dropdown-item>
                                            <!-- <el-dropdown-item>last 15 days</el-dropdown-item> -->
                                        </el-dropdown-menu>
                                    </template>
                                </el-dropdown>
                            </div>
                            <div class="card-content" v-loading="loading">
                                <div class="content-left">
                                    <h1>2.7k</h1>
                                    <h5>
                                        <span style="color: var(--el-color-primary)">+5.2%</span>
                                        <span>VS</span>
                                        <span> {{ $t('近7天') }}</span>
                                    </h5>
                                    <el-button
                                        :size="fontSizeObj.buttonSize"
                                        :style="{ 'font-size': fontSizeObj.baseFontSize }"
                                        type="primary"
                                        >{{ $t('进入详情') }}<i class="ri-arrow-right-s-line"></i
                                    ></el-button>
                                </div>
                                <div>
                                    <!-- echarts图表 -->
                                    <div id="right-bottom-one" style="width: 270px; height: 230px"></div>
                                </div>
                            </div>
                        </el-card>
                        <el-card class="box-card">
                            <div class="card-header">
                                <span>{{ $t('日志量') }}</span>
                                <el-dropdown>
                                    <el-button
                                        :size="fontSizeObj.buttonSize"
                                        :style="{ 'font-size': fontSizeObj.baseFontSize }"
                                    >
                                        {{ $t('最近一周') }}<i class="ri-arrow-down-s-line"></i>
                                    </el-button>
                                    <template #dropdown>
                                        <el-dropdown-menu>
                                            <el-dropdown-item>{{ $t('最近两周') }}</el-dropdown-item>
                                            <el-dropdown-item>{{ $t('最近三周') }}</el-dropdown-item>
                                            <el-dropdown-item>{{ $t('最近一个月') }}</el-dropdown-item>
                                            <!-- <el-dropdown-item>last 15 days</el-dropdown-item> -->
                                        </el-dropdown-menu>
                                    </template>
                                </el-dropdown>
                            </div>
                            <div class="card-item" v-loading="loading">
                                <div class="item-left">
                                    <div class="left-item">
                                        <i class="ri-checkbox-blank-circle-line"></i>
                                        <span>{{ $t('完成') }}</span>
                                        <span>23043</span>
                                    </div>
                                    <div class="left-item">
                                        <i style="color: #95d475" class="ri-checkbox-blank-circle-line"></i>
                                        <span>{{ $t('错误') }}</span>
                                        <span>2343</span>
                                    </div>
                                </div>
                                <div id="right-bottom-two" style="width: 200px; height: 185px"></div>
                            </div>
                        </el-card>
                    </div>
                </div>
            </el-col>
        </el-row>
        <!-- <div class="footer">
            <span>Powered by YouSheng . v1.0.0</span>
        </div> -->
    </div>
</template>

<style lang="scss" scoped>
    .left {
        display: flex;
        flex-direction: column;
        height: 100%;
        .left-two {
            :deep(.el-card__body) {
                padding: 0px;
                height: 100%;
            }
            .two-content {
                height: calc(100% - 50px - 1.1rem);
                display: flex;
                flex-direction: column;
                justify-content: center;
                .row {
                    display: flex;
                    flex-wrap: wrap;
                    margin-right: -14px;
                    margin-left: -14px;
                    justify-content: space-around;
                    .left {
                        display: flex;
                        flex: 0 0 16.6666666667%;
                        max-width: 16.6666666667%;
                        text-align: center;
                        flex-wrap: wrap;
                        flex-direction: column;
                        h1 {
                            font-size: v-bind('fontSizeObj.maximumFontSize');
                            margin: 1.5rem 0 0 1rem;
                            font-weight: 400;
                            font-family: Nunito, Montserrat, system-ui, BlinkMacSystemFont, -apple-system, sans-serif;
                            color: #2c2c2c;
                        }
                        small {
                            font-size: v-bind('fontSizeObj.mediumFontSize');
                            font-weight: 400;
                        }
                    }
                }
                .footer {
                    padding: 1rem 5rem;
                    display: flex;
                    justify-content: space-between;
                    .center {
                        text-align: center;
                        p {
                            margin-top: 0;
                            margin-bottom: 1rem;
                            font-size: v-bind('fontSizeObj.mediumFontSize');
                        }
                        span {
                            font-size: v-bind('fontSizeObj.moreLargeFont') !important;
                        }
                    }
                }
            }
        }
        .box-card {
            box-shadow: 2px 2px 2px 1px rgba(0, 0, 0, 0.06);
            margin-bottom: 1.5rem;
            border-radius: 0.25rem;
            position: relative;
            display: flex;
            flex-direction: column;
            background-color: var(--el-color-primary);
            min-width: 0;
            word-wrap: break-word;
            background-clip: border-box;
            .center {
                text-align: center;
                .image {
                    background: #fff;
                    border: 2px solid #fff;
                    width: 70px;
                    height: 70px;
                    margin-top: 1rem !important;
                    border-radius: 50%;
                }
                h1 {
                    font-weight: 200;
                    font-size: v-bind('fontSizeObj.biggerFontSize');
                    color: #fff;
                    margin-bottom: 3rem !important;
                    margin-top: 1.5rem !important;
                    font-family: Nunito, Montserrat, system-ui, BlinkMacSystemFont, -apple-system, sans-serif;
                }
                .remark {
                    text-align: center;
                    margin-bottom: 2.5rem;
                }
                .remark span {
                    padding: 0 25px;
                    font-size: v-bind('fontSizeObj.baseFontSize');
                    font-weight: 600;
                    letter-spacing: 0.1rem;
                    text-decoration: none;
                    text-transform: uppercase;
                    color: #fff;
                    background-color: transparent;
                }
            }
            .header {
                padding: 1.1rem 1.1rem 0;
            }
            h4 {
                font-size: v-bind('fontSizeObj.extraLargeFont');
                margin-bottom: 1rem;
                font-weight: 400;
            }
        }

        .box-card:nth-child(2) {
            margin-bottom: v-bind("settingStore.device === 'mobile'? '1.5rem' : '0px'");
            height: calc(100% - 338px);
        }
    }
    .card-header {
        display: flex;
        padding: 1.1rem 1.1rem 0;
        justify-content: space-between;
        font-size: v-bind('fontSizeObj.mediumFontSize');
        :deep(.el-button) {
            font-size: v-bind('fontSizeObj.baseFontSize');
            padding: 0.54rem 0.9rem !important;
        }
        span {
            margin-right: 10px;
        }
    }
    .right {
        display: flex;
        flex-direction: column;
        height: 100%;
        .box-card {
            box-shadow: 2px 2px 2px 1px rgba(0, 0, 0, 0.06);
            border-radius: 0.25rem;
        }
        :deep(.el-card__body) {
            padding: 0px;
            height: 100%;
        }
        .right-top {
            display: flex;
            flex-direction: v-bind("settingStore.device === 'mobile'? 'column' : 'row'");
            .box-card {
                margin-bottom: 1.5rem;
            }
        }
        .right-bottom {
            height: calc(100% - 207px);
            .box-card {
                margin: 0 3px 0 10px;
                margin-bottom: 1.5rem;
            }
            .box-card:nth-child(2) {
                margin-bottom: 0;
                height: calc(100% - 285px - 1.5rem);
            }
            .card-content {
                display: flex;
                justify-content: v-bind("settingStore.device === 'mobile'? 'center' : 'space-evenly'");
                flex-wrap: wrap;
                .content-left {
                    text-align: left;
                    padding: 1rem;
                    h1 {
                        font-size: v-bind('fontSizeObj.maximumFontSize');
                        margin-top: 1.5rem;
                        margin-bottom: 0;
                        color: #2c2c2c;
                        font-weight: 400;
                    }
                    h5 {
                        margin-top: 18px;
                        font-size: v-bind('fontSizeObj.baseFontSize');
                        color: #2c2c2c;
                        font-weight: 500;
                        span:nth-child(1) {
                            color: #21b978;
                        }
                        span:nth-child(2) {
                            margin: 0px 8px;
                        }
                    }
                }
            }
            .card-item {
                display: flex;
                align-items: center;
                justify-content: space-evenly;
                margin-bottom: 1rem;
                padding-right: 3.5rem;
                height: calc(100% - 50px - 2.2rem);
                .item-left {
                    margin: 1.5rem 0 1rem 0;
                    .left-item {
                        display: flex;
                        justify-content: space-between;
                        margin-left: 2rem;
                        margin-bottom: 2rem;
                        font-size: v-bind('fontSizeObj.mediumFontSize');
                        span {
                            margin-left: 0.5rem;
                        }
                        i {
                            color: var(--el-color-primary);
                        }
                    }
                }
            }
        }
        .metric-content {
            height: 116px;
            .one-content {
                display: flex;
                margin: 1rem 0 2px 0;
                align-items: center;
                justify-content: space-between;
                box-sizing: border-box;
                h2 {
                    font-size: v-bind('fontSizeObj.moreLargeFont');
                    margin: 0 0 0.5rem 1rem;
                    line-height: 1.3;
                    color: #2c2c2c;
                    font-weight: 500;
                    font-family: Nunito, Montserrat, system-ui, BlinkMacSystemFont, -apple-system, sans-serif;
                }
                span {
                    color: #7c858e;
                    margin: 0 1rem 0 0;
                    font-size: v-bind('fontSizeObj.mediumFontSize');
                }
            }
        }
        #right-top-one {
            :deep(div) {
                canvas {
                    // width: 100% !important;
                    left: -47px !important;
                    top: 5px !important;
                    height: 84px !important;
                }
            }
        }
        #right-top-two {
            :deep(div) {
                canvas {
                    left: 42px !important;
                }
            }
        }
    }
    .footer {
        text-align: center;
        padding: 1.5rem 0 1rem;
        span {
            color: #2c2c2c;
        }
    }
</style>
