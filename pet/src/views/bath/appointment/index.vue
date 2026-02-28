<script setup lang="tsx">
import { ref } from 'vue';
import { ElTag, ElCard, ElTable, ElTableColumn, ElPagination, ElButton, ElMessageBox, ElMessage } from 'element-plus';
import TableHeaderOperation from '@/components/advanced/table-header-operation.vue';
import { fetchGetBathAppointmentList, fetchConfirmAppointment, fetchCancelAppointment, fetchStartService, fetchCompleteService } from '@/service/api/bath';
import { useUIPaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import AppointmentSearch from './modules/appointment-search.vue';

defineOptions({ name: 'BathAppointmentManage' });

const searchParams = ref(getInitSearchParams());

function getInitSearchParams() {
  return {
    current: 1,
    size: 30,
    appointmentNo: undefined,
    petName: undefined,
    serviceId: undefined,
    status: undefined
  };
}

const { columns, columnChecks, data, getData, loading, mobilePagination } = useUIPaginatedTable<
  any,
  Api.Bath.Appointment
>({
  paginationProps: {
    currentPage: searchParams.value.current || 1,
    pageSize: searchParams.value.size || 30
  },
  api: () => fetchGetBathAppointmentList(searchParams.value),
  transform: response => {
    const { data: responseData, error, response: axiosResponse } = response;
    const rawData = responseData || (axiosResponse?.data as any);
    
    if (!error && rawData) {
      return {
        data: (rawData.rows || []) as Api.Bath.Appointment[],
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
    { prop: 'appointmentNo', label: '预约单号', minWidth: 150 },
    { prop: 'petName', label: '宠物名称', width: 120 },
    { prop: 'serviceName', label: '服务名称', minWidth: 150 },
    {
      prop: 'contactPhone',
      label: '联系电话',
      width: 130,
      formatter: row => {
        // 从备注中提取联系电话
        if (row.remark) {
          const phoneMatch = row.remark.match(/联系电话[：:]\s*(\d{11})/);
          if (phoneMatch && phoneMatch[1]) {
            return phoneMatch[1];
          }
        }
        return '-';
      }
    },
    {
      prop: 'appointmentTime',
      label: '预约时间',
      width: 180,
      formatter: row => {
        if (!row.appointmentTime) return '';
        const date = new Date(row.appointmentTime);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return `${year}-${month}-${day} ${hours}:${minutes}`;
      }
    },
    {
      prop: 'expectedPrice',
      label: '预计价格',
      width: 100,
      formatter: row => {
        if (!row.expectedPrice) return '¥0.00';
        return `¥${Number.parseFloat(String(row.expectedPrice)).toFixed(2)}`;
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
          '4': 'danger'
        };
        const labelMap: Record<string, string> = {
          '0': '待确认',
          '1': '已确认',
          '2': '服务中',
          '3': '已完成',
          '4': '已取消'
        };
        return <ElTag type={tagMap[row.status] || 'info'}>{labelMap[row.status] || row.status}</ElTag>;
      }
    },
    {
      prop: 'operation',
      label: '操作',
      align: 'center',
      width: 200,
      fixed: 'right',
      formatter: row => {
        if (!row.appointmentId) return '';
        
        const buttons: any[] = [];
        
        // 待确认状态：显示确认和取消按钮
        if (row.status === '0') {
          buttons.push(
            <ElButton
              type="success"
              size="small"
              onClick={() => handleConfirm(row.appointmentId!)}
            >
              确认
            </ElButton>
          );
          buttons.push(
            <ElButton
              type="danger"
              size="small"
              onClick={() => handleCancel(row.appointmentId!)}
            >
              取消
            </ElButton>
          );
        }
        // 已确认状态：显示开始服务按钮
        else if (row.status === '1') {
          buttons.push(
            <ElButton
              type="primary"
              size="small"
              onClick={() => handleStartService(row.appointmentId!)}
            >
              开始服务
            </ElButton>
          );
        }
        // 服务中状态：显示完成服务按钮
        else if (row.status === '2') {
          buttons.push(
            <ElButton
              type="success"
              size="small"
              onClick={() => handleCompleteService(row.appointmentId!)}
            >
              完成服务
            </ElButton>
          );
        }
        
        return <div style="display: flex; gap: 8px; justify-content: center;">{buttons}</div>;
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

// 确认预约
async function handleConfirm(appointmentId: number) {
  try {
    await ElMessageBox.confirm('确认接受这个预约吗？确认后将自动创建订单。', '确认预约', {
      type: 'warning'
    });
    
    await fetchConfirmAppointment(appointmentId);
    ElMessage.success('预约已确认');
    getData();
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error?.message || '确认预约失败');
    }
  }
}

// 取消预约
async function handleCancel(appointmentId: number) {
  try {
    const { value: cancelReason } = await ElMessageBox.prompt('请输入取消原因', '取消预约', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputType: 'textarea',
      inputPlaceholder: '请输入取消原因（选填）'
    });
    
    // 注意：后端接口需要传递cancelReason，但当前API可能不支持，先不传
    await fetchCancelAppointment(appointmentId);
    ElMessage.success('预约已取消');
    getData();
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error?.message || '取消预约失败');
    }
  }
}

// 开始服务
async function handleStartService(appointmentId: number) {
  try {
    await ElMessageBox.confirm('确认开始服务吗？', '开始服务', {
      type: 'warning'
    });
    
    await fetchStartService(appointmentId);
    ElMessage.success('服务已开始');
    getData();
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error?.message || '开始服务失败');
    }
  }
}

// 完成服务
async function handleCompleteService(appointmentId: number) {
  try {
    await ElMessageBox.confirm('确认完成服务吗？', '完成服务', {
      type: 'warning'
    });
    
    await fetchCompleteService(appointmentId);
    ElMessage.success('服务已完成');
    getData();
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error?.message || '完成服务失败');
    }
  }
}

</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <ElCard>
      <AppointmentSearch v-model:model="searchParams" @reset="handleReset" @search="handleSearch" />
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
          row-key="appointmentId"
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

