<script setup lang="tsx">
import { ref } from 'vue';
import {
  ElButton,
  ElPopconfirm,
  ElTag,
  ElMessage,
  ElMessageBox,
  ElCard,
  ElTable,
  ElTableColumn,
  ElPagination,
  ElRate,
  ElImage
} from 'element-plus';
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
    {
      prop: 'replyContent',
      label: '商家回复',
      minWidth: 160,
      showOverflowTooltip: true,
      formatter: row => row.replyContent || '—'
    },
    { prop: 'replyTime', label: '回复时间', width: 180 },
    {
      prop: 'images',
      label: '评价图片',
      align: 'center',
      width: 180,
      formatter: row => {
        if (!row.images || row.images.trim() === '') {
          return <span style="color: #999;">无图片</span>;
        }
        // 解析图片URL（可能是逗号分隔的字符串）
        const imageUrls = row.images.split(',').filter((url: string) => url.trim() !== '');
        if (imageUrls.length === 0) {
          return <span style="color: #999;">无图片</span>;
        }
        // 处理图片URL：如果是相对路径，拼接基础URL
        const baseURL = import.meta.env.VITE_SERVICE_BASE_URL || '';
        const processedUrls = imageUrls.map((url: string) => {
          const trimmedUrl = url.trim();
          if (trimmedUrl.startsWith('/') && !trimmedUrl.startsWith('http')) {
            return baseURL + trimmedUrl;
          }
          return trimmedUrl;
        });
        // 显示前3张图片作为缩略图，点击可预览
        return (
          <div class="flex-center gap-8px flex-wrap">
            {processedUrls.slice(0, 3).map((url: string, index: number) => (
              <ElImage
                key={index}
                src={url}
                preview-src-list={processedUrls}
                style="width: 50px; height: 50px; border-radius: 4px; cursor: pointer;"
                fit="cover"
                preview-teleported
                lazy
              />
            ))}
            {processedUrls.length > 3 && (
              <span style="color: #999; font-size: 12px;">+{processedUrls.length - 3}</span>
            )}
          </div>
        );
      }
    },
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
      width: 220,
      formatter: row => (
        <div class="flex-center gap-8px">
          <ElButton type="primary" plain size="small" onClick={() => handleReply(row)}>
            回复
          </ElButton>
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

async function handleReply(row: Api.Bath.Review) {
  const reviewId = row.reviewId;
  if (reviewId === undefined || reviewId === null) return;
  try {
    const { value } = await ElMessageBox.prompt('请输入对用户的回复内容', '回复评价', {
      confirmButtonText: '提交',
      cancelButtonText: '取消',
      inputType: 'textarea',
      inputValue: row.replyContent ?? '',
      inputPlaceholder: '商家回复将展示给用户',
      inputValidator: val => {
        if (!val || !String(val).trim()) return '请输入回复内容';
        return true;
      }
    });
    await fetchReplyReview(reviewId, String(value).trim());
    ElMessage.success('回复成功');
    getData();
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e);
      ElMessage.error('回复失败');
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
            :show-add="false"
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

