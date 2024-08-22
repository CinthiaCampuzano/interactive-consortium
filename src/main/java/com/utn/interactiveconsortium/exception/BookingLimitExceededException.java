package com.utn.interactiveconsortium.exception;

public class BookingLimitExceededException extends Exception {
    public BookingLimitExceededException(String message) {
        super(message);
    }

}
