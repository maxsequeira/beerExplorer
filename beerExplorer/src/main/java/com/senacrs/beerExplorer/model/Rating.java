package com.senacrs.beerExplorer.model;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Rating")
public class Rating implements Serializable

{
    @Id
    @GeneratedValue(generator = "rating_generator")
    @SequenceGenerator(
            name = "rating_generator",
            sequenceName = "rating_sequence",
            initialValue = 1501000
    )
    private Long id;
    private Long userId;
    private Long beerId;
    private Float rating;
    private Long timestamp;

    public Rating() {
    }

    public Rating(Long userId, Long beerId, Float rating, Long timestamp) {
        this.userId = userId;
        this.beerId = beerId;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBeerId() {
        return beerId;
    }

    public void setBeerId(Long beerId) {
        this.beerId = beerId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public static Rating parseRating(String str) {
        String[] fields = str.split(",");
        if (fields.length != 4) {
            throw new IllegalArgumentException("Each line must contain 4 fields: userId, movieId, rating,timestamp");
        }
        Long userId = Long.parseLong(fields[0]);
        Long beerId = Long.parseLong(fields[1]);
        float rating = Float.parseFloat(fields[2]);
        Long timestamp = Long.parseLong(fields[3]);
        return new Rating(userId, beerId, rating,timestamp);
    }

}
