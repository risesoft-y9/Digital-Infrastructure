/**
 * 按需引入echart
 * @Author: fuyu
 * @Date: 2022-06-09 17:12:30
 *
 */

// 引入 echarts 核心模块，核心模块提供了 echarts 使用必须要的接口。
import * as echarts from 'echarts/core';

// 标签自动布局，全局过渡动画等特性
import {LabelLayout, UniversalTransition} from 'echarts/features';

// 引入 Canvas 渲染器，注意引入 CanvasRenderer 或者 SVGRenderer 是必须的一步
import {CanvasRenderer} from 'echarts/renderers';

// 引入需要的图表
import {BarChart, GaugeChart, LineChart, PieChart, TreeChart, TreeSeriesOption} from 'echarts/charts';

// 引入提示框，标题，直角坐标系，数据集，内置数据转换器组件，组件后缀都为 Component
import {
    GridComponent,
    LegendComponent,
    PolarComponent,
    TooltipComponent,
    TooltipComponentOption
} from 'echarts/components';

// 注册必须的组件
echarts.use([
    TooltipComponent,
    TreeChart,
    CanvasRenderer,
    PolarComponent,
    LineChart,
    GridComponent,
    LegendComponent,
    BarChart,
    UniversalTransition,
    PieChart,
    LabelLayout,
    GaugeChart
]);

// 通过 ComposeOption 来组合出一个只有必须组件和图表的 Option 类型
type EChartsOption = echarts.ComposeOption<TooltipComponentOption | TreeSeriesOption>;

export default echarts;
