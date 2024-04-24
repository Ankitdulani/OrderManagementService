package com.kmbl.OrderManagementService.services.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmbl.OrderManagementService.exceptions.ServiceExceptions;

public class ServiceUtils {

    private static  final ObjectMapper mapper = new ObjectMapper();

    public static String convertToString(Object object) throws ServiceExceptions {
        try{
            return mapper.writeValueAsString(object);
        }catch (JsonProcessingException e){
            throw new ServiceExceptions(ServiceExceptions.Type.PARSING_EXCEPTION,e.getMessage());
        }
    }
}
