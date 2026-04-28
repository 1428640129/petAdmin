<script setup lang="ts">
import { onActivated, watch } from 'vue';
import { useAppStore } from '@/store/modules/app';
import { useEcharts } from '@/hooks/common/echarts';
import { $t } from '@/locales';

defineOptions({ name: 'PieChart' });

const appStore = useAppStore();

const { domRef, updateOptions } = useEcharts(
  () => ({
  tooltip: {
    trigger: 'item'
  },
  legend: {
    bottom: '1%',
    left: 'center',
    itemStyle: {
      borderWidth: 0
    }
  },
  series: [
    {
      color: ['#FFA500', '#FFB6C1', '#D2B48C', '#98D8C8'], // 橙色、浅粉色、浅棕色、青绿色
      name: $t('page.home.schedule'),
      type: 'pie',
      radius: ['45%', '75%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 1
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: '12'
        }
      },
      labelLine: {
        show: false
      },
      data: [] as { name: string; value: number }[]
    }
  ]
  }),
  {
    // 首次渲染后再拉数据：避免接口先返回时 updateOptions 因 chart 未就绪而直接 return
    onRender() {
      loadChartData();
    }
  }
);

async function loadChartData() {
  try {
    const { fetchGetHomeStatistics } = await import('@/service/api');
    const result = await fetchGetHomeStatistics();
    const data = (result as any)?.data ?? result;
    if (data?.pieChart?.data) {
      updateOptions(opts => {
        opts.series[0].data = data.pieChart.data;
        return opts;
      });
    }
  } catch (error) {
    console.error('获取饼图数据失败:', error);
    // 如果接口失败，使用默认数据
    updateOptions(opts => {
      opts.series[0].data = [
        { name: $t('page.home.study'), value: 35 },
        { name: $t('page.home.work'), value: 45 },
        { name: $t('page.home.rest'), value: 15 },
        { name: $t('page.home.entertainment'), value: 5 }
      ];
      return opts;
    });
  }
}

function updateLocale() {
  updateOptions((opts, factory) => {
    const originOpts = factory();

    opts.series[0].name = originOpts.series[0].name;

    // 重新加载数据以更新语言
    loadChartData();

    return opts;
  });
}

watch(
  () => appStore.locale,
  () => {
    updateLocale();
  }
);

onActivated(() => {
  loadChartData();
});
</script>

<template>
  <ElCard class="card-wrapper">
    <div ref="domRef" class="h-360px overflow-hidden"></div>
  </ElCard>
</template>

<style scoped></style>
