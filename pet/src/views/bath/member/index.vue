<script setup lang="tsx">
import { ref } from 'vue';
import { ElTag, ElCard, ElTable, ElTableColumn, ElPagination } from 'element-plus';
import TableHeaderOperation from '@/components/advanced/table-header-operation.vue';
import { fetchGetMemberInfoList } from '@/service/api/bath';
import { useUIPaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';

defineOptions({ name: 'BathMemberManage' });

const searchParams = ref({
  current: 1,
  size: 30,
  memberLevel: undefined,
  status: undefined
});

const { columns, columnChecks, data, getData, loading, mobilePagination } = useUIPaginatedTable<
  any,
  Api.Bath.MemberInfo
>({
  paginationProps: {
    currentPage: searchParams.value.current || 1,
    pageSize: searchParams.value.size || 30
  },
  api: () => fetchGetMemberInfoList(searchParams.value),
  transform: response => {
    const { data: responseData, error, response: axiosResponse } = response;
    const rawData = responseData || (axiosResponse?.data as any);
    
    if (!error && rawData) {
      return {
        data: (rawData.rows || []) as Api.Bath.MemberInfo[],
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
    { prop: 'userId', label: '用户ID', width: 100 },
    {
      prop: 'memberLevel',
      label: '会员等级',
      width: 120,
      formatter: (row: Api.Bath.MemberInfo) => {
        const levelMap: Record<string, { label: string; type: string }> = {
          '普通': { label: '普通', type: 'info' },
          '银卡': { label: '银卡', type: '' },
          '金卡': { label: '金卡', type: 'warning' },
          '钻石': { label: '钻石', type: 'success' }
        };
        const level = levelMap[row.memberLevel || '普通'] || levelMap['普通'];
        return <ElTag type={level.type as any}>{level.label}</ElTag>;
      }
    },
    { prop: 'points', label: '积分', width: 100 },
    { prop: 'totalConsumption', label: '累计消费(元)', width: 120 },
    {
      prop: 'status',
      label: '状态',
      width: 100,
      formatter: (row: Api.Bath.MemberInfo) => {
        return row.status === '0' ? <ElTag type="success">正常</ElTag> : <ElTag type="danger">过期</ElTag>;
      }
    }
  ]
});
</script>

<template>
  <div class="flex-col-stretch gap-16px <sm:gap-12px">
    <ElCard shadow="never" class="flex-1-hidden">
      <template #header>
        <div class="flex-y-center justify-between">
          <span class="text-18px font-500">会员管理</span>
        </div>
      </template>
      <TableHeaderOperation v-model:columns="columnChecks" :loading="loading" @refresh="getData" />
      <ElTable v-loading="loading" :data="data.list">
        <component :is="columns" />
      </ElTable>
      <ElPagination
        v-model:current-page="mobilePagination.currentPage"
        v-model:page-size="mobilePagination.pageSize"
        :total="mobilePagination.total"
        :page-sizes="[10, 30, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getData"
        @current-change="getData"
      />
    </ElCard>
  </div>
</template>





