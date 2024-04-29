package com.kmbl.OrderManagementService.models;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "customer")
public class Customer implements User {

    @DynamoDBHashKey
    @DynamoDBAttribute(attributeName = "customerId")
    private String contactNumber;

    @DynamoDBAttribute
    private String name;

    @DynamoDBAttribute
    private String address;

    @DynamoDBAttribute
    private List<String> orderHistory = new ArrayList<>();

    @DynamoDBIgnore
    public UserType getUserType() {
        return UserType.CUSTOMER;
    }

    @JsonIgnore
    public void addToOrderHistory(String orderId) {
        orderHistory.add(orderId);
    }

    @Override
    public String getName() {
        return name;
    }
}
