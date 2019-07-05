package com.senacrs.beerExplorer.model;

import javax.persistence.*;

@Entity
@Table(name = "user_data")
public class User {

    @Id
//    @GeneratedValue(generator = "user_generator")
//    @SequenceGenerator(
//            name = "user_generator",
//            sequenceName = "user_sequence",
//            initialValue = 40000
//    )
    private Long id;
    @Column(name = "name", unique = true)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
