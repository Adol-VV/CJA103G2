package com.momento.message.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.momento.message.model.MessageService;
import com.momento.message.model.MessageVO;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 取得指定文章的所有留言
     * GET /message/article/{articleId}
     */
    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<MessageVO>> getMessagesByArticle(@PathVariable Integer articleId) {
        List<MessageVO> messages = messageService.getMessagesByArticleId(articleId);
        return ResponseEntity.ok(messages);
    }

    /**
     * 取得所有留言
     * GET /message/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<MessageVO>> getAllMessages() {
        List<MessageVO> messages = messageService.getAll();
        return ResponseEntity.ok(messages);
    }

    /**
     * 取得單一留言
     * GET /message/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<MessageVO> getOneMessage(@PathVariable Integer id) {
        MessageVO message = messageService.getOneMessage(id);
        if (message == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(message);
    }

    /**
     * 新增留言
     * POST /message
     */
    @PostMapping
    public ResponseEntity<MessageVO> addMessage(@RequestBody MessageVO messageVO) {
        try {
            MessageVO savedMessage = messageService.addMessage(messageVO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 更新留言
     * PUT /message/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<MessageVO> updateMessage(@PathVariable Integer id, @RequestBody MessageVO messageVO) {
        MessageVO existingMessage = messageService.getOneMessage(id);
        if (existingMessage == null) {
            return ResponseEntity.notFound().build();
        }
        messageVO.setMessageId(id);
        MessageVO updatedMessage = messageService.updateMessage(messageVO);
        return ResponseEntity.ok(updatedMessage);
    }

    /**
     * 刪除留言
     * DELETE /message/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Integer id) {
        MessageVO message = messageService.getOneMessage(id);
        if (message == null) {
            return ResponseEntity.notFound().build();
        }
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}
