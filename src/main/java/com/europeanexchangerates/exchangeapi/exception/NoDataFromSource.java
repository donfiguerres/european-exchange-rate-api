package com.europeanexchangerates.exchangeapi.exception;

public class NoDataFromSource extends RuntimeException {
    public NoDataFromSource(String message) {
        super(message);
    }
}
