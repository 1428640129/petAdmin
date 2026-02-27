<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { createReusableTemplate } from '@vueuse/core';
import { $t } from '@/locales';
import { fetchGetHomeStatistics } from '@/service/api';

defineOptions({ name: 'CardData' });

interface CardData {
  key: string;
  title: string;
  value: number;
  unit: string;
  color: {
    start: string;
    end: string;
  };
  icon: string;
}

const cardData = ref<CardData[]>([
  {
    key: 'visitCount',
    title: $t('page.home.visitCount'),
    value: 0,
    unit: '',
    color: {
      start: '#FFD4A3', // 浅橙色
      end: '#FFA500' // 橙色
    },
    icon: 'mdi:calendar-clock'
  },
  {
    key: 'turnover',
    title: $t('page.home.turnover'),
    value: 0,
    unit: '元',
    color: {
      start: '#FFB6C1', // 浅粉色
      end: '#FF8C94' // 粉红色
    },
    icon: 'ant-design:money-collect-outlined'
  },
  {
    key: 'downloadCount',
    title: $t('page.home.downloadCount'),
    value: 0,
    unit: '',
    color: {
      start: '#D2B48C', // 浅棕色
      end: '#BC9A6A' // 中棕色
    },
    icon: 'mdi:clipboard-check'
  },
  {
    key: 'dealCount',
    title: $t('page.home.dealCount'),
    value: 0,
    unit: '',
    color: {
      start: '#98D8C8', // 柔和的青绿色
      end: '#6BC4A6' // 青绿色
    },
    icon: 'mdi:account-group'
  }
]);

// 加载卡片数据
const loadCardData = async () => {
  try {
    const result = await fetchGetHomeStatistics();
    const data = (result as any)?.data ?? result;
    if (data?.cardData) {
      const card = data.cardData;
      cardData.value = [
        {
          key: 'visitCount',
          title: $t('page.home.visitCount'),
          value: card.visitCount || 0,
          unit: '',
          color: {
            start: '#FFD4A3',
            end: '#FFA500'
          },
          icon: 'mdi:calendar-clock'
        },
        {
          key: 'turnover',
          title: $t('page.home.turnover'),
          value: Math.round(card.turnover || 0),
          unit: '元',
          color: {
            start: '#FFB6C1',
            end: '#FF8C94'
          },
          icon: 'ant-design:money-collect-outlined'
        },
        {
          key: 'downloadCount',
          title: $t('page.home.downloadCount'),
          value: card.downloadCount || 0,
          unit: '',
          color: {
            start: '#D2B48C',
            end: '#BC9A6A'
          },
          icon: 'mdi:clipboard-check'
        },
        {
          key: 'dealCount',
          title: $t('page.home.dealCount'),
          value: card.dealCount || 0,
          unit: '',
          color: {
            start: '#98D8C8',
            end: '#6BC4A6'
          },
          icon: 'mdi:account-group'
        }
      ];
    }
  } catch (error) {
    console.error('获取卡片数据失败:', error);
  }
};

onMounted(() => {
  loadCardData();
});

interface GradientBgProps {
  gradientColor: string;
}

const [DefineGradientBg, GradientBg] = createReusableTemplate<GradientBgProps>();

function getGradientColor(color: CardData['color']) {
  return `linear-gradient(to bottom right, ${color.start}, ${color.end})`;
}
</script>

<template>
  <ElCard class="card-wrapper">
    <!-- define component start: GradientBg -->
    <DefineGradientBg v-slot="{ $slots, gradientColor }">
      <div class="rd-8px px-16px pb-4px pt-8px text-white" :style="{ backgroundImage: gradientColor }">
        <component :is="$slots.default" />
      </div>
    </DefineGradientBg>
    <!-- define component end: GradientBg -->
    <ElRow :gutter="16">
      <ElCol v-for="item in cardData" :key="item.key" :lg="6" :md="12" :sm="24" class="my-8px">
        <GradientBg :gradient-color="getGradientColor(item.color)" class="flex-1">
          <h3 class="text-16px">{{ item.title }}</h3>
          <div class="flex justify-between pt-12px">
            <SvgIcon :icon="item.icon" class="text-32px" />
            <CountTo
              :suffix="item.unit"
              :start-value="1"
              :end-value="item.value"
              class="text-30px text-white dark:text-dark"
            />
          </div>
        </GradientBg>
      </ElCol>
    </ElRow>
  </ElCard>
</template>

<style scoped></style>
