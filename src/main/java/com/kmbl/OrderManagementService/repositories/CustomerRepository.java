package com.kmbl.OrderManagementService.repositories;

import com.kmbl.OrderManagementService.models.Customer;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.stereotype.Repository;

@Repository
@EnableScan
public interface CustomerRepository extends DynamoDBCrudRepository<Customer, String> {

}