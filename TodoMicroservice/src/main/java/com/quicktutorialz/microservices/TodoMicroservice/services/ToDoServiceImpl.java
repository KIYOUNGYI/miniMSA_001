package com.quicktutorialz.microservices.TodoMicroservice.services;

import com.quicktutorialz.microservices.TodoMicroservice.daos.ToDoDao;
import com.quicktutorialz.microservices.TodoMicroservice.entities.ToDo;
import com.quicktutorialz.microservices.TodoMicroservice.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ToDoServiceImpl implements ToDoService {

    @Autowired
    ToDoDao toDoDao;

    @Override
    public List<ToDo> getToDos(String email) {
        return toDoDao.findByFkUser(email);
    }

    @Override
    public ToDo addToDo(ToDo toDo) {
        return toDoDao.save(toDo);
    }

    @Override
    public void deleteToDo(Integer id) {
        toDoDao.delete(id);
    }
}
