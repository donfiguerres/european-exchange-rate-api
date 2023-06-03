package com.europeanexchangerates.exchangeapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents the highest rate of a given currency in a given period of time")
public class CurrencyHighestRate {
    private String currency;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal highestRate;

    public CurrencyHighestRate(
            String currency,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal highestRate) {
        this.currency = currency;
        this.startDate = startDate;
        this.endDate = endDate;
        this.highestRate = highestRate;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getHighestRate() {
        return highestRate;
    }
}
