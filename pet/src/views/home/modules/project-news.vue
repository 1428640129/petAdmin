<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { fetchGetHomeStatistics } from '@/service/api';
import { $t } from '@/locales';

defineOptions({ name: 'ProjectNews' });

const router = useRouter();

interface NewsItem {
  id: number;
  content: string;
  time: string;
  avatar?: string;
}

const newses = ref<NewsItem[]>([]);

// 获取头像URL
function getAvatarUrl(avatar?: string) {
  if (!avatar) {
    return '';
  }
  // 如果是相对路径，需要拼接基础URL
  if (avatar.startsWith('/')) {
    const baseURL = import.meta.env.VITE_SERVICE_BASE_URL || '';
    return baseURL + avatar;
  }
  return avatar;
}

// 加载服务动态信息（接口返回的 serviceNews，无数据时显示示例）
const loadServiceNews = async () => {
  try {
    const result = await fetchGetHomeStatistics();
    // request 封装后返回 statistics 对象，可能在 result 或 result.data 中
    const data = (result as any)?.data ?? result;
    if (data && Array.isArray(data.serviceNews) && data.serviceNews.length > 0) {
      newses.value = data.serviceNews;
    } else {
      // 无数据时显示示例动态
      const descs = [
        $t('page.home.projectNews.desc1'),
        $t('page.home.projectNews.desc2'),
        $t('page.home.projectNews.desc3'),
        $t('page.home.projectNews.desc4'),
        $t('page.home.projectNews.desc5')
      ];
      newses.value = descs.map((content, i) => ({
        id: i + 1,
        content,
        time: new Date().toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
      }));
    }
  } catch {
    // 接口失败时显示示例
    const descs = [
      $t('page.home.projectNews.desc1'),
      $t('page.home.projectNews.desc2'),
      $t('page.home.projectNews.desc3')
    ];
    newses.value = descs.map((content, i) => ({
      id: i + 1,
      content,
      time: new Date().toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
    }));
  }
};

onMounted(() => {
  loadServiceNews();
});

function handleMoreNews() {
  router.push('/bath/appointment');
}
</script>

<template>
  <ElCard class="card-wrapper">
    <template #header>
      <ElRow>
        <ElCol :span="18">{{ $t('page.home.projectNews.title') }}</ElCol>
        <ElCol :span="6" class="text-right">
          <a class="text-primary cursor-pointer" @click="handleMoreNews">
            {{ $t('page.home.projectNews.moreNews') }}
          </a>
        </ElCol>
      </ElRow>
    </template>
    <ElTimeline v-if="newses.length">
      <ElTimelineItem v-for="item in newses" :key="item.id" :timestamp="item.time" placement="top">
        <ElSpace>
          <div class="size-48px shrink-0 overflow-hidden rd-1/2">
            <img
              v-if="getAvatarUrl(item.avatar)"
              :src="getAvatarUrl(item.avatar)"
              alt="用户头像"
              class="size-full object-cover"
            />
            <div v-else class="size-full flex items-center justify-center bg-gray-100">
              <SvgIcon icon="ph:user-circle" class="text-48px text-gray-400" />
            </div>
          </div>
          <p>{{ item.content }}</p>
        </ElSpace>
      </ElTimelineItem>
    </ElTimeline>
    <div v-else class="py-16px text-center text-#999">
      暂无服务动态
    </div>
  </ElCard>
</template>

<style scoped></style>
