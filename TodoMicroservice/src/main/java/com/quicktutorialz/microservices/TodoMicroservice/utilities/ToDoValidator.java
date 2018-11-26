package com.quicktutorialz.microservices.TodoMicroservice.utilities;

import com.quicktutorialz.microservices.TodoMicroservice.entities.ToDo;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

//SPRING VALIDATOR
public class ToDoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ToDo.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        ToDo toDo = (ToDo) obj;

        String priority = toDo.getPriority();

        if(!"high".equals(priority) && !"low".equals(priority)){
            errors.rejectValue("priority", "Priority must be 'high' or 'low'!");
        }

//        String description = toDo.getDescription();
//
//        if(toDo.getDescription()==null ||   description=="" || description.isEmpty()){
//            errors.rejectValue("description","Description is empty.");
//        }
    }
}