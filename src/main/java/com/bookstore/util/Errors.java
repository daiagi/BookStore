package com.bookstore.util;

public enum Errors {
    INVALID_AMOUNT("Amount must be greater than 0"),
    NEGATIVE_STOCK("Stock cannot be negative");

    private final String message;

    Errors(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }

    public String getMessage() {
        return this.message;
    }
}