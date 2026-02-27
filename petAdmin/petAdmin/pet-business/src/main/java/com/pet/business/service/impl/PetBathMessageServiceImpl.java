package com.pet.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pet.common.utils.SecurityUtils;
import com.pet.business.mapper.PetBathMessageMapper;
import com.pet.system.domain.PetBathMessage;
import com.pet.business.service.IPetBathMessageService;

/**
 * 聊天消息Service业务层处理
 * 
 * @author Pet
 */
@Service
public class PetBathMessageServiceImpl implements IPetBathMessageService
{
    @Autowired
    private PetBathMessageMapper petBathMessageMapper;

    /**
     * 查询聊天消息
     * 
     * @param messageId 聊天消息主键
     * @return 聊天消息
     */
    @Override
    public PetBathMessage selectPetBathMessageByMessageId(Long messageId)
    {
        return petBathMessageMapper.selectPetBathMessageByMessageId(messageId);
    }

    /**
     * 查询聊天消息列表
     * 
     * @param petBathMessage 聊天消息
     * @return 聊天消息
     */
    @Override
    public List<PetBathMessage> selectPetBathMessageList(PetBathMessage petBathMessage)
    {
        return petBathMessageMapper.selectPetBathMessageList(petBathMessage);
    }

    /**
     * 查询两个用户之间的聊天记录
     * 
     * @param senderId 发送者ID
     * @param senderType 发送者类型
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 聊天消息集合
     */
    @Override
    public List<PetBathMessage> selectChatHistory(Long senderId, String senderType, Long receiverId, String receiverType)
    {
        return petBathMessageMapper.selectChatHistory(senderId, senderType, receiverId, receiverType);
    }

    /**
     * 查询未读消息数量
     * 
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 未读消息数量
     */
    @Override
    public int selectUnreadCount(Long receiverId, String receiverType)
    {
        return petBathMessageMapper.selectUnreadCount(receiverId, receiverType);
    }

    /**
     * 查询与某个用户的聊天列表（最新一条消息）
     * 
     * @param userId 用户ID
     * @param userType 用户类型
     * @return 聊天消息集合
     */
    @Override
    public List<PetBathMessage> selectChatList(Long userId, String userType)
    {
        return petBathMessageMapper.selectChatList(userId, userType);
    }

    /**
     * 新增聊天消息
     * 
     * @param petBathMessage 聊天消息
     * @return 结果
     */
    @Override
    public int insertPetBathMessage(PetBathMessage petBathMessage)
    {
        petBathMessage.setCreateBy(SecurityUtils.getUsername());
        // 默认未读
        if (petBathMessage.getIsRead() == null || petBathMessage.getIsRead().isEmpty())
        {
            petBathMessage.setIsRead("0");
        }
        return petBathMessageMapper.insertPetBathMessage(petBathMessage);
    }

    /**
     * 修改聊天消息
     * 
     * @param petBathMessage 聊天消息
     * @return 结果
     */
    @Override
    public int updatePetBathMessage(PetBathMessage petBathMessage)
    {
        petBathMessage.setUpdateBy(SecurityUtils.getUsername());
        return petBathMessageMapper.updatePetBathMessage(petBathMessage);
    }

    /**
     * 标记消息为已读
     * 
     * @param messageIds 消息ID数组
     * @return 结果
     */
    @Override
    public int markMessagesAsRead(Long[] messageIds)
    {
        return petBathMessageMapper.markMessagesAsRead(messageIds);
    }

    /**
     * 标记与某个用户的所有消息为已读
     * 
     * @param senderId 发送者ID
     * @param senderType 发送者类型
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 结果
     */
    @Override
    public int markChatAsRead(Long senderId, String senderType, Long receiverId, String receiverType)
    {
        return petBathMessageMapper.markChatAsRead(senderId, senderType, receiverId, receiverType);
    }

    /**
     * 批量删除聊天消息
     * 
     * @param messageIds 需要删除的聊天消息主键
     * @return 结果
     */
    @Override
    public int deletePetBathMessageByMessageIds(Long[] messageIds)
    {
        return petBathMessageMapper.deletePetBathMessageByMessageIds(messageIds);
    }

    /**
     * 删除聊天消息信息
     * 
     * @param messageId 聊天消息主键
     * @return 结果
     */
    @Override
    public int deletePetBathMessageByMessageId(Long messageId)
    {
        return petBathMessageMapper.deletePetBathMessageByMessageId(messageId);
    }
}










