package com.quicktutorialz.microservices.TodoMicroservice.utilities;

public class UserNotLoggedException extends Exception{

    public UserNotLoggedException(String message){
        super(message);
    }
}
