package com.quicktutorialz.microservices.TodoMicroservice.services;

import com.quicktutorialz.microservices.TodoMicroservice.entities.ToDo;
import com.quicktutorialz.microservices.TodoMicroservice.utilities.UserNotLoggedException;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface ToDoService {
    List<ToDo> getToDos(String email) throws UnsupportedEncodingException, UserNotLoggedException;
    ToDo addToDo(ToDo toDo) throws UnsupportedEncodingException, UserNotLoggedException;
    void deleteToDo(Integer id) throws UnsupportedEncodingException, UserNotLoggedException;
}
