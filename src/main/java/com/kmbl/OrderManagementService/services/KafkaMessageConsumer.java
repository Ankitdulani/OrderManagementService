package com.kmbl.OrderManagementService.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmbl.OrderManagementService.configuration.Constants;
import com.kmbl.OrderManagementService.exceptions.KafkaProcessingException;
import com.kmbl.OrderManagementService.exceptions.ResourceNotFoundException;
import com.kmbl.OrderManagementService.models.kafka.PaymentMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.kmbl.OrderManagementService.exceptions.KafkaProcessingException.KafkaException.INCORRECT_FORMAT;
import static com.kmbl.OrderManagementService.exceptions.KafkaProcessingException.KafkaException.MISSING_ORDER;

@Slf4j
@Component
public class KafkaMessageConsumer {

    private ObjectMapper objectMapper;
    private OrderService orderService;


    @Autowired
    public KafkaMessageConsumer( ObjectMapper mapper,OrderService service){
        orderService = service;
        objectMapper = mapper;
    }

    @KafkaListener(topics = "payments-queue",
            groupId = "consumerGroup-" + "#{T(java.util.UUID).randomUUID()}",
            autoStartup = "true")
    public void paymentProcessing(@Payload String kafkaMessage)  {

        try {
            log.info(kafkaMessage);
            PaymentMessage message = objectMapper.readValue(kafkaMessage, PaymentMessage.class);
            orderService.paymentProcessing(message);

        }catch ( JsonProcessingException ex){
            log.error(String.format(Constants.errorLogFormat,new Date(), INCORRECT_FORMAT),ex);
        }catch (KafkaProcessingException ex){
            log.error(String.format(Constants.errorLogFormatWithMessage,new Date(),ex.getType() ,ex.getMessage() ),ex);
        }catch (ResourceNotFoundException ex){
            log.error(String.format(Constants.errorLogFormatWithMessage,new Date(),MISSING_ORDER,MISSING_ORDER.getMessage()),ex);
        }
    }
}
