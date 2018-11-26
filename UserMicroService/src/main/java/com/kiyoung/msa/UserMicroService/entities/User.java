package com.kiyoung.msa.UserMicroService.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

//Lombok
//JPA
//VALIDATION JSR-303 -> Hibernate validator DATA BINDING email, name, password => new User(email,name,password)

@Entity
@Table(name = "users")
//@AllArgsConstructor @NoArgsConstructor
public class User {

    @Id
    @Column(name = "EMAIL")
    @Getter
    @Setter
    @NotNull
    @NotBlank
    @NotEmpty
    private String email;

    @Getter
    @Setter
    @Column(name = "NAME")
    @NotNull
    @NotBlank
    @NotEmpty
    private String name;

    @Getter
    @Setter
    @Column(name = "PASSWORD")
    @NotNull
    @NotBlank
    @NotEmpty
    private String password;

    public User() {
    }

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
