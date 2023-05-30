package com.europeanexchangerates.exchangeapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Exchange rates for a given date.
 */
public class ExchangeRate {
    private LocalDate date;
    private Map<String, BigDecimal> rates;

    public ExchangeRate(LocalDate date, Map<String, BigDecimal> rates) {
        this.date = date;
        this.rates = rates;
    }

    public LocalDate getDate() {
        return date;
    }

    public Map<String, BigDecimal> getRates() {
        return rates;
    }
}
