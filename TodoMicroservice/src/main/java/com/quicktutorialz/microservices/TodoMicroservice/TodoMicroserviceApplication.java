package com.quicktutorialz.microservices.TodoMicroservice;

import com.quicktutorialz.microservices.TodoMicroservice.daos.ToDoDao;
import com.quicktutorialz.microservices.TodoMicroservice.daos.ToDoDao;
import com.quicktutorialz.microservices.TodoMicroservice.daos.UserDao;
import com.quicktutorialz.microservices.TodoMicroservice.entities.ToDo;
import com.quicktutorialz.microservices.TodoMicroservice.entities.User;
import com.quicktutorialz.microservices.TodoMicroservice.utilities.EncryptionUtils;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;

import javax.validation.constraints.Null;
import java.util.Date;

@SpringBootApplication
public class TodoMicroserviceApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(TodoMicroserviceApplication.class);


    @Autowired
    UserDao userDao; //userDaoImpl   UserDao userDaoImpl = new UserDaoImpl();

    @Autowired
    ToDoDao todoDao;

    @Autowired
    EncryptionUtils encryptionUtils;

    public static void main(String[] args) {
        SpringApplication.run(TodoMicroserviceApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        //...
        log.info("Let's fill H2 in-memory database");

        // everything here is implemented before our microservices will be available for Http Requests

        String encryptedPwd;
        encryptedPwd = encryptionUtils.encrypt("password");
        userDao.save(new User("yky2798@gmail.com", "Kiyoung Yi", encryptedPwd));
        userDao.save(new User("dohee@gmail.com", "Dohee Kim", encryptedPwd));


        todoDao.save(new ToDo(1, "Learn microservices", new Date(), "high", "yky2798@gmail.com"));
        todoDao.save(new ToDo(2, "Learn MyBatis", new Date(), "high", "dohee@gmail.com"));

        todoDao.save(new ToDo(null, "Do microservices assignment", new Date(), "high", "yky2798@gmail.com"));
        todoDao.save(new ToDo(4, "Do MyBatis assignment", new Date(), "high", "dohee@gmail.com"));

        todoDao.save(new ToDo(null, "Review microservices", new Date(), "high", "yky2798@gmail.com"));
        todoDao.save(new ToDo(6, "Review MyBatis", new Date(), "high", "dohee@gmail.com"));


        log.info("we've finished to fill our database");
    }

    @Bean
    public AlwaysSampler defaultSampler() {
        return new AlwaysSampler();
    }
}
