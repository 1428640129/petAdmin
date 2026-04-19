<script setup lang="tsx">
import { ref } from 'vue';
import { ElButton, ElPopconfirm, ElTag, ElMessage, ElCard, ElTable, ElTableColumn, ElPagination } from 'element-plus';
import TableHeaderOperation from '@/components/advanced/table-header-operation.vue';
import { fetchGetPetProfileList, fetchAddPetProfile, fetchUpdatePetProfile, fetchDeletePetProfile } from '@/service/api/bath';
import { useTableOperate, useUIPaginatedTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import PetOperateDrawer from './modules/pet-operate-drawer.vue';
import PetSearch from './modules/pet-search.vue';

defineOptions({ name: 'BathPetManage' });

const searchParams = ref(getInitSearchParams());

function getInitSearchParams() {
  return {
    current: 1,
    size: 30,
    petName: undefined,
    petBreed: undefined,
    hairType: undefined,
    userId: undefined
  };
}

const { columns, columnChecks, data, getData, loading, mobilePagination } = useUIPaginatedTable<
  any,
  Api.Bath.PetProfile
>({
  paginationProps: {
    currentPage: searchParams.value.current || 1,
    pageSize: searchParams.value.size || 30
  },
  api: () => fetchGetPetProfileList(searchParams.value),
  transform: response => {
    const { data: responseData, error, response: axiosResponse } = response;
    const rawData = responseData || (axiosResponse?.data as any);
    
    if (!error && rawData) {
      return {
        data: (rawData.rows || []) as Api.Bath.PetProfile[],
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
    { prop: 'petName', label: '宠物名称', minWidth: 120 },
    { prop: 'petBreed', label: '品种', width: 120 },
    { prop: 'petAge', label: '年龄(月)', width: 100 },
    {
      prop: 'petSex',
      label: '性别',
      width: 80,
      formatter: (row: Api.Bath.PetProfile) => {
        const sexMap: Record<string, string> = {
          '0': '公',
          '1': '母',
          '2': '未知'
        };
        return sexMap[row.petSex || ''] || '-';
      }
    },
    { prop: 'petWeight', label: '体重(kg)', width: 100 },
    {
      prop: 'hairType',
      label: '毛发类型',
      width: 100,
      formatter: (row: Api.Bath.PetProfile) => {
        const typeMap: Record<string, string> = {
          '0': '短毛',
          '1': '长毛'
        };
        return typeMap[row.hairType || ''] || '-';
      }
    },
    {
      prop: 'isDefault',
      label: '默认',
      align: 'center',
      width: 80,
      formatter: (row: Api.Bath.PetProfile) => {
        return row.isDefault === '1' ? <ElTag type="success">是</ElTag> : <ElTag>否</ElTag>;
      }
    },
    {
      prop: 'operate',
      label: $t('common.operate'),
      align: 'center',
      width: 180,
      fixed: 'right',
      formatter: (row: Api.Bath.PetProfile) => {
        return (
          <div class="flex-y-center gap-8px">
            <ElButton type="primary" link onClick={() => edit(row.petId!)}>
              {$t('common.edit')}
            </ElButton>
            <ElPopconfirm onConfirm={() => handleDeletePet(row.petId!)}>
              {{
                default: () => $t('common.confirmDelete'),
                reference: () => <ElButton type="danger" link>{$t('common.delete')}</ElButton>
              }}
            </ElPopconfirm>
          </div>
        );
      }
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
} = useTableOperate<Api.Bath.PetProfile>(data, 'petId', getData);

function edit(id: number) {
  handleEdit(id);
}

async function handleDeletePet(petId: number) {
  await fetchDeletePetProfile([petId]);
  ElMessage.success($t('common.deleteSuccess'));
  onDeleted();
}

async function handleBatchDelete() {
  if (checkedRowKeys.value.length === 0) return;
  const ids = checkedRowKeys.value.map(key => Number(key));
  await fetchDeletePetProfile(ids);
  ElMessage.success($t('common.deleteSuccess'));
  onBatchDeleted();
}

function handleSearch() {
  searchParams.value.current = 1;
  getData();
}

function handleReset() {
  Object.assign(searchParams.value, getInitSearchParams());
  handleSearch();
}
</script>

<template>
  <div class="flex-col-stretch gap-16px <sm:gap-12px">
    <ElCard shadow="never" class="flex-1-hidden">
      <template #header>
        <div class="flex-y-center justify-between">
          <span class="text-18px font-500">宠物档案管理</span>
        </div>
      </template>
      <PetSearch v-model="searchParams" @search="handleSearch" @reset="handleReset" />
      <TableHeaderOperation
        v-model:columns="columnChecks"
        :loading="loading"
        @refresh="getData"
        @add="handleAdd"
      >
        <template #left>
          <ElButton type="danger" :disabled="checkedRowKeys.length === 0" @click="handleBatchDelete">
            {$t('common.batchDelete')}
          </ElButton>
        </template>
      </TableHeaderOperation>
      <ElTable v-loading="loading" :data="data">
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
    <PetOperateDrawer
      v-model:visible="drawerVisible"
      :operate-type="operateType"
      :row-data="editingData"
      @submitted="getData"
    />
  </div>
</template>

<style scoped></style>

