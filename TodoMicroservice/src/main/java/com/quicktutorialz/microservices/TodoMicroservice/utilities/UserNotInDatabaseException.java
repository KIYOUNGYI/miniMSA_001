package com.quicktutorialz.microservices.TodoMicroservice.utilities;

public class UserNotInDatabaseException extends Exception{

    public UserNotInDatabaseException(String message){
        super(message);
    }
}
