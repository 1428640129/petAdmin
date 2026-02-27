<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { enableStatusOptions, userGenderOptions } from '@/constants/business';
import { fetchGetAllRoles, fetchGetUserDetail, fetchUpdateUser } from '@/service/api';
import { useForm, useFormRules } from '@/hooks/common/form';
import { $t } from '@/locales';

defineOptions({ name: 'UserOperateDrawer' });

interface Props {
  /** the type of operation */
  operateType: UI.TableOperateType;
  /** the edit row data */
  rowData?: Api.SystemManage.User | null;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'submitted'): void;
}

const emit = defineEmits<Emits>();

const visible = defineModel<boolean>('visible', {
  default: false
});

const { formRef, validate, restoreValidation } = useForm();
const { defaultRequiredRule } = useFormRules();

const title = computed(() => {
  const titles: Record<UI.TableOperateType, string> = {
    add: $t('page.manage.user.addUser'),
    edit: $t('page.manage.user.editUser')
  };
  return titles[props.operateType];
});

type Model = Pick<
  Api.SystemManage.User,
  'userName' | 'userGender' | 'nickName' | 'userPhone' | 'userEmail' | 'status'
> & { roleIds: number[] };

const model = ref(createDefaultModel());

function createDefaultModel(): Model {
  return {
    userName: '',
    userGender: undefined,
    nickName: '',
    userPhone: '',
    userEmail: '',
    roleIds: [],
    status: undefined
  };
}

type RuleKey = Extract<keyof Model, 'userName' | 'status'>;

const rules: Record<RuleKey, App.Global.FormRule> = {
  userName: defaultRequiredRule,
  status: defaultRequiredRule
};

/** the enabled role options: { label: roleName, value: roleId } */
const roleOptions = ref<CommonType.Option<number>[]>([]);

async function getRoleOptions() {
  const roles = await fetchGetAllRoles();
  if (Array.isArray(roles)) {
    roleOptions.value = roles.map(item => ({
      label: item.roleName,
      value: item.id
    }));
  }
}

async function handleInitModel() {
  model.value = createDefaultModel();

  if (props.operateType === 'edit' && props.rowData?.id) {
    const detail = await fetchGetUserDetail(props.rowData.id);
    if (detail?.user) {
      Object.assign(model.value, {
        userName: detail.user.userName,
        userGender: detail.user.sex ?? detail.user.userGender,
        nickName: detail.user.nickName,
        userPhone: detail.user.phonenumber ?? detail.user.userPhone,
        userEmail: detail.user.email ?? detail.user.userEmail,
        status: detail.user.status === '0' ? '1' : detail.user.status === '1' ? '2' : detail.user.status,
        roleIds: detail.roleIds ?? []
      });
    } else {
      Object.assign(model.value, props.rowData);
      model.value.roleIds =
        (props.rowData as any).roleIds ??
        (props.rowData as any).roles?.map((r: any) => r.roleId ?? r.id) ??
        [];
    }
  } else if (props.operateType === 'edit' && props.rowData) {
    Object.assign(model.value, props.rowData);
    model.value.roleIds = (props.rowData as any).roleIds ?? (props.rowData as any).roles?.map((r: any) => r.roleId ?? r.id) ?? [];
  }
}

function closeDrawer() {
  visible.value = false;
}

async function handleSubmit() {
  await validate();
  if (props.operateType === 'edit' && props.rowData?.id) {
    await fetchUpdateUser({
      userId: props.rowData.id,
      userName: model.value.userName,
      nickName: model.value.nickName,
      phonenumber: model.value.userPhone,
      email: model.value.userEmail,
      sex: model.value.userGender,
      status: model.value.status,
      roleIds: model.value.roleIds
    });
  }
  window.$message?.success($t('common.updateSuccess'));
  closeDrawer();
  emit('submitted');
}

watch(visible, () => {
  if (visible.value) {
    handleInitModel();
    restoreValidation();
    getRoleOptions();
  }
});
</script>

<template>
  <ElDrawer v-model="visible" :title="title" :size="360">
    <ElForm ref="formRef" :model="model" :rules="rules" label-position="top">
      <ElFormItem :label="$t('page.manage.user.userName')" prop="userName">
        <ElInput v-model="model.userName" :placeholder="$t('page.manage.user.form.userName')" />
      </ElFormItem>
      <ElFormItem :label="$t('page.manage.user.userGender')" prop="userGender">
        <ElRadioGroup v-model="model.userGender">
          <ElRadio v-for="item in userGenderOptions" :key="item.value" :value="item.value" :label="$t(item.label)" />
        </ElRadioGroup>
      </ElFormItem>
      <ElFormItem :label="$t('page.manage.user.nickName')" prop="nickName">
        <ElInput v-model="model.nickName" :placeholder="$t('page.manage.user.form.nickName')" />
      </ElFormItem>
      <ElFormItem :label="$t('page.manage.user.userPhone')" prop="userPhone">
        <ElInput v-model="model.userPhone" :placeholder="$t('page.manage.user.form.userPhone')" />
      </ElFormItem>
      <ElFormItem :label="$t('page.manage.user.userEmail')" prop="email">
        <ElInput v-model="model.userEmail" :placeholder="$t('page.manage.user.form.userEmail')" />
      </ElFormItem>
      <ElFormItem :label="$t('page.manage.user.userStatus')" prop="status">
        <ElRadioGroup v-model="model.status">
          <ElRadio v-for="item in enableStatusOptions" :key="item.value" :value="item.value" :label="$t(item.label)" />
        </ElRadioGroup>
      </ElFormItem>
      <ElFormItem :label="$t('page.manage.user.userRole')" prop="roleIds">
        <ElSelect v-model="model.roleIds" multiple :placeholder="$t('page.manage.user.form.userRole')">
          <ElOption v-for="{ label, value } in roleOptions" :key="value" :label="label" :value="value" />
        </ElSelect>
      </ElFormItem>
    </ElForm>
    <template #footer>
      <ElSpace :size="16">
        <ElButton @click="closeDrawer">{{ $t('common.cancel') }}</ElButton>
        <ElButton type="primary" @click="handleSubmit">{{ $t('common.confirm') }}</ElButton>
      </ElSpace>
    </template>
  </ElDrawer>
</template>

<style scoped></style>
