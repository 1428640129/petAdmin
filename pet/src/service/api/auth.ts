import { request } from '../request';

/**
 * Login
 *
 * @param userName User name
 * @param password Password
 */
export function fetchLogin(userName: string, password: string) {
  return request<Api.Auth.LoginToken>({
    url: '/auth/login',
    method: 'post',
    data: {
      userName,
      password
    }
  });
}

/** Get user info */
export function fetchGetUserInfo() {
  return request<Api.Auth.UserInfo>({ url: '/auth/getUserInfo' });
}

/**
 * Login by SMS code
 *
 * @param phone Phone number
 * @param code SMS verification code
 */
export function fetchLoginBySmsCode(phone: string, code: string) {
  return request<Api.Auth.LoginToken>({
    url: '/auth/loginBySmsCode',
    method: 'post',
    data: {
      phone,
      code
    }
  });
}

/**
 * Register
 *
 * @param phone Phone number
 * @param code Verification code
 * @param password Password
 * @param confirmPassword Confirm password
 */
export function fetchRegister(phone: string, code: string, password: string, confirmPassword: string) {
  return request<void>({
    url: '/auth/register',
    method: 'post',
    data: {
      phone,
      code,
      password,
      confirmPassword
    }
  });
}

/**
 * Refresh token
 *
 * @param refreshToken Refresh token
 */
export function fetchRefreshToken(refreshToken: string) {
  return request<Api.Auth.LoginToken>({
    url: '/auth/refreshToken',
    method: 'post',
    data: {
      refreshToken
    }
  });
}

/**
 * return custom backend error
 *
 * @param code error code
 * @param msg error message
 */
export function fetchCustomBackendError(code: string, msg: string) {
  return request({ url: '/auth/error', params: { code, msg } });
}
