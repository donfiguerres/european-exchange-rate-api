package com.europeanexchangerates.exchangeapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CurrencyAverageRate {
    private String currency;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal averageRate;

    public CurrencyAverageRate(
            String currency,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal averageRate) {
        this.currency = currency;
        this.startDate = startDate;
        this.endDate = endDate;
        this.averageRate = averageRate;
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

    public BigDecimal getAverageRate() {
        return averageRate;
    }
}
