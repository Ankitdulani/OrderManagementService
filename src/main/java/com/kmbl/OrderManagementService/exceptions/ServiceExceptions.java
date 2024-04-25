package com.kmbl.OrderManagementService.exceptions;

import org.springframework.http.HttpStatus;

public class ServiceExceptions extends Exception {

    private Type exceptionType;

    public enum Type {
        PARSING_EXCEPTION(HttpStatus.BAD_REQUEST),
        VALIDATION_FAILURE(HttpStatus.BAD_REQUEST);

        HttpStatus status;

        Type(HttpStatus status) {
            this.status = status;
        }

        public HttpStatus getStatus() {
            return status;
        }
    }

    public ServiceExceptions(Type type, String message) {
        super(message);
        this.exceptionType = type;
    }

    public Type getExceptionType() {
        return exceptionType;
    }
}
