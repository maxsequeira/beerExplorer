package com.senacrs.beerExplorer.model;

import javax.persistence.*;

@Entity
@Table(name = "Beer")
public class Beer {

    @Id
    @GeneratedValue(generator = "beer_generator")
    @SequenceGenerator(
            name = "beer_generator",
            sequenceName = "beer_sequence",
            initialValue = 80000

    )
    private Long id;
    private String name;
    private String style;
    private String abv;
    private String brewery;


    public Beer() {
    }

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

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getAbv() {
        return abv;
    }

    public void setAbv(String abv) {
        this.abv = abv;
    }

    public String getBrewery() {
        return brewery;
    }

    public void setBrewery(String brewery) {
        this.brewery = brewery;
    }
}
