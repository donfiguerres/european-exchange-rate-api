package com.europeanexchangerates.exchangeapi.dto;

import java.math.BigDecimal;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Contains all the exchange rates for a given date")
public class ExchangeRate {
    private Map<String, BigDecimal> rates;

    public ExchangeRate(Map<String, BigDecimal> rates) {
        this.rates = rates;
    }

    public Map<String, BigDecimal> getRates() {
        return rates;
    }
}
