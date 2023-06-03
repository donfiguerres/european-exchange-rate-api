package com.europeanexchangerates.exchangeapi.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.europeanexchangerates.exchangeapi.dto.CurrencyAverageRate;
import com.europeanexchangerates.exchangeapi.dto.CurrencyConversion;
import com.europeanexchangerates.exchangeapi.dto.CurrencyHighestRate;
import com.europeanexchangerates.exchangeapi.dto.ExchangeRate;
import com.europeanexchangerates.exchangeapi.service.ExchangeRateService;

@RestController
public class ExchangeRateController {
    private final ExchangeRateService service;

    @Autowired
    public ExchangeRateController(ExchangeRateService service) {
        this.service = service;
    }

    @GetMapping("/rates")
    @Operation(summary = "Get all the available exchange rates for a given date.")
    @ApiResponse(responseCode = "204", description = "No exchange rates found for the given date.")
    public ResponseEntity<ExchangeRate> getRates(
            @Parameter(description = "Date from which to get the exchange rates. Must be ISO formatted.") @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        ExchangeRate exchangeRate = service.getRatesForDate(date);
        if (exchangeRate == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(exchangeRate);
        }
    }

    @GetMapping("/convert")
    @Operation(summary = "Convert a specific amount from one currency to another.")
    @ApiResponse(responseCode = "204", description = "No exchange rate found for the given date.")
    public ResponseEntity<CurrencyConversion> convertCurrency(
            @Parameter(description = "Date to use for the conversion . Must be ISO formatted.") @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Currency to convert from") @RequestParam("source") String source,
            @Parameter(description = "Currency to convert to") @RequestParam("target") String target,
            @Parameter(description = "Amount to convert") @RequestParam("amount") BigDecimal amount) {
        CurrencyConversion conversion = service.convertCurrency(date, source, target, amount);
        if (conversion == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(conversion);
        }
    }

    @GetMapping("/highest_rate")
    @Operation(summary = "Get the highest exchange rate for a given currency for a given date range.")
    @ApiResponse(responseCode = "204", description = "No exchange rates found for the given date range.")
    public ResponseEntity<CurrencyHighestRate> getHighestRate(
            @Parameter(description = "Start date of the date range. Must be ISO formatted.") @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date of the date range. Must be ISO formatted.") @RequestParam("end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Currency to get the highest rate for.") @RequestParam("currency") String currency) {
        CurrencyHighestRate highestRate = service.getHighestRate(startDate, endDate, currency);
        if (highestRate == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(highestRate);
        }
    }

    @GetMapping("/average_rate")
    @Operation(summary = "Get the average exchange rate for a given currency for a given date range.")
    @ApiResponse(responseCode = "204", description = "No exchange rates found for the given date range.")
    public ResponseEntity<CurrencyAverageRate> getAverageRate(
            @Parameter(description = "Start date of the date range. Must be ISO formatted.") @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date of the date range. Must be ISO formatted.") @RequestParam("end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Currency to get the average rate for.") @RequestParam("currency") String currency) {
        CurrencyAverageRate averageRate = service.getAverageRate(startDate, endDate, currency);
        if (averageRate == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(averageRate);
        }
    }
}
