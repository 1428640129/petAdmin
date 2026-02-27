<script setup lang="ts">
import { ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton } from 'element-plus';
import { $t } from '@/locales';

defineOptions({ name: 'PaymentSearch' });

interface Props {
  model: {
    paymentNo?: string;
    orderNo?: string;
    paymentType?: string;
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
    paymentNo: undefined,
    orderNo: undefined,
    paymentType: undefined,
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
    <ElFormItem label="支付单号">
      <ElInput
        :model-value="props.model.paymentNo"
        @update:model-value="val => emit('update:model', { ...props.model, paymentNo: val })"
        placeholder="请输入支付单号"
        clearable
        @keyup.enter="handleSearch"
      />
    </ElFormItem>
    <ElFormItem label="订单号">
      <ElInput
        :model-value="props.model.orderNo"
        @update:model-value="val => emit('update:model', { ...props.model, orderNo: val })"
        placeholder="请输入订单号"
        clearable
        @keyup.enter="handleSearch"
      />
    </ElFormItem>
    <ElFormItem label="支付方式">
      <ElSelect
        :model-value="props.model.paymentType"
        @update:model-value="val => emit('update:model', { ...props.model, paymentType: val })"
        placeholder="请选择支付方式"
        clearable
        style="width: 150px"
      >
        <ElOption label="支付宝" value="alipay" />
        <ElOption label="微信" value="wechat" />
        <ElOption label="余额" value="balance" />
      </ElSelect>
    </ElFormItem>
    <ElFormItem label="状态">
      <ElSelect
        :model-value="props.model.status"
        @update:model-value="val => emit('update:model', { ...props.model, status: val })"
        placeholder="请选择状态"
        clearable
        style="width: 150px"
      >
        <ElOption label="待支付" value="pending" />
        <ElOption label="已支付" value="paid" />
        <ElOption label="支付失败" value="failed" />
        <ElOption label="已退款" value="refunded" />
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

