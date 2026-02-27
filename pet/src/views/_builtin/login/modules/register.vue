<script setup lang="ts">
import { computed, ref } from 'vue';
import { useRouterPush } from '@/hooks/common/router';
import { useForm, useFormRules } from '@/hooks/common/form';
import { useCaptcha } from '@/hooks/business/captcha';
import { fetchRegister } from '@/service/api';
import { $t } from '@/locales';

defineOptions({ name: 'Register' });

const { toggleLoginModule } = useRouterPush();
const { formRef, validate } = useForm();
const { label, isCounting, loading, getCaptcha } = useCaptcha();
const registerLoading = ref(false);

interface FormModel {
  phone: string;
  code: string;
  password: string;
  confirmPassword: string;
}

const model = ref<FormModel>({
  phone: '',
  code: '',
  password: '',
  confirmPassword: ''
});

const rules = computed<Record<keyof FormModel, App.Global.FormRule[]>>(() => {
  const { formRules, createConfirmPwdRule } = useFormRules();

  return {
    phone: formRules.phone,
    code: formRules.code,
    password: formRules.pwd,
    confirmPassword: createConfirmPwdRule(model.value.password)
  };
});

async function handleSubmit() {
  await validate();
  
  registerLoading.value = true;
  
  try {
    const { error } = await fetchRegister(
      model.value.phone,
      model.value.code,
      model.value.password,
      model.value.confirmPassword
    );
    
    if (!error) {
      window.$message?.success($t('page.login.common.registerSuccess') || '注册成功');
      // 注册成功后跳转到登录页
      setTimeout(() => {
        toggleLoginModule('pwd-login');
      }, 1500);
    }
  } catch (err) {
    console.error('注册失败:', err);
  } finally {
    registerLoading.value = false;
  }
}
</script>

<template>
  <ElForm ref="formRef" :model="model" :rules="rules" size="large" :show-label="false" @keyup.enter="handleSubmit">
    <ElFormItem prop="phone">
      <ElInput v-model="model.phone" :placeholder="$t('page.login.common.phonePlaceholder')" />
    </ElFormItem>
    <ElFormItem prop="code">
      <div class="w-full flex-y-center gap-16px">
        <ElInput v-model="model.code" :placeholder="$t('page.login.common.codePlaceholder')" />
        <ElButton size="large" :disabled="isCounting" :loading="loading" @click="getCaptcha(model.phone)">
          {{ label }}
        </ElButton>
      </div>
    </ElFormItem>
    <ElFormItem prop="password">
      <ElInput
        v-model="model.password"
        type="password"
        show-password-on="click"
        :placeholder="$t('page.login.common.passwordPlaceholder')"
      />
    </ElFormItem>
    <ElFormItem prop="confirmPassword">
      <ElInput
        v-model="model.confirmPassword"
        type="password"
        show-password-on="click"
        :placeholder="$t('page.login.common.confirmPasswordPlaceholder')"
      />
    </ElFormItem>
    <ElSpace direction="vertical" :size="18" fill class="w-full">
      <ElButton type="primary" size="large" round block :loading="registerLoading" @click="handleSubmit">
        {{ $t('common.confirm') }}
      </ElButton>
      <ElButton size="large" round @click="toggleLoginModule('pwd-login')">
        {{ $t('page.login.common.back') }}
      </ElButton>
    </ElSpace>
  </ElForm>
</template>

<style scoped></style>
