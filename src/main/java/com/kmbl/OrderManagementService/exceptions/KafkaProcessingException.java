package com.kmbl.OrderManagementService.exceptions;

public class KafkaProcessingException extends Exception{

    private KafkaException type;

    public enum KafkaException{
        INCORRECT_FORMAT("message format incorrect"),
        MISSING_ORDER("missing order"),
        MISSING_ORDER_ITEM("order item  doesnt persit in database"),
        INVALID_MESSAGE("invalid message format");

        private String message;

        KafkaException(String message){
            this.message = message;
        }
        public String getMessage(){return this.message;}

    }

    public KafkaProcessingException(KafkaException type,String message, Throwable ex){
        super(message, ex);
        this.type = type;
    }

    public KafkaProcessingException(KafkaException type,String message){
        super(message);
        this.type = type;
    }

    public KafkaException getType() {
        return type;
    }

}
