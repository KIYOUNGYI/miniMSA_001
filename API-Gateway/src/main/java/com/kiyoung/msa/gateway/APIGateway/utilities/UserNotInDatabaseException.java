package com.kiyoung.msa.gateway.APIGateway.utilities;

public class UserNotInDatabaseException extends Exception{

    public UserNotInDatabaseException(String message){
        super(message);
    }
}
