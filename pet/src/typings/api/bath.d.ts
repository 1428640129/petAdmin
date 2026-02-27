declare namespace Api.Bath {
    /** 洗浴服务 */
    interface Service {
        serviceId?: number;
        serviceName: string;
        serviceDesc?: string;
        serviceType?: string;
        serviceImages?: string; // JSON格式存储多张图片URL
        duration?: number;
        status?: string;
        sortOrder?: number;
        prices?: ServicePrice[];
    }

    /** 服务价格梯度 */
    interface ServicePrice {
        priceId?: number;
        serviceId?: number;
        weightMin: number;
        weightMax: number;
        price: number;
    }

    /** 预约信息 */
    interface Appointment {
        appointmentId?: number;
        appointmentNo?: string;
        userId: number;
        petId?: number;
        petName?: string;
        petWeight?: number;
        serviceId: number;
        serviceName?: string;
        appointmentTime: string;
        expectedPrice?: number;
        actualPrice?: number;
        status?: string;
        cancelReason?: string;
        cancelTime?: string;
        remark?: string;
    }

    /** 订单信息 */
    interface Order {
        orderId?: number;
        orderNo?: string;
        appointmentId?: number;
        userId: number;
        serviceId: number;
        serviceName?: string;
        totalAmount: number;
        paidAmount?: number;
        refundAmount?: number;
        status?: string;
        payTime?: string;
        completeTime?: string;
        cancelTime?: string;
        cancelReason?: string;
        remark?: string;
    }

    /** 支付记录 */
    interface Payment {
        paymentId?: number;
        paymentNo?: string;
        orderId: number;
        orderNo?: string;
        userId: number;
        paymentType?: string;
        paymentAmount: number;
        status?: string;
        transactionId?: string;
        payTime?: string;
        refundTime?: string;
        refundReason?: string;
        remark?: string;
    }

    /** 评价评论 */
    interface Review {
        reviewId?: number;
        orderId: number;
        appointmentId?: number;
        userId: number;
        serviceId: number;
        rating: number;
        content?: string;
        images?: string;
        replyContent?: string;
        replyTime?: string;
        status?: string;
    }

    /** 通知记录 */
    interface Notification {
        notificationId?: number;
        userId: number;
        appointmentId?: number;
        orderId?: number;
        notificationType: string;
        title: string;
        content?: string;
        isRead?: string;
        readTime?: string;
    }
}

