<script setup lang="tsx">
import { ref } from 'vue';
import { ElTag, ElCard, ElTable, ElTableColumn, ElPagination, ElButton, ElMessageBox, ElMessage } from 'element-plus';
import TableHeaderOperation from '@/components/advanced/table-header-operation.vue';
import { fetchGetBathOrderList, fetchPayOrder } from '@/service/api/bath';
import { useUIPaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import OrderSearch from './modules/order-search.vue';

defineOptions({ name: 'BathOrderManage' });

const searchParams = ref(getInitSearchParams());

function getInitSearchParams() {
  return {
    current: 1,
    size: 30,
    orderNo: undefined,
    userId: undefined,
    status: undefined
  };
}

const { columns, columnChecks, data, getData, loading, mobilePagination } = useUIPaginatedTable<
  any,
  Api.Bath.Order
>({
  paginationProps: {
    currentPage: searchParams.value.current || 1,
    pageSize: searchParams.value.size || 30
  },
  api: () => fetchGetBathOrderList(searchParams.value),
  transform: response => {
    const { data: responseData, error, response: axiosResponse } = response;
    const rawData = responseData || (axiosResponse?.data as any);
    
    if (!error && rawData) {
      return {
        data: (rawData.rows || []) as Api.Bath.Order[],
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
    { prop: 'orderNo', label: '订单号', minWidth: 150 },
    { prop: 'serviceName', label: '服务名称', minWidth: 150 },
    {
      prop: 'totalAmount',
      label: '订单金额',
      width: 120,
      formatter: row => {
        if (!row.totalAmount) return '¥0.00';
        return `¥${Number.parseFloat(String(row.totalAmount)).toFixed(2)}`;
      }
    },
    {
      prop: 'paidAmount',
      label: '已支付',
      width: 120,
      formatter: row => {
        if (!row.paidAmount) return '¥0.00';
        return `¥${Number.parseFloat(String(row.paidAmount)).toFixed(2)}`;
      }
    },
    {
      prop: 'payTime',
      label: '支付时间',
      width: 180,
      formatter: row => {
        if (!row.payTime) return '';
        const date = new Date(row.payTime);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return `${year}-${month}-${day} ${hours}:${minutes}`;
      }
    },
    {
      prop: 'status',
      label: '状态',
      align: 'center',
      width: 120,
      formatter: row => {
        const tagMap: Record<string, string> = {
          '0': 'warning',
          '1': 'success',
          '2': 'primary',
          '3': 'info',
          '4': 'danger',
          '5': 'danger'
        };
        const labelMap: Record<string, string> = {
          '0': '待支付',
          '1': '已支付',
          '2': '服务中',
          '3': '已完成',
          '4': '已退款',
          '5': '已取消'
        };
        return <ElTag type={tagMap[row.status] || 'info'}>{labelMap[row.status] || row.status}</ElTag>;
      }
    },
    {
      prop: 'operation',
      label: '操作',
      align: 'center',
      width: 150,
      fixed: 'right',
      formatter: row => {
        if (!row.orderId) return '';
        
        // 待支付状态：显示支付按钮
        if (row.status === '0') {
          return (
            <ElButton
              type="success"
              size="small"
              onClick={() => handlePayOrder(row.orderId!, row.totalAmount)}
            >
              支付
            </ElButton>
          );
        }
        
        return '';
      }
    },
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

// 支付订单
async function handlePayOrder(orderId: number, totalAmount: number | string) {
  try {
    const amount = typeof totalAmount === 'string' ? Number.parseFloat(totalAmount) : totalAmount;
    const amountText = amount ? `¥${amount.toFixed(2)}` : '订单金额';
    
    await ElMessageBox.confirm(`确认支付 ${amountText} 吗？`, '支付订单', {
      type: 'warning'
    });
    
    await fetchPayOrder(orderId, amount);
    ElMessage.success('订单支付成功');
    getData();
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error?.message || '支付失败');
    }
  }
}

</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <ElCard>
      <OrderSearch v-model:model="searchParams" @reset="handleReset" @search="handleSearch" />
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
          row-key="orderId"
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

