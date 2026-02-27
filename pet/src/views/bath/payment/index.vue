<script setup lang="tsx">
import { ref } from 'vue';
import { ElTag, ElCard, ElTable, ElTableColumn, ElPagination } from 'element-plus';
import TableHeaderOperation from '@/components/advanced/table-header-operation.vue';
import { fetchGetBathPaymentList } from '@/service/api/bath';
import { useUIPaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import PaymentSearch from './modules/payment-search.vue';

defineOptions({ name: 'BathPaymentManage' });

const searchParams = ref(getInitSearchParams());

function getInitSearchParams() {
  return {
    current: 1,
    size: 30,
    paymentNo: undefined,
    orderNo: undefined,
    paymentType: undefined,
    status: undefined
  };
}

const { columns, columnChecks, data, getData, loading, mobilePagination } = useUIPaginatedTable<
  any,
  Api.Bath.Payment
>({
  paginationProps: {
    currentPage: searchParams.value.current || 1,
    pageSize: searchParams.value.size || 30
  },
  api: () => fetchGetBathPaymentList(searchParams.value),
  transform: response => {
    const { data: responseData, error, response: axiosResponse } = response;
    const rawData = responseData || (axiosResponse?.data as any);
    
    if (!error && rawData) {
      return {
        data: (rawData.rows || []) as Api.Bath.Payment[],
        pageNum: searchParams.value.current || 1,
        pageSize: searchParams.value.size || 30,
        total: rawData.total || 0
      };
    }
    return {
      data: [],
      pageNum: 1,
      pageSize: 30,
      total: 0
    };
  },
  onPaginationParamsChange: params => {
    searchParams.value.current = params.currentPage || 1;
    searchParams.value.size = params.pageSize || 30;
  },
  columns: () => [
    { prop: 'index', type: 'index', label: $t('common.index'), width: 64 },
    { prop: 'paymentNo', label: '支付单号', minWidth: 150 },
    { prop: 'orderNo', label: '订单号', minWidth: 150 },
    { prop: 'paymentAmount', label: '支付金额', width: 120 },
    {
      prop: 'paymentType',
      label: '支付方式',
      width: 120,
      formatter: row => {
        const tagMap: Record<string, string> = {
          'alipay': 'primary',
          'wechat': 'success',
          'balance': 'info'
        };
        const labelMap: Record<string, string> = {
          'alipay': '支付宝',
          'wechat': '微信',
          'balance': '余额'
        };
        return <ElTag type={tagMap[row.paymentType] || 'info'}>{labelMap[row.paymentType] || row.paymentType}</ElTag>;
      }
    },
    {
      prop: 'status',
      label: '状态',
      align: 'center',
      width: 120,
      formatter: row => {
        const tagMap: Record<string, string> = {
          'pending': 'warning',
          'paid': 'success',
          'failed': 'danger',
          'refunded': 'info'
        };
        const labelMap: Record<string, string> = {
          'pending': '待支付',
          'paid': '已支付',
          'failed': '支付失败',
          'refunded': '已退款'
        };
        return <ElTag type={tagMap[row.status] || 'info'}>{labelMap[row.status] || row.status}</ElTag>;
      }
    },
    { prop: 'payTime', label: '支付时间', width: 180 },
  ]
});


function handleReset() {
  Object.assign(searchParams.value, getInitSearchParams());
  getData();
}

function handleSearch() {
  searchParams.value.current = 1;
  getData();
}

</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <ElCard>
      <PaymentSearch v-model:model="searchParams" @reset="handleReset" @search="handleSearch" />
    </ElCard>
    <ElCard class="card-wrapper sm:flex-1-hidden">
      <template #header>
        <div class="flex items-center justify-right">
          <TableHeaderOperation
            v-model:columns="columnChecks"
            :loading="loading"
            @refresh="getData"
          />
        </div>
      </template>
      <div class="h-[calc(100%-52px)]">
        <ElTable
          v-loading="loading"
          height="100%"
          border
          class="sm:h-full"
          :data="data"
          row-key="paymentId"
        >
          <ElTableColumn v-for="col in columns" :key="col.prop" v-bind="col" />
        </ElTable>
      </div>
      <div class="mt-20px flex justify-end">
        <ElPagination
          v-if="mobilePagination.total"
          layout="total,prev,pager,next,sizes"
          v-bind="mobilePagination"
          @current-change="mobilePagination['current-change']"
          @size-change="mobilePagination['size-change']"
        />
      </div>
    </ElCard>
  </div>
</template>

<style lang="scss" scoped>
:deep(.el-card) {
  .ht50 {
    height: calc(100% - 50px);
  }
}
</style>

