package com.pet.business.mapper;

import java.util.List;
import com.pet.system.domain.PetBathMessage;

/**
 * 聊天消息 数据层
 * 
 * @author Pet
 */
public interface PetBathMessageMapper
{
    /**
     * 查询聊天消息
     * 
     * @param messageId 聊天消息主键
     * @return 聊天消息
     */
    public PetBathMessage selectPetBathMessageByMessageId(Long messageId);

    /**
     * 查询聊天消息列表
     * 
     * @param petBathMessage 聊天消息
     * @return 聊天消息集合
     */
    public List<PetBathMessage> selectPetBathMessageList(PetBathMessage petBathMessage);

    /**
     * 查询两个用户之间的聊天记录
     * 
     * @param senderId 发送者ID
     * @param senderType 发送者类型
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 聊天消息集合
     */
    public List<PetBathMessage> selectChatHistory(Long senderId, String senderType, Long receiverId, String receiverType);

    /**
     * 查询未读消息数量
     * 
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 未读消息数量
     */
    public int selectUnreadCount(Long receiverId, String receiverType);

    /**
     * 查询与某个用户的聊天列表（最新一条消息）
     * 
     * @param userId 用户ID
     * @param userType 用户类型
     * @return 聊天消息集合
     */
    public List<PetBathMessage> selectChatList(Long userId, String userType);

    /**
     * 新增聊天消息
     * 
     * @param petBathMessage 聊天消息
     * @return 结果
     */
    public int insertPetBathMessage(PetBathMessage petBathMessage);

    /**
     * 修改聊天消息
     * 
     * @param petBathMessage 聊天消息
     * @return 结果
     */
    public int updatePetBathMessage(PetBathMessage petBathMessage);

    /**
     * 标记消息为已读
     * 
     * @param messageIds 消息ID数组
     * @return 结果
     */
    public int markMessagesAsRead(Long[] messageIds);

    /**
     * 标记与某个用户的所有消息为已读
     * 
     * @param senderId 发送者ID
     * @param senderType 发送者类型
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 结果
     */
    public int markChatAsRead(Long senderId, String senderType, Long receiverId, String receiverType);

    /**
     * 删除聊天消息
     * 
     * @param messageId 聊天消息主键
     * @return 结果
     */
    public int deletePetBathMessageByMessageId(Long messageId);

    /**
     * 批量删除聊天消息
     * 
     * @param messageIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePetBathMessageByMessageIds(Long[] messageIds);
}
















