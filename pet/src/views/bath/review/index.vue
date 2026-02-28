<script setup lang="tsx">
import { ref } from 'vue';
import { ElButton, ElPopconfirm, ElTag, ElMessage, ElCard, ElTable, ElTableColumn, ElPagination, ElRate } from 'element-plus';
import TableHeaderOperation from '@/components/advanced/table-header-operation.vue';
import { fetchGetBathReviewList, fetchDeleteBathReview, fetchReplyReview } from '@/service/api/bath';
import { useTableOperate, useUIPaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import ReviewSearch from './modules/review-search.vue';

defineOptions({ name: 'BathReviewManage' });

const searchParams = ref(getInitSearchParams());

function getInitSearchParams() {
  return {
    current: 1,
    size: 30,
    serviceId: undefined,
    rating: undefined,
    status: undefined
  };
}

const { columns, columnChecks, data, getData, loading, mobilePagination } = useUIPaginatedTable<
  any,
  Api.Bath.Review
>({
  paginationProps: {
    currentPage: searchParams.value.current || 1,
    pageSize: searchParams.value.size || 30
  },
  api: () => fetchGetBathReviewList(searchParams.value),
  transform: response => {
    const { data: responseData, error, response: axiosResponse } = response;
    const rawData = responseData || (axiosResponse?.data as any);
    
    if (!error && rawData) {
      return {
        data: (rawData.rows || []) as Api.Bath.Review[],
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
    {
      prop: 'rating',
      label: '评分',
      align: 'center',
      width: 120,
      formatter: row => (
        <ElRate model-value={row.rating} disabled show-score text-color="#ff9900" />
      )
    },
    { prop: 'content', label: '评论内容', minWidth: 200 },
    { prop: 'serviceName', label: '相关联服务名称', minWidth: 150 },
    { prop: 'createTime', label: '评价时间', width: 180 },
    {
      prop: 'status',
      label: '状态',
      align: 'center',
      width: 100,
      formatter: row => {
        const tagMap: Record<string, string> = {
          '0': 'success',
          '1': 'danger'
        };
        const labelMap: Record<string, string> = {
          '0': '正常',
          '1': '隐藏'
        };
        return <ElTag type={tagMap[row.status] || 'info'}>{labelMap[row.status] || row.status}</ElTag>;
      }
    },
    {
      prop: 'operate',
      label: $t('common.operate'),
      align: 'center',
      width: 180,
      formatter: row => (
        <div class="flex-center">
          <ElPopconfirm title={$t('common.confirmDelete')} onConfirm={() => handleDelete(row.reviewId)}>
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
} = useTableOperate<Api.Bath.Review>(data, 'reviewId', getData);

async function handleBatchDelete() {
  if (checkedRowKeys.value.length === 0) return;
  try {
    const ids = checkedRowKeys.value.map(key => Number(key));
    await fetchDeleteBathReview(ids);
    ElMessage.success('删除成功');
    onBatchDeleted();
  } catch (error) {
    console.error(error);
  }
}

async function handleReply(id: number) {
  const replyContent = prompt('请输入回复内容：');
  if (replyContent) {
    try {
      await fetchReplyReview(id, replyContent);
      ElMessage.success('回复成功');
      getData();
    } catch (error) {
      console.error(error);
    }
  }
}

async function handleDelete(id: number) {
  try {
    await fetchDeleteBathReview([id]);
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

function handleSelectionChange(selection: Api.Bath.Review[]) {
  checkedRowKeys.value = selection.map(row => String(row.reviewId));
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <ElCard>
      <ReviewSearch v-model:model="searchParams" @reset="handleReset" @search="handleSearch" />
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
          row-key="reviewId"
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

