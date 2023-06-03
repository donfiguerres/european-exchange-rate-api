package com.europeanexchangerates.exchangeapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents a conversion between two currencies")
public class CurrencyConversion {
    private String sourceCurrency;
    private String targetCurrency;
    private BigDecimal sourceAmount;
    private LocalDate conversionDate;
    private BigDecimal convertedAmount;

    public CurrencyConversion(
            String sourceCurrency,
            String targetCurrency,
            BigDecimal sourceAmount,
            LocalDate conversionDate,
            BigDecimal convertedAmount) {
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
        this.sourceAmount = sourceAmount;
        this.conversionDate = conversionDate;
        this.convertedAmount = convertedAmount;
    }

    public String getSourceCurrency() {
        return sourceCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public BigDecimal getSourceAmount() {
        return sourceAmount;
    }

    public LocalDate getConversionDate() {
        return conversionDate;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }
}
