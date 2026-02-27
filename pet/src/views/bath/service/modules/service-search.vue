<script setup lang="ts">
import { ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton } from 'element-plus';
import { $t } from '@/locales';

defineOptions({ name: 'ServiceSearch' });

interface Props {
  model: {
    serviceName?: string;
    serviceType?: string;
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
    serviceName: undefined,
    serviceType: undefined,
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
    <ElFormItem label="服务名称">
      <ElInput
        :model-value="props.model.serviceName"
        @update:model-value="val => emit('update:model', { ...props.model, serviceName: val })"
        placeholder="请输入服务名称"
        clearable
        @keyup.enter="handleSearch"
      />
    </ElFormItem>
    <ElFormItem label="服务类型">
      <ElSelect
        :model-value="props.model.serviceType"
        @update:model-value="val => emit('update:model', { ...props.model, serviceType: val })"
        placeholder="请选择服务类型"
        clearable
        style="width: 150px"
      >
        <ElOption label="基础服务" value="基础服务" />
        <ElOption label="深度服务" value="深度服务" />
        <ElOption label="豪华服务" value="豪华服务" />
      </ElSelect>
    </ElFormItem>
    <ElFormItem label="状态">
      <ElSelect
        :model-value="props.model.status"
        @update:model-value="val => emit('update:model', { ...props.model, status: val })"
        placeholder="请选择状态"
        clearable
        style="width: 120px"
      >
        <ElOption label="正常" value="0" />
        <ElOption label="停用" value="1" />
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

