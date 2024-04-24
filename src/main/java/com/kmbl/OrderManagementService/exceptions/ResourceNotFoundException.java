package com.kmbl.OrderManagementService.exceptions;

public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException(String errorMessage, String id) {
        super(errorMessage);
    }
}