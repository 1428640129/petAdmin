<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { $t } from '@/locales';

defineOptions({ name: 'CreativityBanner' });

const router = useRouter();

interface QuickAccessItem {
  id: number;
  name: string;
  desc: string;
  icon: string;
  color: string;
  path: string;
}

const quickAccessList = computed<QuickAccessItem[]>(() => [
  {
    id: 1,
    name: $t('page.home.quickAccess.appointment'),
    desc: $t('page.home.quickAccess.appointmentDesc'),
    icon: 'mdi:calendar-clock',
    color: '#FFA500',
    path: '/bath/appointment'
  },
  {
    id: 2,
    name: $t('page.home.quickAccess.order'),
    desc: $t('page.home.quickAccess.orderDesc'),
    icon: 'mdi:clipboard-list',
    color: '#FF8C94',
    path: '/bath/order'
  },
  {
    id: 3,
    name: $t('page.home.quickAccess.service'),
    desc: $t('page.home.quickAccess.serviceDesc'),
    icon: 'mdi:cog',
    color: '#D2B48C',
    path: '/bath/service'
  },
  {
    id: 4,
    name: $t('page.home.quickAccess.review'),
    desc: $t('page.home.quickAccess.reviewDesc'),
    icon: 'mdi:star',
    color: '#98D8C8',
    path: '/bath/review'
  }
]);

function handleClick(item: QuickAccessItem) {
  router.push(item.path);
}
</script>

<template>
  <ElCard :header="$t('page.home.creativity')" class="h-full card-wrapper">
    <div class="h-full flex-col-stretch gap-12px">
      <div
        v-for="item in quickAccessList"
        :key="item.id"
        class="flex-y-center gap-12px p-16px rd-8px border-1px border-#e5e7eb hover:bg-#f9fafb hover:border-primary cursor-pointer transition-all"
        @click="handleClick(item)"
      >
        <div
          class="size-48px shrink-0 flex-center rd-1/2 text-white"
          :style="{ backgroundColor: item.color }"
        >
          <SvgIcon :icon="item.icon" class="text-24px" />
        </div>
        <div class="flex-1 min-w-0">
          <h4 class="text-16px font-semibold mb-4px">{{ item.name }}</h4>
          <p class="text-12px text-#999 line-clamp-2">{{ item.desc }}</p>
        </div>
      </div>
    </div>
  </ElCard>
</template>

<style scoped></style>
