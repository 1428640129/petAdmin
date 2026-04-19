import { request } from '../request';

/**
 * 获取洗浴服务列表
 */
export function fetchGetBathServiceList(params?: any) {
    return request<Api.Common.PaginatedData<Api.Bath.Service>>({
        url: '/bath/service/list',
        method: 'get',
        params
    });
}

/**
 * 获取洗浴服务详情
 */
export function fetchGetBathServiceDetail(serviceId: number) {
    return request<Api.Bath.Service>({
        url: `/bath/service/${serviceId}`,
        method: 'get'
    });
}

/**
 * 新增洗浴服务
 */
export function fetchAddBathService(data: Api.Bath.Service) {
    return request({
        url: '/bath/service',
        method: 'post',
        data
    });
}

/**
 * 修改洗浴服务
 */
export function fetchUpdateBathService(data: Api.Bath.Service) {
    return request({
        url: '/bath/service',
        method: 'put',
        data
    });
}

/**
 * 删除洗浴服务
 */
export function fetchDeleteBathService(serviceIds: number[]) {
    return request({
        url: `/bath/service/${serviceIds.join(',')}`,
        method: 'delete'
    });
}

/**
 * 根据体重计算价格
 */
export function fetchCalculatePrice(serviceId: number, weight: number) {
    return request<number>({
        url: '/bath/service/calculatePrice',
        method: 'get',
        params: { serviceId, weight }
    });
}

// ==================== 预约管理 ====================

/**
 * 获取预约列表
 */
export function fetchGetBathAppointmentList(params?: any) {
    return request<Api.Common.PaginatedData<Api.Bath.Appointment>>({
        url: '/bath/appointment/list',
        method: 'get',
        params
    });
}

/**
 * 获取预约详情
 */
export function fetchGetBathAppointmentDetail(appointmentId: number) {
    return request<Api.Bath.Appointment>({
        url: `/bath/appointment/${appointmentId}`,
        method: 'get'
    });
}

/**
 * 新增预约
 */
export function fetchAddBathAppointment(data: Api.Bath.Appointment) {
    return request({
        url: '/bath/appointment',
        method: 'post',
        data
    });
}

/**
 * 修改预约
 */
export function fetchUpdateBathAppointment(data: Api.Bath.Appointment) {
    return request({
        url: '/bath/appointment',
        method: 'put',
        data
    });
}

/**
 * 删除预约
 */
export function fetchDeleteBathAppointment(appointmentIds: number[]) {
    return request({
        url: `/bath/appointment/${appointmentIds.join(',')}`,
        method: 'delete'
    });
}

/**
 * 确认预约
 */
export function fetchConfirmAppointment(appointmentId: number) {
    return request({
        url: `/bath/appointment/confirm/${appointmentId}`,
        method: 'put'
    });
}

/**
 * 取消预约
 */
export function fetchCancelAppointment(appointmentId: number) {
    return request({
        url: `/bath/appointment/cancel/${appointmentId}`,
        method: 'put'
    });
}

/**
 * 开始服务
 */
export function fetchStartService(appointmentId: number) {
    return request({
        url: `/bath/appointment/start/${appointmentId}`,
        method: 'put'
    });
}

/**
 * 完成服务
 */
export function fetchCompleteService(appointmentId: number) {
    return request({
        url: `/bath/appointment/complete/${appointmentId}`,
        method: 'put'
    });
}

// ==================== 订单管理 ====================

/**
 * 获取订单列表
 */
export function fetchGetBathOrderList(params?: any) {
    return request<Api.Common.PaginatedData<Api.Bath.Order>>({
        url: '/bath/order/list',
        method: 'get',
        params
    });
}

/**
 * 获取订单详情
 */
export function fetchGetBathOrderDetail(orderId: number) {
    return request<Api.Bath.Order>({
        url: `/bath/order/${orderId}`,
        method: 'get'
    });
}

/**
 * 新增订单
 */
export function fetchAddBathOrder(data: Api.Bath.Order) {
    return request({
        url: '/bath/order',
        method: 'post',
        data
    });
}

/**
 * 修改订单
 */
export function fetchUpdateBathOrder(data: Api.Bath.Order) {
    return request({
        url: '/bath/order',
        method: 'put',
        data
    });
}

