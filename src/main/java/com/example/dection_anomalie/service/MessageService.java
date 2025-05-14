package com.example.dection_anomalie.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.dection_anomalie.entity.Message;
import com.example.dection_anomalie.repository.MessageRepo;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepo messageRepository; // Utilisation de MessageRepo ici

    // Récupérer les messages entre un utilisateur et un nutritionniste
    public List<Message> getMessages(String senderId, String receiverId) {
        return messageRepository.findBySenderIdAndReceiverId(senderId, receiverId);
    }

    // Sauvegarder un message
    public void saveMessage(Message message) {
        messageRepository.save(message);
    }

    // Récupérer les messages non lus
    public List<Message> getUnreadMessages(String receiverId) {
        return messageRepository.findByReceiverIdAndIsRead(receiverId, false);
    }

    // Marquer les messages comme lus
    public void markMessagesAsRead(List<String> messageIds) {
        List<Message> messages = messageRepository.findAllById(messageIds);
        for (Message message : messages) {
            message.setRead(true);
        }
        messageRepository.saveAll(messages);
    }
}
