package com.europeanexchangerates.exchangeapi.provider;

import java.time.LocalDate;
import java.util.TreeMap;

import com.europeanexchangerates.exchangeapi.dto.ExchangeRate;

public interface ExchangeRateProvider {
    TreeMap<LocalDate, ExchangeRate> getExchangeRates() throws Exception;
}
