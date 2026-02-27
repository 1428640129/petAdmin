import { computed } from 'vue';
import { useCountDown, useLoading } from '@sa/hooks';
import { REG_PHONE } from '@/constants/reg';
import { request } from '@/service/request';
import { $t } from '@/locales';

export function useCaptcha() {
  const { loading, startLoading, endLoading } = useLoading();
  const { count, start, stop, isCounting } = useCountDown(10);

  const label = computed(() => {
    let text = $t('page.login.codeLogin.getCode');

    const countingLabel = $t('page.login.codeLogin.reGetCode', { time: count.value });

    if (loading.value) {
      text = '';
    }

    if (isCounting.value) {
      text = countingLabel;
    }

    return text;
  });

  function isPhoneValid(phone: string) {
    if (phone.trim() === '') {
      window.$message?.error?.($t('form.phone.required'));

      return false;
    }

    if (!REG_PHONE.test(phone)) {
      window.$message?.error?.($t('form.phone.invalid'));

      return false;
    }

    return true;
  }

  async function getCaptcha(phone: string) {
    const valid = isPhoneValid(phone);

    if (!valid || loading.value) {
      return;
    }

    startLoading();

    try {
      // 调用短信验证码接口
      const { data, error } = await request<string>({
        url: '/auth/sendSmsCode',
        method: 'post',
        data: {
          phone
        }
      });

      if (error) {
        window.$message?.error?.($t('page.login.codeLogin.sendCodeFailed'));
        return;
      }

      window.$message?.success?.($t('page.login.codeLogin.sendCodeSuccess'));
      start();
    } catch (err) {
      window.$message?.error?.($t('page.login.codeLogin.sendCodeFailed'));
    } finally {
      endLoading();
    }
  }

  return {
    label,
    start,
    stop,
    isCounting,
    loading,
    getCaptcha
  };
}
