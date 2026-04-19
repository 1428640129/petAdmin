<script setup lang="tsx">
import { ref, watch } from 'vue';
import {
  ElButton,
  ElDrawer,
  ElForm,
  ElFormItem,
  ElInput,
  ElInputNumber,
  ElMessage,
  ElOption,
  ElSelect,
  ElSwitch
} from 'element-plus';
import { fetchAddPetProfile, fetchUpdatePetProfile } from '@/service/api/bath';
import { $t } from '@/locales';

defineOptions({ name: 'PetOperateDrawer' });

interface Props {
  visible: boolean;
  operateType: 'add' | 'edit';
  rowData?: Api.Bath.PetProfile | null;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  'update:visible': [value: boolean];
  submitted: [];
}>();

const formRef = ref<InstanceType<typeof ElForm>>();
const loading = ref(false);
const formData = ref({
  petId: undefined,
  userId: undefined,
  petName: '',
  petBreed: '',
  petAge: undefined,
  petSex: '',
  petWeight: undefined,
  hairType: '0',
  petPhoto: '',
  healthStatus: '',
  specialNeeds: '',
  allergyHistory: '',
  isDefault: '0',
  remark: ''
});

const sexOptions = [
  { label: '公', value: '0' },
  { label: '母', value: '1' },
  { label: '未知', value: '2' }
];

const hairTypeOptions = [
  { label: '短毛', value: '0' },
  { label: '长毛', value: '1' }
];

watch(
  () => props.visible,
  async newVal => {
    if (newVal) {
      if (props.operateType === 'add') {
        resetForm();
      } else if (props.rowData) {
        Object.assign(formData.value, {
          petId: props.rowData.petId,
          userId: props.rowData.userId,
          petName: props.rowData.petName || '',
          petBreed: props.rowData.petBreed || '',
          petAge: props.rowData.petAge,
          petSex: props.rowData.petSex || '',
          petWeight: props.rowData.petWeight,
          hairType: props.rowData.hairType || '0',
          petPhoto: props.rowData.petPhoto || '',
          healthStatus: props.rowData.healthStatus || '',
          specialNeeds: props.rowData.specialNeeds || '',
          allergyHistory: props.rowData.allergyHistory || '',
          isDefault: props.rowData.isDefault || '0',
          remark: props.rowData.remark || ''
        });
      }
    }
  }
);

function resetForm() {
  formData.value = {
    petId: undefined,
    userId: undefined,
    petName: '',
    petBreed: '',
    petAge: undefined,
    petSex: '',
    petWeight: undefined,
    hairType: '0',
    petPhoto: '',
    healthStatus: '',
    specialNeeds: '',
    allergyHistory: '',
    isDefault: '0',
    remark: ''
  };
  formRef.value?.resetFields();
}

async function handleSubmit() {
  await formRef.value?.validate();
  loading.value = true;
  try {
    const submitData = { ...formData.value };
    if (props.operateType === 'add') {
      await fetchAddPetProfile(submitData);
      ElMessage.success($t('common.addSuccess'));
    } else {
      await fetchUpdatePetProfile(submitData);
      ElMessage.success($t('common.updateSuccess'));
    }
    emit('update:visible', false);
    emit('submitted');
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
}

function handleClose() {
  emit('update:visible', false);
}
</script>

<template>
  <ElDrawer
    :model-value="visible"
    :title="operateType === 'add' ? '新增宠物档案' : '编辑宠物档案'"
    :size="480"
    @close="handleClose"
  >
    <ElForm ref="formRef" :model="formData" label-width="100px">
      <ElFormItem label="宠物名称" prop="petName" :rules="[{ required: true, message: '请输入宠物名称' }]">
        <ElInput v-model="formData.petName" placeholder="请输入宠物名称" />
      </ElFormItem>
      <ElFormItem label="品种" prop="petBreed">
        <ElInput v-model="formData.petBreed" placeholder="请输入品种" />
      </ElFormItem>
      <ElFormItem label="年龄(月)" prop="petAge">
        <ElInputNumber v-model="formData.petAge" :min="0" :max="300" placeholder="请输入年龄" />
      </ElFormItem>
      <ElFormItem label="性别" prop="petSex">
        <ElSelect v-model="formData.petSex" placeholder="请选择性别" clearable>
          <ElOption v-for="item in sexOptions" :key="item.value" :label="item.label" :value="item.value" />
        </ElSelect>
      </ElFormItem>
      <ElFormItem label="体重(kg)" prop="petWeight">
        <ElInputNumber v-model="formData.petWeight" :min="0" :precision="2" placeholder="请输入体重" />
      </ElFormItem>
      <ElFormItem label="毛发类型" prop="hairType">
        <ElSelect v-model="formData.hairType">
          <ElOption v-for="item in hairTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </ElSelect>
      </ElFormItem>
      <ElFormItem label="照片URL" prop="petPhoto">
        <ElInput v-model="formData.petPhoto" placeholder="请输入照片URL" />
      </ElFormItem>
      <ElFormItem label="健康状况" prop="healthStatus">
        <ElInput v-model="formData.healthStatus" type="textarea" :rows="2" placeholder="请输入健康状况" />
      </ElFormItem>
      <ElFormItem label="特殊需求" prop="specialNeeds">
        <ElInput v-model="formData.specialNeeds" type="textarea" :rows="2" placeholder="请输入特殊需求" />
      </ElFormItem>
      <ElFormItem label="过敏史" prop="allergyHistory">
        <ElInput v-model="formData.allergyHistory" type="textarea" :rows="2" placeholder="请输入过敏史" />
      </ElFormItem>
      <ElFormItem label="设为默认" prop="isDefault">
        <ElSwitch v-model="formData.isDefault" active-value="1" inactive-value="0" />
      </ElFormItem>
      <ElFormItem label="备注" prop="remark">
        <ElInput v-model="formData.remark" type="textarea" :rows="2" placeholder="请输入备注" />
      </ElFormItem>
    </ElForm>
    <template #footer>
      <ElButton @click="handleClose">取消</ElButton>
      <ElButton type="primary" :loading="loading" @click="handleSubmit">确定</ElButton>
    </template>
  </ElDrawer>
</template>





