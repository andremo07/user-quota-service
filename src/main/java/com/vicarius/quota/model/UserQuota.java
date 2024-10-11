package com.vicarius.quota.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserQuota {

    private User user;
    private Integer requestNumber;

    public UserQuota(User user, Integer requestNumber) {
        this.user = user;
        this.requestNumber = requestNumber;
    }

    public void incrementRequestNumber() {
        requestNumber++;
    }
}
