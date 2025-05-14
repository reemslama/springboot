package com.example.dection_anomalie.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import com.example.dection_anomalie.entity.Message;

public interface MessageRepo extends MongoRepository<Message, String> {

    // Méthode pour récupérer les messages entre un utilisateur et un nutritionniste
    List<Message> findBySenderIdAndReceiverId(String senderId, String receiverId);

    // Méthode pour récupérer les messages non lus pour un utilisateur
    List<Message> findByReceiverIdAndIsRead(String receiverId, boolean isRead);

}
