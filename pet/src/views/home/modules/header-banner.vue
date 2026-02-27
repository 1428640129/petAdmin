<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { fetchGetHomeStatistics } from '@/service/api'; 
import { useAppStore } from '@/store/modules/app';
import { useAuthStore } from '@/store/modules/auth';
import { $t } from '@/locales';

defineOptions({ name: 'HeaderBanner' });

const appStore = useAppStore();
const authStore = useAuthStore();

const gap = computed(() => (appStore.isMobile ? 0 : 16));

interface StatisticData {
  id: number;
  title: string;
  value: number;
  formatter?: (val: number) => string;
}

const statisticData = ref<StatisticData[]>([
  { id: 0, title: $t('page.home.projectCount'), value: 0 },
  { id: 1, title: $t('page.home.todo'), value: 0 },
  { id: 2, title: $t('page.home.message'), value: 0 }
]);

// 加载统计数据
const loadStatistics = async () => {
  try {
    const result = await fetchGetHomeStatistics();
    const data = (result as any)?.data ?? result;
    if (data) {
      statisticData.value = [
        { id: 0, title: $t('page.home.projectCount'), value: data.headerStats?.projectCount ?? 0 },
        { id: 1, title: $t('page.home.todo'), value: data.headerStats?.todoCount ?? 0 },
        { id: 2, title: $t('page.home.message'), value: data.headerStats?.messageCount ?? 0 }
      ];
    }
  } catch (error) {
    console.error('获取统计数据失败:', error);
  }
};

onMounted(() => {
  loadStatistics();
});
</script>

<template>
  <ElCard class="card-wrapper">
    <ElRow :gutter="gap" class="px-8px">
      <ElCol :md="18" :sm="24">
        <div class="flex-y-center">
          <div class="size-72px shrink-0 overflow-hidden rd-1/2">
            <img src="@/assets/imgs/soybean.jpg" class="size-full" />
          </div>
          <div class="pl-12px">
            <h3 class="text-18px font-semibold">
              {{ $t('page.home.greeting', { userName: authStore.userInfo.userName }) }}
            </h3>
            <p class="text-#999 leading-30px">{{ $t('page.home.weatherDesc') }}</p>
          </div>
        </div>
      </ElCol>
      <ElCol :md="6" :sm="24">
        <ElSpace direction="horizontal" class="w-full justify-end" :size="24">
          <ElStatistic v-for="item in statisticData" :key="item.id" class="whitespace-nowrap" v-bind="item" />
        </ElSpace>
      </ElCol>
    </ElRow>
  </ElCard>
</template>

<style scoped></style>
