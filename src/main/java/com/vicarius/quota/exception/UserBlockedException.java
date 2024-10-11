package com.vicarius.quota.exception;

public class UserBlockedException extends Exception {

    public UserBlockedException(String errorMsg) {
        super(errorMsg);
    }
}
