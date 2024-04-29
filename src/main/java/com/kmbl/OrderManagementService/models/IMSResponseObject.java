package com.kmbl.OrderManagementService.models;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IMSResponseObject {
    private String orderStatus;
    private List<ResponseObject> orderItemStatus;
}
