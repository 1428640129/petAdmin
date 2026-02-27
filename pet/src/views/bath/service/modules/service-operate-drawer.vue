<script setup lang="tsx">
import { ref, watch } from 'vue';
import type { UploadProps } from 'element-plus';
import {
  ElButton,
  ElDrawer,
  ElForm,
  ElFormItem,
  ElIcon,
  ElImage,
  ElInput,
  ElInputNumber,
  ElMessage,
  ElOption,
  ElSelect,
  ElUpload
} from 'element-plus';
import { Delete, Plus } from '@element-plus/icons-vue';
import { fetchAddBathService, fetchUpdateBathService } from '@/service/api/bath';
import { fetchUploadFile } from '@/service/api/common';
import { $t } from '@/locales';

defineOptions({ name: 'ServiceOperateDrawer' });

interface Props {
  visible: boolean;
  operateType: 'add' | 'edit';
  rowData?: Api.Bath.Service | null;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  'update:visible': [value: boolean];
  submitted: [];
}>();

const formRef = ref<InstanceType<typeof ElForm>>();
const loading = ref(false);
const uploadLoading = ref(false);
const formData = ref({
  serviceId: undefined,
  serviceName: '',
  serviceDesc: '',
  serviceType: '',
  serviceImages: '',
  duration: 60,
  status: '0',
  sortOrder: 0,
  prices: [] as Array<{
    petType: string;  // 宠物类型（0=短毛,1=长毛）
    weightMin: number;
    weightMax: number;
    price: number;
  }>
});

// 图片列表
const imageList = ref<string[]>([]);

