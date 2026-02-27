<script setup lang="ts">
import { ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton } from 'element-plus';
import { $t } from '@/locales';

defineOptions({ name: 'AppointmentSearch' });

interface Props {
  model: {
    appointmentNo?: string;
    petName?: string;
    serviceId?: number;
    status?: string;
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
    appointmentNo: undefined,
    petName: undefined,
    serviceId: undefined,
    status: undefined
  });
  emit('reset');
}

function handleSearch() {
  emit('search');
}
</script>

<template>
  <ElForm :model="props.model" :inline="true">
    <ElFormItem label="预约单号">
      <ElInput
        :model-value="props.model.appointmentNo"
        @update:model-value="val => emit('update:model', { ...props.model, appointmentNo: val })"
        placeholder="请输入预约单号"
        clearable
        @keyup.enter="handleSearch"
      />
    </ElFormItem>
    <ElFormItem label="宠物名称">
      <ElInput
        :model-value="props.model.petName"
        @update:model-value="val => emit('update:model', { ...props.model, petName: val })"
        placeholder="请输入宠物名称"
        clearable
        @keyup.enter="handleSearch"
      />
    </ElFormItem>
    <ElFormItem label="状态">
      <ElSelect
        :model-value="props.model.status"
        @update:model-value="val => emit('update:model', { ...props.model, status: val })"
        placeholder="请选择状态"
        clearable
        style="width: 150px"
      >
        <ElOption label="待确认" value="pending" />
        <ElOption label="已确认" value="confirmed" />
        <ElOption label="服务中" value="in_service" />
        <ElOption label="已完成" value="completed" />
        <ElOption label="已取消" value="cancelled" />
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

