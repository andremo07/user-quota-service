package com.vicarius.quota.exception;

public class ResourceNotFoundException extends Exception {

    public ResourceNotFoundException(String errorMsg) {
        super(errorMsg);
    }
}
