package com.kmbl.OrderManagementService.models;
import java.util.List;
public class IMSResponseObject {
    private String inventoryOrderStatus;
    private List<ResponseObject> responseObjects;

    // Getters and setters
    public String getInventoryOrderStatus() {
        return inventoryOrderStatus;
    }

    public void setInventoryOrderStatus(String inventoryOrderStatus) {
        this.inventoryOrderStatus = inventoryOrderStatus;
    }


    public List<ResponseObject> getResponseObjects() {
        return responseObjects;
    }

    public void setResponseObjects(List<ResponseObject> responseObjects) {
        this.responseObjects = responseObjects;
    }
}
