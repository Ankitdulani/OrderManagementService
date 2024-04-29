package com.kmbl.OrderManagementService.exceptions;

public class ServiceExceptions extends Exception{

    private Type exceptionType;
    public enum Type{
        PARSING_EXCEPTION,
        BAD_REQUEST;
    }

    public ServiceExceptions(Type type,String message) {
        super(message);
        this.exceptionType = type;
    }
    public Type getExceptionType() {
        return this.exceptionType;
    }
}
