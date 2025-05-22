package com.example.dection_anomalie.repository;

import com.example.dection_anomalie.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface MessageRepo extends MongoRepository<Message, String> {

    @Query("{ '$or': [ " +
           "{ 'senderEmail': ?0, 'receiverEmail': ?1 }, " +
           "{ 'senderEmail': ?1, 'receiverEmail': ?0 } ] }")
    List<Message> findConversationBetween(String email1, String email2);
}
