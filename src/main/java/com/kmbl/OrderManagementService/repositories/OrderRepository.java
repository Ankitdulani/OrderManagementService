package com.kmbl.OrderManagementService.repositories;


import com.kmbl.OrderManagementService.models.Order;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@EnableScan
public interface  OrderRepository extends CrudRepository<Order,String> {


}