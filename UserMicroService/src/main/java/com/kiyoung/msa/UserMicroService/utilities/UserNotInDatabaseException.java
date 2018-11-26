package com.kiyoung.msa.UserMicroService.utilities;

public class UserNotInDatabaseException extends Exception{

    public UserNotInDatabaseException(String message){
        super(message);
    }
}
