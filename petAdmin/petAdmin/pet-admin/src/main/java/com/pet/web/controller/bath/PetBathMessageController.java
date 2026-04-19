package com.pet.web.controller.bath;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.pet.common.annotation.Log;
import com.pet.common.core.controller.BaseController;
import com.pet.common.core.domain.AjaxResult;
import com.pet.common.core.page.TableDataInfo;
import com.pet.common.enums.BusinessType;
import com.pet.common.utils.poi.ExcelUtil;
import com.pet.system.domain.PetBathMessage;
import com.pet.business.service.IPetBathMessageService;

/**
 * 聊天消息Controller
 * 
 * @author Pet
 */
@RestController
@RequestMapping("/bath/message")
public class PetBathMessageController extends BaseController
{
    @Autowired
    private IPetBathMessageService petBathMessageService;

    /**
     * 查询聊天消息列表
     */
    @PreAuthorize("@ss.hasPermi('bath:message:list')")
    @GetMapping("/list")
    public TableDataInfo list(PetBathMessage petBathMessage)
    {
        startPage();
        List<PetBathMessage> list = petBathMessageService.selectPetBathMessageList(petBathMessage);
        return getDataTable(list);
    }

    /**
     * 查询聊天列表（最新一条消息）
     */
    @GetMapping("/chatList")
    public AjaxResult chatList(@RequestParam Long userId, @RequestParam String userType)
    {
        List<PetBathMessage> list = petBathMessageService.selectChatList(userId, userType);
        return success(list);
    }

    /**
     * 查询聊天记录
     */
    @GetMapping("/chatHistory")
    public AjaxResult chatHistory(
            @RequestParam Long senderId,
            @RequestParam String senderType,
            @RequestParam Long receiverId,
            @RequestParam String receiverType)
    {
        List<PetBathMessage> list = petBathMessageService.selectChatHistory(senderId, senderType, receiverId, receiverType);
        return success(list);
    }

    /**
     * 查询未读消息数量
     */
    @GetMapping("/unreadCount")
    public AjaxResult unreadCount(@RequestParam Long receiverId, @RequestParam String receiverType)
    {
        int count = petBathMessageService.selectUnreadCount(receiverId, receiverType);
        return success(count);
    }

    /**
     * 导出聊天消息列表
     */
    @PreAuthorize("@ss.hasPermi('bath:message:export')")
    @Log(title = "聊天消息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PetBathMessage petBathMessage)
    {
        List<PetBathMessage> list = petBathMessageService.selectPetBathMessageList(petBathMessage);
        ExcelUtil<PetBathMessage> util = new ExcelUtil<PetBathMessage>(PetBathMessage.class);
        util.exportExcel(response, list, "聊天消息数据");
    }

    /**
     * 获取聊天消息详细信息
     */
    @PreAuthorize("@ss.hasPermi('bath:message:query')")
    @GetMapping(value = "/{messageId}")
    public AjaxResult getInfo(@PathVariable("messageId") Long messageId)
    {
        return success(petBathMessageService.selectPetBathMessageByMessageId(messageId));
    }

    /**
     * 新增聊天消息
     */
    @PostMapping
    public AjaxResult add(@RequestBody PetBathMessage petBathMessage)
    {
        return toAjax(petBathMessageService.insertPetBathMessage(petBathMessage));
    }

    /**
     * 修改聊天消息
     */
    @PreAuthorize("@ss.hasPermi('bath:message:edit')")
    @Log(title = "聊天消息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PetBathMessage petBathMessage)
    {
        return toAjax(petBathMessageService.updatePetBathMessage(petBathMessage));
    }

    /**
     * 标记消息为已读
     */
    @PostMapping("/markRead")
    public AjaxResult markRead(@RequestBody Long[] messageIds)
    {
        return toAjax(petBathMessageService.markMessagesAsRead(messageIds));
    }

    /**
     * 标记聊天为已读
     */
    @PostMapping("/markChatRead")
    public AjaxResult markChatRead(
            @RequestParam Long senderId,
            @RequestParam String senderType,
            @RequestParam Long receiverId,
            @RequestParam String receiverType)
    {
        return toAjax(petBathMessageService.markChatAsRead(senderId, senderType, receiverId, receiverType));
    }

    /**
     * 删除聊天消息
     */
    @PreAuthorize("@ss.hasPermi('bath:message:remove')")
    @Log(title = "聊天消息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{messageIds}")
    public AjaxResult remove(@PathVariable Long[] messageIds)
    {
        return toAjax(petBathMessageService.deletePetBathMessageByMessageIds(messageIds));
    }
}
















