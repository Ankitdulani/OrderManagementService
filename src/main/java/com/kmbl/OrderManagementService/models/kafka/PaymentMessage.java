package com.kmbl.OrderManagementService.models.kafka;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kmbl.OrderManagementService.configuration.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMessage implements Message {

    String orderId;
    String status;

    @Override
    @JsonIgnore
    public String getMessage() {
        return this.toString();
    }

    @JsonIgnore
    public boolean isPaymentSuccessful() {
        return this.status.equals(Constants.SUCCESS);
    }

    @JsonIgnore
    public boolean isPaymentFailed() {
        return this.status.equals(Constants.FAILURE);
    }

    @Override
    @JsonIgnore
    public KafkaMessagesType getType() {
        return KafkaMessagesType.PAYMENT;
    }
}
