package com.quicktutorialz.microservices.TodoMicroservice.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "todos")
//@AllArgsConstructor
//@NoArgsConstructor
public class ToDo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    @Getter
    @Setter
    private Integer id;

    @Column(name = "DESCRIPTION")
    @Getter
    @Setter
    @NotNull
    @NotBlank
    @NotEmpty
    private String description;

    @Column(name = "DATE")
    @Getter
    @Setter
    private Date date;

    @Column(name = "PRIORITY")
    @Getter
    @Setter
    @NotNull
    @NotBlank
    @NotEmpty
    private String priority;

    @Column(name = "FK_USER")
    @Getter
    @Setter
    @NotNull
    @NotBlank
    @NotEmpty
    private String fkUser;

    @PrePersist
    void getTimeOperation() {
        this.date = new Date();
    }

    public ToDo() {
    }

    //
    public ToDo(Integer id, String description, Date date, String priority, String fkUser) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.priority = priority;
        this.fkUser = fkUser;
    }

    @Override
    public String toString() {
        return "ToDo{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", priority='" + priority + '\'' +
                ", fkUser='" + fkUser + '\'' +
                '}';
    }
}
