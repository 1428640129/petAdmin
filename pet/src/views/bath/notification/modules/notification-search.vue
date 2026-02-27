<script setup lang="ts">
import { ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton } from 'element-plus';
import { $t } from '@/locales';

defineOptions({ name: 'NotificationSearch' });

interface Props {
  model: {
    userId?: number;
    notificationType?: string;
    isRead?: string;
  };
}

const props = defineProps<Props>();

const emit = defineEmits<{
  'update:model': [value: Props['model']];
  reset: [];
  search: [];
}>();

function handleReset() {
  emit('update:model', {
    userId: undefined,
    notificationType: undefined,
    isRead: undefined
  });
  emit('reset');
}

function handleSearch() {
  emit('search');
}
</script>

<template>
  <ElForm :model="props.model" :inline="true">
    <ElFormItem label="通知类型">
      <ElSelect
        :model-value="props.model.notificationType"
        @update:model-value="val => emit('update:model', { ...props.model, notificationType: val })"
        placeholder="请选择通知类型"
        clearable
        style="width: 180px"
      >
        <ElOption label="预约创建" value="appointment_created" />
        <ElOption label="预约确认" value="appointment_confirmed" />
        <ElOption label="服务开始" value="service_started" />
        <ElOption label="服务完成" value="service_completed" />
        <ElOption label="订单支付" value="order_paid" />
      </ElSelect>
    </ElFormItem>
    <ElFormItem label="是否已读">
      <ElSelect
        :model-value="props.model.isRead"
        @update:model-value="val => emit('update:model', { ...props.model, isRead: val })"
        placeholder="请选择状态"
        clearable
        style="width: 120px"
      >
        <ElOption label="未读" value="0" />
        <ElOption label="已读" value="1" />
      </ElSelect>
    </ElFormItem>
    <ElFormItem>
      <ElButton type="primary" @click="handleSearch">
        <template #icon>
          <icon-ic-round-search />
        </template>
        {{ $t('common.search') }}
      </ElButton>
      <ElButton @click="handleReset">
        <template #icon>
          <icon-ic-round-refresh />
        </template>
        {{ $t('common.reset') }}
      </ElButton>
    </ElFormItem>
  </ElForm>
</template>

