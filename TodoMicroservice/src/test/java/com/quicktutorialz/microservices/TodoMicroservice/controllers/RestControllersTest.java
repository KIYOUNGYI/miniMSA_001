package com.quicktutorialz.microservices.TodoMicroservice.controllers;

import com.quicktutorialz.microservices.TodoMicroservice.entities.User;
import com.quicktutorialz.microservices.TodoMicroservice.services.LoginService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(RestControllers.class)
public class RestControllersTest {

    @Autowired
    private MockMvc mvc;

    @Mock
    LoginService loginService;


//    @Test
//    public void login(){
//        User user = new User("yky2798@gmail.com","Kiyoung Yi","password");
//        given()
//    }
}
