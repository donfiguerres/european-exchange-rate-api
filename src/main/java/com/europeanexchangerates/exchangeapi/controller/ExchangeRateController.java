package com.europeanexchangerates.exchangeapi.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
    public ExchangeRate getRates(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.getRatesForDate(date);
    }

    @GetMapping("/convert")
    public CurrencyConversion convertCurrency(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("source") String source,
            @RequestParam("target") String target,
            @RequestParam("amount") BigDecimal amount) {
        return service.convertCurrency(date, source, target, amount);
    }

    @GetMapping("/highest_rate")
    public CurrencyHighestRate getHighestRate(
            @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam("currency") String currency) {
        return service.getHighestRate(startDate, endDate, currency);
    }

    @GetMapping("/average_rate")
    public CurrencyAverageRate getAverageRate(
            @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam("currency") String currency) {
        return service.getAverageRate(startDate, endDate, currency);
    }
}
