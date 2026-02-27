<script setup lang="ts">
import { ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton } from 'element-plus';
import { $t } from '@/locales';

defineOptions({ name: 'ReviewSearch' });

interface Props {
  model: {
    serviceId?: number;
    rating?: number;
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
    serviceId: undefined,
    rating: undefined,
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
    <ElFormItem label="评分">
      <ElSelect
        :model-value="props.model.rating"
        @update:model-value="val => emit('update:model', { ...props.model, rating: val })"
        placeholder="请选择评分"
        clearable
        style="width: 150px"
      >
        <ElOption label="1星" :value="1" />
        <ElOption label="2星" :value="2" />
        <ElOption label="3星" :value="3" />
        <ElOption label="4星" :value="4" />
        <ElOption label="5星" :value="5" />
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
        <ElOption label="隐藏" value="1" />
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

