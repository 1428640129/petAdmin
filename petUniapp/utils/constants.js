/**
 * 状态常量定义
 * 与后端保持一致，使用数字表示状态
 */

// 预约状态
export const APPOINTMENT_STATUS = {
	PENDING: '0',      // 待确认
	CONFIRMED: '1',    // 已确认
	IN_SERVICE: '2',   // 服务中
	COMPLETED: '3',    // 已完成
	CANCELLED: '4'     // 已取消
}

// 预约状态文本映射
export const APPOINTMENT_STATUS_TEXT = {
	'0': '待处理',
	'1': '已确认',
	'2': '服务中',
	'3': '已完成',
	'4': '已取消'
}

// 订单状态
export const ORDER_STATUS = {
	UNPAID: '0',       // 待支付
	PAID: '1',         // 已支付
	IN_SERVICE: '2',   // 服务中
	COMPLETED: '3',    // 已完成
	REFUNDED: '4',     // 已退款
	CANCELLED: '5'     // 已取消
}

// 订单状态文本映射
export const ORDER_STATUS_TEXT = {
	'0': '待支付',
	'1': '已支付',
	'2': '服务中',
	'3': '已完成',
	'4': '已退款',
	'5': '已取消'
}

// 支付方式
export const PAYMENT_TYPE = {
	ALIPAY: '0',       // 支付宝
	WECHAT: '1',       // 微信
	BALANCE: '2'       // 余额
}

// 支付方式文本映射
export const PAYMENT_TYPE_TEXT = {
	'0': '支付宝',
	'1': '微信',
	'2': '余额'
}

// 支付状态
export const PAYMENT_STATUS = {
	PENDING: '0',     // 待支付
	PAID: '1',        // 已支付
	FAILED: '2',      // 支付失败
	REFUNDED: '3'     // 已退款
}

// 支付状态文本映射
export const PAYMENT_STATUS_TEXT = {
	'0': '待支付',
	'1': '已支付',
	'2': '支付失败',
	'3': '已退款'
}

// 通知类型
export const NOTIFICATION_TYPE = {
	APPOINTMENT_CREATED: '0',    // 预约创建
	APPOINTMENT_CONFIRMED: '1',  // 预约确认
	SERVICE_STARTED: '2',        // 服务开始
	SERVICE_COMPLETED: '3',      // 服务完成
	ORDER_PAID: '4',            // 订单支付
	ORDER_CANCELLED: '5'        // 订单取消
}

// 通知类型文本映射
export const NOTIFICATION_TYPE_TEXT = {
	'0': '预约创建',
	'1': '预约确认',
	'2': '服务开始',
	'3': '服务完成',
	'4': '订单支付',
	'5': '订单取消'
}

// 宠物类型
export const PET_TYPE = {
	SHORT_HAIR: '0',  // 短毛
	LONG_HAIR: '1'    // 长毛
}

// 宠物类型文本映射
export const PET_TYPE_TEXT = {
	'0': '短毛',
	'1': '长毛'
}

// 服务类型
export const SERVICE_TYPE = {
	BASIC: '0',        // 基础洗浴
	DEEP_CARE: '1',    // 深度护理
	LUXURY: '2'        // 豪华套餐
}

// 服务类型文本映射
export const SERVICE_TYPE_TEXT = {
	'0': '基础洗浴',
	'1': '深度护理',
	'2': '豪华套餐'
}

