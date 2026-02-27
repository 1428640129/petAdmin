<script setup lang="ts">
import { watch } from 'vue';
import { useAppStore } from '@/store/modules/app';
import { useEcharts } from '@/hooks/common/echarts';
import { $t } from '@/locales';

defineOptions({ name: 'LineChart' });

const appStore = useAppStore();

const { domRef, updateOptions } = useEcharts(() => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross',
      label: {
        backgroundColor: '#6a7985'
      }
    }
  },
  legend: {
    data: [$t('page.home.downloadCount'), $t('page.home.registerCount')]
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: [] as string[]
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      color: '#FFA500', // 橙色
      name: $t('page.home.downloadCount'),
      type: 'line',
      smooth: true,
      stack: 'Total',
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            {
              offset: 0.25,
              color: '#FFA500' // 橙色
            },
            {
              offset: 1,
              color: '#fff'
            }
          ]
        }
      },
      emphasis: {
        focus: 'series'
      },
      data: [] as number[]
    },
    {
      color: '#98D8C8', // 柔和的青绿色
      name: $t('page.home.registerCount'),
      type: 'line',
      smooth: true,
      stack: 'Total',
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            {
              offset: 0.25,
              color: '#98D8C8' // 柔和的青绿色
            },
            {
              offset: 1,
              color: '#fff'
            }
          ]
        }
      },
      emphasis: {
        focus: 'series'
      },
      data: []
    }
  ]
}));

async function loadChartData() {
  try {
    const { fetchGetHomeStatistics } = await import('@/service/api');
    const result = await fetchGetHomeStatistics();
    const data = (result as any)?.data ?? result;
    if (data?.lineChart) {
      updateOptions(opts => {
        opts.xAxis.data = data.lineChart.xAxis || [];
        opts.series[0].data = data.lineChart.serviceOrders || [];
        opts.series[1].data = data.lineChart.newUsers || [];
        return opts;
      });
    }
  } catch (error) {
    console.error('获取折线图数据失败:', error);
    // 如果接口失败，使用默认数据（最近30天的日期）
    const defaultDates: string[] = [];
    const today = new Date();
    for (let i = 29; i >= 0; i--) {
      const date = new Date(today);
      date.setDate(date.getDate() - i);
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      defaultDates.push(`${month}-${day}`);
    }
    updateOptions(opts => {
      opts.xAxis.data = defaultDates;
      opts.series[0].data = new Array(30).fill(0).map(() => Math.floor(Math.random() * 20));
      opts.series[1].data = new Array(30).fill(0).map(() => Math.floor(Math.random() * 10));
      return opts;
    });
  }
}

function updateLocale() {
  updateOptions((opts, factory) => {
    const originOpts = factory();

    opts.legend.data = originOpts.legend.data;
    opts.series[0].name = originOpts.series[0].name;
    opts.series[1].name = originOpts.series[1].name;

    return opts;
  });
}

async function init() {
  loadChartData();
}

watch(
  () => appStore.locale,
  () => {
    updateLocale();
  }
);

// init
init();
</script>

<template>
  <ElCard class="card-wrapper">
    <div ref="domRef" class="h-360px overflow-hidden"></div>
  </ElCard>
</template>

<style scoped></style>
