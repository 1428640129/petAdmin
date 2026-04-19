import { request } from '../request';

/** 后端状态 0=正常 1=停用 -> 前端 1=启用 2=禁用 */
function mapStatus(s: string | undefined): string {
  if (s === '0') return '1';
  if (s === '1') return '2';
  return s ?? '1';
}

/** 后端 sex：0=男 1=女 2=未知（与 SysUser Excel 注释一致） <-> 前端选项 1=男 2=女 */
function mapSexFromBackend(s: string | number | undefined | null): string | undefined {
  if (s === undefined || s === null || s === '') return undefined;
  const v = String(s);
  if (v === '0') return '1';
  if (v === '1') return '2';
  return undefined;
}

function mapSexToBackend(s: string | number | undefined | null): string | undefined {
  if (s === undefined || s === null || s === '') return undefined;
  const v = String(s);
  if (v === '1') return '0';
  if (v === '2') return '1';
  return undefined;
}

/** get role list - 对接后端 /system/role/list */
export function fetchGetRoleList(params?: Api.SystemManage.RoleSearchParams) {
  const backendParams = {
    pageNum: params?.current ?? 1,
    pageSize: params?.size ?? 10,
    roleName: params?.roleName,
    roleKey: params?.roleCode,
    status: params?.status
  };
  return request<any>({
    url: '/system/role/list',
    method: 'get',
    params: backendParams
  }).then((res: any) => {
    // request 返回 { data, error, response }，后端数据在 data 中
    const payload = res?.data ?? res;
    const rows = payload?.rows ?? [];
    const total = payload?.total ?? 0;
    const records = rows.map((r: any) => ({
      ...r,
      id: r.roleId ?? r.id,
      roleCode: r.roleKey ?? r.roleCode,
      roleDesc: r.remark ?? r.roleDesc,
      status: mapStatus(r.status)
    }));
    return {
      data: { records, current: params?.current ?? 1, size: params?.size ?? 10, total },
      error: null
    };
  });
}

/**
 * get all roles - 对接后端 /system/role/optionselect
 */
export function fetchGetAllRoles() {
  return request<any>({
    url: '/system/role/optionselect',
    method: 'get'
  }).then((res: any) => {
    const payload = res?.data ?? res;
    const list = Array.isArray(payload) ? payload : (payload?.data ?? payload?.rows ?? []);
    return list.map((r: any) => ({
      id: r.roleId ?? r.id,
      roleName: r.roleName,
      roleCode: r.roleKey ?? r.roleCode
    }));
  });
}

function parseUserDetailResponse(res: any) {
  const raw = res?.response?.data ?? res?.data ?? res;
  const user = raw?.data ?? raw;
  const roleIds = getRoleIds(raw, user);
  return {
    user: user
      ? {
          ...user,
          id: user.userId ?? user.id,
          userGender: mapSexFromBackend(user.sex)
        }
      : null,
    roleIds: Array.isArray(roleIds) ? roleIds : [],
    roles: raw?.roles ?? user?.roles ?? []
  };
}

function getRoleIds(raw: any, user: any): number[] {
  if (Array.isArray(raw?.roleIds)) return raw.roleIds;
  if (Array.isArray(user?.roleIds)) return user.roleIds;
  const roles = user?.roles ?? [];
  return roles.map((r: any) => r.roleId ?? r.id);
}

/** get user detail for edit - 对接后端 GET /system/user/{userId}，返回 user + roleIds + roles */
export function fetchGetUserDetail(userId: number) {
  return request<any>({ url: `/system/user/${userId}`, method: 'get' }).then(parseUserDetailResponse);
}

/** 前端状态 1=启用 2=禁用 -> 后端 0=正常 1=停用 */
function toBackendStatus(s: string | undefined): string {
  if (s === '1') return '0';
  if (s === '2') return '1';
  return s ?? '0';
}

/** update user - 对接后端 PUT /system/user */
export function fetchUpdateUser(data: {
  userId: number;
  userName?: string;
  nickName?: string;
  phonenumber?: string;
  email?: string;
  sex?: string;
  status?: string;
  roleIds?: number[];
}) {
  return request<any>({
    url: '/system/user',
    method: 'put',
    data: {
      userId: data.userId,
      userName: data.userName,
      nickName: data.nickName,
      phonenumber: data.phonenumber,
      email: data.email,
      sex: data.sex !== undefined ? mapSexToBackend(data.sex) : undefined,
      status: toBackendStatus(data.status),
      roleIds: data.roleIds
    }
  });
}

