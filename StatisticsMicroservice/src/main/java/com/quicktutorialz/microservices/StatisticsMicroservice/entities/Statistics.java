package com.quicktutorialz.microservices.StatisticsMicroservice.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name="latest_statistics")

public class Statistics {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="ID")
    @Getter @Setter
    private Integer id;

    @Column(name="DESCRIPTION")
    @Getter @Setter
    @NotNull @NotEmpty @NotBlank
    private String description;

    @Column(name="DATE")
    @Getter @Setter
    private Date date;

    @Column(name="EMAIL")
    @Getter @Setter
    private String email;

    @PrePersist // AUTOMATICALLY GGENERATE DATE
    private void getTimeOperation() {
        this.date = new Date();
    }

    public Statistics(){}

    public Statistics(Integer id, String description, Date date, String email) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.email = email;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", email='" + email + '\'' +
                '}';
    }
}
