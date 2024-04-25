package com.kmbl.OrderManagementService.services;

import static com.kmbl.OrderManagementService.exceptions.KafkaProcessingException.KafkaException.INCORRECT_FORMAT;
import static com.kmbl.OrderManagementService.exceptions.KafkaProcessingException.KafkaException.MISSING_ORDER;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmbl.OrderManagementService.configuration.Constants;
import com.kmbl.OrderManagementService.exceptions.KafkaProcessingException;
import com.kmbl.OrderManagementService.exceptions.ResourceNotFoundException;
import com.kmbl.OrderManagementService.models.kafka.Message;
import com.kmbl.OrderManagementService.models.kafka.PaymentMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class KafkaMessageProducer implements MessagingService{

    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaMessageProducer(KafkaTemplate<String, String> template){
        kafkaTemplate = template;
    }

    @Override
    public void publish(Message message) {
        try{
        kafkaTemplate.send(message.getType().getKafkaTopic(),message.getMessage());}
        catch(Exception e){
            log.error(String.format(Constants.errorLogFormatWithMessage,new Date(), message.getType(), e.getMessage()));
        }
    }
}
