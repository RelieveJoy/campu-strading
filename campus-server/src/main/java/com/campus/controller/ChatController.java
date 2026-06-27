package com.campus.controller;

import com.campus.context.BaseContext;
import com.campus.dto.MessageDTO;
import com.campus.result.Result;
import com.campus.service.MessageService;
import com.campus.vo.ConversationVO;
import com.campus.vo.MessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "聊天相关接口")
public class ChatController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @Operation(summary = "查询某个商品下与对方的聊天记录")
    @GetMapping("/{itemId}")
    public Result<List<MessageVO>> getHistory(@PathVariable Long itemId,
                                               @RequestParam String sourceType,
                                               @RequestParam Long receiverId) {
        List<MessageVO> list = messageService.getHistory(itemId, sourceType, BaseContext.getCurrentId(), receiverId);
        return Result.success(list);
    }

    @Operation(summary = "发送消息")
    @PostMapping
    public Result<MessageVO> send(@RequestBody @Valid MessageDTO dto) {
        Long senderId = BaseContext.getCurrentId();
        String st = dto.getSourceType() != null ? dto.getSourceType() : "item";
        MessageVO vo = messageService.send(senderId, dto.getItemId(), st, dto.getReceiverId(), dto.getContent());

        // 只推送给收发双方各自的频道（不广播给所有订阅者）
        messagingTemplate.convertAndSend(
                "/topic/chat." + senderId + "." + dto.getItemId(), vo);
        messagingTemplate.convertAndSend(
                "/topic/chat." + dto.getReceiverId() + "." + dto.getItemId(), vo);

        return Result.success(vo);
    }

    @Operation(summary = "获取当前用户的对话列表")
    @GetMapping("/conversations")
    public Result<List<ConversationVO>> listConversations() {
        List<ConversationVO> list = messageService.listConversations(BaseContext.getCurrentId());
        return Result.success(list);
    }

    @Operation(summary = "标记商品消息为已读")
    @PostMapping("/{itemId}/read")
    public Result<String> markRead(@PathVariable Long itemId,
                                    @RequestParam(defaultValue = "item") String sourceType) {
        messageService.markRead(BaseContext.getCurrentId(), itemId, sourceType);
        return Result.success();
    }
}
