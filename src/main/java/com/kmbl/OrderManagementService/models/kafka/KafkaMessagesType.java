package com.kmbl.OrderManagementService.models.kafka;

import lombok.Getter;

@Getter
public enum KafkaMessagesType {
    DELETED_ORDER("delete-order-queue"), PAYMENT("payments-queue");

    KafkaMessagesType(String topic) {
        kafkaTopic = topic.toLowerCase();
    }

    private final String kafkaTopic;

}