// 从JSON字符串解析图片列表
function parseImages(imagesJson?: string): string[] {
  if (!imagesJson) return [];
  try {
    const parsed = JSON.parse(imagesJson);
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

// 将图片列表转换为JSON字符串
function stringifyImages(images: string[]): string {
  return JSON.stringify(images);
}

const defaultPrices = [
  { petType: '0', weightMin: 0, weightMax: 5, price: 0 },
  { petType: '0', weightMin: 5.01, weightMax: 15, price: 0 },
  { petType: '0', weightMin: 15.01, weightMax: 30, price: 0 },
  { petType: '0', weightMin: 30.01, weightMax: 999, price: 0 }
];

// 服务类型选项
const serviceTypeOptions = [
  { label: '基础洗浴', value: '0' },
  { label: '深度护理', value: '1' },
  { label: '豪华套餐', value: '2' }
];

watch(
  () => props.visible,
  async newVal => {
    if (newVal) {
      if (props.operateType === 'add') {
        resetForm();
        // 初始化短毛价格梯度
        formData.value.prices = JSON.parse(JSON.stringify(defaultPrices));
        imageList.value = [];
      } else if (props.rowData) {
        Object.assign(formData.value, props.rowData);
        if (!formData.value.prices || formData.value.prices.length === 0) {
          // 如果没有价格梯度，初始化短毛价格梯度
          formData.value.prices = JSON.parse(JSON.stringify(defaultPrices));
        } else {
          // 确保所有价格梯度都有petType字段（兼容旧数据）
          formData.value.prices = formData.value.prices.map((p: any) => ({
            ...p,
            petType: p.petType || '0'  // 如果没有petType，默认为短毛
          }));
        }
        // 解析图片列表
        imageList.value = parseImages(formData.value.serviceImages);
      }
    }
  }
);


function resetForm() {
  formData.value = {
    serviceId: undefined,
    serviceName: '',
    serviceDesc: '',
    serviceType: '',
    serviceImages: '',
    duration: 60,
    status: '0',
    sortOrder: 0,
    prices: []
  };
  imageList.value = [];
  formRef.value?.resetFields();
}

// 上传前验证
const beforeUpload: UploadProps['beforeUpload'] = (file: File) => {
  const isImage = file.type.startsWith('image/');
  const isLt5M = file.size / 1024 / 1024 < 5;

  if (!isImage) {
    ElMessage.error('只能上传图片文件!');
    return false;
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!');
    return false;
  }
  return true;
};

// 自定义上传方法
const handleHttpRequest: UploadProps['httpRequest'] = async options => {
  const file = options.file as File;
  try {
    uploadLoading.value = true;
    const result = await fetchUploadFile(file);
    
    if (result) {
      let url = '';
      
      // 检查不同的可能格式
      if (typeof result === 'object') {
        // 直接有 url 字段
        if ('url' in result) {
          url = (result as any).url;
        }
        // 或者嵌套在 data 中（虽然上传接口应该不会）
        else if ('data' in result && typeof (result as any).data === 'object' && 'url' in (result as any).data) {
          url = (result as any).data.url;
        }
      }
      
      if (url) {
        imageList.value.push(url);
        formData.value.serviceImages = stringifyImages(imageList.value);
        ElMessage.success('上传成功');
      } else {
        ElMessage.error('上传失败：未获取到图片URL');
        console.error('无法从返回结果中获取URL，返回结果:', result);
      }
    } else {
      ElMessage.error('上传失败：返回结果为空');
      console.error('上传返回结果为空');
    }
  } catch (error: any) {
    const errorMsg = error?.response?.data?.msg || error?.message || error?.msg || '上传失败';
    ElMessage.error(errorMsg);
    console.error('上传错误:', error);
  } finally {
    uploadLoading.value = false;
  }
};

// 删除图片
function handleRemoveImage(index: number) {
  imageList.value.splice(index, 1);
  formData.value.serviceImages = stringifyImages(imageList.value);
}

// 添加长毛价格梯度
function addLongHairPrices() {
  const longHairPrices = [
    { petType: '1', weightMin: 0, weightMax: 5, price: 0 },
    { petType: '1', weightMin: 5.01, weightMax: 15, price: 0 },
    { petType: '1', weightMin: 15.01, weightMax: 30, price: 0 },
    { petType: '1', weightMin: 30.01, weightMax: 999, price: 0 }
  ];
  formData.value.prices.push(...longHairPrices);
}

async function handleSubmit() {
  await formRef.value?.validate(async valid => {
    if (valid) {
      // 确保图片列表已同步到formData
      formData.value.serviceImages = stringifyImages(imageList.value);
      
      // 确保所有价格梯度都有petType字段
      formData.value.prices = formData.value.prices.map((p: any) => ({
        ...p,
        petType: p.petType || '0'  // 如果没有petType，默认为短毛
      }));
      
      loading.value = true;
      try {
        if (props.operateType === 'add') {
          await fetchAddBathService(formData.value);
          ElMessage.success('新增成功');
        } else {
          await fetchUpdateBathService(formData.value);
          ElMessage.success('修改成功');
        }
        emit('update:visible', false);
        emit('submitted');
      } catch (error) {
        console.error(error);
      } finally {
        loading.value = false;
      }
    }
  });
}

function handleCancel() {
  emit('update:visible', false);
}
</script>

<template>
  <ElDrawer
    :model-value="visible"
    @update:model-value="val => emit('update:visible', val)"
    :title="operateType === 'add' ? '新增洗浴服务' : '编辑洗浴服务'"
    :size="680"
    :close-on-click-modal="false"
  >
    <ElForm ref="formRef" :model="formData" label-width="100px">
      <ElFormItem label="服务名称" prop="serviceName" :rules="[{ required: true, message: '请输入服务名称' }]">
        <ElInput v-model="formData.serviceName" placeholder="请输入服务名称" />
      </ElFormItem>
      <ElFormItem label="服务类型" prop="serviceType">
        <ElSelect v-model="formData.serviceType" placeholder="请选择服务类型" class="w-full">
          <ElOption
            v-for="option in serviceTypeOptions"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
        </ElSelect>
      </ElFormItem>
      <ElFormItem label="服务图片" prop="serviceImages">
        <div class="w-full">
          <ElUpload
            action="#"
            :auto-upload="true"
            :before-upload="beforeUpload"
            :http-request="handleHttpRequest"
            :show-file-list="false"
            list-type="picture-card"
            :limit="9"
            :disabled="uploadLoading"
          >
            <ElIcon v-if="!uploadLoading"><Plus /></ElIcon>
            <template v-else>
              <div class="loading-spinner"></div>
            </template>
          </ElUpload>
          <div v-if="imageList.length > 0" class="mt-12px flex flex-wrap gap-12px">
            <div
              v-for="(image, index) in imageList"
              :key="index"
              class="relative w-100px h-100px border-1px border-#d9d9d9 rounded-4px overflow-hidden group"
            >
              <ElImage
                :src="image"
                fit="cover"
                class="w-full h-full"
                :preview-src-list="imageList"
                :initial-index="index"
              />
              <div
                class="absolute top-0 right-0 w-20px h-20px bg-red-500 text-white flex-center cursor-pointer opacity-0 group-hover:opacity-100 transition-opacity"
                @click="handleRemoveImage(index)"
              >
                <ElIcon><Delete /></ElIcon>
              </div>
            </div>
          </div>
          <div v-else class="mt-8px text-12px text-gray-500">
            支持上传多张图片，最多9张，单张图片不超过5MB
          </div>
        </div>
      </ElFormItem>
      <ElFormItem label="服务描述" prop="serviceDesc">
        <ElInput v-model="formData.serviceDesc" type="textarea" :rows="3" placeholder="请输入服务描述" />
      </ElFormItem>
      <ElFormItem label="服务时长" prop="duration" :rules="[{ required: true, message: '请输入服务时长' }]">
        <ElInputNumber v-model="formData.duration" :min="1" :max="999" placeholder="分钟" style="width: 100%" />
      </ElFormItem>
      <ElFormItem label="状态" prop="status">
        <ElSelect v-model="formData.status" style="width: 100%">
          <ElOption label="正常" value="0" />
          <ElOption label="停用" value="1" />
        </ElSelect>
      </ElFormItem>
      <ElFormItem label="排序" prop="sortOrder">
        <ElInputNumber v-model="formData.sortOrder" :min="0" style="width: 100%" />
      </ElFormItem>
      <ElFormItem label="价格梯度">
        <div class="w-full">
          <!-- 短毛价格梯度 -->
          <div class="mb-16px">
            <div class="mb-8px text-14px font-bold text-gray-700">短毛价格梯度</div>
            <div
              v-for="(price, index) in formData.prices.filter(p => p.petType === '0')"
              :key="`short-${index}`"
              class="flex-y-center gap-8px mb-8px"
            >
              <span class="w-100px">{{ price.weightMin }}kg - {{ price.weightMax }}kg</span>
              <ElInputNumber
                v-model="price.price"
                :min="0"
                :precision="2"
                placeholder="价格"
                style="flex: 1"
              />
              <span>元</span>
            </div>
          </div>
          
          <!-- 长毛价格梯度 -->
          <div>
            <div class="mb-8px text-14px font-bold text-gray-700">长毛价格梯度</div>
            <div
              v-for="(price, index) in formData.prices.filter(p => p.petType === '1')"
              :key="`long-${index}`"
              class="flex-y-center gap-8px mb-8px"
            >
              <span class="w-100px">{{ price.weightMin }}kg - {{ price.weightMax }}kg</span>
              <ElInputNumber
                v-model="price.price"
                :min="0"
                :precision="2"
                placeholder="价格"
                style="flex: 1"
              />
              <span>元</span>
            </div>
            <ElButton
              v-if="formData.prices.filter(p => p.petType === '1').length === 0"
              type="primary"
              size="small"
              :icon="Plus"
              @click="addLongHairPrices"
            >
              添加长毛价格梯度
            </ElButton>
          </div>
        </div>
      </ElFormItem>
    </ElForm>
    <template #footer>
      <div class="flex-y-center justify-end gap-12px">
        <ElButton @click="handleCancel">{{ $t('common.cancel') }}</ElButton>
        <ElButton type="primary" :loading="loading" @click="handleSubmit">
          {{ $t('common.confirm') }}
        </ElButton>
      </div>
    </template>
  </ElDrawer>
</template>

