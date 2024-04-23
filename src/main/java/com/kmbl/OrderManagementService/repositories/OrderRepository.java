package com.kmbl.OrderManagementService.repositories;


import com.kmbl.OrderManagementService.models.Order;
import org.springframework.data.repository.CrudRepository;

public interface  OrderRepository extends CrudRepository<Order,String> {


}