/**
 * 支付订单（JSON 请求体，避免纯数字被当成 form-urlencoded 导致 415/500）
 */
export function fetchPayOrder(orderId: number, payAmount?: number) {
    return request({
        url: `/bath/order/pay/${orderId}`,
        method: 'put',
        data: { payAmount: payAmount ?? null }
    });
}

/**
 * 删除订单
 */
export function fetchDeleteBathOrder(orderIds: number[]) {
    return request({
        url: `/bath/order/${orderIds.join(',')}`,
        method: 'delete'
    });
}

// ==================== 评价管理 ====================

/**
 * 获取评价列表
 */
export function fetchGetBathReviewList(params?: any) {
    return request<Api.Common.PaginatedData<Api.Bath.Review>>({
        url: '/bath/review/list',
        method: 'get',
        params
    });
}

/**
 * 获取评价详情
 */
export function fetchGetBathReviewDetail(reviewId: number) {
    return request<Api.Bath.Review>({
        url: `/bath/review/${reviewId}`,
        method: 'get'
    });
}

/**
 * 新增评价
 */
export function fetchAddBathReview(data: Api.Bath.Review) {
    return request({
        url: '/bath/review',
        method: 'post',
        data
    });
}

/**
 * 修改评价
 */
export function fetchUpdateBathReview(data: Api.Bath.Review) {
    return request({
        url: '/bath/review',
        method: 'put',
        data
    });
}

/**
 * 删除评价
 */
export function fetchDeleteBathReview(reviewIds: number[]) {
    return request({
        url: `/bath/review/${reviewIds.join(',')}`,
        method: 'delete'
    });
}

/**
 * 回复评价
 */
export function fetchReplyReview(reviewId: number, replyContent: string) {
    return request({
        url: `/bath/review/reply/${reviewId}`,
        method: 'put',
        data: { replyContent }
    });
}

// ==================== 通知管理 ====================

/**
 * 获取通知列表
 */
export function fetchGetBathNotificationList(params?: any) {
    return request<Api.Common.PaginatedData<Api.Bath.Notification>>({
        url: '/bath/notification/list',
        method: 'get',
        params
    });
}

/**
 * 获取通知详情
 */
export function fetchGetBathNotificationDetail(notificationId: number) {
    return request<Api.Bath.Notification>({
        url: `/bath/notification/${notificationId}`,
        method: 'get'
    });
}

/**
 * 新增通知
 */
export function fetchAddBathNotification(data: Api.Bath.Notification) {
    return request({
        url: '/bath/notification',
        method: 'post',
        data
    });
}

/**
 * 修改通知
 */
export function fetchUpdateBathNotification(data: Api.Bath.Notification) {
    return request({
        url: '/bath/notification',
        method: 'put',
        data
    });
}

/**
 * 删除通知
 */
export function fetchDeleteBathNotification(notificationIds: number[]) {
    return request({
        url: `/bath/notification/${notificationIds.join(',')}`,
        method: 'delete'
    });
}

/**
 * 发送通知
 */
export function fetchSendNotification(data: Api.Bath.Notification) {
    return request({
        url: '/bath/notification/send',
        method: 'post',
        data
    });
}

// ==================== 支付管理 ====================

/**
 * 获取支付列表
 */
export function fetchGetBathPaymentList(params?: any) {
    return request<Api.Common.PaginatedData<Api.Bath.Payment>>({
        url: '/bath/payment/list',
        method: 'get',
        params
    });
}

/**
 * 获取支付详情
 */
export function fetchGetBathPaymentDetail(paymentId: number) {
    return request<Api.Bath.Payment>({
        url: `/bath/payment/${paymentId}`,
        method: 'get'
    });
}

/**
 * 新增支付
 */
export function fetchAddBathPayment(data: Api.Bath.Payment) {
    return request({
        url: '/bath/payment',
        method: 'post',
        data
    });
}

/**
 * 修改支付
 */
export function fetchUpdateBathPayment(data: Api.Bath.Payment) {
    return request({
        url: '/bath/payment',
        method: 'put',
        data
    });
}

/**
 * 删除支付
 */
export function fetchDeleteBathPayment(paymentIds: number[]) {
    return request({
        url: `/bath/payment/${paymentIds.join(',')}`,
        method: 'delete'
    });
}

/**
 * 处理退款
 */
