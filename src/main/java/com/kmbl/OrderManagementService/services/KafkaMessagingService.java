package com.kmbl.OrderManagementService.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmbl.OrderManagementService.models.kafka.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaMessagingService implements MessagingService{

    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;

    private final String errorLogFormat = " DATE: %s - TYPE : { %s }";
    private final String errorLogFormatWithMessage = " DATE: %s - TYPE : { %s } - Message : { %s }";

    @Autowired
    public KafkaMessagingService(KafkaTemplate<String, String> template, ObjectMapper mapper){
        kafkaTemplate = template;
        objectMapper = mapper;
    }

    @Override
    public void publish(Message message) {
        try{
        kafkaTemplate.send(message.getType().getKafkaTopic(),message.getMessage());}
        catch(Exception e){
            log.error(String.format("Type:  [ %s ] Message: [ %s ]", message.getType(), e.getMessage()));
        }
    }
}
