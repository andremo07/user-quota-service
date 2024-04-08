package com.vicarius.quota.model;

public class UserQuota {

    private User user;
    private Integer requestNumber;

    public UserQuota(User user, Integer requestNumber) {
        this.user = user;
        this.requestNumber = requestNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(Integer requestNumber) {
        this.requestNumber = requestNumber;
    }

    public void incrementRequestNumber() {
        requestNumber++;
    }
}