export function fetchRefundPayment(paymentId: number, refundReason: string) {
    return request({
        url: `/bath/payment/refund/${paymentId}`,
        method: 'put',
        data: refundReason
    });
}

// ==================== 宠物档案管理 ====================

/**
 * 获取宠物档案列表
 */
export function fetchGetPetProfileList(params?: any) {
    return request<Api.Common.PaginatedData<Api.Bath.PetProfile>>({
        url: '/bath/pet/list',
        method: 'get',
        params
    });
}

/**
 * 获取宠物档案详情
 */
export function fetchGetPetProfileDetail(petId: number) {
    return request<Api.Bath.PetProfile>({
        url: `/bath/pet/${petId}`,
        method: 'get'
    });
}

/**
 * 新增宠物档案
 */
export function fetchAddPetProfile(data: Api.Bath.PetProfile) {
    return request({
        url: '/bath/pet',
        method: 'post',
        data
    });
}

/**
 * 修改宠物档案
 */
export function fetchUpdatePetProfile(data: Api.Bath.PetProfile) {
    return request({
        url: '/bath/pet',
        method: 'put',
        data
    });
}

/**
 * 删除宠物档案
 */
export function fetchDeletePetProfile(petIds: number[]) {
    return request({
        url: `/bath/pet/${petIds.join(',')}`,
        method: 'delete'
    });
}

/**
 * 根据用户ID获取宠物列表（小程序端）
 */
export function fetchGetPetProfileListByUser(userId: number) {
    return request<Api.Bath.PetProfile[]>({
        url: '/bath/pet/listByUser',
        method: 'get',
        params: { userId }
    });
}

/**
 * 获取默认宠物（小程序端）
 */
export function fetchGetDefaultPet(userId: number) {
    return request<Api.Bath.PetProfile>({
        url: '/bath/pet/default',
        method: 'get',
        params: { userId }
    });
}

// ==================== 会员管理 ====================

/**
 * 获取会员信息列表
 */
export function fetchGetMemberInfoList(params?: any) {
    return request<Api.Common.PaginatedData<Api.Bath.MemberInfo>>({
        url: '/bath/member/list',
        method: 'get',
        params
    });
}

/**
 * 获取会员信息详情
 */
export function fetchGetMemberInfoDetail(memberId: number) {
    return request<Api.Bath.MemberInfo>({
        url: `/bath/member/${memberId}`,
        method: 'get'
    });
}

/**
 * 根据用户ID获取会员信息（小程序端）
 */
export function fetchGetMemberInfoByUser(userId: number) {
    return request<Api.Bath.MemberInfo>({
        url: '/bath/member/info',
        method: 'get',
        params: { userId }
    });
}

/**
 * 新增会员信息
 */
export function fetchAddMemberInfo(data: Api.Bath.MemberInfo) {
    return request({
        url: '/bath/member',
        method: 'post',
        data
    });
}

/**
 * 修改会员信息
 */
export function fetchUpdateMemberInfo(data: Api.Bath.MemberInfo) {
    return request({
        url: '/bath/member',
        method: 'put',
        data
    });
}

/**
 * 删除会员信息
 */
export function fetchDeleteMemberInfo(memberIds: number[]) {
    return request({
        url: `/bath/member/${memberIds.join(',')}`,
        method: 'delete'
    });
}

// ==================== 积分记录管理 ====================

/**
 * 获取积分记录列表
 */
export function fetchGetPointsRecordList(params?: any) {
    return request<Api.Common.PaginatedData<Api.Bath.PointsRecord>>({
        url: '/bath/points/list',
        method: 'get',
        params
    });
}

/**
 * 获取积分记录详情
 */
export function fetchGetPointsRecordDetail(recordId: number) {
    return request<Api.Bath.PointsRecord>({
        url: `/bath/points/${recordId}`,
        method: 'get'
    });
}

/**
 * 根据用户ID获取积分记录列表（小程序端）
 */
export function fetchGetPointsRecordListByUser(userId: number) {
    return request<Api.Bath.PointsRecord[]>({
        url: '/bath/points/listByUser',
        method: 'get',
        params: { userId }
    });
}

/**
 * 删除积分记录
 */
export function fetchDeletePointsRecord(recordIds: number[]) {
    return request({
        url: `/bath/points/${recordIds.join(',')}`,
        method: 'delete'
    });
}

