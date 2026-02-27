<script setup lang="tsx">
import { ref } from 'vue';
import { ElButton, ElPopconfirm, ElTag, ElMessage, ElCard, ElTable, ElTableColumn, ElPagination } from 'element-plus';
import TableHeaderOperation from '@/components/advanced/table-header-operation.vue';
import { fetchGetBathNotificationList, fetchDeleteBathNotification } from '@/service/api/bath';
import { useTableOperate, useUIPaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import NotificationSearch from './modules/notification-search.vue';

defineOptions({ name: 'BathNotificationManage' });

const searchParams = ref(getInitSearchParams());

function getInitSearchParams() {
  return {
    current: 1,
    size: 30,
    userId: undefined,
    notificationType: undefined,
    isRead: undefined
  };
}

const { columns, columnChecks, data, getData, loading, mobilePagination } = useUIPaginatedTable<
  any,
  Api.Bath.Notification
>({
  paginationProps: {
    currentPage: searchParams.value.current || 1,
    pageSize: searchParams.value.size || 30
  },
  api: () => fetchGetBathNotificationList(searchParams.value),
  transform: response => {
    const { data: responseData, error, response: axiosResponse } = response;
    const rawData = responseData || (axiosResponse?.data as any);
    
    if (!error && rawData) {
      return {
        data: (rawData.rows || []) as Api.Bath.Notification[],
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
    { prop: 'title', label: '通知标题', minWidth: 150 },
    { prop: 'content', label: '通知内容', minWidth: 200 },
    { prop: 'notificationType', label: '通知类型', width: 150 },
    {
      prop: 'isRead',
      label: '是否已读',
      align: 'center',
      width: 100,
      formatter: row => {
        const tagMap: Record<string, string> = {
          '0': 'warning',
          '1': 'success'
        };
        const labelMap: Record<string, string> = {
          '0': '未读',
          '1': '已读'
        };
        return <ElTag type={tagMap[row.isRead] || 'info'}>{labelMap[row.isRead] || row.isRead}</ElTag>;
      }
    },
    { prop: 'createTime', label: '创建时间', width: 180 },
    {
      prop: 'operate',
      label: $t('common.operate'),
      align: 'center',
      width: 130,
      formatter: row => (
        <div class="flex-center">
          <ElPopconfirm title={$t('common.confirmDelete')} onConfirm={() => handleDelete(row.notificationId)}>
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
  checkedRowKeys,
  onBatchDeleted,
  onDeleted
} = useTableOperate<Api.Bath.Notification>(data, 'notificationId', getData);

async function handleBatchDelete() {
  if (checkedRowKeys.value.length === 0) return;
  try {
    const ids = checkedRowKeys.value.map(key => Number(key));
    await fetchDeleteBathNotification(ids);
    ElMessage.success('删除成功');
    onBatchDeleted();
  } catch (error) {
    console.error(error);
  }
}

async function handleDelete(id: number) {
  try {
    await fetchDeleteBathNotification([id]);
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

function handleSelectionChange(selection: Api.Bath.Notification[]) {
  checkedRowKeys.value = selection.map(row => String(row.notificationId));
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <ElCard>
      <NotificationSearch v-model:model="searchParams" @reset="handleReset" @search="handleSearch" />
    </ElCard>
    <ElCard class="card-wrapper sm:flex-1-hidden">
      <template #header>
        <div class="flex items-center justify-right">
          <TableHeaderOperation
            v-model:columns="columnChecks"
            :disabled-delete="checkedRowKeys.length === 0"
            :loading="loading"
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
          row-key="notificationId"
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

