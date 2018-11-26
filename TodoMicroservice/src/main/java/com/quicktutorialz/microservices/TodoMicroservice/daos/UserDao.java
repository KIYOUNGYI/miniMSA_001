package com.quicktutorialz.microservices.TodoMicroservice.daos;

import com.quicktutorialz.microservices.TodoMicroservice.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserDao extends JpaRepository <User,String> {

    /**
     * main strategy method
     * @param email
     * @return
     */
    Optional<User> findUserByEmail(String email);

    /**
     * Query annotiation
     * @param email
     * @return
     */
    @Query(value="SELECT * FROM users WHERE EMAIL=:email",nativeQuery = true)
    Optional<User> findUserByTheEmail(@Param("email") String email);

    /**
     * native method
     * @param email
     * @return
     */
    User findOne(String email);
}