/** get user list - 对接后端 /system/user/list */
export function fetchGetUserList(params?: Api.SystemManage.UserSearchParams) {
  const backendParams = {
    pageNum: params?.current ?? 1,
    pageSize: params?.size ?? 30,
    userName: params?.userName,
    nickName: params?.nickName,
    phonenumber: params?.userPhone,
    email: params?.userEmail,
    sex:
      params?.userGender !== undefined && params?.userGender !== null ? mapSexToBackend(params.userGender) : undefined,
    status: params?.status !== undefined && params?.status !== null ? toBackendStatus(params.status) : undefined
  };
  return request<any>({
    url: '/system/user/list',
    method: 'get',
    params: backendParams
  }).then((res: any) => {
    // request 返回 { data, error, response }，后端数据在 data 中
    const payload = res?.data ?? res;
    const rows = payload?.rows ?? [];
    const total = payload?.total ?? 0;
    const records = rows.map((r: any) => ({
      ...r,
      id: r.userId ?? r.id,
      userGender: mapSexFromBackend(r.sex ?? r.userGender),
      userPhone: r.phonenumber ?? r.userPhone,
      userEmail: r.email ?? r.userEmail,
      status: mapStatus(r.status)
    }));
    return {
      data: { records, current: params?.current ?? 1, size: params?.size ?? 30, total },
      error: null
    };
  });
}

/** add user - 对接后端 POST /system/user */
export function fetchAddUser(data: {
  userName: string;
  nickName?: string;
  phonenumber?: string;
  email?: string;
  sex?: string;
  status?: string;
  password: string;
  roleIds?: number[];
  deptId?: number;
}) {
  return request<any>({
    url: '/system/user',
    method: 'post',
    data: {
      userName: data.userName,
      nickName: (data.nickName && data.nickName.trim()) || data.userName,
      phonenumber: data.phonenumber,
      email: data.email,
      sex: data.sex !== undefined ? mapSexToBackend(data.sex) : undefined,
      status: toBackendStatus(data.status),
      password: data.password,
      roleIds: data.roleIds?.length ? data.roleIds : [],
      postIds: []
    }
  });
}

/** get menu list - 对接后端 /system/menu/list，后端返回列表需包装为分页格式 */
export function fetchGetMenuList() {
  return request<any>({
    url: '/system/menu/list',
    method: 'get'
  }).then((res: any) => {
    const payload = res?.data ?? res;
    const list = Array.isArray(payload) ? payload : (payload?.data ?? payload?.rows ?? []);
    const records = list.map((m: any) => ({
      ...m,
      id: m.menuId ?? m.id,
      routePath: m.path ?? m.routePath,
      routeName: m.routeName ?? m.path
    }));
    return {
      data: { records, current: 1, size: records.length, total: records.length },
      error: null
    };
  });
}

/** get all pages - 从菜单列表提取路由路径，后端无独立接口 */
export function fetchGetAllPages() {
  return request<any>({
    url: '/system/menu/list',
    method: 'get'
  }).then((res: any) => {
    const payload = res?.data ?? res;
    const menus = Array.isArray(payload) ? payload : (payload?.data ?? payload?.rows ?? []);
    return menus.map((m: any) => m.path || m.routePath || '').filter(Boolean);
  });
}

/** get menu tree - 对接后端 /system/menu/treeselect */
export function fetchGetMenuTree() {
  return request<Api.SystemManage.MenuTree[]>({
    url: '/system/menu/treeselect',
    method: 'get'
  });
}

/**
 * delete role(s) - 对接后端 DELETE /system/role/{roleIds}
 * @param roleIds 角色ID数组
 */
export function fetchDeleteRole(roleIds: number[]) {
  // 后端接口路径格式：/system/role/{roleIds}，多个ID用逗号分隔
  const ids = roleIds.join(',');
  return request<any>({
    url: `/system/role/${ids}`,
    method: 'delete'
  });
}

/** add role - 对接后端 POST /system/role */
export function fetchAddRole(data: {
  roleName: string;
  roleCode: string;
  roleDesc?: string;
  status?: string;
  roleSort?: number;
}) {
  return request<any>({
    url: '/system/role',
    method: 'post',
    data: {
      roleName: data.roleName,
      roleKey: data.roleCode,
      remark: data.roleDesc ?? '',
      status: toBackendStatus(data.status),
      roleSort: data.roleSort ?? 1,
      dataScope: '1'
    }
  });
}

/** update role - 对接后端 PUT /system/role（不传 menuIds 时不改菜单权限） */
export function fetchUpdateRole(data: {
  roleId: number;
  roleName: string;
  roleCode: string;
  roleDesc?: string;
  status?: string;
  roleSort?: number;
}) {
  return request<any>({
    url: '/system/role',
    method: 'put',
    data: {
      roleId: data.roleId,
      roleName: data.roleName,
      roleKey: data.roleCode,
      remark: data.roleDesc ?? '',
      status: toBackendStatus(data.status),
      roleSort: data.roleSort ?? 1
    }
  });
}
