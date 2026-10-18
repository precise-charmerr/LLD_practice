package com.splitwise.demo.exception;

public class GroupAlreadyExistsException extends RuntimeException{
    public GroupAlreadyExistsException(String message) {
        super(message);
    }
}