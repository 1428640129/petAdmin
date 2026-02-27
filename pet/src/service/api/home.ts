import { request } from '../request';

/**
 * 获取首页统计数据
 */
export function fetchGetHomeStatistics() {
  return request<Api.Home.Statistics>({
    url: '/system/index/statistics',
    method: 'get'
  });
}





