package com.example.dection_anomalie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.dection_anomalie.entity.Message;
import com.example.dection_anomalie.service.MessageService;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:4200")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // Récupérer les messages entre un utilisateur et un nutritionniste
    @GetMapping("/chat")
    public List<Message> getChat(@RequestParam String senderId, @RequestParam String receiverId) {
        return messageService.getMessages(senderId, receiverId);
    }

    // Envoyer un message
    @PostMapping
    public void sendMessage(@RequestBody Message message) {
        messageService.saveMessage(message);
    }

    // Récupérer les messages non lus pour un utilisateur
    @GetMapping("/unread/{receiverId}")
    public List<Message> getUnreadMessages(@PathVariable String receiverId) {
        return messageService.getUnreadMessages(receiverId);
    }

    // Marquer les messages comme lus
    @PostMapping("/mark-read")
    public void markAsRead(@RequestBody List<String> messageIds) {
        messageService.markMessagesAsRead(messageIds);
    }
}
