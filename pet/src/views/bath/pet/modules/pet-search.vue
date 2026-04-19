<script setup lang="tsx">
import { ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton } from 'element-plus';
import { $t } from '@/locales';

defineOptions({ name: 'PetSearch' });

interface Props {
  modelValue: {
    current: number;
    size: number;
    petName?: string;
    petBreed?: string;
    hairType?: string;
    userId?: number;
  };
}

const props = defineProps<Props>();

const emit = defineEmits<{
  'update:modelValue': [value: Props['modelValue']];
  search: [];
  reset: [];
}>();

const formData = ref({ ...props.modelValue });

function handleSearch() {
  emit('update:modelValue', { ...formData.value, current: 1 });
  emit('search');
}

function handleReset() {
  formData.value = {
    current: 1,
    size: 30,
    petName: undefined,
    petBreed: undefined,
    hairType: undefined,
    userId: undefined
  };
  emit('update:modelValue', { ...formData.value });
  emit('reset');
}
</script>

<template>
  <ElForm :model="formData" class="flex-y-center flex-wrap gap-12px">
    <ElFormItem label="宠物名称">
      <ElInput v-model="formData.petName" placeholder="请输入宠物名称" clearable @keyup.enter="handleSearch" />
    </ElFormItem>
    <ElFormItem label="品种">
      <ElInput v-model="formData.petBreed" placeholder="请输入品种" clearable @keyup.enter="handleSearch" />
    </ElFormItem>
    <ElFormItem label="毛发类型">
      <ElSelect v-model="formData.hairType" placeholder="请选择" clearable style="width: 150px">
        <ElOption label="短毛" value="0" />
        <ElOption label="长毛" value="1" />
      </ElSelect>
    </ElFormItem>
    <ElFormItem>
      <ElButton type="primary" @click="handleSearch">{$t('common.search')}</ElButton>
      <ElButton @click="handleReset">{$t('common.reset')}</ElButton>
    </ElFormItem>
  </ElForm>
</template>





