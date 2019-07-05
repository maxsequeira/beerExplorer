package com.senacrs.beerExplorer.model;

public class UserInfo {

    private User user;
    private Long numberOfRatings;

    public UserInfo() {
    }

    public UserInfo(User user, Long numberOfRatings) {
        this.user = user;
        this.numberOfRatings = numberOfRatings;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getNumberOfRatings() {
        return numberOfRatings;
    }

    public void setNumberOfRatings(Long numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }
}
