<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { fetchGetHomeStatistics, fetchGetProfile } from '@/service/api';
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

// 用户头像
const userAvatar = ref('');

// 获取头像URL
function getAvatarUrl() {
  if (!userAvatar.value) {
    return '';
  }
  // 如果是相对路径，需要拼接基础URL
  if (userAvatar.value.startsWith('/')) {
    const baseURL = import.meta.env.VITE_SERVICE_BASE_URL || '';
    return baseURL + userAvatar.value;
  }
  return userAvatar.value;
}

// 加载个人信息（获取头像）
const loadProfile = async () => {
  try {
    const { data, error } = await fetchGetProfile();
    if (!error && data && data.avatar) {
      userAvatar.value = data.avatar;
    }
  } catch {
    // 加载失败，静默处理
  }
};

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
  } catch {
    // 获取统计数据失败，静默处理
  }
};

onMounted(() => {
  loadStatistics();
  loadProfile();
});
</script>

<template>
  <ElCard class="card-wrapper">
    <ElRow :gutter="gap" class="px-8px">
      <ElCol :md="18" :sm="24">
        <div class="flex-y-center">
          <div class="size-72px shrink-0 overflow-hidden rd-1/2">
            <img v-if="getAvatarUrl()" :src="getAvatarUrl()" alt="头像" class="size-full object-cover" />
            <div v-else class="size-full flex items-center justify-center bg-gray-100">
              <SvgIcon icon="ph:user-circle" class="text-72px text-gray-400" />
            </div>
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
