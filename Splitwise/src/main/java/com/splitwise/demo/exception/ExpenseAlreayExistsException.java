package com.splitwise.demo.exception;

public class ExpenseAlreayExistsException extends RuntimeException {
    public ExpenseAlreayExistsException(String message) {
        super(message);
    }
}
