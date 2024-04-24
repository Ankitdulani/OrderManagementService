package com.kmbl.OrderManagementService.models.kafka;

import com.kmbl.OrderManagementService.exceptions.ServiceExceptions;

public interface Message {

    public String getMessage() throws ServiceExceptions;
    public KafkaMessagesType getType();
}
