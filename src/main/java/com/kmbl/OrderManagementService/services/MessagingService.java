package com.kmbl.OrderManagementService.services;

import com.kmbl.OrderManagementService.models.kafka.Message;

public interface MessagingService {
    public void publish(Message message);
}
