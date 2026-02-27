<script setup lang="tsx">
import { ref } from 'vue';
import { ElButton, ElPopconfirm, ElTag, ElMessage, ElCard, ElTable, ElTableColumn, ElPagination } from 'element-plus';
import TableHeaderOperation from '@/components/advanced/table-header-operation.vue';
import { fetchGetBathServiceList, fetchAddBathService, fetchUpdateBathService, fetchDeleteBathService } from '@/service/api/bath';
import { useTableOperate, useUIPaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import ServiceOperateDrawer from './modules/service-operate-drawer.vue';
import ServiceSearch from './modules/service-search.vue';

defineOptions({ name: 'BathServiceManage' });

const searchParams = ref(getInitSearchParams());

function getInitSearchParams() {
  return {
    current: 1,
    size: 30,
    serviceName: undefined,
    serviceType: undefined,
    status: undefined
  };
}

const { columns, columnChecks, data, getData, loading, mobilePagination } = useUIPaginatedTable<
  any,
  Api.Bath.Service
>({
  paginationProps: {
    currentPage: searchParams.value.current || 1,
    pageSize: searchParams.value.size || 30
  },
  api: () => fetchGetBathServiceList(searchParams.value),
  transform: response => {
    const { data: responseData, error, response: axiosResponse } = response;
    // 如果 transform 返回的数据为空（因为后端没有嵌套的 data 字段），直接从原始响应中获取
    const rawData = responseData || (axiosResponse?.data as any);
    
    if (!error && rawData) {
      // 后端返回格式: { code, msg, rows, total }
      // 前端期望格式: { data: records, pageNum: current, pageSize: size, total }
      return {
        data: (rawData.rows || []) as Api.Bath.Service[],
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
    { prop: 'selection', type: 'selection', width: 48 },
    { prop: 'index', type: 'index', label: $t('common.index'), width: 64 },
    { prop: 'serviceName', label: '服务名称', minWidth: 150 },
    { prop: 'serviceType', label: '服务类型', width: 120 },
    { prop: 'serviceDesc', label: '服务描述', minWidth: 200 },
    { prop: 'duration', label: '服务时长(分钟)', width: 120 },
    {
      prop: 'status',
      label: '状态',
      align: 'center',
      width: 100,
      formatter: (row: Api.Bath.Service) => {
        const tagMap: Record<string, 'success' | 'danger'> = {
          '0': 'success',
          '1': 'danger'
        };
        const labelMap: Record<string, string> = {
          '0': '正常',
          '1': '停用'
        };
        const status = row.status || '0';
        return <ElTag type={tagMap[status]}>{labelMap[status]}</ElTag>;
      }
    },
    {
      prop: 'operate',
      label: $t('common.operate'),
      align: 'center',
      width: 180,
      formatter: (row: Api.Bath.Service) => (
        <div class="flex-center">
          <ElButton type="primary" plain size="small" onClick={() => edit(row.serviceId!)}>
            {$t('common.edit')}
          </ElButton>
          <ElPopconfirm title={$t('common.confirmDelete')} onConfirm={() => handleDelete(row.serviceId!)}>
            {{
              reference: () => (
                <ElButton type="danger" plain size="small">
                  {$t('common.delete')}
                </ElButton>
              )
            }}
          </ElPopconfirm>
        </div>
      )
    }
  ]
});

const {
  drawerVisible,
  operateType,
  editingData,
  handleAdd,
  handleEdit,
  checkedRowKeys,
  onBatchDeleted,
  onDeleted
} = useTableOperate<Api.Bath.Service>(data, 'serviceId', getData);

async function handleBatchDelete() {
  if (checkedRowKeys.value.length === 0) return;
  try {
    const ids = checkedRowKeys.value.map(key => Number(key));
    await fetchDeleteBathService(ids);
    ElMessage.success('删除成功');
    onBatchDeleted();
  } catch (error) {
    console.error(error);
  }
}

function edit(id: number) {
  handleEdit(id);
}

async function handleDelete(id: number) {
  try {
    await fetchDeleteBathService([id]);
    ElMessage.success('删除成功');
    onDeleted();
  } catch (error) {
    console.error(error);
  }
}

function handleReset() {
  Object.assign(searchParams.value, getInitSearchParams());
  getData();
}

function handleSearch() {
  searchParams.value.current = 1;
  getData();
}

function handleSelectionChange(selection: Api.Bath.Service[]) {
  checkedRowKeys.value = selection.map(row => String(row.serviceId));
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <ElCard>
      <ServiceSearch v-model:model="searchParams" @reset="handleReset" @search="handleSearch" />
    </ElCard>
    <ElCard class="card-wrapper sm:flex-1-hidden">
      <template #header>
        <div class="flex items-center justify-right">
          <TableHeaderOperation
            v-model:columns="columnChecks"
            :disabled-delete="checkedRowKeys.length === 0"
            :loading="loading"
            @add="handleAdd"
            @delete="handleBatchDelete"
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
          row-key="serviceId"
          @selection-change="handleSelectionChange"
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
      <ServiceOperateDrawer
        v-model:visible="drawerVisible"
        :operate-type="operateType"
        :row-data="editingData"
        @submitted="getData"
      />
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

