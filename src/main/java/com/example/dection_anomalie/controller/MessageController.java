package com.example.dection_anomalie.controller;

import com.example.dection_anomalie.entity.Message;
import com.example.dection_anomalie.repository.MessageRepo;
import com.example.dection_anomalie.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:4200")
public class MessageController {

    @Autowired
    private MessageRepo messageRepository;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse> sendMessage(@RequestBody Message message) {
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("Le contenu du message est vide."));
        }

        message.setTimestamp(new Date());
        Message savedMessage = messageRepository.save(message);
        return ResponseEntity.ok(ApiResponse.ok("Message envoyé avec succès.", savedMessage));
    }

    @GetMapping("/conversation")
    public ResponseEntity<ApiResponse> getConversation(
            @RequestParam String senderEmail,
            @RequestParam String receiverEmail) {

        List<Message> conversation = messageRepository.findConversationBetween(senderEmail, receiverEmail);
        conversation.sort(Comparator.comparing(Message::getTimestamp));
        return ResponseEntity.ok(ApiResponse.ok("Conversation récupérée avec succès.", conversation));
    }
}
