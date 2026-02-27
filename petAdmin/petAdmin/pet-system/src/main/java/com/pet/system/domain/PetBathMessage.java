package com.pet.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.pet.common.annotation.Excel;
import com.pet.common.annotation.Excel.ColumnType;
import com.pet.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 聊天消息对象 pet_bath_message
 * 
 * @author Pet
 */
public class PetBathMessage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 消息ID */
    @Excel(name = "消息ID", cellType = ColumnType.NUMERIC)
    private Long messageId;

    /** 发送者ID */
    @Excel(name = "发送者ID", cellType = ColumnType.NUMERIC)
    private Long senderId;

    /** 发送者类型（0=用户,1=商家） */
    @Excel(name = "发送者类型", readConverterExp = "0=用户,1=商家")
    private String senderType;

    /** 接收者ID */
    @Excel(name = "接收者ID", cellType = ColumnType.NUMERIC)
    private Long receiverId;

    /** 接收者类型（0=用户,1=商家） */
    @Excel(name = "接收者类型", readConverterExp = "0=用户,1=商家")
    private String receiverType;

    /** 消息类型（0=文字,1=图片,2=视频） */
    @Excel(name = "消息类型", readConverterExp = "0=文字,1=图片,2=视频")
    private String messageType;

    /** 消息内容 */
    @Excel(name = "消息内容")
    private String content;

    /** 文件URL */
    @Excel(name = "文件URL")
    private String fileUrl;

    /** 文件大小（字节） */
    @Excel(name = "文件大小", cellType = ColumnType.NUMERIC)
    private Long fileSize;

    /** 视频时长（秒） */
    @Excel(name = "视频时长", cellType = ColumnType.NUMERIC)
    private Integer fileDuration;

    /** 缩略图URL */
    @Excel(name = "缩略图URL")
    private String thumbnailUrl;

    /** 是否已读（0=未读,1=已读） */
    @Excel(name = "是否已读", readConverterExp = "0=未读,1=已读")
    private String isRead;

    /** 阅读时间 */
    @Excel(name = "阅读时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;

    public Long getMessageId()
    {
        return messageId;
    }

    public void setMessageId(Long messageId)
    {
        this.messageId = messageId;
    }

    public Long getSenderId()
    {
        return senderId;
    }

    public void setSenderId(Long senderId)
    {
        this.senderId = senderId;
    }

    public String getSenderType()
    {
        return senderType;
    }

    public void setSenderType(String senderType)
    {
        this.senderType = senderType;
    }

    public Long getReceiverId()
    {
        return receiverId;
    }

    public void setReceiverId(Long receiverId)
    {
        this.receiverId = receiverId;
    }

    public String getReceiverType()
    {
        return receiverType;
    }

    public void setReceiverType(String receiverType)
    {
        this.receiverType = receiverType;
    }

    public String getMessageType()
    {
        return messageType;
    }

    public void setMessageType(String messageType)
    {
        this.messageType = messageType;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getFileUrl()
    {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl)
    {
        this.fileUrl = fileUrl;
    }

    public Long getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    public Integer getFileDuration()
    {
        return fileDuration;
    }

    public void setFileDuration(Integer fileDuration)
    {
        this.fileDuration = fileDuration;
    }

    public String getThumbnailUrl()
    {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl)
    {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getIsRead()
    {
        return isRead;
    }

    public void setIsRead(String isRead)
    {
        this.isRead = isRead;
    }

    public Date getReadTime()
    {
        return readTime;
    }

    public void setReadTime(Date readTime)
    {
        this.readTime = readTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("messageId", getMessageId())
            .append("senderId", getSenderId())
            .append("senderType", getSenderType())
            .append("receiverId", getReceiverId())
            .append("receiverType", getReceiverType())
            .append("messageType", getMessageType())
            .append("content", getContent())
            .append("fileUrl", getFileUrl())
            .append("fileSize", getFileSize())
            .append("fileDuration", getFileDuration())
            .append("thumbnailUrl", getThumbnailUrl())
            .append("isRead", getIsRead())
            .append("readTime", getReadTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}










