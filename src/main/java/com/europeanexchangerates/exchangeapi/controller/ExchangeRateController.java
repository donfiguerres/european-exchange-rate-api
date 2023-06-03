package com.europeanexchangerates.exchangeapi.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
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
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("source") String source,
            @RequestParam("target") String target,
            @RequestParam("amount") BigDecimal amount) {
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
            @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam("currency") String currency) {
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
            @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam("currency") String currency) {
        CurrencyAverageRate averageRate = service.getAverageRate(startDate, endDate, currency);
        if (averageRate == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(averageRate);
        }
    }
}
